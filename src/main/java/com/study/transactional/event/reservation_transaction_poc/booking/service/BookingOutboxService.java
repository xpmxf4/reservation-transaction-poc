package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.booking.repository.BookingOutboxRepository;
import com.study.transactional.event.reservation_transaction_poc.booking.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingOutboxService {

    private final ObjectMapper objectMapper;
    private final BookingOutboxRepository outboxRepository;

    @Transactional
    public void createOutboxEvent(ReservationCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            BookingOutbox reservedBookingOutboxEvent = new BookingOutbox(payload);

            outboxRepository.save(reservedBookingOutboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error creating booking outbox: " + e.getMessage(), e);
        }
    }
}
