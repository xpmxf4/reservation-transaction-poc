package com.study.transactional.event.reservation_transaction_poc.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ReservationRepositoryTest {

    private final ReservationRepository reservationRepository;

    @Autowired
    ReservationRepositoryTest(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Test
    void 예약을_성공적으로_저장해야_한다() {
        // given - 상황 만들기
        Reservation reservation = new Reservation(1L, "PRODUCT-001");

        // when - 동작
        Reservation savedReservation = reservationRepository.save(reservation);

        // then - 검증
        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getUserId()).isEqualTo(1L);
        assertThat(savedReservation.getProductId()).isEqualTo("PRODUCT-001");
    }
}