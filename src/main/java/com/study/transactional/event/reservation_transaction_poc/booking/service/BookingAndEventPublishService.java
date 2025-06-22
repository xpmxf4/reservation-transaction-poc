package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.booking.service.event.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus.RESERVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingAndEventPublishService {

    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createReservationAndPublishReservationEvent(Long userId, String productId) {
        // 1. 유저 정보(번호) 조회 (일단 임시)
        String userPhone = "010-1111-2222";

        // 2. 예약 정보를 저장
        Reservation reservation = new Reservation(userId, productId, userPhone, RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. 알림톡 전송 이벤트 발행
        try {
            eventPublisher.publishEvent(new ReservationCreatedEvent(
                    savedReservation.getId(),
                    userPhone,
                    productId
            ));
        } catch (Exception e) {
            // 예외 처리 로직 (예: 로그 기록)
            System.err.println("Error publishing reservation created event: " + e.getMessage());
        }

        log.info("Reservation created with ID: {}", savedReservation.getId());
    }

}
