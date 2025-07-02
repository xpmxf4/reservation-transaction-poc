package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.booking.publisher.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingTransactionalEventListenerService {

    private final BookingEventPublisher bookingEventPublisher;

    //     event 발행 및, tx=AFTER_COMMIT 후 처리
    @Retryable(value = {RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    @TransactionalEventListener
    public void handleReservationCreatedEvent(BookingCreatedEvent event) {

        log.info("[TRANSACTIONAL EVENT LISTENER] Handling ReservationCreatedEvent: bookingId={}, userPhone={}, productId={}", event.bookingId(), event.userPhone(), event.productId());

        bookingEventPublisher.publishReservationCreatedEvent(event);
    }

}
