package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.BookingOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateBookingOutboxRepository extends JpaRepository<BookingOutbox, Long> {

}
