package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.booking.enums.OutboxStatus;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadBookingOutboxRepository extends JpaRepository<BookingOutbox, Long> {

    List<BookingOutbox> findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus status);

}

