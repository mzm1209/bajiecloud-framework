package com.bajiezu.cloud.excel.export.config;

import com.bajiezu.cloud.excel.export.ExportApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 导出用到的feign 配置
 */
@AutoConfiguration
@EnableFeignClients(clients = ExportApi.class) // 主要是引入相关的 API 服务
public class BajiecloudExportRpcAutoConfiguration {
}
