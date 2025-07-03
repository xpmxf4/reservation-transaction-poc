package com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns;

import com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.config.SnsProperties;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.dto.SnsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnsEventPublisher {

    private final SnsClient snsClient;
    private final SnsProperties snsProperties;

    public void publishEvent(SnsEvent event) { // dto 를 interface 로 만들어, DI 를 해볼수도?
        String topicArn = snsProperties.topics().reservationCreated();

        PublishRequest publishRequest = PublishRequest.builder()
            .topicArn(topicArn)
            .message(event.message())
            .build();

        snsClient.publish(publishRequest);

        log.info("Successfully published event to SNS topic {}: {}", topicArn, event);
    }
}
