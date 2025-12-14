package com.bajiezu.cloud.framework.security.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

class SecurityFrameworkUtilsTest {

  AntPathMatcher pathMatcher = new AntPathMatcher();

  @Test
  void testMatch() {
    System.out.println(
        pathMatcher.match("/*/swagger-ui/index.html", "/system/swagger-ui/index.html"));

    System.out.println(
        pathMatcher.match("/system/v3/api-docs/**", "/system/v3/api-docs/swagger-config"));
  }

}