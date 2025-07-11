package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record BookingResponse(
    boolean isSuccess,
    String bookingId,
    String message
) {

}
