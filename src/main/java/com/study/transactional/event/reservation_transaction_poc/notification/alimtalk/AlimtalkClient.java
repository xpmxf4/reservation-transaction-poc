package com.study.transactional.event.reservation_transaction_poc.notification.alimtalk;

public interface AlimtalkClient {
    void send(String to, String content);
}
