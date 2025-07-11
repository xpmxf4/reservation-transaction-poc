package com.study.transactional.event.reservation_transaction_poc.booking.dto;

public record CreateBooking(
    Long userNo,
    String companyId,
    String productId,
    String productDetailId) {

}
