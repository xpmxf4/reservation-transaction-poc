package com.study.transactional.event.reservation_transaction_poc.logging;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Component
public class MemoryLogPolicyProvider implements LogPolicyProvider {

    private final Map<LogEventEnum, LogLevel> policyMap = new HashMap<>();

    @Override
    public LogLevel getLogLevel(LogEventEnum event) {
        return policyMap.get(event);
    }
}
