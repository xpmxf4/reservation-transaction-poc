package com.study.transactional.event.reservation_transaction_poc.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.Booking;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.BookingRepository;
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
        Booking booking = new Booking(1L, "PRODUCT-001", "010-1111-2222", BookingStatus.RESERVED);

        // when - 동작
        Booking savedBooking = bookingRepository.save(booking);

        // then - 검증
        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getUserId()).isEqualTo(1L);
        assertThat(savedBooking.getProductId()).isEqualTo("PRODUCT-001");
    }
}