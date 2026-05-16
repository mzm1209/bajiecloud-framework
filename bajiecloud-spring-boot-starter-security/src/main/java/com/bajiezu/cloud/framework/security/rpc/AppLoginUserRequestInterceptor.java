package com.bajiezu.cloud.framework.security.rpc;

import static com.bajiezu.cloud.common.web.cloud.constants.RpcConstants.FEGIN_REQUEST_HEADER;

import com.bajiezu.cloud.common.context.RequestContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * APP LoginUser 的 RequestInterceptor 实现类：Feign 请求时，将 APP LoginUser 设置到 header 中
 */
@Slf4j
public class AppLoginUserRequestInterceptor implements RequestInterceptor {

  @Override
  @SneakyThrows
  public void apply(RequestTemplate requestTemplate) {
    LoginUser<?> user = AppSecurityFrameworkUtils.getLoginUser();
    if (user == null) {
      return;
    }
    requestTemplate.header(AppSecurityFrameworkUtils.APP_LOGIN_USER_HEADER, user.getToken());
    requestTemplate.header("X-Request-ID", RequestContext.getRequestId());
    requestTemplate.header(FEGIN_REQUEST_HEADER, "feign");
  }
}
