package com.bajiezu.cloud.framework.security.util;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.framework.security.context.AppLoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * APP 安全服务工具类
 */
public class AppSecurityFrameworkUtils {

  public static final String APP_TOKEN_PARAMETER_NAME = "app_security_token";

  public static final String APP_LOGIN_USER_HEADER = "app-user-token";

  private AppSecurityFrameworkUtils() {
  }

  public static String getToken(HttpServletRequest request) {
    String token = request.getHeader(APP_LOGIN_USER_HEADER);
    if (StrUtil.isEmpty(token)) {
      token = request.getParameter(APP_TOKEN_PARAMETER_NAME);
    }
    if (!StringUtils.hasText(token)) {
      return null;
    }
    int index = token.indexOf(SecurityFrameworkUtils.AUTHORIZATION_BEARER + " ");
    return index >= 0 ? token.substring(index + 7).trim() : token;
  }

  @Nullable
  public static LoginUser<?> getLoginUser() {
    return AppLoginUserContext.getLoginUser();
  }
}
