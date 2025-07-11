package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatHold { // 좌석 선점 정보를 나타내는 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private String productDetailId; // 실제 좌석/객실 ID
    private String userId;
    private LocalDateTime expiresAt; // 선점 만료 시간 (예: 10분 후)
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status { HOLDING, CONFIRMED, RELEASED }

    @Builder
    public SeatHold(String productId, String productDetailId, String userId) {
        this.productId = productId;
        this.productDetailId = productDetailId;
        this.userId = userId;
        this.expiresAt = LocalDateTime.now().plusMinutes(10); // 10분간 유효
        this.status = Status.HOLDING;
    }

    public void confirm() { this.status = Status.CONFIRMED; }
    public void release() { this.status = Status.RELEASED; }
}
