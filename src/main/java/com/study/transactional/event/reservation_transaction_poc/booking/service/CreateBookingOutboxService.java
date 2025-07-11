package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.dto.CreateBookingCreatedOutbox;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateBookingOutboxService {

    private final ObjectMapper objectMapper;
    private final BookingOutboxRepository bookingOutboxRepository;

    @Transactional
    public Long createBookingOutbox(CreateBookingCreatedOutbox createBookingCreatedOutbox) {
        try {
            String payload = objectMapper.writeValueAsString(createBookingCreatedOutbox);

            BookingOutbox bookingOutbox = new BookingOutbox(
                    "BOOKING_CREATED",
                    createBookingCreatedOutbox.traceId(),
                    payload
            );

            BookingOutbox savedOutbox = bookingOutboxRepository.save(bookingOutbox);

            return savedOutbox.getId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
