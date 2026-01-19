package com.bajiezu.cloud.framework.security.config;

import com.bajiezu.cloud.framework.security.rpc.LoginUserRequestInterceptor;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BajieSecurityRpcAutoConfiguration {


  @Value("${system.rpc.security-token:}")
  private String rpcSecurityToken;

  @Bean
  public LoginUserRequestInterceptor loginUserRequestInterceptor() {
    SecurityFrameworkUtils.setSecurityToken(rpcSecurityToken);
    return new LoginUserRequestInterceptor();
  }


}
