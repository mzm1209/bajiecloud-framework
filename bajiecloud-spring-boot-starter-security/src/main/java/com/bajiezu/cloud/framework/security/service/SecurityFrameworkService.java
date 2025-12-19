package com.bajiezu.cloud.framework.security.service;

/**
 * Security 框架 Service 接口，定义权限相关的校验操作
 */
public interface SecurityFrameworkService {

    /**
     * 判断是否有权限
     */
    boolean hasPermission(String permission);
}
