package com.bajiezu.cloud.alipay.model;

import lombok.Builder;
import lombok.Data;

/**
 * alipay.trade.query 同步返回，用于回调丢包时主动兜底查询。
 */
@Data
@Builder
public class AlipayTradeStatus {

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 平台外部交易号。
     */
    private String outTradeNo;

    /**
     * 交易状态：WAIT_BUYER_PAY / TRADE_CLOSED / TRADE_SUCCESS / TRADE_FINISHED。
     */
    private String tradeStatus;

    /**
     * 交易金额（平台单位，1 元 = 10000）。
     */
    private Long totalAmount;
}
