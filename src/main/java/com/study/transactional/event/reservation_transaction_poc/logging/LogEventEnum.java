package com.study.transactional.event.reservation_transaction_poc.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;

@Getter
@AllArgsConstructor
public enum LogEventEnum {

    BOOKING_SUCCESS(LogLevel.INFO, "예약 성공"),
    BOOKING_FAIL(LogLevel.ERROR, "예약 실패"),
    SYSTEM_ERROR(LogLevel.ERROR, "시스템 에러");

    private final LogLevel logLevel;
    private final String defaultMessage;
}
