package com.bajiezu.cloud.alipay.model;

import lombok.Builder;
import lombok.Data;

/**
 * 支付宝退款请求（alipay.trade.refund）。
 */
@Data
@Builder
public class RefundRequest {

    /**
     * 原交易号（{@code PaymentItem.payItemNo}）。
     */
    private String outTradeNo;

    /**
     * 退款单号（平台 {@code pay_refund_record.refund_no}，作为支付宝 {@code out_request_no} 实现幂等）。
     */
    private String outRequestNo;

    /**
     * 退款金额（平台单位，1 元 = 10000）。
     */
    private Long refundAmount;

    /**
     * 退款原因，限长 256，可选。
     */
    private String refundReason;
}
