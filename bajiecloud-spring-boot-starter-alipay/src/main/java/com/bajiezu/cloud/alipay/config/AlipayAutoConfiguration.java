package com.bajiezu.cloud.alipay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.bajiezu.cloud.alipay.AlipayClientHolder;
import com.bajiezu.cloud.alipay.AlipayProperties;
import com.bajiezu.cloud.alipay.service.AlipayCallbackService;
import com.bajiezu.cloud.alipay.service.AlipayPayService;
import com.bajiezu.cloud.alipay.service.impl.AlipayCallbackServiceImpl;
import com.bajiezu.cloud.alipay.service.impl.AlipayPayServiceImpl;
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
 * <p>装配三类 Bean：①{@link AlipayClientHolder}（持有底层 SDK Client）；②{@link AlipayPayService}（交易门面）；
 * ③{@link AlipayCallbackService}（回调验签）。业务模块只需依赖后两者，{@link AlipayClient} 不直接对外暴露。
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

    @Bean
    public AlipayPayService alipayPayService() {
        return new AlipayPayServiceImpl();
    }

    @Bean
    public AlipayCallbackService alipayCallbackService() {
        return new AlipayCallbackServiceImpl();
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

