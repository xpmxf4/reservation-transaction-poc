package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.event.dto;

public record BookingCreatedEvent(
    String traceId,
    Long eventId,
    Long bookingId,
    String userPhone,
    String productId
) {

}
