package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.framework.security.context.LoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import java.util.function.Supplier;

public class RemoteMethodInvokeUtils {

  /**
   * 获取当前请求的FegieToken
   */
  public static <RESP> RESP invokeWithSecurityToken(Supplier<RESP> supplier) {

    LoginUser<?> oriLoginUser = LoginUserContext.getLoginUser();
    try {
      LoginUserUtils.initSystemSecurityUser(SecurityFrameworkUtils.getSecurityToken());
      return supplier.get();
    } finally {
      if (oriLoginUser != null) {
        LoginUserContext.setLoginUser(oriLoginUser);
      } else {
        LoginUserContext.clear();
      }
    }
  }
}
