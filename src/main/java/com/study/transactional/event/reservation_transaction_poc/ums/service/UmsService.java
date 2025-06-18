package com.study.transactional.event.reservation_transaction_poc.ums.service;

import com.study.transactional.event.reservation_transaction_poc.ums.enums.TemplateCode;
import org.springframework.stereotype.Service;

@Service
public interface UmsService {

    void sendAlimtalk(String phoneNo, TemplateCode templateCode, String key);
}
