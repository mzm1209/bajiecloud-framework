package com.bajiezu.cloud.alipay.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 退款同步返回。
 */
@Data
@Builder
public class AlipayRefundResult {

    /**
     * 是否成功（支付宝 {@code code=10000} 视为成功）。
     */
    private boolean success;

    /**
     * 支付宝错误子码，失败时回填。
     */
    private String subCode;

    /**
     * 支付宝错误描述，失败时回填。
     */
    private String subMsg;

    /**
     * 原交易号。
     */
    private String outTradeNo;

    /**
     * 退款单号（{@code out_request_no}）。
     */
    private String outRequestNo;

    /**
     * 支付宝交易号。
     */
    private String tradeNo;

    /**
     * 退款实际到账金额（平台单位）。
     */
    private Long refundFee;

    /**
     * 退款支付完成时间。
     */
    private Date gmtRefundPay;
}
