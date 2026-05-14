package com.bajiezu.cloud.framework.security.rpc;

import static com.bajiezu.cloud.common.web.cloud.constants.RpcConstants.FEGIN_REQUEST_HEADER;

import com.bajiezu.cloud.common.context.RequestContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.AppSecurityFrameworkUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.SneakyThrows;

/**
 * APP LoginUser 的 RequestInterceptor：Feign 请求时透传 app-user-token。
 */
public class AppLoginUserRequestInterceptor implements RequestInterceptor {

  @Override
  @SneakyThrows
  public void apply(RequestTemplate requestTemplate) {
    LoginUser<?> user = AppSecurityFrameworkUtils.getLoginUser();
    if (user == null) {
      return;
    }
    requestTemplate.header(AppSecurityFrameworkUtils.APP_TOKEN_HEADER, user.getToken());
    requestTemplate.header("X-Request-ID", RequestContext.getRequestId());
    requestTemplate.header(FEGIN_REQUEST_HEADER, "feign");
  }
}
