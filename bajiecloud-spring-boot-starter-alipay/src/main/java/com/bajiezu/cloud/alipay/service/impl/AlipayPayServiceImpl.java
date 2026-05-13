package com.bajiezu.cloud.alipay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.bajiezu.cloud.alipay.AlipayClientHolder;
import com.bajiezu.cloud.alipay.AlipayProperties;
import com.bajiezu.cloud.alipay.exception.AlipayException;
import com.bajiezu.cloud.alipay.model.AlipayCreateTradeResult;
import com.bajiezu.cloud.alipay.model.AlipayRefundQueryResult;
import com.bajiezu.cloud.alipay.model.AlipayRefundResult;
import com.bajiezu.cloud.alipay.model.AlipayTradeStatus;
import com.bajiezu.cloud.alipay.model.JsapiPayRequest;
import com.bajiezu.cloud.alipay.model.RefundRequest;
import com.bajiezu.cloud.alipay.service.AlipayPayService;
import com.bajiezu.cloud.alipay.util.AlipayMoneyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付宝交易服务实现。
 *
 * <p>所有方法在 SDK 抛 {@link AlipayApiException} 或业务态失败时统一抛 {@link AlipayException}。
 */
@Slf4j
public class AlipayPayServiceImpl implements AlipayPayService {

    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String PRODUCT_CODE_JSAPI = "JSAPI_PAY";

    @Resource
    private AlipayClientHolder alipayClientHolder;

    @Resource
    private AlipayProperties alipayProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AlipayCreateTradeResult createJsapiPay(JsapiPayRequest req) {
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        String notifyUrl = StringUtils.defaultIfBlank(req.getNotifyUrl(), alipayProperties.getPay().getNotifyUrl());
        if (StringUtils.isNotBlank(notifyUrl)) {
            request.setNotifyUrl(notifyUrl);
        }
        ObjectNode biz = objectMapper.createObjectNode();
        biz.put("out_trade_no", req.getOutTradeNo());
        biz.put("total_amount", AlipayMoneyUtils.toYuanString(req.getTotalAmount()));
        biz.put("subject", req.getSubject());
        if (StringUtils.isNotBlank(req.getBody())) {
            biz.put("body", req.getBody());
        }
        biz.put("product_code", PRODUCT_CODE_JSAPI);
        if (alipayProperties.getMiniapp() != null && StringUtils.isNotBlank(alipayProperties.getMiniapp().getAppId())) {
            biz.put("op_app_id", alipayProperties.getMiniapp().getAppId());
        }
        biz.put("buyer_open_id", req.getBuyerOpenId());
        if (StringUtils.isNotBlank(req.getPassbackParams())) {
            biz.put("passback_params", req.getPassbackParams());
        }
        request.setBizContent(biz.toString());

        AlipayTradeCreateResponse response = execute(request, "alipay.trade.create");
        return AlipayCreateTradeResult.builder()
            .outTradeNo(response.getOutTradeNo())
            .tradeNo(response.getTradeNo())
            .build();
    }

    @Override
    public AlipayTradeStatus queryTrade(String outTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        ObjectNode biz = objectMapper.createObjectNode();
        biz.put("out_trade_no", outTradeNo);
        request.setBizContent(biz.toString());

        AlipayTradeQueryResponse response = execute(request, "alipay.trade.query");
        return AlipayTradeStatus.builder()
            .outTradeNo(response.getOutTradeNo())
            .tradeNo(response.getTradeNo())
            .tradeStatus(response.getTradeStatus())
            .totalAmount(AlipayMoneyUtils.fromYuanString(response.getTotalAmount()))
            .build();
    }

    @Override
    public AlipayRefundResult refund(RefundRequest req) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        ObjectNode biz = objectMapper.createObjectNode();
        biz.put("out_trade_no", req.getOutTradeNo());
        biz.put("out_request_no", req.getOutRequestNo());
        biz.put("refund_amount", AlipayMoneyUtils.toYuanString(req.getRefundAmount()));
        if (StringUtils.isNotBlank(req.getRefundReason())) {
            biz.put("refund_reason", req.getRefundReason());
        }
        request.setBizContent(biz.toString());

        AlipayTradeRefundResponse response = invoke(request, "alipay.trade.refund");
        boolean success = response.isSuccess();
        return AlipayRefundResult.builder()
            .success(success)
            .subCode(response.getSubCode())
            .subMsg(response.getSubMsg())
            .outTradeNo(response.getOutTradeNo())
            .outRequestNo(req.getOutRequestNo())
            .tradeNo(response.getTradeNo())
            .refundFee(AlipayMoneyUtils.fromYuanString(response.getRefundFee()))
            .gmtRefundPay(response.getGmtRefundPay())
            .build();
    }

    @Override
    public AlipayRefundQueryResult queryRefund(String outTradeNo, String outRequestNo) {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        ObjectNode biz = objectMapper.createObjectNode();
        biz.put("out_trade_no", outTradeNo);
        biz.put("out_request_no", outRequestNo);
        request.setBizContent(biz.toString());

        AlipayTradeFastpayRefundQueryResponse response = execute(request, "alipay.trade.fastpay.refund.query");
        boolean exists = StringUtils.isNotBlank(response.getRefundAmount());
        return AlipayRefundQueryResult.builder()
            .refundExists(exists)
            .refundAmount(exists ? AlipayMoneyUtils.fromYuanString(response.getRefundAmount()) : 0L)
            .refundStatus(response.getRefundStatus())
            .build();
    }

    @Override
    public boolean closeTrade(String outTradeNo) {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        ObjectNode biz = objectMapper.createObjectNode();
        biz.put("out_trade_no", outTradeNo);
        request.setBizContent(biz.toString());

        AlipayTradeCloseResponse response = invoke(request, "alipay.trade.close");
        return response.isSuccess();
    }

    /**
     * 执行 SDK 调用，{@code response.isSuccess()=false} 时抛 {@link AlipayException}。
     */
    private <T extends com.alipay.api.AlipayResponse> T execute(com.alipay.api.AlipayRequest<T> request, String api) {
        T response = invoke(request, api);
        if (!response.isSuccess()) {
            String code = StringUtils.defaultIfBlank(response.getSubCode(), response.getCode());
            String msg = StringUtils.defaultIfBlank(response.getSubMsg(), response.getMsg());
            log.warn("[alipay] api={} fail, code={}, msg={}", api, code, msg);
            throw new AlipayException(code, msg);
        }
        return response;
    }

    /**
     * 只做底层调用与 IO 异常包装，不判定 isSuccess —— 退款 / 关单允许业务侧基于失败码做下游决策。
     */
    private <T extends com.alipay.api.AlipayResponse> T invoke(com.alipay.api.AlipayRequest<T> request, String api) {
        AlipayClient client = alipayClientHolder.payClient();
        if (client == null) {
            throw new AlipayException("alipay.client.not_ready",
                "支付宝支付商户 Client 尚未初始化（请检查 alipay.pay 配置）");
        }
        try {
            return client.execute(request);
        } catch (AlipayApiException e) {
            log.warn("[alipay] api={} exception, code={}, msg={}", api, e.getErrCode(), e.getErrMsg(), e);
            throw new AlipayException(e.getErrCode(), e.getErrMsg(), e);
        }
    }
}
