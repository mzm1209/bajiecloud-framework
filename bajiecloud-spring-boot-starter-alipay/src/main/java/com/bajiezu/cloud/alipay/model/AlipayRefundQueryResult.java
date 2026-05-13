package com.bajiezu.cloud.alipay.model;

import lombok.Builder;
import lombok.Data;

/**
 * alipay.trade.fastpay.refund.query 同步返回。
 */
@Data
@Builder
public class AlipayRefundQueryResult {

    /**
     * 是否查到记录（退款成功时 refund_amount 非空）。
     */
    private boolean refundExists;

    /**
     * 退款金额（平台单位）。
     */
    private Long refundAmount;

    /**
     * 退款状态。
     */
    private String refundStatus;
}
