package com.bajiezu.cloud.alipay.service;

import java.util.Map;

/**
 * 支付宝异步回调验签与解析。
 *
 * <p>业务侧收到支付宝 form-data 回调后，第一步调 {@link #verifyAndParse} 验签 + 解析为 {@code Map}，失败直接
 * 抛 {@link com.bajiezu.cloud.alipay.exception.AlipayException}；验签通过后业务侧再依赖 {@code trade_status}、
 * {@code total_amount} 等字段做进一步处理。
 */
public interface AlipayCallbackService {

    /**
     * 验签并返回 params。
     *
     * @param params 支付宝回调原始参数（已经过 servlet 解析的 form 键值对）
     * @param type   回调类型，决定用哪套公钥（支付 / 退款共用 pay 公钥，预留枚举位做多商户扩展）
     * @return 已通过验签的参数字典
     */
    Map<String, String> verifyAndParse(Map<String, String> params, CallbackType type);

    enum CallbackType {
        /** 支付结果通知。 */
        PAY,
        /** 退款结果通知。 */
        REFUND
    }
}
