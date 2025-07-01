package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.event.dto;

public record ReservationCreatedEvent(
        Long reservationId,
        String userPhone,
        String productId
) {
}
