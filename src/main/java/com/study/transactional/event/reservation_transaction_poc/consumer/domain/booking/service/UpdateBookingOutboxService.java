package com.study.transactional.event.reservation_transaction_poc.consumer.domain.booking.service;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.UpdateBookingOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBookingOutboxService {

    private final UpdateBookingOutboxRepository updateBookingOutboxRepository;
}
