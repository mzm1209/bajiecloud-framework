package com.bajiezu.cloud.framework.security.app;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * App 登录态上下文（基于 Request Attribute）。
 */
public final class AppSecurityContext {

  private AppSecurityContext() {
  }

  public static AppLoginUserInfo getLoginUser() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
      return null;
    }
    HttpServletRequest request = servletRequestAttributes.getRequest();
    Object loginUser = request.getAttribute(AppSecurityConstants.LOGIN_USER_ATTR);
    if (loginUser instanceof AppLoginUserInfo appLoginUserInfo) {
      return appLoginUserInfo;
    }
    return null;
  }

  public static Long getCustomerId() {
    AppLoginUserInfo loginUser = getLoginUser();
    return loginUser != null ? loginUser.getCustomerId() : null;
  }

}
