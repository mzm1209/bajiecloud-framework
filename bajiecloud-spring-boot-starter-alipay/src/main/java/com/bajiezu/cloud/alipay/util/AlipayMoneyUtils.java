package com.bajiezu.cloud.alipay.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额单位换算：平台内部 1 元 = 10000 ↔ 支付宝接口 1 元 = 100 分（字符串元，2 位小数）。
 *
 * <p>平台侧账单/订单金额一律以 {@code Long}（万分位整数）流转；调用支付宝下单/退款时需要将其转换为 {@code String}
 * 形式的"元.分"（2 位小数，精度 0.01）；回调里的 {@code total_amount} 也是元字符串。
 */
public final class AlipayMoneyUtils {

    /** 平台单位与"分"之间的倍率：10000 / 100 = 100。 */
    private static final long PLATFORM_TO_CENT_RATIO = 100L;

    private static final BigDecimal CENT_PER_YUAN = new BigDecimal("100");

    private AlipayMoneyUtils() {
    }

    /**
     * 平台单位（1 元 = 10000）→ 支付宝接口 "元" 字符串（保留 2 位小数）。
     *
     * @param platformAmount 平台单位金额
     * @return 形如 {@code "12.34"} 的元字符串；入参为 {@code null} 时返回 {@code "0.00"}
     */
    public static String toYuanString(Long platformAmount) {
        if (platformAmount == null) {
            return "0.00";
        }
        long cent = platformAmount / PLATFORM_TO_CENT_RATIO;
        return new BigDecimal(cent).divide(CENT_PER_YUAN, 2, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     * 支付宝接口 "元" 字符串 → 平台单位（1 元 = 10000）。
     *
     * @param yuanString 形如 {@code "12.34"}；为 {@code null} / 空串时返回 0
     * @return 平台单位金额
     */
    public static long fromYuanString(String yuanString) {
        if (yuanString == null || yuanString.isEmpty()) {
            return 0L;
        }
        BigDecimal cent = new BigDecimal(yuanString).multiply(CENT_PER_YUAN).setScale(0, RoundingMode.HALF_UP);
        return cent.longValueExact() * PLATFORM_TO_CENT_RATIO;
    }
}
