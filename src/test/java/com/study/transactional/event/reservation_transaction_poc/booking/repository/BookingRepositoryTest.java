package com.study.transactional.event.reservation_transaction_poc.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class BookingRepositoryTest {

    private final BookingRepository bookingRepository;

    @Autowired
    BookingRepositoryTest(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
    }

    @Test
    void 예약을_성공적으로_저장해야_한다() {
        // given - 상황 만들기
        Reservation reservation = new Reservation(1L, "PRODUCT-001", "010-1111-2222", BookingStatus.RESERVED);

        // when - 동작
        Reservation savedReservation = bookingRepository.save(reservation);

        // then - 검증
        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getUserId()).isEqualTo(1L);
        assertThat(savedReservation.getProductId()).isEqualTo("PRODUCT-001");
    }
}