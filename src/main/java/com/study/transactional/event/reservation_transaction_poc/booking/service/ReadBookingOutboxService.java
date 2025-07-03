package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.ReadBookingOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadBookingOutboxService {

    private final ReadBookingOutboxRepository readBookingOutboxRepository;

}
