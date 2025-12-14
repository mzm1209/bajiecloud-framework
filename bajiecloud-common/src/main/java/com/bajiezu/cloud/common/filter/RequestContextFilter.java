package com.bajiezu.cloud.common.filter;

import com.bajiezu.cloud.common.context.RequestContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.MDC;

// 在请求入口处设置MDC上下文
public class RequestContextFilter implements Filter {

  public static final String REQUEST_ID_KEY = "requestId";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      // 生成或获取requestId
      String requestId = generateRequestId((HttpServletRequest) request);

      // 设置MDC上下文
      MDC.put(REQUEST_ID_KEY, requestId);
      RequestContext.setRequestId(requestId);
      RequestContext.setRequestTime(LocalDateTime.now());
      chain.doFilter(request, response);
    } finally {
      // 清理MDC上下文
      MDC.clear();
      RequestContext.clear();
    }
  }

  private String generateRequestId(HttpServletRequest request) {
    // 从HTTP头获取，如果不存在则生成新的
    String headerRequestId = request.getHeader("X-Request-ID");
    return headerRequestId != null ? headerRequestId : UUID.randomUUID().toString();
  }
}
