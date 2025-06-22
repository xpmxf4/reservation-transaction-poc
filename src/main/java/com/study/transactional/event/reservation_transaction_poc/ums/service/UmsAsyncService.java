package com.study.transactional.event.reservation_transaction_poc.ums.service;

import com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UmsAsyncService {

    @Async
    public void sendAlimtalkAsyncThrowsException(String phoenNo, TemplateCode templateCode, String key) {
         log.info("[ASYNC-UMS] 알림톡 발송 시작: phoneNo={}, templateCode={}, key={}",
                 phoenNo, templateCode, key);

        // change the strings into korean
        log.info("[ASYNC-UMS] 알림톡 발송 중 예외 발생: phoneNo={}, templateCode={}, key={}",
                 phoenNo, templateCode, key);
    }

    @Async
    public void sendAlimtalkAsync(String phoenNo, TemplateCode templateCode, String key) {
        log.info(">> [ASYNC-UMS] 별도 스레드에서 알림톡 발송 시작! (부모 트랜잭션의 성공/실패 여부를 모름)");
        log.info(">> [ASYNC-UMS] 알림톡 발송 성공!");
    }
}
