package com.bajiezu.cloud.framework.security.filter;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.UNAUTHORIZED;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.servlet.ServletUtils;
import com.bajiezu.cloud.common.web.cloud.constants.RpcConstants;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.security.context.LoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.service.RedisService;
import com.bajiezu.cloud.framework.security.util.LoginUserUtils;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Token 过滤器，验证 token 的有效性 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 */
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final RedisService redisService;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  @Setter
  private Set<String> permitAllPaths;
  @Setter
  private Set<String> noNeedLoginPath;


  /**
   * 登录有效期，各个需要登录的业务需要自行设置
   */
  @Setter
  private Duration tokenExpireDuration = Duration.ofDays(1);


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String requestUri = request.getRequestURI();
    return requestUri.startsWith("/app");
  }

  @Override
  @SuppressWarnings("NullableProblems")
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain)
      throws IOException, ServletException {
    // 情况一，基于 header[login-user] 获得用户，例如说来自 Gateway 或者其它服务透传
    String requestUri = request.getRequestURI();
    if (isSwaggerPath(requestUri)
        || permitAllPaths.contains(requestUri)) {
      chain.doFilter(request, response);
      return;
    }
    String token = SecurityFrameworkUtils.getToken(request,
        SecurityFrameworkUtils.LOGIN_USER_HEADER,
        SecurityFrameworkUtils.TOKEN_PARAMETER_NAME);
    if (StrUtil.isBlank(token)) {
      chain.doFilter(request, response);
      return;
    }
    log.info("TokenAuthenticationFilter,requestUri:{}", requestUri);
    try {
      LoginUser<?> loginUser = buildLoginUserByHeader(request, token);
      if (loginUser != null) {
        // 设置到 Spring Security 上下文
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                loginUser,
                null,
                getAuthorities(loginUser)
            );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 设置到 ThreadLocal 上下文
        LoginUserContext.setLoginUser(loginUser);

        log.debug("user:{} authenticate success", loginUser.getUsername());
        chain.doFilter(request, response);
      } else {
        log.warn("no user info found by url:{}", requestUri);
        ServletUtils.writeJSON(response, CommonResult.error(UNAUTHORIZED));
      }
      // 继续过滤链
    } finally {
      LoginUserContext.clear();
    }

  }


  /**
   * 判断是否是 Swagger 相关路径
   */
  private boolean isSwaggerPath(String requestUri) {
    return noNeedLoginPath.stream().anyMatch(path -> antPathMatcher.match(path, requestUri));
  }


  @SneakyThrows
  private LoginUser<?> buildLoginUserByHeader(HttpServletRequest request, String token) {
    String feginHeader = request.getHeader(RpcConstants.FEGIN_REQUEST_HEADER);
    boolean isFromFegin = StrUtil.equals(RpcConstants.FEGIN_REQUEST_HEADER_VALUE, feginHeader);
    // fegin 的请求需要特殊处理，因为 fegin 是在 system 服务中调用的，所以需要特殊处理
    if (isFromFegin && StrUtil.equals(SecurityFrameworkUtils.getSecurityToken(), token)) {
      return LoginUserUtils.buildSystemSecurityUser(token);
    }
    return redisService.getUser(token, tokenExpireDuration);
  }

  /**
   * 获取权限列表
   */
  private Collection<? extends GrantedAuthority> getAuthorities(LoginUser<?> loginUser) {
    if (loginUser.getPermissions() == null) {
      return Collections.emptyList();
    }
    return loginUser.getPermissions().stream()
        .map(SimpleGrantedAuthority::new)
        .toList();
  }

}
