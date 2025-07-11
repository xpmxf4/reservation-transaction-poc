package com.study.transactional.event.reservation_transaction_poc.booking.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.cloud.aws.sns")
public class SnsProperties {

    private final Topic topic;

    @Getter
    public static class Topic {
        private String reservationCreated;
    }
}
