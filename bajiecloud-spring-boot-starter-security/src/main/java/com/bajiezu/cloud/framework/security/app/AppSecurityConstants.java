package com.bajiezu.cloud.framework.security.app;

/**
 * App 登录态相关常量。
 */
public final class AppSecurityConstants {

  public static final String TOKEN_KEY_PREFIX = "bajie:auth:app-user:";
  public static final String USER_TOKEN_SET_PREFIX = "bajie:auth:app-user-tokens:";
  public static final String LOGIN_USER_ATTR = "APP_LOGIN_USER";
  public static final long TOKEN_EXPIRE_SECONDS = 604800L;

  private AppSecurityConstants() {
  }

}
