package com.bajiezu.cloud.alipay.enums;

import lombok.Getter;

/**
 * 支付宝业务错误码 → 平台错误语义的映射。
 *
 * <p>该枚举只承担"已知错误码的解释 + 是否可重试的判定"两件事，不直接抛给前端；业务侧若需要把 SDK 错误转成
 * 平台 ResultCode，应在 catch {@link com.bajiezu.cloud.alipay.exception.AlipayException} 时按 {@link #getByCode}
 * 查表后再决定。
 *
 * <p>未列出的错误码 → 走 {@link #UNKNOWN}，默认不可重试，由业务方记录告警。
 */
@Getter
public enum AlipayErrorCode {

    /** 系统繁忙，可重试。 */
    SYSTEM_BUSY("ACQ.SYSTEM_ERROR", "系统繁忙", true),
    /** 渠道侧网关繁忙，可重试。 */
    INVOKE_TIMEOUT("ACQ.INVOKE_TIMEOUT", "调用支付宝超时", true),
    /** 交易已存在，幂等命中，按成功处理。 */
    TRADE_HAS_SUCCESS("ACQ.TRADE_HAS_SUCCESS", "交易已被成功支付", false),
    TRADE_HAS_CLOSE("ACQ.TRADE_HAS_CLOSE", "交易已被关闭", false),
    /** 用户余额不足等业务态失败，不可重试。 */
    BUYER_BALANCE_NOT_ENOUGH("ACQ.BUYER_BALANCE_NOT_ENOUGH", "买家余额不足", false),
    BUYER_BANKCARD_BALANCE_NOT_ENOUGH("ACQ.BUYER_BANKCARD_BALANCE_NOT_ENOUGH", "银行卡余额不足", false),
    /** 退款相关。 */
    REFUND_AMT_NOT_EQUAL_TOTAL("ACQ.REFUND_AMT_NOT_EQUAL_TOTAL", "退款金额超出可退余额", false),
    TRADE_NOT_EXIST("ACQ.TRADE_NOT_EXIST", "交易不存在", false),
    /** 验签 / 参数错误，永久失败。 */
    INVALID_PARAMETER("ACQ.INVALID_PARAMETER", "参数无效", false),
    /** 兜底：未知错误码。 */
    UNKNOWN("UNKNOWN", "未知错误", false),
    ;

    private final String code;
    private final String desc;
    private final boolean retryable;

    AlipayErrorCode(String code, String desc, boolean retryable) {
        this.code = code;
        this.desc = desc;
        this.retryable = retryable;
    }

    public static AlipayErrorCode getByCode(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (AlipayErrorCode value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
