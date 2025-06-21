package com.study.transactional.event.reservation_transaction_poc.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

@SpringBootTest
class BookingPropagationServiceTest {

    @Autowired
    private BookingPropagationService bookingPropagationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("[REQUIRED] 자식 트랜잭션 실패 시, 'rollback-only' 상태가 전파되어 부모 트랜잭션도 롤백된다.")
    void umsRequiredFailAndRollbackTest() {
        // when & then
        // 자식의 실패로 부모까지 rollback-only 상태가 되어 커밋 시 예외가 발생한다.
        assertThrows(UnexpectedRollbackException.class, () -> {
            bookingPropagationService.createReservationWithUmsRequired(1L, "PRODUCT-001");
        });

        // then
        // 최종적으로 예약 정보는 롤백되어야 한다.
        long numOfCreatedReservation = reservationRepository.count();
        assertThat(numOfCreatedReservation).isZero();
    }


    @Test
    @DisplayName("[REQUIRES_NEW] 자식 트랜잭션이 실패해도, 별도 트랜잭션이므로 부모 트랜잭션은 커밋된다.")
    void umsRequiresNewFailsAndReservationDoesExist() {
        // when
        // 자식 트랜잭션에서 발생한 예외는 부모가 catch 한다.
        bookingPropagationService.createReservationWithUmsRequiresNew(2L, "PRODUCT-002");

        // then
        // 자식의 실패는 부모의 트랜잭션에 영향을 주지 않으므로 예약 정보는 커밋되어야 한다.
        assertThat(reservationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("[NESTED] 자식 트랜잭션이 실패해도, 'rollback-only' 상태는 전파 x, 세이브포인트로 롤백되어 부모 트랜잭션은 커밋된다.")
    void umsNestedFailsAndReservationDoesExist() {
        // when
        // 자식(중첩) 트랜잭션에서 발생한 예외는 부모가 catch 한다.
        bookingPropagationService.createReservationWithUmsNested(3L, "PRODUCT-003");

        // then
        // 자식의 실패는 세이브포인트로만 롤백될 뿐, 부모 트랜잭션은 정상 커밋된다.
        assertThat(reservationRepository.count()).isEqualTo(1);
    }
}