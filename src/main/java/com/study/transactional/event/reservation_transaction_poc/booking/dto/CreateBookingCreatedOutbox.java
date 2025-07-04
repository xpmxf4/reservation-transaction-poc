package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record CreateBookingCreatedOutbox(
    String traceId,
    Long reservationId,
    String userPhone,
    String productId
) {

}
