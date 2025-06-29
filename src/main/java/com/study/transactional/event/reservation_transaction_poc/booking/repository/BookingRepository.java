package com.study.transactional.event.reservation_transaction_poc.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Reservation, Long> {

}
