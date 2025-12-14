package com.bajiezu.cloud.framework.security.po;

import com.bajiezu.cloud.common.constants.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录用户信息
 */
@Data
@Accessors(chain = true)
public class LoginUser<T extends LoginInfoEntity> implements Serializable {

  /**
   * 用户编号
   */
  private Long id;
  /**
   * 用户类型
   * <p>
   * 关联 {@link UserTypeEnum}
   */
  private Integer userType;

  /**
   * 登录类型
   */
  private LoginType loginType;

  /**
   * 登陆用户额外信息
   */
  private T loginInfo;

  /**
   * 过期时间
   */
  private LocalDateTime expiresTime;

  /**
   * 登录时间
   */
  private LocalDateTime loginTime = LocalDateTime.now();

  private String ip;

  private String token;

  /**
   * 合作商id
   */
  private Long partnerId;


  /**
   * 登录来源（WEB、APP、MINI_PROGRAM等）
   */
  private String loginSource;

  /**
   * 用户角色
   */
  private Set<String> roles;

  /**
   * 用户权限
   */
  private Set<String> permissions;

  // ========== 上下文 ==========
  /**
   * 上下文字段，不进行持久化
   * <p>
   * 1. 用于基于 LoginUser 维度的临时缓存
   */
  @JsonIgnore
  private Map<String, Object> context;


  public String getUsername() {
    return loginInfo != null ? loginInfo.getUsername() : null;
  }

  public Long getUserId() {
    return loginInfo != null ? loginInfo.getUserId() : null;
  }


  /**
   * 判断是否已过期
   */
  public boolean isExpired() {
    return expiresTime != null && LocalDateTime.now().isAfter(expiresTime);
  }


  /**
   * 登录类型枚举
   */
  public enum LoginType {
    USERNAME_PASSWORD,   // 用户名密码登录
    FEISHU,              // 飞书扫码登录
    MINI_PROGRAM,        // 小程序登录
    PHONE,               // 手机号登录
    EMAIL                // 邮箱登录
  }


}
