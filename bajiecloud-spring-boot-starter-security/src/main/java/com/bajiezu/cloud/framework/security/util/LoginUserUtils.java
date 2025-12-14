package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.framework.security.context.LoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginInfoEntity;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import org.springframework.util.Assert;

/**
 * 登录用户工具类
 */
public class LoginUserUtils {

  /**
   * 获取当前登录用户（确保已登录）
   */
  public static <T extends LoginInfoEntity> LoginUser<T> getRequiredLoginUser() {
    LoginUser<T> loginUser = LoginUserContext.getLoginUser();
    Assert.notNull(loginUser, "用户未登录");
    return loginUser;
  }

  /**
   * 获取用户ID（确保已登录）
   */
  public static Long getRequiredUserId() {
    Long userId = LoginUserContext.getUserId();
    Assert.notNull(userId, "用户未登录");
    return userId;
  }

  /**
   * 获取用户名（确保已登录）
   */
  public static String getRequiredUsername() {
    String username = LoginUserContext.getUsername();
    Assert.notNull(username, "用户未登录");
    return username;
  }

  /**
   * 获取用户扩展信息（确保已登录）
   */
  public static <T extends LoginInfoEntity> T getRequiredUserInfo() {
    T userInfo = LoginUserContext.getLoginInfo();
    Assert.notNull(userInfo, "用户信息不存在");
    return userInfo;
  }

  /**
   * 判断是否拥有指定角色
   */
  public static boolean hasRole(String role) {
    LoginUser<?> loginUser = LoginUserContext.getLoginUser();
    if (loginUser == null || loginUser.getRoles() == null) {
      return false;
    }
    return loginUser.getRoles().contains(role);
  }

  /**
   * 判断是否拥有指定权限
   */
  public static boolean hasPermission(String permission) {
    LoginUser<?> loginUser = LoginUserContext.getLoginUser();
    if (loginUser == null || loginUser.getPermissions() == null) {
      return false;
    }
    return loginUser.getPermissions().contains(permission);
  }

  /**
   * 判断是否为管理员
   */
  public static boolean isAdmin() {
    return hasRole("ADMIN");
  }
}