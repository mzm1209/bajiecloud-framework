package com.bajiezu.cloud.alipay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.bajiezu.cloud.alipay.AlipayProperties;
import com.bajiezu.cloud.alipay.exception.AlipayException;
import com.bajiezu.cloud.alipay.service.AlipayCallbackService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 支付宝回调验签实现。
 *
 * <p>一期保留的"只校验金额、不验签"路径在本批退出；所有通过本服务的回调都必须先通过 RSA2 验签。
 */
@Slf4j
public class AlipayCallbackServiceImpl implements AlipayCallbackService {

    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String DEFAULT_SIGN_TYPE = "RSA2";

    @Resource
    private AlipayProperties alipayProperties;

    @Override
    public Map<String, String> verifyAndParse(Map<String, String> params, CallbackType type) {
        if (params == null || params.isEmpty()) {
            throw new AlipayException("alipay.callback.empty", "回调参数为空");
        }
        AlipayProperties.ClientConfig cfg = alipayProperties.getPay();
        if (cfg == null || StringUtils.isBlank(cfg.getAlipayPublicKey())) {
            throw new AlipayException("alipay.callback.public_key_missing", "未配置 alipay.pay.alipay-public-key");
        }
        String signType = StringUtils.defaultIfBlank(cfg.getSignType(), DEFAULT_SIGN_TYPE);
        try {
            boolean valid = AlipaySignature.rsaCheckV1(params, cfg.getAlipayPublicKey(),
                CHARSET_UTF_8, signType);
            if (!valid) {
                log.warn("[alipay] callback sign invalid, type={}, out_trade_no={}",
                    type, params.get("out_trade_no"));
                throw new AlipayException("alipay.callback.sign_invalid", "回调验签失败");
            }
        } catch (AlipayApiException e) {
            log.warn("[alipay] callback verify exception, type={}, code={}", type, e.getErrCode(), e);
            throw new AlipayException(e.getErrCode(), e.getErrMsg(), e);
        }
        return params;
    }
}
