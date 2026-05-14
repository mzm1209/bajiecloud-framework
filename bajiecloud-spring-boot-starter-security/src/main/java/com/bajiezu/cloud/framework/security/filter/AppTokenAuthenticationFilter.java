package com.bajiezu.cloud.framework.security.filter;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.UNAUTHORIZED;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.servlet.ServletUtils;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.security.context.AppLoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.service.RedisService;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * APP token 认证过滤器。
 */
@RequiredArgsConstructor
@Slf4j
public class AppTokenAuthenticationFilter extends OncePerRequestFilter {

  private final RedisService redisService;
  @Setter
  private Duration tokenExpireDuration = Duration.ofDays(1);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    String requestUri = request.getRequestURI();
    if (!requestUri.startsWith("/api/app/")) {
      chain.doFilter(request, response);
      return;
    }
    try {
      String token = AppSecurityFrameworkUtils.getToken(request);
      if (StrUtil.isEmpty(token)) {
        ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
        return;
      }
      LoginUser<?> loginUser = redisService.getAppUser(token, tokenExpireDuration);
      if (loginUser == null) {
        ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
        return;
      }
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(loginUser, null, null);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      AppLoginUserContext.setLoginUser(loginUser);
      chain.doFilter(request, response);
    } finally {
      AppLoginUserContext.clear();
    }
  }
}
