package com.bajiezu.cloud.common.util.string;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

/**
 * 字符串工具类
 */
@UtilityClass
public class StrUtils {

  // ==================== 常用常量 ====================

  /**
   * 空字符串
   */
  public static final String EMPTY = "";

  /**
   * 空格
   */
  public static final String SPACE = " ";

  /**
   * 下划线
   */
  public static final String UNDERLINE = "_";

  /**
   * 点号
   */
  public static final String DOT = ".";

  /**
   * 逗号
   */
  public static final String COMMA = ",";

  /**
   * 冒号
   */
  public static final String COLON = ":";

  /**
   * 分号
   */
  public static final String SEMICOLON = ";";

  /**
   * 问号
   */
  public static final String QUESTION_MARK = "?";

  /**
   * 等号
   */
  public static final String EQUAL = "=";

  /**
   * 连接符
   */
  public static final String DASH = "-";

  /**
   * 斜杠
   */
  public static final String SLASH = "/";

  /**
   * 反斜杠
   */
  public static final String BACKSLASH = "\\";

  // ==================== 常用方法 ====================

  /**
   * 判断字符串是否为 null 或空字符串
   *
   * @param str 待判断字符串
   * @return 是否为空
   */
  public static boolean isEmpty(CharSequence str) {
    return StrUtil.isEmpty(str);
  }

  /**
   * 判断字符串是否不为 null 且不为空字符串
   *
   * @param str 待判断字符串
   * @return 是否不为空
   */
  public static boolean isNotEmpty(CharSequence str) {
    return StrUtil.isNotEmpty(str);
  }

  /**
   * 判断字符串是否为 null、空字符串或空白字符
   *
   * @param str 待判断字符串
   * @return 是否为空白
   */
  public static boolean isBlank(CharSequence str) {
    return StrUtil.isBlank(str);
  }

  /**
   * 判断字符串是否不为 null、空字符串且不为空白字符
   *
   * @param str 待判断字符串
   * @return 是否不为空白
   */
  public static boolean isNotBlank(CharSequence str) {
    return StrUtil.isNotBlank(str);
  }

  /**
   * 驼峰转下划线
   *
   * @param str 待转换字符串
   * @return 转换后的下划线格式字符串
   */
  public static String camelToUnderline(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }

    int length = str.length();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      char c = str.charAt(i);
      if (Character.isUpperCase(c) && i > 0) {
        sb.append(UNDERLINE);
      }
      sb.append(Character.toLowerCase(c));
    }
    return sb.toString();
  }

  /**
   * 下划线转驼峰
   *
   * @param str 待转换字符串
   * @return 转换后的驼峰格式字符串
   */
  public static String underlineToCamel(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }

    String lowerCaseStr = str.toString().toLowerCase();
    int length = lowerCaseStr.length();
    StringBuilder sb = new StringBuilder(length);
    boolean nextUpperCase = false;

    for (int i = 0; i < length; i++) {
      char c = lowerCaseStr.charAt(i);
      if (c == UNDERLINE.charAt(0)) {
        nextUpperCase = true;
      } else {
        if (nextUpperCase) {
          sb.append(Character.toUpperCase(c));
          nextUpperCase = false;
        } else {
          sb.append(c);
        }
      }
    }

    return sb.toString();
  }


  /**
   * 字符串转小写
   *
   * @param str 待转换字符串
   * @return 小写字符串
   */
  public static String toLowerCase(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }
    return str.toString().toLowerCase();
  }

  /**
   * 字符串转大写
   *
   * @param str 待转换字符串
   * @return 大写字符串
   */
  public static String toUpperCase(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }
    return str.toString().toUpperCase();
  }

  /**
   * 截取字符串的前n个字符
   *
   * @param str    待截取字符串
   * @param length 截取长度
   * @return 截取后的字符串
   */
  public static String substring(CharSequence str, int length) {
    if (isEmpty(str)) {
      return EMPTY;
    }
    if (length < 0) {
      length = 0;
    }
    if (str.length() <= length) {
      return str.toString();
    }
    return str.toString().substring(0, length);
  }


  /**
   * 去除字符串两端的空白字符
   *
   * @param str 待处理字符串
   * @return 去除空白后的字符串
   */
  public static String trim(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }
    return str.toString().trim();
  }

  /**
   * 去除字符串左边的空白字符
   *
   * @param str 待处理字符串
   * @return 去除左边空白后的字符串
   */
  public static String trimStart(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }
    int length = str.length();
    int start = 0;
    while (start < length && Character.isWhitespace(str.charAt(start))) {
      start++;
    }
    return str.toString().substring(start);
  }

  /**
   * 去除字符串右边的空白字符
   *
   * @param str 待处理字符串
   * @return 去除右边空白后的字符串
   */
  public static String trimEnd(CharSequence str) {
    if (isEmpty(str)) {
      return EMPTY;
    }
    int end = str.length();
    while (end > 0 && Character.isWhitespace(str.charAt(end - 1))) {
      end--;
    }
    return str.toString().substring(0, end);
  }

  /**
   * 重复字符串
   *
   * @param str   待重复字符串
   * @param count 重复次数
   * @return 重复后的字符串
   */
  public static String repeat(CharSequence str, int count) {
    if (isEmpty(str) || count <= 0) {
      return EMPTY;
    }
    return StrUtil.repeat(str, count);
  }

  /**
   * 分割字符串
   *
   * @param str       待分割字符串
   * @param separator 分隔符
   * @return 分割后的字符串数组
   */
  public static String[] split(CharSequence str, CharSequence separator) {
    if (isEmpty(str)) {
      return new String[0];
    }
    return StrUtil.split(str, separator).toArray(new String[0]);
  }

  /**
   * 连接字符串数组
   *
   * @param strs      字符串数组
   * @param separator 分隔符
   * @return 连接后的字符串
   */
  public static String join(CharSequence[] strs, CharSequence separator) {
    if (strs == null || strs.length == 0) {
      return EMPTY;
    }
    return StrUtil.join(separator, strs);
  }

  /**
   * 连接字符串集合
   *
   * @param strs      字符串集合
   * @param separator 分隔符
   * @return 连接后的字符串
   */
  public static String join(Iterable<? extends CharSequence> strs, CharSequence separator) {
    if (strs == null) {
      return EMPTY;
    }
    return StrUtil.join(separator, strs);
  }
}