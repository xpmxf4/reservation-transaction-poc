package com.study.transactional.event.reservation_transaction_poc.outbox.repository;

import com.study.transactional.event.reservation_transaction_poc.outbox.entity.Outbox;
import com.study.transactional.event.reservation_transaction_poc.outbox.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
