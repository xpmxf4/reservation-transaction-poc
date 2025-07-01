package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.event.dto;

public record BookingCreatedEvent(
    Long eventId,
    Long bookingId,
    String userPhone,
    String productId
) {

}
