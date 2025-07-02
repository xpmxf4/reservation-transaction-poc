package com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.config;

import com.zaxxer.hikari.util.Credentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.aws.sns")
public record SnsProperties(
    String region,
    String endpoint,
    Topics topics
) {
    public record Topics(String reservationCreated) {}
}