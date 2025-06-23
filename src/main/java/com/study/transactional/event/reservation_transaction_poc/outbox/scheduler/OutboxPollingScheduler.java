package com.study.transactional.event.reservation_transaction_poc.outbox.scheduler;

import com.study.transactional.event.reservation_transaction_poc.outbox.entity.Outbox;
import com.study.transactional.event.reservation_transaction_poc.outbox.enums.OutboxStatus;
import com.study.transactional.event.reservation_transaction_poc.outbox.handler.OutboxEventDispatcher;
import com.study.transactional.event.reservation_transaction_poc.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollingScheduler {

    private final OutboxRepository outboxRepository;
    private final OutboxEventDispatcher outboxEventDispatcher;

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void pollAndProcessOutboxEvents() {
        List<Outbox> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("found {} pending outbox events, processing...", pendingEvents.size());

        for (Outbox outbox : pendingEvents) {
            outbox.markAsProcessing();
            outboxRepository.save(outbox);

            outboxEventDispatcher.dispatch(outbox);
        }
    }
}
