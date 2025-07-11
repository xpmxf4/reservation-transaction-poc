package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record CreateBookingCreatedOutbox(
        String traceId,
        Long bookingId,
        Long userId,
        String productId
) {

}
