package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBookingCreatedOutbox;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.Booking;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CreateBookingOutboxService createBookingOutboxService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Long createBooking(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Booking newBooking = new Booking(userId, productId, userPhone, BookingStatus.RESERVED);
        Booking createdBooking = bookingRepository.save(newBooking);

        // 3. 아웃박스 테이블에 저장
        CreateBookingCreatedOutbox createBookingCreatedOutbox = new CreateBookingCreatedOutbox(
            MDC.get("traceId"),
            createdBooking.getId(),
            userPhone,
            productId
        );
        Long createdEventId = createBookingOutboxService.createBookingOutbox(createBookingCreatedOutbox);

        // 4. 예약 이벤트 발행
        BookingCreatedEvent bookingCreatedEvent = new BookingCreatedEvent(
            MDC.get("traceId"),
            createdEventId,
            createdBooking.getId(),
            userPhone,
            productId
        );
        applicationEventPublisher.publishEvent(bookingCreatedEvent);

        // 5. 예약 ID 반환
        return createdBooking.getId();
    }
}
