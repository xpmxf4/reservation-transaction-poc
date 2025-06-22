package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.ums.service.UmsAsyncService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
class BookingAsyncServiceTest {

    @Autowired
    private BookingAsyncService bookingAsyncService;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private UmsAsyncService umsAsyncService;

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("비동기 UMS 전송이 에러가 나도, 예약은 정상적으로 생성된다.")
    void sendingUmsAsynchronously_doesntAffectOriginalTransaction() {
        // given - 상황 만들기
        bookingAsyncService.createReservationWithUmsAsync(1L, "PRODUCT-001");

        long numOfCreatedReservations = reservationRepository.count();
        assertThat(numOfCreatedReservations).isEqualTo(1);
    }

    @Test
    @DisplayName("예약 트랜잭션이 실패해도, 비동기 UMS 전송은 영향을 받지 않는다.")
    void sendingUmsAsynchronous_doesntGetAffectedByBookingTransaction() {
        // given - 상황 만들기
        Assertions.assertThrows(RuntimeException.class, () -> {
            bookingAsyncService.createReservationAndFailsAfterAsyncUms(1L, "PRODUCT-001");
        });

        verify(umsAsyncService, timeout(1000).times(1))
                .sendAlimtalkAsync(any(), any(), any());

        assertThat(reservationRepository.count()).isZero();
    }
}