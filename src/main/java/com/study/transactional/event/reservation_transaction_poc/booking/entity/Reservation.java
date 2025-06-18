package com.study.transactional.event.reservation_transaction_poc.booking.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id; // 예약 고유 ID

    private Long userId; // 사용자 ID

    private String productId; // 공급사 번호, 공급사 상품 번호 분리가 필요하지만 도메인 치중적이라 제외

    public Reservation(Long userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }
}
