package com.bajiezu.cloud.framework.security.service;

import com.bajiezu.cloud.framework.security.po.LoginUser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * APP 端 token 服务，生成与校验逻辑与平台一致，数据域独立。
 */
@Slf4j
public class AppLoginTokenService {

  private static final String APP_TOKEN_DOMAIN = "APP";
  private static final String JWT_SECRET = "your-256-bit-secret-key-here-must-be-at-least-32-bytes";

  private final RedisService redisService;
  private final Duration expiredDuration;
  private final long expiration;

  public AppLoginTokenService(RedisService redisService, long tokenExpiration) {
    this.redisService = redisService;
    this.expiredDuration = Duration.ofMillis(tokenExpiration);
    this.expiration = tokenExpiration;
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
    if (!isJwtToken(token)) {
      return false;
    }
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> claims = (Map<String, Object>) Jwts.parser()
          .setSigningKey(getJwtSecretKey())
          .parseClaimsJws(token)
          .getBody();
      if (!APP_TOKEN_DOMAIN.equals(claims.get("tokenDomain"))) {
        return false;
      }
      return redisService.existsAppUser(token);
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("APP JWT Token check failed: {}", token, e);
      return false;
    }
  }

  public LoginUser<?> getLoginUser(String token) {
    return redisService.getAppUser(token, expiredDuration);
  }

  public void deleteToken(String token) {
    redisService.deleteAppUser(token);
  }

  private Key getJwtSecretKey() {
    return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
  }

  private boolean isJwtToken(String token) {
    return token != null && token.chars().filter(ch -> ch == '.').count() == 2;
  }
}
