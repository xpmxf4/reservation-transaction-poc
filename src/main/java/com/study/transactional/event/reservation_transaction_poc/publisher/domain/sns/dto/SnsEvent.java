package com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.dto;

public record SnsEvent(
    Long eventId,
    String message,
    String type
) {

}
