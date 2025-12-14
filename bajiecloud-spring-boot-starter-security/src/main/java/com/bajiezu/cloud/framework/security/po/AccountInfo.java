package com.bajiezu.cloud.framework.security.po;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * B端用户登录信息，可以来这里补全登录信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo implements LoginInfoEntity {

  /**
   * 账户ID
   */
  private Long accountId;

  /**
   * 账户名
   */
  private String username;

  /**
   * 账户类型
   */
  private String accountType;

  /**
   * 部门信息
   */
  private String department;

  /**
   * 职位
   */
  private String position;

  /**
   * 账户状态
   */
  private Integer status;

  /**
   * 额外权限
   */
  private List<String> extraPermissions;

  @Override
  public Long getUserId() {
    return accountId;
  }

  @Override
  public String getUsername() {
    return username;
  }
}
