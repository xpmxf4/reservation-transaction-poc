package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record ClientCreateBookingRequestDto(
    Long userNo,
    Long companyId,
    String productId,
    String productDetailId) {

}
