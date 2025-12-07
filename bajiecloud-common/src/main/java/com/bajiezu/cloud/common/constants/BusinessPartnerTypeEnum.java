package com.bajiezu.cloud.common.constants;

import cn.hutool.core.util.ArrayUtil;
import com.bajiezu.cloud.common.type.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 合作商类型枚举
 */
@AllArgsConstructor
@Getter
public enum BusinessPartnerTypeEnum implements ArrayValuable<Integer> {

  PLATFORM(1, "平台"),
  MERCHANT(2, "商户"),
  SUPPLIER(3, "供应商"),
  LAW_FIRM(4, "司法律所"),
  THIRD_PARTY_COLLECTION(5, "委外催收"),
  ASSIGNMENT_OF_DEBT(6, "债权转让");


  public static final Integer[] ARRAYS = Arrays.stream(values())
      .map(BusinessPartnerTypeEnum::getValue)
      .toArray(Integer[]::new);

  /**
   * 类型
   */
  private final Integer value;
  /**
   * 类型名
   */
  private final String name;

  public static BusinessPartnerTypeEnum valueOf(Integer value) {
    return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value),
        BusinessPartnerTypeEnum.values());
  }

  @Override
  public Integer[] array() {
    return ARRAYS;
  }
}