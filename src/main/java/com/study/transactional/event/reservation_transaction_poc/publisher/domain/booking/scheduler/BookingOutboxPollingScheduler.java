package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.enums.OutboxStatus;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.event.dto.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.repository.BookingOutboxRepository;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.sns.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingOutboxPollingScheduler {

    private final ObjectMapper objectMapper;
    private final BookingOutboxRepository outboxRepository;
    private final BookingEventPublisher bookingEventPublisher;

    @Transactional
    @Scheduled(fixedDelay = 10000)
    public void pollAndProcessOutboxEvents() {
        List<BookingOutbox> pendingEvents = outboxRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (BookingOutbox outbox : pendingEvents) {
            try {
                outbox.markAsProcessing();
                BookingCreatedEvent event = objectMapper.readValue(outbox.getPayload(),
                        BookingCreatedEvent.class
                );

                // Publish the event using the publisher
                bookingEventPublisher.publishReservationCreatedEvent(event);

                // Update the outbox status to PROCESSING
                outbox.markAsSuccess();

                log.info("Processed outbox event: {}", outbox.getId());
            } catch (Exception e) {
                log.error("Failed to process outbox event: {}, error: {}", outbox.getId(), e.getMessage(), e);
                outbox.markAsFailed();
            }
        }

        log.info("found {} pending outbox events, processing...", pendingEvents.size());

    }
}
