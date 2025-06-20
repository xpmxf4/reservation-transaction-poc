package com.study.transactional.event.reservation_transaction_poc.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.ums.service.UmsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private UmsService umsService;

    @Test
    void 알림톡_전송에_실패하면_예약정보가_롤백_된다() {
        // given - umsService 는 항상 예외를 던짐
        doThrow(new RuntimeException())
            .when(umsService)
            .sendAlimtalk(any(), any(), any());

        // when & then - 실행과 검증
        Assertions.assertThrows(RuntimeException.class, () -> {
            bookingService.createReservation(1L, "product");
        });

        assertThat(reservationRepository.count()).isEqualTo(0);
    }

}