package com.bajiezu.cloud.framework.web.core.filter;

import static com.bajiezu.cloud.common.web.cloud.constants.RpcConstants.FEGIN_REQUEST_HEADER;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.bajiezu.cloud.common.context.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * HTTP 接口日志记录
 */
@Component
@Slf4j(topic = "HTTP_ACCESS")
public class AccessLogFilter extends OncePerRequestFilter {

  private final Set<String> excludedPaths = Set.of(
      "/actuator/health",
      "/actuator/prometheus",
      "/favicon.ico"
  );


  @Override
  protected void doFilterInternal(HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {

    // 检查是否排除此路径
    String path = request.getRequestURI();
    if (excludedPaths.contains(path)) {
      filterChain.doFilter(request, response);
      return;
    }
    String feginHeader = JakartaServletUtil.getHeader(request, FEGIN_REQUEST_HEADER,
        StandardCharsets.UTF_8);
    // 检查是否为Feign请求, 则不记录日志
    if (StringUtils.isNotBlank(feginHeader)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 包装请求和响应以便重复读取内容
    ContentCachingRequestWrapper wrappedRequest =
        new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappedResponse =
        new ContentCachingResponseWrapper(response);

    long startTime = System.currentTimeMillis();

    try {
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      long duration = System.currentTimeMillis() - startTime;
      logAccess(wrappedRequest, wrappedResponse, duration);
      // 确保响应内容被写入客户端
      wrappedResponse.copyBodyToResponse();
    }
  }

  private void logAccess(ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response,
      long duration) {

    // 构建日志信息
    Map<String, Object> logMap = new LinkedHashMap<>();
    logMap.put("timestamp", LocalDateTime.now());
    logMap.put("requestId", RequestContext.getRequestId());
    logMap.put("remoteAddr", JakartaServletUtil.getClientIP(request));
    logMap.put("method", request.getMethod());
    logMap.put("uri", request.getRequestURI());
    logMap.put("query", request.getQueryString());
    logMap.put("status", response.getStatus());
    logMap.put("duration", duration + "ms");
    logMap.put("userAgent", request.getHeader("User-Agent"));
    logMap.put("referer", request.getHeader("Referer"));

    // 记录请求头（可选）
    Enumeration<String> headerNames = request.getHeaderNames();
    Map<String, String> headers = new HashMap<>();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      headers.put(name, request.getHeader(name));
    }
    logMap.put("headers", headers);

    // 记录请求参数（对于GET请求）
    Map<String, String[]> params = request.getParameterMap();
    if (!params.isEmpty()) {
      logMap.put("params", params);
    }

    // 记录请求体（对于POST/PUT请求，需要谨慎处理敏感信息）
    String requestBody = JakartaServletUtil.getBody(request);
    if (!requestBody.isEmpty()) {
      // 对敏感信息进行脱敏处理（示例）
      requestBody = maskSensitiveInfo(requestBody);
      logMap.put("requestBody", requestBody.length() > 2000 ?
          requestBody.substring(0, 2000) + "..." : requestBody);
    }

    // 记录响应信息（可选）
    String responseBody = getResponseBody(response);
    if (!responseBody.isEmpty()) {
      logMap.put("responseBody", responseBody.length() > 1000 ?
          responseBody.substring(0, 1000) + "..." : responseBody);
    }

    log.info("{}", logMap);
  }


  private String getResponseBody(ContentCachingResponseWrapper response) {
    byte[] content = response.getContentAsByteArray();
    if (content.length > 0) {
      try {
        return new String(content, response.getCharacterEncoding());
      } catch (UnsupportedEncodingException e) {
        return "[UNKNOWN ENCODING]";
      }
    }
    return "";
  }

  private String maskSensitiveInfo(String body) {
    // 脱敏敏感信息，如密码、身份证号等
    return body
        .replaceAll("(\"password\"\\s*:\\s*\")[^\"]*(\")", "$1***$2")
        .replaceAll("(\"idCard\"\\s*:\\s*\")[^\"]*(\")", "$1*****$2")
        .replaceAll("(\"phone\"\\s*:\\s*\")[^\"]*(\")", "$1****$2");
  }
}