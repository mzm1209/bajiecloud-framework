package com.bajiezu.cloud.common.context;

// 使用ThreadLocal确保线程安全
public class RequestContext {

  private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();

  public static String getRequestId() {
    return requestIdHolder.get();
  }

  public static void setRequestId(String requestId) {
    requestIdHolder.set(requestId);
  }

  public static void clear() {
    requestIdHolder.remove();
  }
}
