package com.bajiezu.cloud.framework.security.filter;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.UNAUTHORIZED;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.servlet.ServletUtils;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.security.LoginUser;
import com.bajiezu.cloud.framework.security.context.LoginUserContext;
import com.bajiezu.cloud.framework.security.service.RedisService;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Token 过滤器，验证 token 的有效性 验证通过后，获得 {@link com.bajiezu.cloud.framework.security.LoginUser} 信息，并加入到
 * Spring Security 上下文
 */
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final RedisService redisService;

  @Override
  @SuppressWarnings("NullableProblems")
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain)
      throws IOException, ServletException {
    // 情况一，基于 header[login-user] 获得用户，例如说来自 Gateway 或者其它服务透传
    try {
      LoginUser<?> loginUser = buildLoginUserByHeader(request);

      // 情况二，基于 Token 获得用户
      // 注意，这里主要满足直接使用 Nginx 直接转发到 Spring Cloud 服务的场景。
      if (loginUser == null) {
        CommonResult<?> result = CommonResult.error(UNAUTHORIZED);
        ServletUtils.writeJSON(response, result);
        return;
      }
      LoginUserContext.setLoginUser(loginUser);
      // 继续过滤链
      chain.doFilter(request, response);
    } finally {
      LoginUserContext.clear();
    }

  }


  @SneakyThrows
  private LoginUser<?> buildLoginUserByHeader(HttpServletRequest request) {
    String token = SecurityFrameworkUtils.getToken(request,
        SecurityFrameworkUtils.LOGIN_USER_HEADER,
        SecurityFrameworkUtils.TOKEN_PARAMETER_NAME);
    if (StrUtil.isEmpty(token)) {
      return null;
    }
    return redisService.getUser(token);
  }

}
