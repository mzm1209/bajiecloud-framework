package com.bajiezu.cloud.framework.security.app;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * App 端登录用户信息。
 */
@Data
public class AppLoginUserInfo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private Long customerId;
  private String mobile;
  private String userType;
  private Integer realnameStatus;
  private Integer faceAuthStatus;
  private LocalDateTime loginTime;
  private String deviceId;

}
