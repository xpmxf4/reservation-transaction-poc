package com.study.transactional.event.reservation_transaction_poc.payment.exception;

public class PaymentGatewayException extends RuntimeException {

    public PaymentGatewayException(String message) {
        super(message);
    }
}
