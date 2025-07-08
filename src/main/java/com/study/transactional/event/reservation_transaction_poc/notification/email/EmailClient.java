package com.study.transactional.event.reservation_transaction_poc.notification.email;

public interface EmailClient {
    void send(String to, String subject, String content);
}
