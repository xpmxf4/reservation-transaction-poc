package com.study.transactional.event.reservation_transaction_poc.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class TrackEventAspect {

    private final LogPolicyProvider logPolicyProvider;

    @Around("@annotation(trackEvent)")
    public Object logTrackEvent(ProceedingJoinPoint joinPoint, TrackEvent trackEvent) throws Throwable {

        LogEventEnum eventEnum = trackEvent.value();
        LogLevel level = logPolicyProvider.getLogLevel(eventEnum);

        MDC.put("eventCategory", eventEnum.name());

        String message = buildMessage(joinPoint, eventEnum.getDefaultMessage());

        // 로그 레벨에 따라 로그 기록
        switch (level) {
            case INFO:
                log.info(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
        }

        try {
            return joinPoint.proceed();
        } finally {
            MDC.remove("eventCategory");
        }
    }

    private String buildMessage(ProceedingJoinPoint joinPoint, String defaultMessage) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if(paramNames == null || paramNames.length == 0) {
            return defaultMessage;
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            params.put(paramNames[i], args[i] );
        }

        String paramString = params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(", "));

        return defaultMessage + ", " + paramString;
    }
}
