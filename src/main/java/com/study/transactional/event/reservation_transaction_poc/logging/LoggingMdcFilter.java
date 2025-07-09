package com.study.transactional.event.reservation_transaction_poc.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoggingMdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            String traceId = UUID.randomUUID().toString();
            String userId = "TEST-USER-001"; // test

            MDC.put("traceId", traceId);
            MDC.put("userId", userId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
