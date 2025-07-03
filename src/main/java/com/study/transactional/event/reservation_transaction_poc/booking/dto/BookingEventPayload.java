package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record BookingEventPayload(
    Long bookingId,
    String userPhone,
    String productId
){

}
