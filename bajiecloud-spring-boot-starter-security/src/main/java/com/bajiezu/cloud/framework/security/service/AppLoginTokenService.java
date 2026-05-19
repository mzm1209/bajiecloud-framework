package com.bajiezu.cloud.framework.security.service;

import com.bajiezu.cloud.framework.security.po.LoginUser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * APP 端 token 服务，生成与校验逻辑与平台一致，数据域独立。
 */
@Slf4j
public class AppLoginTokenService {

  private static final String APP_TOKEN_DOMAIN = "APP";

  private final Duration expiredDuration;
  private final long expiration;

  @Value("${jwt.secret:defaultSecretKey:bajiezu-cloud}")
  private String secretKey;

  @Resource
  private RedisService redisService;

  public AppLoginTokenService(long tokenExpiration) {
    this.expiredDuration = Duration.ofMillis(tokenExpiration);
    this.expiration = tokenExpiration;
  }

  private SecretKey getJwtSecretKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length >= 32) {
      return Keys.hmacShaKeyFor(keyBytes);
    }
    try {
      byte[] hashedKey = MessageDigest.getInstance("SHA-256").digest(keyBytes);
      return Keys.hmacShaKeyFor(hashedKey);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
    }
  }

  public String generateToken(LoginUser<?> loginUser) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", loginUser.getUserId());
    claims.put("username", loginUser.getUsername());
    claims.put("loginType", loginUser.getLoginType().name());
    claims.put("tokenDomain", APP_TOKEN_DOMAIN);

    Date now = new Date();
    Date expireDate = new Date(now.getTime() + expiration);

    String jwtToken = Jwts.builder()
        .claims(claims)
        .subject(loginUser.getUsername())
        .issuedAt(now)
        .expiration(expireDate)
        .signWith(getJwtSecretKey(), Jwts.SIG.HS256)
        .compact();
    loginUser.setToken(jwtToken);
    redisService.saveAppUser(jwtToken, loginUser, expiredDuration);
    return jwtToken;
  }

  public boolean validateToken(String token) {
    if (isJwtToken(token)) {
      try {
        String tokenDomain = Jwts.parser()
            .verifyWith(getJwtSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("tokenDomain", String.class);
        if (!APP_TOKEN_DOMAIN.equals(tokenDomain)) {
          return false;
        }
        return redisService.existsAppUser(token);
      } catch (JwtException | IllegalArgumentException e) {
        log.warn("APP JWT Token check failed: {}", token, e);
        return false;
      }
    }
    return false;
  }

  public LoginUser<?> getLoginUser(String token) {
    return redisService.getAppUser(token, expiredDuration);
  }

  public void deleteToken(String token) {
    redisService.deleteAppUser(token);
  }

  private boolean isJwtToken(String token) {
    return token != null && token.chars().filter(ch -> ch == '.').count() == 2;
  }
}
