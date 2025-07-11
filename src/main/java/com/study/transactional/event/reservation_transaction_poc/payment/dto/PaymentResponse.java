package com.study.transactional.event.reservation_transaction_poc.payment.dto;

public record PaymentResponse(
    boolean isSuccess,
    String paymentId,
    String message
) {

}
