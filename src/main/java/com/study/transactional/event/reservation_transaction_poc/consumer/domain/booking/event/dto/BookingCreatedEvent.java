package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.event.dto;

public record BookingCreatedEvent(
    Long eventId,
    Long reservationId,
    String userPhone,
    String productId
) {

}
