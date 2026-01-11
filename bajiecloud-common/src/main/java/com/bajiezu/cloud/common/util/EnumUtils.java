package com.bajiezu.cloud.common.util;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * 枚举工具类
 */
@UtilityClass
public class EnumUtils {

  // 缓存所有CodeEnum枚举的映射关系
  private static final Map<Class<? extends CodeEnum>, Map<Integer, ? extends CodeEnum>> ENUM_CACHE = new ConcurrentHashMap<>();

  /**
   * 根据code获取枚举
   */
  @SuppressWarnings("unchecked")
  public static <E extends CodeEnum> E getByCode(Class<E> enumClass, int code) {
    Map<Integer, ? extends CodeEnum> map = ENUM_CACHE.computeIfAbsent(enumClass, clazz -> {
      E[] enums = enumClass.getEnumConstants();
      return Arrays.stream(enums)
          .collect(Collectors.toMap(CodeEnum::getCode, e -> e));
    });

    return (E) map.get(code);
  }

  /**
   * 根据code获取枚举，带默认值
   */
  public static <E extends CodeEnum> E getByCode(Class<E> enumClass, int code, E defaultValue) {
    E result = getByCode(enumClass, code);
    return result != null ? result : defaultValue;
  }

  /**
   * 根据code获取枚举，找不到时抛出异常
   */
  public static <E extends CodeEnum> E getByCodeRequired(Class<E> enumClass, int code) {
    E result = getByCode(enumClass, code);
    if (result == null) {
      throw new IllegalArgumentException(
          String.format(" %s 中不存在code=%d的值", enumClass.getSimpleName(), code)
      );
    }
    return result;
  }


  public static boolean isValidCode(Class<? extends CodeEnum> enumClass, int code) {
    return getByCode(enumClass, code) != null;
  }


  /**
   * 清除缓存（用于热部署等场景）
   */
  public static void clearCache() {
    ENUM_CACHE.clear();
  }
}