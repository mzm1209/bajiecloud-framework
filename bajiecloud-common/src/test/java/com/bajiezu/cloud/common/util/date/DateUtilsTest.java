package com.bajiezu.cloud.common.util.date;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

  @Test
  void formatLocaleDateTime() {

    Assertions.assertNull(DateUtils.formatLocaleDateTime(null, DateUtils.YYYY_MM_DD));
    String timeStr = "251221233751164";
    LocalDateTime date = DateUtils.parseDateTimeFormat(timeStr, DateUtils.YYMMDDHHMMSSSSS);
    Assertions.assertNotNull(date);
    Assertions.assertEquals("2025-12-21 23:37:51:164",
        DateUtils.formatLocaleDateTime(date, DateUtils.YYYY_MM_DD_HH_MM_SS_SSS));
    Assertions.assertEquals("2025-12-21 23:37:51",
        DateUtils.formatLocaleDateTime(date, DateUtils.YYYY_MM_DD_HH_MM_SS));
    Assertions.assertEquals("2025-12-21",
        DateUtils.formatLocaleDateTime(date, DateUtils.YYYY_MM_DD));


  }
}