package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.framework.security.context.AppLoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

/**
 * APP 端安全工具类，与平台侧 token 严格隔离。
 */
public class AppSecurityFrameworkUtils {

  public static final String APP_TOKEN_HEADER = "app-user-token";
  public static final String APP_TOKEN_PARAMETER_NAME = "app_security_token";

  private AppSecurityFrameworkUtils() {
  }

  @Nullable
  public static LoginUser<?> getLoginUser() {
    return AppLoginUserContext.getLoginUser();
  }

  public static String getToken(HttpServletRequest request) {
    return SecurityFrameworkUtils.getToken(request, APP_TOKEN_HEADER, APP_TOKEN_PARAMETER_NAME);
  }
}
