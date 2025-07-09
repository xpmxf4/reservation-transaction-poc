package com.study.transactional.event.reservation_transaction_poc.logging;

import org.springframework.boot.logging.LogLevel;

public interface LogPolicyProvider {

    LogLevel getLogLevel(LogEventEnum event);

}
