package com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "booking") // 실제 테이블명은 'booking'으로 가정
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String productId;
    private String userPhone; // 이 필드는 MDC나 다른 서비스에서 가져온다고 가정
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Builder
    public Booking(Long userId, String productId, String userPhone, BookingStatus status) {
        this.userId = userId;
        this.productId = productId;
        this.userPhone = userPhone;
        this.status = status;
    }
}
