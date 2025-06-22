package com.study.transactional.event.reservation_transaction_poc.ums.service;

import com.study.transactional.event.reservation_transaction_poc.booking.service.event.ReservationCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class BookingEventListener {

    // event 발행 및, tx=AFTER_COMMIT 후 처리
    @TransactionalEventListener
    public void handleReservationCreatedEvent(ReservationCreatedEvent event) {
        log.info("[TRANSACTIONAL EVENT LISTENER] Handling ReservationCreatedEvent: id={}, userPhone={}, productId={}",
                event.id(), event.userPhone(), event.productId());
    }

    @TransactionalEventListener
    public void handleReservationCreatedEventWithException(ReservationCreatedEvent event) {
        log.info("[TRANSACTIONAL EVENT LISTENER WITH EXCEPTION] Handling ReservationCreatedEvent: id={}, userPhone={}, productId={}",
                event.id(), event.userPhone(), event.productId());

        throw new RuntimeException("Simulated exception to trigger rollback");
    }

    // event 발행 되고 바로 처리
    @EventListener
    public void handleReservationCreatedEventWithoutTransaction(ReservationCreatedEvent event) {
        log.info("[EVENT LISTENER] Handling ReservationCreatedEvent: id={}, userPhone={}, productId={}",
                event.id(), event.userPhone(), event.productId());
    }
}
