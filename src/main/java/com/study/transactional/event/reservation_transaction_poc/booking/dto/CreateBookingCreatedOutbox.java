package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record CreateBookingCreatedOutbox(
    Long reservationId,
    String userPhone,
    String productId
) {

}
