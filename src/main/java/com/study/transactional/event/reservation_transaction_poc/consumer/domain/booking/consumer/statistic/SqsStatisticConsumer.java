package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.consumer.statistic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.event.dto.BookingCreatedEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsStatisticConsumer {

    private final ObjectMapper objectMapper;

    @SqsListener("reservation-stat-queue")
    public void consumeReservationCreatedEvent(@Payload String payload) {
        log.info("Received SQS message from 'reservation-stat-queue' : {}", payload);

        BookingCreatedEvent reservationCreatedEvent = objectMapper.convertValue(payload, BookingCreatedEvent.class);

        // 중복 여부 체크

        // outbox event 테이블 상태 업데이트 : pending -> success

        // 이벤트 처리

    }
}
