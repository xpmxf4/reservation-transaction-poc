package com.study.transactional.event.reservation_transaction_poc.seat.client;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeatProviderClient {

    private final Random random = new Random();

    // 좌석을 10분간 선점하는 외부 API 호출을 모방합니다.
    public boolean holdSeat(String productId, String productDetailId) {
        simulateNetworkLatency(); // 네트워크 지연 시간 시뮬레이션
        log.info("공급사 좌석 선점 API 호출 시작. ProductId: {}, Seat: {}", productId, productDetailId);

        // 30% 확률로 실패를 시뮬레이션합니다.
        if (random.nextInt(10) < 3) {
            log.error("공급사 API 응답 실패: SEAT_UNAVAILABLE");
            return false;
        }

        log.info("공급사 좌석 선점 성공.");
        return true;
    }

    // 선점했던 좌석을 해제하는 API 호출을 모방합니다. (보상 트랜잭션용)
    public void releaseSeat(String productId, String productDetailId) {
        simulateNetworkLatency(); // 네트워크 지연 시간 시뮬레이션
        log.info("공급사 좌석 해제 API 호출 시작. ProductId: {}, Seat: {}", productId, productDetailId);
        // 해제는 대부분 성공한다고 가정
        log.info("공급사 좌석 해제 성공.");
    }

    // 외부 API 호출 시 발생하는 네트워크 지연을 모방하는 private 메서드
    private void simulateNetworkLatency() {
        try {
            // 100ms ~ 500ms 사이의 랜덤한 지연 발생
            int latency = 100 + random.nextInt(400);
            Thread.sleep(latency);
            log.info("... 공급사 API 응답 시간: {}ms", latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
