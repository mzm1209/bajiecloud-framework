package com.bajiezu.cloud.framework.security.context;

import com.bajiezu.cloud.framework.security.LoginUser;

public class LoginUserContext {

  private static final ThreadLocal<LoginUser<?>> loginUser = new ThreadLocal<>();

  public static LoginUser<?> getLoginUser() {
    return loginUser.get();
  }

  public static void setLoginUser(LoginUser<?> loginUser) {
    LoginUserContext.loginUser.set(loginUser);
  }

  public static Long getLoginUserId() {
    if (getLoginUser() == null) {
      return null;
    }
    return getLoginUser().getId();
  }

  public static void clear() {
    loginUser.remove();
  }
}
