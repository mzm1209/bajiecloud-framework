package com.bajiezu.cloud.rocketmq.constant;

/**
 * 定义消息队列的TAG
 */
public interface MessageTag {

  /**
   * 业务信息，如果 order 表示订单业务， bill 表示账单业务， risk 订单风控
   *
   * @return 消息tag的前缀
   */
  String businessType();

  /**
   * 业务操作，如 create 创建， update 更新， delete 删除, pay 支付
   *
   * @return 消息tag的后缀
   */
  String operate();

  /**
   * 获取tag
   *
   * @return 生成消息的TAG
   */
  default String tag() {
    return businessType() + "_" + operate();
  }

}
