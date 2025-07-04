package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.consumer.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.event.dto.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.event.dto.SnsNotification;
import com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.service.UpdateBookingOutboxService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsNotificationConsumer {

    private final ObjectMapper objectMapper;
    private final UpdateBookingOutboxService updateBookingOutboxService;

    @SqsListener("reservation-noti-queue")
    public void consumeReservationCreatedEvent(@Payload String payload) {
        log.info("Received SQS message from 'reservation-noti-queue' : {}", payload);

        BookingCreatedEvent bookingCreatedEvent = null;
        try {
            // payload -> bookingCreatedEvent 객체로 변환
            SnsNotification snsNotification = objectMapper.readValue(payload, SnsNotification.class);
            bookingCreatedEvent = objectMapper.readValue(snsNotification.message(), BookingCreatedEvent.class);

            // traceId 설정
            MDC.put("traceId", bookingCreatedEvent.traceId());
            log.info("Received SNS message from 'reservation-noti-queue'");

            // 이벤트 처리 로직 (예: 알림 발송)
            log.info("consuming... bookingId={}", bookingCreatedEvent.bookingId());
            Thread.sleep(5000);

            // Outbox 테이블 상태를 'SUCCESS' 로 업데이트
            updateBookingOutboxService.markOutboxSuccess(bookingCreatedEvent.eventId());

        } catch (IllegalArgumentException e) {
            log.error("Error parsing reservation-noti-queue payload: {}", payload, e);
            // TODO : 별도 처리 (ex.DLQ)
        } catch (Exception e) {
            log.error("Error consuming reservation-noti-queue payload for eventId: {}",
                (bookingCreatedEvent != null ? bookingCreatedEvent.eventId() : "N/A"), e);

            // 4. 처리 중 에외 발생 시, Outbox 테이블 상태를 'FAILED'로 업데이트
            if (bookingCreatedEvent != null) {
                updateBookingOutboxService.markOutboxFailed(bookingCreatedEvent.eventId());
            }
        } finally {
            MDC.remove("traceId");
        }
    }
}
