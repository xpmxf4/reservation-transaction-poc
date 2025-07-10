package com.study.transactional.event.reservation_transaction_poc.logging;

import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisLogPolicyProvider implements LogPolicyProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "log:policy:level:";

    @Override
    public LogLevel getLogLevel(LogEventEnum event) {
        // 1. Redis 에서 정책 조회 (예: "log:policy:level:BOOKING_FAIL")
        String key = KEY_PREFIX + event.name();
        String storedLevel = redisTemplate.opsForValue().get(key);

        // 2. Redis 에 설정된 값이 있으면 해당 레벨을 반환
        if (Objects.nonNull(storedLevel)) {
            try {
                return LogLevel.valueOf(storedLevel.toUpperCase());
            } catch (IllegalArgumentException e) {
                return event.getLogLevel();
            }
        }

        // 3. Redis 에 설정이 없으면 enum에 정의된 기본 레벨을 반환
        return event.getLogLevel();
    }
}
