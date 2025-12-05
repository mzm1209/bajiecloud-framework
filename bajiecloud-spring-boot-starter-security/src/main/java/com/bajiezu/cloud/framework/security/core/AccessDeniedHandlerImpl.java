package com.bajiezu.cloud.framework.security.core;

import static com.bajie.cloud.common.web.exception.ServiceException.GlobalErrorCodeConstants.FORBIDDEN;

import com.bajie.cloud.common.util.servlet.ServletUtils;
import com.bajie.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.security.context.LoginUserContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;


/**
 * 访问一个需要认证的 URL 资源，已经认证（登录）但是没有权限的情况下，返回 {@link GlobalErrorCodeConstants#FORBIDDEN} 错误码。
 * <p>
 * 补充：Spring Security 通过 {@link org.springframework.security.web.access.ExceptionTranslationFilter#handleAccessDeniedException(HttpServletRequest, HttpServletResponse, FilterChain, org.springframework.security.access.AccessDeniedException)} 方法，调用当前类
 */
@Slf4j
@SuppressWarnings("JavadocReference")
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
      throws IOException, ServletException {
    // 打印 warn 的原因是，不定期合并 warn，看看有没恶意破坏
    log.warn("[commence][访问 URL({}) 时，用户({}) 权限不够]", request.getRequestURI(),
        LoginUserContext.getLoginUserId(), e);
    // 返回 403
    ServletUtils.writeJSON(response, CommonResult.error(FORBIDDEN));
  }

}
