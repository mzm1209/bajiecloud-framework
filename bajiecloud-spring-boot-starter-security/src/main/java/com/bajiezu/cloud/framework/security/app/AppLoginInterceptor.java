package com.bajiezu.cloud.framework.security.app;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.UNAUTHORIZED;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.servlet.ServletUtils;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AppLoginInterceptor implements HandlerInterceptor {

  private static final String AUTHORIZATION = "Authorization";
  private static final String APP_TOKEN_HEADER = "app-token";
  private static final String BEARER_PREFIX = "Bearer ";

  private final AppTokenService appTokenService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String token = extractToken(request);
    if (StrUtil.isBlank(token)) {
      ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
      return false;
    }
    AppLoginUserInfo loginUserInfo = appTokenService.getLoginUser(token);
    if (loginUserInfo == null) {
      ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
      return false;
    }
    if (!AppUserTypeEnum.APP_CUSTOMER.name().equals(loginUserInfo.getUserType())) {
      ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
      return false;
    }
    request.setAttribute(AppSecurityConstants.LOGIN_USER_ATTR, loginUserInfo);
    return true;
  }

  private String extractToken(HttpServletRequest request) {
    String authorization = request.getHeader(AUTHORIZATION);
    if (StrUtil.isNotBlank(authorization)) {
      if (StrUtil.startWithIgnoreCase(authorization, BEARER_PREFIX)) {
        return StrUtil.trim(authorization.substring(BEARER_PREFIX.length()));
      }
      return StrUtil.trim(authorization);
    }
    return StrUtil.trim(request.getHeader(APP_TOKEN_HEADER));
  }

}
