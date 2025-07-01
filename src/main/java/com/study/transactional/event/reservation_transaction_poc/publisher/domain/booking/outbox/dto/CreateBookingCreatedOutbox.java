package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.dto;

public record CreateBookingCreatedOutbox(
    Long reservationId,
    String userPhone,
    String productId
) {

}
