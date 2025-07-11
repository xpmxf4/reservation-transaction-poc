package com.study.transactional.event.reservation_transaction_poc.seat.service;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.SeatHold;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.SeatHoldRepository;
import com.study.transactional.event.reservation_transaction_poc.seat.client.SeatProviderClient;
import com.study.transactional.event.reservation_transaction_poc.seat.dto.HoldSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatHoldRepository seatHoldRepository;
    private final SeatProviderClient seatProviderClient;

    @Transactional
    public HoldSeatResponse holdSeat(String productId, String productDetailId) {
        MDC.put("eventName", "SEAT_HOLD_ATTEMPT");
        try {
            boolean success = seatProviderClient.holdSeat(productId, productDetailId);
            if(!success) {
                MDC.put("failureReason", "SEAT_ALREADY_TAKEN");
                log.warn("[SEAT_SELECTION_FAILED] 좌석 선점에 실패했습니다.");
                return new HoldSeatResponse(false, null);
            }
            SeatHold seatHold = SeatHold.builder()
                .productId(productId)
                .productDetailId(productDetailId)
                .userId(MDC.get("userId"))
                .build();
            seatHoldRepository.save(seatHold);
            log.info("좌석 선점 성공 및 DB 저장 완료. SeatHold Id : {}", seatHold.getId());
            return new HoldSeatResponse(true, seatHold.getId());
        } finally {
            MDC.remove("eventName");
            MDC.remove("failureReason");
        }
    }

    @Transactional
    public void releaseSeat(Long seatHoldId) {
        MDC.put("eventName", "SEAT_RELEASE_COMPENSATION");
        try {
            SeatHold seatHold = seatHoldRepository.findById(seatHoldId)
                .orElseThrow(() -> new IllegalStateException("SeatHold Not Found"));

            seatProviderClient.releaseSeat(
                seatHold.getProductId(),
                seatHold.getProductDetailId()
            );
            seatHold.release();

            log.warn("결제 실패로 인한 좌석 선점 해제(보상 트랜잭션) 실행");
        } finally {
            MDC.remove("eventName");
        }
    }

    @Transactional
    public void confirmHold(Long seatHoldId) {
        MDC.put("eventName", "SEAT_CONFIRM");
        try {
            SeatHold seatHold = seatHoldRepository.findById(seatHoldId)
                .orElseThrow(() -> new IllegalStateException("SeatHold not found: " + seatHoldId));
            seatHold.confirm();
            log.info("좌석 상태를 CONFIRMED로 변경했습니다. SeatHold ID: {}", seatHoldId);
        } finally {
            MDC.remove("eventName");
        }
    }}