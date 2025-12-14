package com.bajiezu.cloud.framework.security.context;

import com.bajiezu.cloud.framework.security.po.LoginInfoEntity;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import java.util.Optional;


/**
 * 登录用户上下文（ThreadLocal存储）
 */
public class LoginUserContext {

  private static final ThreadLocal<LoginUser<? extends LoginInfoEntity>> LOGIN_USER_CONTEXT = new ThreadLocal<>();
  private static final ThreadLocal<Long> PARTNER_CONTEXT = new ThreadLocal<>();

  /**
   * 获取登录用户
   */
  @SuppressWarnings("unchecked")
  public static <T extends LoginInfoEntity> LoginUser<T> getLoginUser() {
    return (LoginUser<T>) LOGIN_USER_CONTEXT.get();
  }

  /**
   * 设置登录用户
   */
  public static <T extends LoginInfoEntity> void setLoginUser(LoginUser<T> loginUser) {
    LOGIN_USER_CONTEXT.set(loginUser);
    if (loginUser != null) {
      PARTNER_CONTEXT.set(loginUser.getPartnerId());
    }
  }

  /**
   * 获取用户ID
   */
  public static Long getUserId() {
    LoginUser<?> loginUser = getLoginUser();
    return loginUser != null ? loginUser.getUserId() : null;
  }

  /**
   * 获取用户名
   */
  public static String getUsername() {
    LoginUser<?> loginUser = getLoginUser();
    return loginUser != null ? loginUser.getUsername() : null;
  }

  /**
   * 获取用户扩展信息
   */
  public static <T extends LoginInfoEntity> T getLoginInfo() {
    LoginUser<T> loginUser = getLoginUser();
    return loginUser.getLoginInfo();
  }

  /**
   * 获取租户ID
   */
  public static Long getPartnerId() {
    return PARTNER_CONTEXT.get();
  }


  /**
   * 清除上下文
   */
  public static void clear() {
    LOGIN_USER_CONTEXT.remove();
    PARTNER_CONTEXT.remove();
  }

  /**
   * 判断是否已登录
   */
  public static boolean isLogin() {
    return getLoginUser() != null;
  }

  /**
   * 获取登录用户（Optional方式）
   */
  public static Optional<LoginUser<?>> getLoginUserOptional() {
    return Optional.ofNullable(LOGIN_USER_CONTEXT.get());
  }
}
