package com.bajiezu.cloud.framework.security.config;

import com.bajiezu.cloud.framework.security.core.AuthenticationEntryPointImpl;
import com.bajiezu.cloud.framework.security.filter.AppTokenAuthenticationFilter;
import com.bajiezu.cloud.framework.security.filter.TokenAuthenticationFilter;
import com.bajiezu.cloud.framework.security.service.RedisService;
import com.bajiezu.cloud.framework.security.service.SecurityFrameworkService;
import com.bajiezu.cloud.framework.security.service.SecurityFrameworkServiceImpl;
import com.bajiezu.cloud.framework.security.service.AppLoginTokenService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;


/**
 * Spring Security 自动配置类，主要用于相关组件的配置
 * <p>
 * 注意，不能和 {@link com.bajiezu.cloud.framework.web.config.BajiezuWebAutoConfiguration}
 * 用一个，原因是会导致初始化报错。 参见
 * https://stackoverflow.com/questions/53847050/spring-boot-delegatebuilder-cannot-be-null-on-autowiring-authenticationmanager
 * 文档。
 */
@AutoConfiguration
@AutoConfigureOrder(-1) // 目的：先于 Spring Security 自动配置，避免一键改包后，org.* 基础包无法生效
public class BajieCloudSecurityAutoConfiguration {


    /**
     * 认证失败处理类 Bean
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 权限不够处理器 Bean
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    /**
     * Spring Security 加密器 考虑到安全性，这里采用 BCryptPasswordEncoder 加密器
     *
     * @see <a href="http://stackabuse.com/password-encoding-with-spring-security/">Password Encoding
     * with Spring Security</a>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public RedisService redisService(RedissonClient redissonClient) {
        return new RedisService(redissonClient);
    }

    /**
     * Token 认证过滤器 Bean
     */
    @Bean
    public TokenAuthenticationFilter authenticationTokenFilter(RedisService redisService) {
        return new TokenAuthenticationFilter(redisService);
    }



    @Bean
    public AppTokenAuthenticationFilter appAuthenticationTokenFilter(RedisService redisService) {
        return new AppTokenAuthenticationFilter(redisService);
    }

    @Bean
    public AppLoginTokenService appLoginTokenService() {
        return new AppLoginTokenService(86400000L);
    }

    @Bean("ss") // 使用 Spring Security 的缩写，方便使用
    public SecurityFrameworkService securityFrameworkService() {
        return new SecurityFrameworkServiceImpl();
    }
}
