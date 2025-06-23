package com.study.transactional.event.reservation_transaction_poc.outbox.handler;

import com.study.transactional.event.reservation_transaction_poc.outbox.entity.Outbox;
import com.study.transactional.event.reservation_transaction_poc.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventDispatcher {

    private final List<SpecificEventHandler> handlers;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void dispatch(Outbox outbox) {
        try {
            log.info("Dispatching outbox event: {}", outbox);

            handlers.stream()
                    .filter(handler -> handler.supports(outbox.getEvertType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No handler found for event type: " + outbox.getEvertType()))
                    .handle(outbox.getPayload());

        } catch (Exception e) {
            log.error("Failed to process outbox event: {}", outbox.getId(), e);
            outbox.incrementRetryCount();
            outbox.markAsFailed();
            outboxRepository.save(outbox);
        }
    }
}
