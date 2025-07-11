package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingOutboxRepository extends JpaRepository<BookingOutbox, Long> {
    List<BookingOutbox> findByPublishedFalseOrderByCreatedAtAsc();
}