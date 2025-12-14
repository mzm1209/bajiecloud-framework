package com.bajiezu.cloud.common.context;

import java.time.LocalDateTime;

// 使用ThreadLocal确保线程安全
public class RequestContext {

  private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();
  private static final ThreadLocal<LocalDateTime> requestTimeHolder = new ThreadLocal<>();

  public static String getRequestId() {
    return requestIdHolder.get();
  }

  public static void setRequestId(String requestId) {
    requestIdHolder.set(requestId);
  }

  public static LocalDateTime getRequestTime() {
    return requestTimeHolder.get();
  }

  public static void setRequestTime(LocalDateTime requestTime) {
    requestTimeHolder.set(requestTime);
  }

  public static void clear() {
    requestIdHolder.remove();
    requestTimeHolder.remove();
  }
}
