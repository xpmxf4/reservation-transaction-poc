package com.study.transactional.event.reservation_transaction_poc.outbox.handler;

public interface SpecificEventHandler {
    boolean supports(String eventType);
    void handle(String payload);
}
