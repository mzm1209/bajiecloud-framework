package com.bajiezu.cloud.framework.web.config;

import com.bajiezu.cloud.common.constants.WebFilterOrderEnum;
import com.bajiezu.cloud.common.filter.RequestContextFilter;
import com.bajiezu.cloud.common.mybatis.config.MybatisPlusConfig;
import com.bajiezu.cloud.framework.web.core.filter.CacheRequestBodyFilter;
import com.bajiezu.cloud.framework.web.core.handler.GlobalExceptionHandler;
import com.bajiezu.cloud.framework.web.core.handler.GlobalResponseBodyHandler;
import com.bajiezu.cloud.rpc.mse.NacosThreadConfig;
import com.fhs.trans.config.EasyTransMybatisPlusConfig;
import com.fhs.trans.config.TransServiceConfig;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@AutoConfiguration
@Slf4j
@Import(value = {MybatisPlusConfig.class, //mybatis的配置
    EasyTransMybatisPlusConfig.class, TransServiceConfig.class, //easytrans的配置
    NacosThreadConfig.class}) //设置Nacos的线程
@EnableDiscoveryClient
public class BajiezuWebAutoConfiguration {

  /**
   * 应用名
   */
  @Value("${spring.application.name}")
  private String applicationName;

  // ========== Filter 相关 ==========

  public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter,
      Integer order) {
    FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
    bean.setOrder(order);
    return bean;
  }

  @Bean
  public GlobalExceptionHandler globalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

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

  @Bean
  public FilterRegistrationBean<RequestContextFilter> requestIdFilter() {
    return createFilterBean(new RequestContextFilter(),
        WebFilterOrderEnum.REQUEST_ID_FILTER);
  }

}
