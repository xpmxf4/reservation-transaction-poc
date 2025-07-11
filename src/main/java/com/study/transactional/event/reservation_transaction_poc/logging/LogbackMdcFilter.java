package com.study.transactional.event.reservation_transaction_poc.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LogbackMdcFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String COMPANY_ID_KEY = "companyId";
    private static final String USER_ID_KEY = "userNo";

    private static final String MOCK_AUTH_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // 1. Trace Id 설정
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(TRACE_ID_KEY, traceId);

        // 2. 모의 인증 헤더에서 사용자/고객사 정보 추출
        // 실제 시스템에서는 Spring Security 의 Authentication 객체에서 ㅈ어보 가져옴
        String authHeader = request.getHeader(MOCK_AUTH_HEADER);
        if(StringUtils.hasText(authHeader)) {
            // "User 1, Compnay 123" -> ["User 1", "Company 123"]
            String[] parts = authHeader.split(",");
            if(parts.length == 2) {
                String userNo = parts[0].trim().replace("User ", "");
                String companyId = parts[1].trim().replace("Company ", "");
                MDC.put(USER_ID_KEY, userNo);
                MDC.put(COMPANY_ID_KEY, companyId);

                request.setAttribute(USER_ID_KEY, Long.valueOf(userNo));
                request.setAttribute(COMPANY_ID_KEY, Long.valueOf(companyId));
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
