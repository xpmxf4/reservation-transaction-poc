package com.study.transactional.event.reservation_transaction_poc.booking.event;

public record ReservationCreatedEvent(
        Long reservationId,
        String userPhone,
        String productId
) {
}
