package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.listener;

import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.event.dto.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.sns.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingTransactionalEventListener {

    private final BookingEventPublisher bookingEventPublisher;

    //     event 발행 및, tx=AFTER_COMMIT 후 처리
    @Retryable(value = {RuntimeException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    @TransactionalEventListener
    public void handleReservationCreatedEvent(BookingCreatedEvent event) {

        log.info("[TRANSACTIONAL EVENT LISTENER] Handling ReservationCreatedEvent: bookingId={}, userPhone={}, productId={}", event.bookingId(), event.userPhone(), event.productId());

        bookingEventPublisher.publishReservationCreatedEvent(event);
    }

}
