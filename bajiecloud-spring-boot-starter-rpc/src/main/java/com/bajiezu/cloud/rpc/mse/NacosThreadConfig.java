package com.bajiezu.cloud.rpc.mse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosThreadConfig {

  /**
   * 为 Nacos 配置专用的线程池
   */
  @Bean(name = "nacosExecutorService")
  public ExecutorService nacosExecutorService() {
    // 使用固定大小的线程池
    return Executors.newFixedThreadPool(
        5,
        r -> {
          Thread thread = new Thread(r, "nacos-config-thread");
          thread.setDaemon(true); // 设置为守护线程
          thread.setPriority(Thread.MIN_PRIORITY + 1);
          return thread;
        }
    );
  }

  /**
   * 定时任务线程池
   */
  @Bean(name = "nacosScheduledExecutor")
  public ScheduledExecutorService nacosScheduledExecutor() {
    return Executors.newScheduledThreadPool(
        2,
        r -> {
          Thread thread = new Thread(r, "nacos-schedule-thread");
          thread.setDaemon(true);
          return thread;
        }
    );
  }
}