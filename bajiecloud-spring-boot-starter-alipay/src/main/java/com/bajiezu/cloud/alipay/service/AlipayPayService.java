package com.bajiezu.cloud.alipay.service;

import com.bajiezu.cloud.alipay.model.AlipayCreateTradeResult;
import com.bajiezu.cloud.alipay.model.AlipayRefundQueryResult;
import com.bajiezu.cloud.alipay.model.AlipayRefundResult;
import com.bajiezu.cloud.alipay.model.AlipayTradeStatus;
import com.bajiezu.cloud.alipay.model.JsapiPayRequest;
import com.bajiezu.cloud.alipay.model.RefundRequest;

/**
 * 支付宝交易能力门面。
 *
 * <p>仅暴露业务侧需要的 5 个动作：JSAPI 下单 / 查询 / 退款 / 退款查询 / 关单。所有方法在失败时抛
 * {@link com.bajiezu.cloud.alipay.exception.AlipayException}。
 */
public interface AlipayPayService {

    /**
     * alipay.trade.create —— JSAPI 小程序主动下单，返回支付宝交易号。
     */
    AlipayCreateTradeResult createJsapiPay(JsapiPayRequest req);

    /**
     * alipay.trade.query —— 主动查询订单交易状态（回调丢包兜底用）。
     */
    AlipayTradeStatus queryTrade(String outTradeNo);

    /**
     * alipay.trade.refund —— 退款。
     */
    AlipayRefundResult refund(RefundRequest req);

    /**
     * alipay.trade.fastpay.refund.query —— 退款结果查询。
     */
    AlipayRefundQueryResult queryRefund(String outTradeNo, String outRequestNo);

    /**
     * alipay.trade.close —— 关闭未支付订单。
     *
     * @return 是否成功关闭；交易已支付时返回 {@code false}
     */
    boolean closeTrade(String outTradeNo);
}
