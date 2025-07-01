package com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.entity;

import com.study.transactional.event.reservation_transaction_poc.publisher.domain.booking.outbox.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private static final String evertType="BOOKING_CREATED"; // 예약 생성 이벤트

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status; // PENDING, PROCESSING, SUCCESS, RETRYING, FAILED

    @Column(nullable = false)
    private int retryCount = 0;

    private LocalDateTime lastAttemptAt;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public BookingOutbox(String payload) {
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
