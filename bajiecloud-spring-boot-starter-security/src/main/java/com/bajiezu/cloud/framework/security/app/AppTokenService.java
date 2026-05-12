package com.bajiezu.cloud.framework.security.app;

public interface AppTokenService {

  String createToken(AppLoginUserInfo loginUserInfo);

  AppLoginUserInfo getLoginUser(String token);

  boolean validateToken(String token);

  void removeToken(String token);

  void removeAllTokensByCustomerId(Long customerId);

}
