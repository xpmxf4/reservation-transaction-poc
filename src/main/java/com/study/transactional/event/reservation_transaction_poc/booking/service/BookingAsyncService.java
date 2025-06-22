package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.ums.service.UmsAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus.RESERVED;
import static com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode.RESERVATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingAsyncService {

    private final UmsAsyncService umsAsyncService;
    private final ReservationRepository reservationRepository;

    public void createReservationWithUmsAsync(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        reservationRepository.save(new Reservation(userId, productId, userPhone, RESERVED));

        // 3. 알림톡 전송(비동기)
        umsAsyncService.sendAlimtalkAsyncThrowsException(userPhone, RESERVATED, String.valueOf(userId));
    }

    @Transactional
    public void createReservationAndFailsAfterAsyncUms(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        reservationRepository.save(new Reservation(userId, productId, userPhone, RESERVED));
        log.info(">> [Booking Service] 예약 정보 저장 완료. (아직 커밋 전)");


        // 3. 알림톡 전송(비동기)
        umsAsyncService.sendAlimtalkAsync(userPhone, RESERVATED, String.valueOf(userId));
        log.info(">> [Booking Service] 비동기 알림톡 전송 요청 완료. (아직 커밋 전)");

        // 4. 예외 발생
        throw new RuntimeException("예약 생성 중 예외 발생");
    }
}
