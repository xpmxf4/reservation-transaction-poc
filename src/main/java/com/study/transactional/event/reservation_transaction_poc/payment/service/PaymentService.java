package com.study.transactional.event.reservation_transaction_poc.payment.service;

import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.entity.Payment;
import com.study.transactional.event.reservation_transaction_poc.jpa.domain.booking.repository.PaymentRepository;
import com.study.transactional.event.reservation_transaction_poc.payment.client.PaymentProviderClient;
import com.study.transactional.event.reservation_transaction_poc.payment.dto.PaymentResponse;
import com.study.transactional.event.reservation_transaction_poc.payment.exception.PaymentGatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProviderClient paymentProviderClient;
    private final PaymentRepository paymentRepository;

    // 결제는 단일 트랜잭션으로 처리
    @Transactional
    public PaymentResponse processPayment(String productId, Long amount) {
        MDC.put("eventName", "PAYMENT_ATTEMPT");
        try {
            String paymentId = paymentProviderClient.processPayment(amount);
            MDC.put("paymentId", paymentId);

            Payment payment = Payment.builder()
                .paymentId(paymentId)
                .amount(amount)
                .productId(productId)
                .userId(MDC.get("userId"))
                .build();
            payment.markAsSuccess(); // PG사 성공 응답 후 상태 변경
            paymentRepository.save(payment);

            log.info("결제 성공 및 DB 저장 완료.");
            return new PaymentResponse(true, paymentId, "결제 성공");
        } catch (PaymentGatewayException e) {
            MDC.put("failureReason", "PG_SYSTEM_ERROR");
            log.error("[PAYMENT_FAILED] PG 시스템 오류로 결제에 실패했습니다: {}", e.getMessage());
            return new PaymentResponse(false, null, e.getMessage());
        } finally {
            MDC.remove("eventName");
            MDC.remove("failureReason");
            MDC.remove("paymentId");
        }
    }
}