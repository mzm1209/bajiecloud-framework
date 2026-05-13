package com.bajiezu.cloud.alipay.model;

import lombok.Builder;
import lombok.Data;

/**
 * 支付宝 JSAPI 下单请求（alipay.trade.create）。
 *
 * <p>金额字段使用平台内部单位（1 元 = 10000），由 SDK 内部换算为支付宝要求的"元字符串"。
 */
@Data
@Builder
public class JsapiPayRequest {

    /**
     * 平台侧外部交易号，建议取 {@code PaymentItem.payItemNo}，在渠道侧保持唯一。
     */
    private String outTradeNo;

    /**
     * 订单总金额（平台单位，1 元 = 10000）。
     */
    private Long totalAmount;

    /**
     * 商品标题 / 交易标题，支付宝限长 256。
     */
    private String subject;

    /**
     * 交易描述，支付宝限长 400，可选。
     */
    private String body;

    /**
     * 买家支付宝 openId（由 customer 服务根据 customerId 反查得到）。
     */
    private String buyerOpenId;

    /**
     * 异步通知地址，为空时使用 {@code AlipayProperties.pay.notifyUrl} 默认值。
     */
    private String notifyUrl;

    /**
     * 业务回调透传参数，支付宝原样回传在通知报文里，建议放 payItemNo 便于回调侧快速定位。
     */
    private String passbackParams;
}
