package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record CreateBookingRequestDto(
        Long companyId,
        Long userId,
        String productId,
        String productDetailId,
        String paymentId
) {
}