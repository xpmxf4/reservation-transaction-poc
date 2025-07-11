package com.study.transactional.event.reservation_transaction_poc.booking.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.BookingEventPayload;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.OutboxStatus;
import com.study.transactional.event.reservation_transaction_poc.booking.event.BookingCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.booking.publisher.BookingEventPublisher;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingOutboxRepository;
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
        List<BookingOutbox> pendingEvents = outboxRepository.findTop10ByStatusOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (BookingOutbox outbox : pendingEvents) {
            try {
                outbox.markAsProcessing();
                BookingEventPayload payload = objectMapper.readValue(outbox.getPayload(),
                        BookingEventPayload.class
                );

                BookingCreatedEvent event = new BookingCreatedEvent(
                    outbox.getTraceId(),
                    outbox.getId(),
                    payload.bookingId(),
                    payload.userPhone(),
                    payload.productId()
                );

                bookingEventPublisher.publishReservationCreatedEvent(event);

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
