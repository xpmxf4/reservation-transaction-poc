package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.repository;

import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.entity.BookingOutbox;
import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingOutboxRepository extends JpaRepository<BookingOutbox, Long> {

    List<BookingOutbox> findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus status);

}

