package com.study.transactional.event.reservation_transaction_poc.payment.client;

import com.study.transactional.event.reservation_transaction_poc.payment.exception.PaymentGatewayException;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentProviderClient {

    private final Random random = new Random();

    // 외부 PG사를 통해 결제를 진행하는 API 호출을 모방합니다.
    public String processPayment(Long amount) {
        simulateNetworkLatency(); // 네트워크 지연 시간 시뮬레이션
        log.info("공급사(PG) 결제 API 호출 시작. Amount: {}", amount);

        // 40% 확률로 실패를 시뮬레이션합니다.
        if (random.nextInt(10) < 4) {
            log.error("공급사(PG) API 응답 실패: INSUFFICIENT_FUNDS");
            throw new PaymentGatewayException("잔액 부족");
        }

        String paymentId = "pay-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("공급사(PG) 결제 성공. Payment ID: {}", paymentId);
        return paymentId;
    }

    // 결제를 환불/취소하는 API 호출을 모방합니다. (보상 트랜잭션용)
    public void refundPayment(String paymentId) {
        simulateNetworkLatency(); // 네트워크 지연 시간 시뮬레이션
        log.info("공급사(PG) 환불 API 호출 시작. Payment ID: {}", paymentId);
        log.info("공급사(PG) 환불 성공.");
    }

    // 외부 API 호출 시 발생하는 네트워크 지연을 모방하는 private 메서드
    private void simulateNetworkLatency() {
        try {
            // 200ms ~ 800ms 사이의 랜덤한 지연 발생 (결제는 더 오래 걸린다고 가정)
            int latency = 200 + random.nextInt(600);
            Thread.sleep(latency);
            log.info("... 공급사(PG) API 응답 시간: {}ms", latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}