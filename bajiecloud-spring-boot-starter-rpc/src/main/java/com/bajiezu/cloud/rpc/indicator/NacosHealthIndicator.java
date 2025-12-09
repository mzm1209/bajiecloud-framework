package com.bajiezu.cloud.rpc.indicator;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class NacosHealthIndicator implements HealthIndicator {

  @Autowired
  private NacosDiscoveryProperties nacosProperties;

  @Override
  public Health health() {
    try {
      String serverAddr = nacosProperties.getServerAddr();
      String namespace = nacosProperties.getNamespace();

      // 这里可以添加实际的 Nacos 连接检查逻辑
      // 如果检查通过
      return Health.up()
          .withDetail("serverAddr", serverAddr)
          .withDetail("namespace", namespace)
          .withDetail("serviceName", nacosProperties.getService())
          .build();

    } catch (Exception e) {
      return Health.down()
          .withException(e)
          .withDetail("error", "Nacos 连接异常")
          .build();
    }
  }
}