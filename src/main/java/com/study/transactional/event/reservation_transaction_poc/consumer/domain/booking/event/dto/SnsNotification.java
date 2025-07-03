package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SnsNotification(
    @JsonProperty("MessageId") String messageId,
    @JsonProperty("TopicArn") String topicArn,
    @JsonProperty("Message") String message, // 실제 내용물이 담긴 JSON 문자열
    @JsonProperty("Timestamp") String timestamp
) {
}