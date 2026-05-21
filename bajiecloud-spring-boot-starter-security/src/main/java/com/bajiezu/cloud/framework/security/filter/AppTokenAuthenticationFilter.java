package com.bajiezu.cloud.framework.security.filter;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.UNAUTHORIZED;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.servlet.ServletUtils;
import com.bajiezu.cloud.common.web.cloud.constants.RpcConstants;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.security.context.AppLoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.service.RedisService;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import com.bajiezu.cloud.framework.security.util.LoginUserUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * APP Token 过滤器，验证 token 的有效性，验证通过后加入 Spring Security 与 AppLoginUserContext
 */
@RequiredArgsConstructor
@Slf4j
public class AppTokenAuthenticationFilter extends OncePerRequestFilter {

  private final RedisService redisService;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  @Setter
  private Set<String> permitAllPaths = Collections.emptySet();
  @Setter
  private Set<String> noNeedLoginPath = Collections.emptySet();

  @Setter
  private Duration tokenExpireDuration = Duration.ofDays(1);

  @Override
  @SuppressWarnings("NullableProblems")
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain)
      throws IOException, ServletException {
    String requestUri = request.getRequestURI();
    if (isSwaggerPath(requestUri)
        || permitAllPaths.contains(requestUri)) {
      chain.doFilter(request, response);
      return;
    }
    log.info("AppTokenAuthenticationFilter,requestUri:{}", requestUri);
    try {
      LoginUser<?> loginUser = buildLoginUserByHeader(request);
      if (loginUser != null) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                loginUser,
                null,
                getAuthorities(loginUser)
            );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppLoginUserContext.setLoginUser(loginUser);

        log.debug("app user:{} authenticate success", loginUser.getUsername());
        chain.doFilter(request, response);
      } else {
        log.warn("no app user info found by url:{}", requestUri);
        ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
      }
    } finally {
      AppLoginUserContext.clear();
    }
  }

  private boolean isSwaggerPath(String requestUri) {
    return noNeedLoginPath.stream().anyMatch(path -> antPathMatcher.match(path, requestUri));
  }

  private LoginUser<?> buildLoginUserByHeader(HttpServletRequest request) {
    String token = AppSecurityFrameworkUtils.getToken(request);
    if (StrUtil.isEmpty(token)) {
      return null;
    }
    String feginHeader = request.getHeader(RpcConstants.FEGIN_REQUEST_HEADER);
    boolean isFromFegin = StrUtil.equals(RpcConstants.FEGIN_REQUEST_HEADER_VALUE, feginHeader);
    // fegin 的请求需要特殊处理，因为 fegin 是在 system 服务中调用的，所以需要特殊处理
    if (isFromFegin && StrUtil.equals(AppSecurityFrameworkUtils.getSecurityToken(), token)) {
      return LoginUserUtils.buildSystemSecurityUser(token);
    }
    return redisService.getAppUser(token, tokenExpireDuration);
  }

  private Collection<? extends GrantedAuthority> getAuthorities(LoginUser<?> loginUser) {
    if (loginUser.getPermissions() == null) {
      return Collections.emptyList();
    }
    return loginUser.getPermissions().stream()
        .map(SimpleGrantedAuthority::new)
        .toList();
  }
}
