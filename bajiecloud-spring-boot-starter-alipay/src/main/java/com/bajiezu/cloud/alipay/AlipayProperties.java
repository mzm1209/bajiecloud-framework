package com.bajiezu.cloud.alipay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付宝 SDK 配置项。
 *
 * <p>通过 Nacos {@code alipay.yml} 注入，支持小程序（miniapp）与支付商户（pay）两套独立配置；
 * 若实际为同一个支付宝应用，两套填相同值即可。
 *
 * <p>沙箱 / 生产通过 Nacos 不同 namespace 区分，与现有 Spring Profile 策略一致。
 */
@Data
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    /**
     * 小程序端配置（用于授权登录、获取手机号等）。
     */
    private ClientConfig miniapp;

    /**
     * 支付商户配置（用于下单、退款、查询、回调验签）。
     */
    private ClientConfig pay;

    @Data
    public static class ClientConfig {

        /**
         * 应用 appId。
         */
        private String appId;

        /**
         * 应用私钥（RSA2，PKCS8 格式）。
         */
        private String privateKey;

        /**
         * 支付宝公钥。
         */
        private String alipayPublicKey;

        /**
         * 网关 host。生产：openapi.alipay.com；沙箱：openapi.sandbox.alipaydev.com。
         */
        private String gatewayHost;

        /**
         * 签名类型，默认 RSA2。
         */
        private String signType = "RSA2";
    }
}
