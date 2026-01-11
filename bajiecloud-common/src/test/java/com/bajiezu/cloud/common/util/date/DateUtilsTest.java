package com.bajiezu.cloud.common.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

  @Test
  void calculateDaysBetween() {
    LocalDateTime startDate = LocalDateTime.of(2025, 12, 21, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2025, 12, 25, 0, 0, 0);
    Assertions.assertEquals(4, DateUtils.calculateDaysBetween(startDate, endDate));
    LocalDate startDate1 = LocalDate.of(2025, 12, 21);
    LocalDate endDate1 = LocalDate.of(2025, 12, 25);
    Assertions.assertEquals(4, DateUtils.calculateDaysBetween(startDate1, endDate1));
    Date startDate2 = DateUtils.buildTime(2025, 12, 21, 0, 0, 0);
    Date endDate2 = DateUtils.buildTime(2025, 12, 25, 0, 0, 0);
    Assertions.assertEquals(4, DateUtils.calculateDaysBetween(startDate2, endDate2));

  }

}