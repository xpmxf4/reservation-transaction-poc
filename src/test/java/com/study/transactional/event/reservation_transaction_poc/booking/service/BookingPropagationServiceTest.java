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
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("[NESTED] 'Savepoint 미지원' 예외가 발생하지만, 부모가 catch하여 부모 트랜잭션은 정상 커밋된다.")
    void umsNestedFailsAndReservationDoesExist() {
        // when
        // JpaTransactionManager는 NESTED를 지원하지 않아 'Savepoint' 관련 예외를 던진다.
        // 이 예외는 BookingService의 try-catch 블록에 의해 처리된다.
        // 따라서 이 테스트 메서드 레벨에서는 아무런 예외도 발생하지 않는다.
        bookingPropagationService.createReservationWithUmsNested(3L, "PRODUCT-003");

        // then
        // 'Savepoint 미지원' 예외가 부모 트랜잭션에 영향을 주지 않고 내부적으로 처리되었으므로,
        // 예약 정보는 정상적으로 커밋되어야 한다.
        assertThat(reservationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("[REQUIRES_NEW_SLEEP] 새로운 트랜잭션이 오래 걸리는 작업을 수행하는 동안 부모 스레드는 블록된다.")
    void umsRequiresNewWithSleepBlocksParentThread() {
        // given - 상황 만들기
        StopWatch stopWatch = new StopWatch();
        List<String> threadNames = new ArrayList<>();

        // when - 동작
        stopWatch.start();
        bookingPropagationService.createReservationWithUmsRequiresNewTakesLongTime(4L, "PRODUCT-004", threadNames);
        stopWatch.stop();

        // then - 검증
        // 1. 총 실행 시간 검증
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        System.out.println("총 실행 시간 : " + totalTimeMillis + "ms");
        assertThat(totalTimeMillis).isGreaterThanOrEqualTo(2000L);

        // 2. 예약 성공적으로 저장되는지 검증
        assertThat(reservationRepository.count()).isEqualTo(1);

        // 3. 스레드 이름 검증
        System.out.println("실행된 스레드 이름들: " + threadNames);
        assertThat(threadNames).hasSize(2);
        assertThat(threadNames.get(0)).isEqualTo(threadNames.get(1));
    }

    @Test
    @DisplayName("[NESTED_SLEEP] JpaDialect가 savepoint를 지원하지 않아 예외가 발생하고, 부모는 이를 catch하여 정상 커밋된다.")
    void umsNestedWithSleep_FailsToCreateSavepoint_AndParentCommits() {
        // given - 상황 만들기
        StopWatch stopWatch = new StopWatch();
        List<String> threadNames = new ArrayList<>();

        // when - 동작
        // JpaDialect(Savepoint) 관련 예외는 BookingService 내부에서 catch 되어 처리된다.
        // 따라서 이 테스트 메서드 레벨에서는 아무런 예외도 발생하지 않아야 한다.
        stopWatch.start();
        bookingPropagationService.createReservationWithUmsNestedTakesLongTime(5L, "PRODUCT-005", threadNames);
        stopWatch.stop();

        // then - 검증
        // 1. 블로킹 시간 검증
        // Savepoint 생성 실패 예외가 Thread.sleep()보다 먼저 발생하므로 2초가 걸리지 않는다.
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        System.out.println("총 실행 시간 (NESTED): " + totalTimeMillis + "ms");
        assertThat(totalTimeMillis).isLessThan(1000L); // 2초보다 훨씬 짧은 시간 안에 끝나야 함

        // 2. 예약 정보는 성공적으로 커밋되었는가? (가장 중요한 검증)
        // 자식 트랜잭션의 실패(Savepoint 생성 실패)가 부모에 영향을 주지 않고 정상 커밋되었기 때문
        assertThat(reservationRepository.count()).isEqualTo(1);

        // 3. UmsService의 스레드 이름은 수집되지 않았는가?
        // Savepoint 생성 실패로 UmsService의 핵심 로직(스레드 이름 추가, sleep)에 진입조차 못했기 때문
        System.out.println("실행된 스레드 이름들 (NESTED): " + threadNames);
        assertThat(threadNames).hasSize(1); // BookingService의 스레드만 기록됨
    }

}