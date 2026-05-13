package com.bajiezu.cloud.alipay.exception;

/**
 * 支付宝 SDK 调用统一异常。
 *
 * <p>所有来自 {@code AlipayClient.execute} 的 {@code AlipayApiException} 或业务态失败（返回 {@code code!=10000}）
 * 都转成本类型抛出，业务方只需捕获 {@link AlipayException} 即可。
 */
public class AlipayException extends RuntimeException {

    /**
     * 支付宝错误码：优先取 {@code sub_code}，缺省取 {@code code}。
     */
    private final String errorCode;

    /**
     * 支付宝错误描述：优先取 {@code sub_msg}，缺省取 {@code msg}。
     */
    private final String errorMsg;

    public AlipayException(String errorCode, String errorMsg) {
        super("[alipay] " + errorCode + ":" + errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public AlipayException(String errorCode, String errorMsg, Throwable cause) {
        super("[alipay] " + errorCode + ":" + errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
