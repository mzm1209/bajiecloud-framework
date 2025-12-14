// RedisService.java
package com.bajiezu.cloud.framework.security.service;

import com.bajiezu.cloud.framework.security.po.LoginUser;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private static final String USER_KEY_PREFIX = "bajie:auth:user:";
  private final RedissonClient redissonClient;

  public void saveUser(String token, LoginUser<?> user, Duration duration) {
    String key = USER_KEY_PREFIX + token;
    RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
    bucket.set(user, duration);
  }

  public LoginUser<?> getUser(String token, Duration duration) {
    String key = USER_KEY_PREFIX + token;
    RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
    return bucket.getAndExpire(duration);
  }


  public void deleteUser(String token) {
    String key = USER_KEY_PREFIX + token;
    RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
    bucket.delete();
  }

  public boolean exists(String token) {
    String key = USER_KEY_PREFIX + token;
    return redissonClient.getBucket(key).isExists();
  }
}