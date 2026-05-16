package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.framework.security.context.AppLoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/**
 * APP 安全服务工具类
 */
public class AppSecurityFrameworkUtils {

  public static final String APP_TOKEN_PARAMETER_NAME = "app_security_token";

  public static final String APP_LOGIN_USER_HEADER = "app-user-token";

  private AppSecurityFrameworkUtils() {
  }

  public static String getToken(HttpServletRequest request) {
    return SecurityFrameworkUtils.getToken(request, APP_LOGIN_USER_HEADER, APP_TOKEN_PARAMETER_NAME);
  }

  @Nullable
  public static LoginUser<?> getLoginUser() {
    return AppLoginUserContext.getLoginUser();
  }
}
