package com.study.transactional.event.reservation_transaction_poc.outbox.entity;

import com.study.transactional.event.reservation_transaction_poc.outbox.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String evertType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status; // PENDING, PROCESSING, SUCCESS, RETRYING, FAILED

    @Column(nullable = false)
    private int retryCount = 0;

    private LocalDateTime lastAttemptAt;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public Outbox(String evertType, String payload) {
        this.evertType = evertType;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
    }

    public void markAsProcessing() {
        this.status = OutboxStatus.PROCESSING;
        this.lastAttemptAt = LocalDateTime.now();
    }

    public void markAsSuccess() {
        this.status = OutboxStatus.SUCCESS;
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }
}
