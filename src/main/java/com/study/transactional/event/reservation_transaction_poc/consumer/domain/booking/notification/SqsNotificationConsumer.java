package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsNotificationConsumer {

    private final ObjectMapper objectMapper;

    @SqsListener("reservation-noti-queue")
    public void consumeReservationCreatedEvent(@Payload String payload) {
        log.info("Received SQS message from 'reservation-noti-queue' : {}", payload);

    }
}
