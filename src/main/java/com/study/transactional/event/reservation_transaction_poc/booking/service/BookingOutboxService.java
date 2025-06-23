package com.study.transactional.event.reservation_transaction_poc.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.transactional.event.reservation_transaction_poc.booking.entity.Reservation;
import com.study.transactional.event.reservation_transaction_poc.booking.enums.BookingStatus;
import com.study.transactional.event.reservation_transaction_poc.booking.repository.ReservationRepository;
import com.study.transactional.event.reservation_transaction_poc.booking.service.event.ReservationCreatedEvent;
import com.study.transactional.event.reservation_transaction_poc.outbox.entity.Outbox;
import com.study.transactional.event.reservation_transaction_poc.outbox.enums.OutboxStatus;
import com.study.transactional.event.reservation_transaction_poc.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingOutboxService {

    private final ObjectMapper objectMapper;
    private final ReservationRepository reservationRepository;
    private final OutboxRepository outboxRepository;

    public void createBookingOutbox(Long userNo, String productId) {
        // 1.유저 정보 조회 - 번호 가져왔다고 생각
        String userPhone = "010-1111-2222";

        // 2. 예약 정보 저장
        Reservation reservation = new Reservation(userNo, productId, userPhone, BookingStatus.RESERVED);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 3. 알림톡 아웃박스에 저장
        try {
            ReservationCreatedEvent reservationCreatedEvent = new ReservationCreatedEvent(
                    savedReservation.getId(), userPhone, productId
            );
            String payload = objectMapper.writeValueAsString(reservationCreatedEvent);

            Outbox reservedOutboxEvent = new Outbox(
                    "RESERVED",
                    payload
            );
            outboxRepository.save(reservedOutboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error creating booking outbox: " + e.getMessage(), e);
        }

    }
}
