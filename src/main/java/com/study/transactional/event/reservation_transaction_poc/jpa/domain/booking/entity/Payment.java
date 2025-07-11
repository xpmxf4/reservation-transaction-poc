package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment { // 결제 정보를 나타내는 엔티티
    @Id
    private String paymentId; // PG사에서 받은 고유 ID를 PK로 사용
    private Long amount;
    private String productId;
    private String userId;
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status { PENDING, SUCCESS, FAILED, REFUNDED }

    @Builder
    public Payment(String paymentId, Long amount, String productId, String userId) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.productId = productId;
        this.userId = userId;
        this.status = Status.PENDING;
    }

    public void markAsSuccess() { this.status = Status.SUCCESS; }
    public void markAsRefunded() { this.status = Status.REFUNDED; }
}