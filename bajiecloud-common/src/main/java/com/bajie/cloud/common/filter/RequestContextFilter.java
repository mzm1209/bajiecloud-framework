package com.bajie.cloud.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;

// 在请求入口处设置MDC上下文
public class RequestContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // 生成或获取requestId
            String requestId = generateRequestId((HttpServletRequest) request);
            
            // 设置MDC上下文
            MDC.put("requestId", requestId);

            chain.doFilter(request, response);
        } finally {
            // 清理MDC上下文
            MDC.clear();
        }
    }

    private String generateRequestId(HttpServletRequest request) {
        // 从HTTP头获取，如果不存在则生成新的
        String headerRequestId = request.getHeader("X-Request-ID");
        return headerRequestId != null ? headerRequestId : UUID.randomUUID().toString();
    }
}
