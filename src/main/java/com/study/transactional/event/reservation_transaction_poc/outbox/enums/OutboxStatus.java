package com.study.transactional.event.reservation_transaction_poc.outbox.enums;

public enum OutboxStatus {
    PENDING,      // Initial state when the event is created
    PROCESSING,   // When the event is being processed
    SUCCESS,      // When the event is successfully processed
    RETRYING,     // When the event processing failed and is being retried
    FAILED        // When the event processing has permanently failed
}
