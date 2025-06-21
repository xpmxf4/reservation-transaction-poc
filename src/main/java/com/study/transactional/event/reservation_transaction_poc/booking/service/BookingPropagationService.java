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
            umsService.sendAlimtalkWithRequired(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
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
            umsService.sendAlimtalkWithRequiresNew(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
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
            umsService.sendAlimtalkWithNested(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
