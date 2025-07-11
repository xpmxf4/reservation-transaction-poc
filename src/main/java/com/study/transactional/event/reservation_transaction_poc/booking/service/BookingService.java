package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.dto.BookingResponse;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.ClientCreateBookingRequestDto;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBookingRequestDto;
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
    private final CreateBookingService createBookingService;

    public BookingResponse createBooking(ClientCreateBookingRequestDto clientCreateBookingRequestDto) {
        String productId = clientCreateBookingRequestDto.productId();
        String productDetailId = clientCreateBookingRequestDto.productDetailId();

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

            CreateBookingRequestDto createBookingRequestDto = new CreateBookingRequestDto(
                    clientCreateBookingRequestDto.companyId(),
                    clientCreateBookingRequestDto.userNo(),
                    productId,
                    productDetailId,
                    paymentId
            );
            Long createdBookingId = createBookingService.createBooking(createBookingRequestDto);

            return new BookingResponse(
                    true,
                    createdBookingId.toString(),
                    "예약 성공. 결제 ID: " + paymentId + ", 예약 ID: " + createdBookingId
            );
        } catch (Exception e) {
            log.error("결제 중 예측하지 못한 예외 발생. 좌석 선점 해제 시도.", e);
            seatService.releaseSeat(seatHoldId);
            return new BookingResponse(false, null, "알 수 없는 오류로 예약 실패");
        } finally {
            MDC.remove("productId");
            MDC.remove("productDetailId");
        }
    }

}
