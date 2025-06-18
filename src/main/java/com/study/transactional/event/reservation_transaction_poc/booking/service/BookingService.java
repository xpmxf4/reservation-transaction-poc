package com.study.transactional.event.reservation_transaction_poc.booking.service;

import static com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus.RESERVED;
import static com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode.RESERVATED;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.ums.service.UmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ReservationRepository reservationRepository;
    private final UmsService umsService;

    @Transactional
    public void createReservation(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. 알림톡 전송
        umsService.sendAlimtalk(userPhone, RESERVATED, String.valueOf(savedReservation.getId()));
    }
}
