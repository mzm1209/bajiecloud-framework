package com.bajiezu.cloud.framework.security.service;

import com.bajiezu.cloud.framework.security.po.LoginUser;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String USER_KEY_PREFIX = "bajie:auth:user:";
    private static final String APP_USER_KEY_PREFIX = "bajie:auth:app-user:";
    private final RedissonClient redissonClient;

    public void saveUser(String token, LoginUser<?> user, Duration duration) {
        String key = USER_KEY_PREFIX + token;
        RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
        bucket.set(user, duration);
    }

    public LoginUser<?> getUser(String token, Duration duration) {
        String key = USER_KEY_PREFIX + token;
        RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
        LoginUser<?> loginUser = bucket.get();
        bucket.set(loginUser, duration);
        return loginUser;
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


    public void saveAppUser(String token, LoginUser<?> user, Duration duration) {
        String key = APP_USER_KEY_PREFIX + token;
        RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
        bucket.set(user, duration);
    }

    public LoginUser<?> getAppUser(String token, Duration duration) {
        String key = APP_USER_KEY_PREFIX + token;
        RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
        LoginUser<?> loginUser = bucket.get();
        if (loginUser != null) {
            bucket.set(loginUser, duration);
        }
        return loginUser;
    }

    public void deleteAppUser(String token) {
        String key = APP_USER_KEY_PREFIX + token;
        RBucket<LoginUser<?>> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    public boolean existsAppUser(String token) {
        String key = APP_USER_KEY_PREFIX + token;
        return redissonClient.getBucket(key).isExists();
    }
}