package com.study.transactional.event.reservation_transaction_poc.ums.service;

import com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UmsPropagationService {

    @Transactional(propagation = Propagation.REQUIRED) // 기본
    public void sendAlimtalkWithRequiredThrowsException(String phoneNo, TemplateCode templateCode, String key) {
        log.info("[REQUIRED] 알림톡 전송 ...");

        throw new RuntimeException("[REQUIRED] 알림톡 전송에서 의도된 API 호출 실패");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 기본
    public void sendAlimtalkWithRequiresNewThrowsException(String phoneNo, TemplateCode templateCode, String key) {
        log.info("[REQUIRES_NEW] 알림톡 전송 ...");

        throw new RuntimeException("[REQUIRES_NEW] 알림톡 전송에서 의도된 API 호출 실패");
    }

    @Transactional(propagation = Propagation.NESTED) // 기본
    public void sendAlimtalkWithNestedThrowsException(String phoneNo, TemplateCode templateCode, String key) {
        log.info("[NESTED] 알림톡 전송 ...");

        throw new RuntimeException("[NESTED] 알림톡 전송에서 의도된 API 호출 실패");
    }

}
