package com.bajiezu.cloud.rocketmq.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rocketmq")
@Data
public class RocketMQProperties {

  /**
   * rocket 中topic 和 consumerGroup 的后缀 暂定 dev 环境都是 dev 测试环境都是 test 生产环境没有后缀
   */
  private String envSuffix;

  /**
   * topic 配置 其中key在 ServiceName 中维护
   *
   * @see com.bajiezu.cloud.common.constants.ServiceName
   */
  private Map<String, TopicConfig> topics = new HashMap<>();


  // 便捷方法
  public String getTopicName(String topicKey) {
    TopicConfig config = topics.get(topicKey);
    return config != null ? wrapSuffix(config.getName()) : null;
  }

  private String wrapSuffix(String name) {
    if (StringUtils.isNoneBlank(envSuffix) && StringUtils.isNotBlank(name)) {
      return name + "-" + envSuffix;
    }
    return name;
  }

  public String getConsumerGroup(String topicKey) {
    TopicConfig config = topics.get(topicKey);
    return config != null ? wrapSuffix(config.getConsumerGroup()) : null;
  }

  /**
   * topic 配置
   */
  @Data
  public static class TopicConfig {

    /**
     * topic 名称
     */
    private String name;
    /**
     * 消费组名称
     */
    private String consumerGroup;

  }
}