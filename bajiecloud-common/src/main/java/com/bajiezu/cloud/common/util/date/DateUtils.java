package com.bajiezu.cloud.common.util.date;

import cn.hutool.core.date.LocalDateTimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

/**
 * 时间工具类
 */
@UtilityClass
public class DateUtils {

  /**
   * 时区 - 默认
   */
  public static final String TIME_ZONE_DEFAULT = "GMT+8";

  /**
   * 秒转换成毫秒
   */
  public static final long SECOND_MILLIS = 1000;

  /**
   * 默认的时间格式
   */
  public static final String YYYY_MM_DD = "yyyy-MM-dd";

  /**
   * 带时分秒的时间格式
   */
  public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

  /**
   * 带毫秒的时间格式
   */
  public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";

  /**
   * 此处一般是用来生成各种业务code使用的时间格式
   */
  public static final String YYMMDDHHMMSSSSS = "yyMMddHHmmssSSS";


  private static final Map<String, DateTimeFormatter> DATE_FORMATTER_MAP = new HashMap<>();

  static {
    DATE_FORMATTER_MAP.put(YYYY_MM_DD, DateTimeFormatter.ofPattern(YYYY_MM_DD));
    DATE_FORMATTER_MAP.put(YYYY_MM_DD_HH_MM_SS, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    DATE_FORMATTER_MAP.put(YYYY_MM_DD_HH_MM_SS_SSS,
        DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS));
    DATE_FORMATTER_MAP.put(YYMMDDHHMMSSSSS, DateTimeFormatter.ofPattern(YYMMDDHHMMSSSSS));
  }


  /**
   * 将 LocalDateTime 转换成 Date
   *
   * @param date LocalDateTime
   * @return LocalDateTime
   */
  public static Date of(LocalDateTime date) {
    if (date == null) {
      return null;
    }
    // 将此日期时间与时区相结合以创建 ZonedDateTime
    ZonedDateTime zonedDateTime = date.atZone(ZoneId.systemDefault());
    // 本地时间线 LocalDateTime 到即时时间线 Instant 时间戳
    Instant instant = zonedDateTime.toInstant();
    // UTC时间(世界协调时间,UTC + 00:00)转北京(北京,UTC + 8:00)时间
    return Date.from(instant);
  }

  /**
   * 格式化LocalDateTime
   *
   * @param date   LocalDateTime
   * @param format 时间格式
   * @return 时间字符串
   */
  public static String formatLocaleDateTime(LocalDateTime date, String format) {
    if (date == null) {
      return null;
    }
    return LocalDateTimeUtil.format(date, DATE_FORMATTER_MAP.get(format));
  }

  /**
   * 格式化LocalDate
   *
   * @param date   LocalDate
   * @param format 时间格式
   * @return 时间字符串
   */
  public static String formatLocaleDate(LocalDate date, String format) {
    if (date == null) {
      return null;
    }
    return LocalDateTimeUtil.format(date, DATE_FORMATTER_MAP.get(format));
  }

  /**
   * 将 Date 转换成 LocalDateTime
   *
   * @param date Date
   * @return LocalDateTime
   */
  public static LocalDateTime of(Date date) {
    if (date == null) {
      return null;
    }
    // 转为时间戳
    Instant instant = date.toInstant();
    // UTC时间(世界协调时间,UTC + 00:00)转北京(北京,UTC + 8:00)时间
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  public static Date addTime(Duration duration) {
    return new Date(System.currentTimeMillis() + duration.toMillis());
  }

  public static boolean isExpired(LocalDateTime time) {
    LocalDateTime now = LocalDateTime.now();
    return now.isAfter(time);
  }

  /**
   * 创建指定时间
   *
   * @param year  年
   * @param month 月
   * @param day   日
   * @return 指定时间
   */
  public static Date buildTime(int year, int month, int day) {
    return buildTime(year, month, day, 0, 0, 0);
  }

  /**
   * 创建指定时间
   *
   * @param year   年
   * @param month  月
   * @param day    日
   * @param hour   小时
   * @param minute 分钟
   * @param second 秒
   * @return 指定时间
   */
  public static Date buildTime(int year, int month, int day,
      int hour, int minute, int second) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, 0); // 一般情况下，都是 0 毫秒
    return calendar.getTime();
  }

  public static Date max(Date a, Date b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    return a.compareTo(b) > 0 ? a : b;
  }

  public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
    if (a == null) {
      return b;
    }
    if (b == null) {
      return a;
    }
    return a.isAfter(b) ? a : b;
  }

  /**
   * 是否今天
   *
   * @param date 日期
   * @return 是否
   */
  public static boolean isToday(LocalDateTime date) {
    return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
  }

  /**
   * 是否昨天
   *
   * @param date 日期
   * @return 是否
   */
  public static boolean isYesterday(LocalDateTime date) {
    return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now().minusDays(1));
  }

  /**
   * 解析时间
   *
   * @param dateStr 时间字符串
   * @param format  时间格式
   * @return LocalDateTime
   */
  public static LocalDateTime parseDateTimeFormat(String dateStr, String format) {
    if (dateStr == null) {
      return null;
    }
    return LocalDateTime.parse(dateStr, DATE_FORMATTER_MAP.get(format));
  }

  /**
   * 解析时间
   *
   * @param dateStr 时间字符串
   * @param format  时间格式
   * @return LocalDate
   */
  public static LocalDate parseDateFormat(String dateStr, String format) {
    if (dateStr == null) {
      return null;
    }
    return LocalDate.parse(dateStr, DATE_FORMATTER_MAP.get(format));
  }

  /**
   * 将Date设置为当天的开始时间（00:00:00.000）
   * 使用Java 8+ LocalDateTime实现（推荐）
   *
   * @param date 原始日期
   * @return 当天开始时间的Date对象
   */
  public static Date getBeginOfDayWithJava8(Date date) {
    if (date == null) {
      return null;
    }

    LocalDate localDate = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    LocalDateTime startOfDay = localDate.atStartOfDay();

    return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
  }

  /**
   * 将Date设置为当天的结束时间（23:59:59.000）
   * 使用Java 8+ LocalDateTime实现（推荐）
   *
   * @param date 原始日期
   * @return 当天结束时间的Date对象
   */
  public static Date getEndOfDayWithJava8(Date date) {
    if (date == null) {
      return null;
    }

    LocalDate localDate = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

    LocalDateTime endOfDay = localDate.atTime(23, 59, 59, 0);

    return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
  }

}