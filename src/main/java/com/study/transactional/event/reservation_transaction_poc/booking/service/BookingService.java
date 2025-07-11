package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.dto.BookingResponse;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBooking;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.Booking;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingRepository;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.SeatHoldRepository;
import com.study.transactional.event.reservation_transaction_poc.payment.dto.PaymentResponse;
import com.study.transactional.event.reservation_transaction_poc.payment.service.PaymentService;
import com.study.transactional.event.reservation_transaction_poc.seat.dto.HoldSeatResponse;
import com.study.transactional.event.reservation_transaction_poc.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatService seatService;
    private final PaymentService paymentService;
    private final CreateBookingOutboxService createBookingOutboxService;
    private final ApplicationEventPublisher eventPublisher;
    private final SeatHoldRepository seatHoldRepository;

    public BookingResponse createBooking(CreateBooking createBooking) {
        String productId = createBooking.productId();
        String productDetailId = createBooking.productDetailId();

        // 공통 컨텍스트 및 이 단계의 주요 비즈니스 컨텍스트 설정
        MDC.put("productId", productId);
        MDC.put("productDetailId", productDetailId);

        // 1. 좌석 선점
        HoldSeatResponse holdSeatResponse = seatService.holdSeat(productId, productDetailId);
        if (!holdSeatResponse.isSuccess()) {
            log.warn("Hold seat failed: {}", holdSeatResponse);
            return new BookingResponse(false, null, "좌석 확보 실패");
        }
        Long seatHoldId = holdSeatResponse.seatHoldId();

        // --- 좌석 선점 성공 ---
        // 이 상태에서 결제가 실패하면, 선점한 좌석을 반드시 해제
        try {
            // 2. 공급사 결제 시도
            // 실제로는 상품 가격 정보를 사용자한테서 받아야 하지만, 여기서는 10000원으로 고정
            PaymentResponse paymentResponse = paymentService.processPayment(productId, 10000L);

            if (!paymentResponse.isSuccess()) {
                // 3-1. 결제 실패 시 보상 트랜잭션 (좌석 해제)
                log.error("결제 실패. 좌석 선점 해제 로직을 실행합니다.");
                seatService.releaseSeat(seatHoldId); // 보상 로직
                return new BookingResponse(false, null, "결제 실패 : " + paymentResponse.message());
            }

            // 3-2. 결제 성공 시, 최종 예약 처리 (DB 에 예약/아웃박스 저장, 이벤트 발행)
            String paymentId = paymentResponse.paymentId();
            return finalizeBookingAndCreateOutbox(productId, paymentId, seatHoldId);

        } catch (Exception e) {
            log.error("결제 중 예측하지 못한 예외 발생. 좌석 선점 해제 시도.", e);
            seatService.releaseSeat(seatHoldId);
            return new BookingResponse(false, null, "알 수 없는 오류로 예약 실패");
        } finally {
            MDC.remove("productId");
            MDC.remove("productDetailId");
        }
    }

    @Transactional
    public BookingResponse finalizeBookingAndCreateOutbox(String productId, String paymentId, Long seatHoldId) {
        MDC.put("paymentId", paymentId);
        MDC.put("eventName", "BOOKING_FINALIZE");
        try {
            // 1. Seat 도메인 서비스 호출을 통해 좌석 상태를 '확정'으로 변경
            seatService.confirmHold(seatHoldId);

            // 2. 최종 Booking 엔티티 생성 및 저장 (사용자 정의 구조 사용)
            Booking booking = Booking.builder()
                .userId(Long.valueOf(MDC.get("userId")))
                .productId(productId)
                .userPhone("010-0000-0000") // 실제로는 user-service 등에서 조회
                .status(BookingStatus.RESERVED)
                .build();
            bookingRepository.save(booking);

            // 3. Outbox 이벤트 생성 및 저장 (사용자 정의 구조 사용)
            String eventPayload = createEventPayload("BOOKING_CONFIRMED", booking);
            BookingOutbox outboxEvent = BookingOutbox.builder()
                .eventType("BOOKING_CONFIRMED")
                .traceId(MDC.get("traceId"))
                .payload(eventPayload)
                .build();
            outboxRepository.save(outboxEvent);

            log.info("[BOOKING_CONFIRM_SUCCESS] 최종 예약 및 Outbox 저장 완료. Booking ID: {}", booking.getId());
            return new BookingResponse(true, booking.getId().toString(), "예약 성공");
        } finally {
            MDC.remove("paymentId");
            MDC.remove("eventName");
        }
    }
}
