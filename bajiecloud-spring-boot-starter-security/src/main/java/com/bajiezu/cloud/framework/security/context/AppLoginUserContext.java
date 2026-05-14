package com.bajiezu.cloud.framework.security.context;

import com.bajiezu.cloud.framework.security.po.LoginInfoEntity;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import java.util.Optional;

/**
 * APP 登录用户上下文（ThreadLocal存储）
 */
public class AppLoginUserContext {

  private static final ThreadLocal<LoginUser<? extends LoginInfoEntity>> LOGIN_USER_CONTEXT = new ThreadLocal<>();
  private static final ThreadLocal<Long> PARTNER_CONTEXT = new ThreadLocal<>();

  @SuppressWarnings("unchecked")
  public static <T extends LoginInfoEntity> LoginUser<T> getLoginUser() {
    return (LoginUser<T>) LOGIN_USER_CONTEXT.get();
  }

  public static <T extends LoginInfoEntity> void setLoginUser(LoginUser<T> loginUser) {
    LOGIN_USER_CONTEXT.set(loginUser);
    if (loginUser != null) {
      PARTNER_CONTEXT.set(loginUser.getPartnerId());
    }
  }

  public static Long getUserId() {
    LoginUser<?> loginUser = getLoginUser();
    return loginUser != null ? loginUser.getUserId() : null;
  }

  public static String getUsername() {
    LoginUser<?> loginUser = getLoginUser();
    return loginUser != null ? loginUser.getUsername() : null;
  }

  public static <T extends LoginInfoEntity> T getLoginInfo() {
    LoginUser<T> loginUser = getLoginUser();
    return loginUser.getLoginInfo();
  }

  public static Long getPartnerId() {
    return PARTNER_CONTEXT.get();
  }

  public static void clear() {
    LOGIN_USER_CONTEXT.remove();
    PARTNER_CONTEXT.remove();
  }

  public static boolean isLogin() {
    return getLoginUser() != null;
  }

  public static Optional<LoginUser<?>> getLoginUserOptional() {
    return Optional.ofNullable(LOGIN_USER_CONTEXT.get());
  }
}
