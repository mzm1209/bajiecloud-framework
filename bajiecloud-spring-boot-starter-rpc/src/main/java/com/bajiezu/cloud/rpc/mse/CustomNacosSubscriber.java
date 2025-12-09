package com.bajiezu.cloud.rpc.mse;

import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomNacosSubscriber extends Subscriber<Event> {

  private static final Logger logger = LoggerFactory.getLogger(CustomNacosSubscriber.class);

  @Override
  public void onEvent(Event event) {
    try {
      logger.debug("接收到 Nacos 事件: {}", event.getClass().getSimpleName());
      // 处理事件逻辑
    } catch (Exception e) {
      logger.error("处理 Nacos 事件时发生异常", e);
    }
  }

  @Override
  public Class<? extends Event> subscribeType() {
    // 订阅所有事件
    return Event.class;
  }
}