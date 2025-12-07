package com.bajiezu.cloud.framework.web.config;

import com.bajiezu.cloud.common.constants.WebFilterOrderEnum;
import com.bajiezu.cloud.framework.web.core.filter.CacheRequestBodyFilter;
import com.bajiezu.cloud.framework.web.core.handler.GlobalResponseBodyHandler;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@AutoConfiguration
public class BajiezuWebAutoConfiguration {

  /**
   * 应用名
   */
  @Value("${spring.application.name}")
  private String applicationName;

  public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter,
      Integer order) {
    FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
    bean.setOrder(order);
    return bean;
  }

  // ========== Filter 相关 ==========

  @Bean
  public GlobalResponseBodyHandler<?> globalResponseBodyHandler() {
    return new GlobalResponseBodyHandler<>();
  }

  /**
   * 创建 CorsFilter Bean，解决跨域问题
   */
  @Bean
  @Order(value = WebFilterOrderEnum.CORS_FILTER) // 特殊：修复因执行顺序影响到跨域配置不生效问题
  public FilterRegistrationBean<CorsFilter> corsFilterBean() {
    // 创建 CorsConfiguration 对象
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOriginPattern("*"); // 设置访问源地址
    config.addAllowedHeader("*"); // 设置访问源请求头
    config.addAllowedMethod("*"); // 设置访问源请求方法
    // 创建 UrlBasedCorsConfigurationSource 对象
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置
    return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
  }

  /**
   * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
   */
  @Bean
  public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
    return createFilterBean(new CacheRequestBodyFilter(),
        WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
  }


}
