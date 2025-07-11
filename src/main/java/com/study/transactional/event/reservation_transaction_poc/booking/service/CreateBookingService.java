package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.dto.BookingResponse;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBookingCreatedOutbox;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBookingRequestDto;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.Booking;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingRepository;
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
public class CreateBookingService {

    private final ApplicationEventPublisher publisher;
    private final CreateBookingOutboxService createBookingOutboxService;
    private final BookingRepository bookingRepository;

    @Transactional
    public Long createBooking(CreateBookingRequestDto createBookingRequestDto) {
        Long userId = createBookingRequestDto.userId();
        String productId = createBookingRequestDto.productId();
        String productDetailId = createBookingRequestDto.productDetailId();
        String paymentId = createBookingRequestDto.paymentId();

        MDC.put("paymentId", paymentId);
        MDC.put("eventName", "BOOKING_FINALIZE");
        MDC.put("productDetailId", productDetailId);
        try {
            // 1. 최종 Booking 엔티티 생성 및 저장 (사용자 정의 구조 사용)
            Booking booking = Booking.builder()
                    .userId(userId)
                    .productId(productId)
                    .userPhone("010-0000-0000") // 실제로는 user-service 등에서 조회
                    .status(BookingStatus.RESERVED).build();
            Booking savedBooking = bookingRepository.save(booking);

            // 2. Outbox 이벤트 생성 및 저장 (사용자 정의 구조 사용)
            CreateBookingCreatedOutbox createdOutboxEvent = new CreateBookingCreatedOutbox(MDC.get("traceId"),
                    booking.getId(),
                    booking.getUserId(),
                    booking.getProductId()
            );
            Long createdEventId = createBookingOutboxService.createBookingOutbox(createdOutboxEvent);

            publisher.publishEvent(new BookingCreatedEvent(
                    MDC.get("traceId"),
                    createdEventId,
                    savedBooking.getUserId(),
                    savedBooking.getProductId(),
                    paymentId
            ));

            log.info("[BOOKING_CONFIRM_SUCCESS] 최종 예약 및 Outbox 저장 완료. Booking ID: {}", booking.getId());
            return booking.getId();
        } finally {
            MDC.remove("paymentId");
            MDC.remove("eventName");
            MDC.remove("productDetailId");
        }

    }

}
