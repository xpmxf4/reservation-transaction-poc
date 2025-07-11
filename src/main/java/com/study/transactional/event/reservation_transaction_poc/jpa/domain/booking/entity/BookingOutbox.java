package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.study.transactional.event.reservation_transaction_poc.booking.enums.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String eventType; // static final이 아닌 인스턴스 필드로 변경
    @Column(nullable = false)
    private String traceId;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;
    @Column(nullable = false)
    private int retryCount = 0;
    private LocalDateTime lastAttemptAt;
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public BookingOutbox(String eventType, String traceId, String payload) {
        this.eventType = eventType;
        this.traceId = traceId;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
    }

    public void markAsPublished() {
        this.status = OutboxStatus.SUCCESS;
    }
}