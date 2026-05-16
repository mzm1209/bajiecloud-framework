package com.bajiezu.cloud.framework.security.app;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.json.JsonUtils;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppTokenServiceImpl implements AppTokenService {

  private static final Duration TOKEN_EXPIRE_DURATION = Duration.ofSeconds(
      AppSecurityConstants.TOKEN_EXPIRE_SECONDS);

  private final RedissonClient redissonClient;

  @Override
  public String createToken(AppLoginUserInfo loginUserInfo) {
    if (loginUserInfo == null || loginUserInfo.getCustomerId() == null) {
      throw new IllegalArgumentException("loginUserInfo or customerId must not be null");
    }
    if (!AppUserTypeEnum.APP_CUSTOMER.name().equals(loginUserInfo.getUserType())) {
      throw new IllegalArgumentException("userType must be APP_CUSTOMER");
    }
    String token = UUID.randomUUID().toString().replace("-", "");
    String tokenKey = buildTokenKey(token);
    RBucket<String> bucket = redissonClient.getBucket(tokenKey);
    bucket.set(JsonUtils.toJsonString(loginUserInfo), TOKEN_EXPIRE_DURATION);

    String tokenSetKey = buildTokenSetKey(loginUserInfo.getCustomerId());
    RSet<String> tokenSet = redissonClient.getSet(tokenSetKey);
    tokenSet.add(token);
    tokenSet.expire(TOKEN_EXPIRE_DURATION);
    return token;
  }

  @Override
  public AppLoginUserInfo getLoginUser(String token) {
    if (StrUtil.isBlank(token)) {
      return null;
    }
    String tokenKey = buildTokenKey(token);
    RBucket<String> bucket = redissonClient.getBucket(tokenKey);
    String loginUserJson = bucket.get();
    if (StrUtil.isBlank(loginUserJson)) {
      return null;
    }
    bucket.expire(TOKEN_EXPIRE_DURATION);
    AppLoginUserInfo loginUser = JsonUtils.parseObject(loginUserJson, AppLoginUserInfo.class);
    if (loginUser != null && loginUser.getCustomerId() != null) {
      redissonClient.getSet(buildTokenSetKey(loginUser.getCustomerId())).expire(TOKEN_EXPIRE_DURATION);
    }
    return loginUser;
  }

  @Override
  public boolean validateToken(String token) {
    return getLoginUser(token) != null;
  }

  @Override
  public void removeToken(String token) {
    if (StrUtil.isBlank(token)) {
      return;
    }
    AppLoginUserInfo loginUser = getLoginUser(token);
    redissonClient.getBucket(buildTokenKey(token)).delete();
    if (loginUser != null && loginUser.getCustomerId() != null) {
      redissonClient.getSet(buildTokenSetKey(loginUser.getCustomerId())).remove(token);
    }
  }

  @Override
  public void removeAllTokensByCustomerId(Long customerId) {
    if (customerId == null) {
      return;
    }
    String tokenSetKey = buildTokenSetKey(customerId);
    RSet<String> tokenSet = redissonClient.getSet(tokenSetKey);
    Set<String> tokens = tokenSet.readAll();
    for (String token : tokens) {
      redissonClient.getBucket(buildTokenKey(token)).delete();
    }
    tokenSet.delete();
  }

  private String buildTokenKey(String token) {
    return AppSecurityConstants.TOKEN_KEY_PREFIX + token;
  }

  private String buildTokenSetKey(Long customerId) {
    return AppSecurityConstants.USER_TOKEN_SET_PREFIX + customerId;
  }

}
