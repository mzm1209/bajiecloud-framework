package com.bajiezu.cloud.alipay;

import com.alipay.api.AlipayClient;

/**
 * 支付宝 {@link AlipayClient} 持有者。
 *
 * <p>同时持有小程序端与支付商户端两个 Client 实例，业务侧通过本类拿到 {@link AlipayClient} 后
 * 直接调用 SDK；Client 实例由 {@code AlipayAutoConfiguration} 在启动时构建，并在 Nacos 配置变更
 * 时整体替换。引用通过 {@code volatile} 保证可见性。
 *
 * <p>多商户演进路径：把 {@code payClient} 替换为 {@code Map<String, AlipayClient>}，
 * 提供 {@code payClient(String appId)} 方法即可，调用方代码无需改动。
 */
public class AlipayClientHolder {

    private volatile AlipayClient miniappClient;
    private volatile AlipayClient payClient;

    /**
     * 获取小程序端 Client。可能为 {@code null}（当 alipay.miniapp 配置缺失时）。
     */
    public AlipayClient miniappClient() {
        return miniappClient;
    }

    /**
     * 获取支付商户端 Client。可能为 {@code null}（当 alipay.pay 配置缺失时）。
     */
    public AlipayClient payClient() {
        return payClient;
    }

    /**
     * 整体替换两个 Client 实例。Nacos 配置变更时由 AutoConfiguration 调用。
     *
     * <p>老实例由 GC 自然回收，无静态状态需要清理。
     */
    public void refresh(AlipayClient miniappClient, AlipayClient payClient) {
        this.miniappClient = miniappClient;
        this.payClient = payClient;
    }
}
