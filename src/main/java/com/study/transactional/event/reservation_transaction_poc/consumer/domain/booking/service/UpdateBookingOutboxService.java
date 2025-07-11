package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.service;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingOutboxRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateBookingOutboxService {

    private final BookingOutboxRepository outboxRepository;

    @Transactional
    public void markOutboxSuccess(Long eventId) {
       // 1. event id 로 아웃박스 조회
        BookingOutbox outbox = outboxRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Booking outbox not found with id: " + eventId));

        // 2. entity 의 상태 변경하는 메서드 호출
        outbox.markAsSuccess();
    }

    @Transactional
    public void markOutboxFailed(Long eventId) {
        // 1. event id 로 아웃박스 조회
        BookingOutbox outbox = outboxRepository.findById(eventId)
            .orElseThrow(() -> new EntityNotFoundException("Booking outbox not found with id: " + eventId));

        // 2. entity 의 상태 변경 메서드 호출
        outbox.markAsFailed();
    }
}
