package com.bajiezu.cloud.common.web.cloud.constants;

/**
 * RPC 相关的枚举
 */
public interface RpcConstants {

  /**
   * RPC API 的前缀
   */
  String RPC_API_PREFIX = "/rpc-api";

  /**
   * system 服务名
   * <p>
   * 注意，需要保证和 spring.application.name 保持一致
   */
  String SYSTEM_NAME = "system-server";

  /**
   * system 服务的前缀
   */
  String SYSTEM_PREFIX = RPC_API_PREFIX + "/system";

  String FEGIN_REQUEST_HEADER = "X-Fegin-Request";

  String FEGIN_REQUEST_HEADER_VALUE = "feign";


}