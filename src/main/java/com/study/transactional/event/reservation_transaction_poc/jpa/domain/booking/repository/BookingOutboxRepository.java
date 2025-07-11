package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingOutboxRepository extends JpaRepository<BookingOutbox, Long> {
    @Query("SELECT bo FROM BookingOutbox bo WHERE bo.status = 'PENDING' ORDER BY bo.createdAt ASC")
    List<BookingOutbox> findTop10ByStatusOrderByCreatedAtAsc();
}