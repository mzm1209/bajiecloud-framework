package com.bajiezu.cloud.common.web.cloud.config;

import feign.RequestInterceptor;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import feign.codec.ErrorDecoder.Default;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Feign 请求参数日志记录
 */
@Slf4j(topic = "FEIGN_ACCESS") // 关键：使用专门的日志名称
@Configuration
public class FeignAccessLogConfig {

  private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

  /**
   * 请求拦截器：记录请求信息
   */
  @Bean
  public RequestInterceptor feignAccessLogRequestInterceptor() {
    return template -> {
      // 记录请求开始时间
      startTime.set(System.currentTimeMillis());

      // 构建请求日志信息
      Map<String, Object> logMap = new LinkedHashMap<>();
      logMap.put("event", "FEIGN_REQUEST");
      logMap.put("timestamp", LocalDateTime.now());
      logMap.put("feignClient", template.feignTarget().type().getSimpleName());
      logMap.put("method", template.method());
      logMap.put("url", template.url());
      logMap.put("headers", template.headers());

      // 谨慎记录请求体（可能包含敏感信息）
      if (template.body() != null && template.body().length > 0) {
        logMap.put("body", new String(template.body(), StandardCharsets.UTF_8));
      }

      // 使用 FEIGN_ACCESS 这个 Logger 记录
      log.info("{}", logMap);
    };
  }

  /**
   * 响应/错误处理器：记录响应信息
   */
  @Bean
  public ErrorDecoder feignAccessLogErrorDecoder() {
    return (methodKey, response) -> {
      // 记录响应日志（包括错误响应）
      logFeignResponse(response, null);
      return new Default().decode(methodKey, response);
    };
  }

  /**
   * 响应日志记录方法
   */
  private void logFeignResponse(Response response, String body) {
    try {
      long duration = System.currentTimeMillis() - startTime.get();
      Map<String, Object> logMap = new LinkedHashMap<>();
      logMap.put("event", "FEIGN_RESPONSE");
      logMap.put("timestamp", LocalDateTime.now());
      logMap.put("status", response.status());
      logMap.put("durationMs", duration);
      logMap.put("headers", response.headers());

      if (body == null && response.body() != null) {
        // 注意：读取body会消耗流，确保不影响后续业务处理
        body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
      }
      if (body != null && !body.isEmpty()) {
        // 可考虑对过大的body进行截断
        logMap.put("body", body.length() > 1000 ? body.substring(0, 1000) + "..." : body);
      }

      log.info("{}", logMap);
    } catch (IOException e) {
      log.error("记录Feign响应日志失败", e);
    } finally {
      startTime.remove(); // 清理ThreadLocal
    }
  }
}