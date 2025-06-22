package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BookingAndEventPublishServiceTest {

    @Autowired
    private BookingAndEventPublishService bookingAndEventPublishService;

    @Autowired
    private ReservationRepository reservationRepository;

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("발행된 이벤트를 소비하다 에러가 나면, 예약 생성은 되지만 해당 이벤트는 유실된다.")
    void exceptionInTransactionalEventListener_doesntAffectBookingTransaction() {
        // when - 동작
        // 1. 서비스 메소드를 호출하면, 트랜잭션 커밋 후 이벤트가 발행되고,
        //    리스너 중 하나인 handleReservationCreatedEventWithException이 예외를 던질 것이다.
        bookingAndEventPublishService.createReservationAndPublishReservationEvent(
                2L,
                "product-12345"
        );

        // then - 검증
        assertThat(reservationRepository.count()).isEqualTo(1L);
    }
}