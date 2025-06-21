package com.study.transactional.event.reservation_transaction_poc.booking.service;

import static com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus.RESERVED;
import static com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode.RESERVATED;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.ums.service.UmsPropagationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingPropagationService {

    private final ReservationRepository reservationRepository;
    private final UmsPropagationService umsService;

    @Transactional
    public void createReservationWithUmsRequired(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. 알림톡 전송(REQUIRES)
        try {
            umsService.sendAlimtalkWithRequiredThrowsException(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void createReservationWithUmsRequiresNew(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. 알림톡 전송(REQUIRES_NEW)
        try {
            umsService.sendAlimtalkWithRequiresNewThrowsException(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void createReservationWithUmsNested(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. 알림톡 전송(REQUIRES_NEW)
        try {
            umsService.sendAlimtalkWithNestedThrowsException(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void createReservationWithUmsRequiresNewTakesLongTime(Long userId, String productId, List<String> threadNames) {
        String currentThreadName = Thread.currentThread().getName();
        threadNames.add(currentThreadName);
        log.info(">> [Booking Service] 실행 스레드: {}", currentThreadName);

        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info(">> [Booking Service] 예약 정보 저장 완료: {}", savedReservation);

        // 3. 알림톡 전송(REQUIRES_NEW)
        try {
            umsService.sendAlimtalkWithRequiresNewTakesLongTimeThrowsException(userPhone,
                    RESERVATED,
                    String.valueOf(savedReservation.getId()),
                    threadNames);
        } catch (Exception e) {
            log.error(">> [Booking Service] 알림톡 서비스의 예외를 catch 했습니다: {}", e.getMessage());
        }
        log.info(">> [Booking Service] 모든 로직 완료 후 트랜잭션을 커밋합니다.");
    }

    @Transactional
    public void createReservationWithUmsNestedTakesLongTime(Long userId, String productId, List<String> threadNames) {
        String currentThreadName = Thread.currentThread().getName();
        threadNames.add(currentThreadName);
        log.info(">> [Booking Service] 실행 스레드: {}", currentThreadName);

        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info(">> [Booking Service] 예약 정보 저장 완료: {}", savedReservation);

        // 3. 알림톡 전송(NESTED)
        try {
            umsService.sendAlimtalkWithNestedTakesLongTime(userPhone,
                    RESERVATED,
                    String.valueOf(savedReservation.getId()),
                    threadNames);
        } catch (Exception e) {
            log.error(">> [Booking Service] 알림톡 서비스의 예외를 catch 했습니다: {}", e.getMessage());
        }
        log.info(">> [Booking Service] 모든 로직 완료 후 트랜잭션을 커밋합니다.");
    }
}
