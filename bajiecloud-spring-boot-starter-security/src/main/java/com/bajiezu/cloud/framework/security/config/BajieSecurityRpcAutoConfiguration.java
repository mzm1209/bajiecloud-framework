package com.bajiezu.cloud.framework.security.config;

import com.bajiezu.cloud.framework.security.rpc.LoginUserRequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BajieSecurityRpcAutoConfiguration {

  @Bean
  public LoginUserRequestInterceptor loginUserRequestInterceptor() {
    return new LoginUserRequestInterceptor();
  }

}
