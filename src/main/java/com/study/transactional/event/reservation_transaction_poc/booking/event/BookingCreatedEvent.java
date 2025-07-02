package com.study.transactional.event.reservation_transaction_poc.booking.event;

public record BookingCreatedEvent(
    Long eventId,
    Long bookingId,
    String userPhone,
    String productId
) {

}
