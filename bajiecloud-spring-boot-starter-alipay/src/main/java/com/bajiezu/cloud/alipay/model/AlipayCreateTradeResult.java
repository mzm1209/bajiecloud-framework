package com.bajiezu.cloud.alipay.model;

import lombok.Builder;
import lombok.Data;

/**
 * alipay.trade.create 同步返回。
 *
 * <p>对小程序 JSAPI 支付，业务方拿到 {@link #tradeNo} 后下发给前端调起 {@code my.tradePay({tradeNo})}。
 */
@Data
@Builder
public class AlipayCreateTradeResult {

    /**
     * 平台外部交易号（回显 {@code out_trade_no}）。
     */
    private String outTradeNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;
}
