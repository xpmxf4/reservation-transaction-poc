package com.study.transactional.event.reservation_transaction_poc.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingOutboxRepository extends JpaRepository<BookingOutbox, Long> {

    List<BookingOutbox> findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus status);

}

