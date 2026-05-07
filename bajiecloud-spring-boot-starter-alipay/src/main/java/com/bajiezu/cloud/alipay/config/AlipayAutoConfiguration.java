package com.bajiezu.cloud.alipay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.bajiezu.cloud.alipay.AlipayClientHolder;
import com.bajiezu.cloud.alipay.AlipayProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

/**
 * 支付宝 SDK 自动装配。
 *
 * <p>仅做"配置 → Client Bean"，不封装任何业务 API。OAuth、下单、退款、回调验签等业务调用
 * 由各业务模块自己写薄包装（注入 {@link AlipayClientHolder} 拿到 {@link AlipayClient} 后直接
 * 调 SDK）。
 *
 * <p>Nacos 配置变更时，监听 {@link RefreshScopeRefreshedEvent} 整体重建 Client 实例。
 */
@AutoConfiguration
@EnableConfigurationProperties(AlipayProperties.class)
@Slf4j
public class AlipayAutoConfiguration {

    @Resource
    private AlipayProperties alipayProperties;

    @Resource
    private AlipayClientHolder alipayClientHolder;

    @Bean
    public AlipayClientHolder alipayClientHolder(AlipayProperties props) {
        AlipayClientHolder holder = new AlipayClientHolder();
        rebuild(holder, props);
        log.info(">>>>>>>>>>> alipay client init.");
        return holder;
    }

    /**
     * Nacos 配置变更时整体重建 Client。老实例由 GC 自然回收。
     */
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefresh() {
        rebuild(alipayClientHolder, alipayProperties);
        log.info(">>>>>>>>>>> alipay client refreshed.");
    }

    private void rebuild(AlipayClientHolder holder, AlipayProperties props) {
        holder.refresh(buildClient("miniapp", props.getMiniapp()),
            buildClient("pay", props.getPay()));
    }

    private AlipayClient buildClient(String tag, AlipayProperties.ClientConfig cfg) {
        if (cfg == null
            || StringUtils.isBlank(cfg.getAppId())
            || StringUtils.isBlank(cfg.getPrivateKey())
            || StringUtils.isBlank(cfg.getAlipayPublicKey())
            || StringUtils.isBlank(cfg.getGatewayHost())) {
            log.warn(">>>>>>>>>>> alipay [{}] config incomplete, skip building client. "
                + "Calls relying on alipay client will fail until config is provided.", tag);
            return null;
        }
        String gateway = "https://" + cfg.getGatewayHost() + "/gateway.do";
        return new DefaultAlipayClient(
            gateway,
            cfg.getAppId(),
            cfg.getPrivateKey(),
            "json",
            "UTF-8",
            cfg.getAlipayPublicKey(),
            cfg.getSignType()
        );
    }
}
