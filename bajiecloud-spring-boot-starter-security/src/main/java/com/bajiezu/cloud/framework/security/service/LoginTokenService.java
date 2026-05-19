package com.bajiezu.cloud.framework.security.service;

import com.bajiezu.cloud.framework.security.po.LoginUser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 有登录服务需求的业务，自己去注册这个bean
 */
@Slf4j
public class LoginTokenService {

    private final Duration expiredDuration;
    private final long expiration;

    @Value("${jwt.secret:defaultSecretKey:bajiezu-cloud}")
    private String secretKey;
    @Resource
    private RedisService redisService;

    public LoginTokenService(long tokenExpiration) {
        expiredDuration = Duration.ofMillis(tokenExpiration);
        expiration = tokenExpiration;
    }

    /**
     * 生成安全的HMAC-SHA密钥 (JJWT 0.13.0+ 推荐方式)
     */
    private SecretKey getJwtSecretKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length >= 32) {
            return Keys.hmacShaKeyFor(keyBytes);
        }
        try {
            // 对短密钥做 SHA-256 扩展，保证至少 256 bit（32 bytes）
            byte[] hashedKey = MessageDigest.getInstance("SHA-256").digest(keyBytes);
            return Keys.hmacShaKeyFor(hashedKey);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
        }
    }

    /**
     * 根据loginUser 生成jwtToken
     *
     * @param loginUser 登录用户信息
     * @return jwtToken
     */
    public String generateToken(LoginUser<?> loginUser) {
        // 生成随机 Token（可以使用JWT或UUID）
        return generateJwtToken(loginUser);
    }

    private String generateJwtToken(LoginUser<?> loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        claims.put("loginType", loginUser.getLoginType().name());

        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        String jwtToken = Jwts.builder()
                .claims(claims)                     // 新版API：设置claims
                .subject(loginUser.getUsername())   // 主题
                .issuedAt(now)                      // 签发时间
                .expiration(expireDate)             // 过期时间
                .signWith(getJwtSecretKey(), Jwts.SIG.HS256) // 新版API：签名
                .compact();
        loginUser.setToken(jwtToken);
        redisService.saveUser(jwtToken, loginUser, expiredDuration);
        return jwtToken;
    }

    public boolean validateToken(String token) {
        if (isJwtToken(token)) {
            // JWT验证：1.检查黑名单 2.验证签名和过期
            try {
                // 新版API：使用 verifyWith 和 parseSignedClaims
                Jwts.parser()
                        .verifyWith(getJwtSecretKey())
                        .build()
                        .parseSignedClaims(token);
                // 可选：再检查一下Redis中是否存在（用于实现即时登出）
                return redisService.exists(token);
            } catch (JwtException | IllegalArgumentException e) {
                log.warn("JWT Token check failed: {}", token, e);
                return false;
            }
        }
        return false;
    }

    public LoginUser<?> getLoginUser(String token) {
        return redisService.getUser(token, expiredDuration);
    }

    public void deleteToken(String token) {
        redisService.deleteUser(token);
    }


    /**
     * 判断是否为 JWT Token
     */
    private boolean isJwtToken(String token) {
        // JWT Token 格式：xxx.yyy.zzz
        return token.chars().filter(ch -> ch == '.').count() == 2;
    }


}