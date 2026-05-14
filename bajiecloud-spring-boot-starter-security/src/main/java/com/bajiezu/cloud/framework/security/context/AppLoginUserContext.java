package com.bajiezu.cloud.framework.security.context;

import com.bajiezu.cloud.framework.security.po.LoginUser;

/**
 * APP 登录用户上下文。
 */
public class AppLoginUserContext {

  private static final ThreadLocal<LoginUser<?>> LOGIN_USER_THREAD_LOCAL = new ThreadLocal<>();

  private AppLoginUserContext() {
  }

  public static LoginUser<?> getLoginUser() {
    return LOGIN_USER_THREAD_LOCAL.get();
  }

  public static void setLoginUser(LoginUser<?> loginUser) {
    LOGIN_USER_THREAD_LOCAL.set(loginUser);
  }

  public static void clear() {
    LOGIN_USER_THREAD_LOCAL.remove();
  }
}
