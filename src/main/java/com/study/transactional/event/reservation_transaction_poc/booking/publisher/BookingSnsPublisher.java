package com.study.transactional.event.reservation_transaction_poc.booking.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.config.SnsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingSnsPublisher implements BookingEventPublisher {

    private final SnsClient snsClient;
    private final SnsProperties snsProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void publishReservationCreatedEvent(BookingCreatedEvent event) {
        try {
            String topicArn = snsProperties.topics().reservationCreated();

            String payload = objectMapper.writeValueAsString(event);

            PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(payload)
                .build();

            snsClient.publish(publishRequest);

            log.info("Successfully published event to SNS topic {}: {}", topicArn, event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event to JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Failed to publish event to SNS. event : {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
