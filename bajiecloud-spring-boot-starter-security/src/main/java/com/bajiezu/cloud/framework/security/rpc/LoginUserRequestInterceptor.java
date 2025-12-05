package com.bajiezu.cloud.framework.security.rpc;

import com.bajiezu.cloud.framework.security.LoginUser;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * LoginUser 的 RequestInterceptor 实现类：Feign 请求时，将 {@link LoginUser} 设置到 header 中，继续透传给被调用的服务
 */
@Slf4j
public class LoginUserRequestInterceptor implements RequestInterceptor {

  @Override
  @SneakyThrows
  public void apply(RequestTemplate requestTemplate) {
    LoginUser<?> user = SecurityFrameworkUtils.getLoginUser();
    if (user == null) {
      return;
    }
    // 传递 token 到被调用的服务
    requestTemplate.header(SecurityFrameworkUtils.LOGIN_USER_HEADER, user.getToken());
  }

}
