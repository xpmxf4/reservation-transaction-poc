package com.study.transactional.event.reservation_transaction_poc.booking.service.transactionalEventListener.event;

public record ReservationCreatedEvent(
        Long id,
        String userPhone,
        String productId
) {
}
