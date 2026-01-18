package com.bajiezu.cloud.common.constants;

import cn.hutool.core.util.ArrayUtil;
import com.bajiezu.cloud.common.type.ArrayValuable;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum implements ArrayValuable<Integer> {

  CUSTOMER(1, "客户"), // 面向 c 端，普通用户
  MEMBER(2, "管理员"), // 面向 b 端，管理后台

  SYSTEM(3, "系统用户"); // 系统发起的请求，无需登录认证

  public static final Integer[] ARRAYS = Arrays.stream(values()).map(UserTypeEnum::getValue)
      .toArray(Integer[]::new);

  /**
   * 类型
   */
  private final Integer value;
  /**
   * 类型名
   */
  private final String name;

  public static UserTypeEnum valueOf(Integer value) {
    return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value),
        UserTypeEnum.values());
  }

  @Override
  public Integer[] array() {
    return ARRAYS;
  }
}