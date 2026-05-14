package com.bajiezu.cloud.framework.security.service;

import com.bajiezu.cloud.framework.security.po.LoginUser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
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
    return Keys.hmacShaKeyFor(keyBytes);
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
      Map<String, Object> claims = parseClaimsCompat(token);
      if (!APP_TOKEN_DOMAIN.equals(claims.get("tokenDomain"))) {
        return false;
      }
      return redisService.existsAppUser(token);
    } catch (Exception e) {
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

  @SuppressWarnings("unchecked")
  private Map<String, Object> parseClaimsCompat(String token) throws Exception {
    Object parser = Jwts.parser();
    try {
      // JJWT 新版：parser().verifyWith(key).build().parseSignedClaims(token).getPayload()
      Object parserBuilder = parser.getClass().getMethod("verifyWith", SecretKey.class)
          .invoke(parser, getJwtSecretKey());
      Object builtParser = parserBuilder.getClass().getMethod("build").invoke(parserBuilder);
      Object jws = builtParser.getClass().getMethod("parseSignedClaims", String.class)
          .invoke(builtParser, token);
      Object payload = jws.getClass().getMethod("getPayload").invoke(jws);
      return (Map<String, Object>) payload;
    } catch (NoSuchMethodException ignored) {
      // JJWT 旧版：parser().setSigningKey(key).parseClaimsJws(token).getBody()
      Object configured = parser.getClass().getMethod("setSigningKey", java.security.Key.class)
          .invoke(parser, getJwtSecretKey());
      Object jws = configured.getClass().getMethod("parseClaimsJws", String.class)
          .invoke(configured, token);
      Object body = jws.getClass().getMethod("getBody").invoke(jws);
      return (Map<String, Object>) body;
    }
  }

  private boolean isJwtToken(String token) {
    return token != null && token.chars().filter(ch -> ch == '.').count() == 2;
  }

}
