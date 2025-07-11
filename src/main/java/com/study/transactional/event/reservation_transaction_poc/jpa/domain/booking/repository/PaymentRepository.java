package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
