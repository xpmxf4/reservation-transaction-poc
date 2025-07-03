package com.study.transactional.event.reservation_transaction_poc.booking.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.booking.service.ReadBookingOutboxService;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.SnsEventPublisher;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.config.SnsProperties;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.sns.dto.SnsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventSnsPublisher implements BookingEventPublisher {

    private final SnsEventPublisher snsEventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void publishReservationCreatedEvent(BookingCreatedEvent event) {
        try {
            Long eventId = event.eventId();
            String payload = objectMapper.writeValueAsString(event);
            String type = "reservation-created";

            SnsEvent snsEvent = new SnsEvent(
                eventId, payload, type
            );

            snsEventPublisher.publishEvent(snsEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event to JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Failed to publish event to SNS. event : {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
