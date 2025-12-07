package com.bajiezu.cloud.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;

@Configuration
public class FilterConfig {

  @Bean
  public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
    FilterRegistrationBean<RequestContextFilter> registrationBean =
        new FilterRegistrationBean<>();
    registrationBean.setFilter(new RequestContextFilter());
    registrationBean.addUrlPatterns("/*");
    registrationBean.setOrder(1);
    return registrationBean;
  }
}
