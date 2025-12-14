package com.bajiezu.cloud.framework.security.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户基础信息，登录的时候将这些信息放到 redis中，方便后续查询
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo implements LoginInfoEntity {

  /**
   * 客户ID
   */
  private Long customerId;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 小程序OpenId
   */
  private String miniProgramOpenId;

  /**
   * 昵称
   */
  private String nickname;

  /**
   * 头像
   */
  private String avatar;


  @Override
  public Long getUserId() {
    return customerId;
  }

  @Override
  public String getUsername() {
    return miniProgramOpenId;
  }
}
