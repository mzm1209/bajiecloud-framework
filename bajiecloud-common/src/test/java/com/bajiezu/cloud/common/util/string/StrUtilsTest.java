package com.bajiezu.cloud.common.util.string;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StrUtilsTest {

  @Test
  void isEmpty() {
    assertTrue(StrUtils.isEmpty(null));
    assertTrue(StrUtils.isEmpty(""));
    assertFalse(StrUtils.isEmpty(" "));
    assertFalse(StrUtils.isEmpty("a"));
  }

  @Test
  void isNotEmpty() {
    assertFalse(StrUtils.isNotEmpty(null));
    assertFalse(StrUtils.isNotEmpty(""));
    assertTrue(StrUtils.isNotEmpty(" "));
    assertTrue(StrUtils.isNotEmpty("a"));
  }

  @Test
  void isBlank() {
    assertTrue(StrUtils.isBlank(null));
    assertTrue(StrUtils.isBlank(""));
    assertTrue(StrUtils.isBlank(" "));
    assertFalse(StrUtils.isBlank("a"));
  }

  @Test
  void isNotBlank() {
    assertFalse(StrUtils.isNotBlank(null));
    assertFalse(StrUtils.isNotBlank(""));
    assertFalse(StrUtils.isNotBlank(" "));
    assertTrue(StrUtils.isNotBlank("a"));
  }

  @Test
  void camelToUnderline() {
    assertEquals("a_b", StrUtils.camelToUnderline("aB"));
    assertEquals("a_b_c", StrUtils.camelToUnderline("aBC"));
  }

  @Test
  void underlineToCamel() {
    assertEquals("aB", StrUtils.underlineToCamel("a_b"));
    assertEquals("aBC", StrUtils.underlineToCamel("a_b_c"));
  }

  @Test
  void toLowerCase() {
    assertEquals("a", StrUtils.toLowerCase("A"));
    assertEquals("a_b", StrUtils.toLowerCase("A_B"));
  }

  @Test
  void toUpperCase() {
    assertEquals("A", StrUtils.toUpperCase("a"));
    assertEquals("A_B", StrUtils.toUpperCase("a_b"));
  }

  @Test
  void substring() {
    assertEquals("a", StrUtils.substring("a", 1));
    assertEquals("a_b", StrUtils.substring("a_b", 3));
  }



  @Test
  void trim() {
    assertEquals("a", StrUtils.trim(" a "));
  }

  @Test
  void trimStart() {
    assertEquals("a ", StrUtils.trimStart(" a "));
  }

  @Test
  void trimEnd() {
    assertEquals(" a", StrUtils.trimEnd(" a "));
  }

  @Test
  void repeat() {
    assertEquals("a", StrUtils.repeat("a", 1));
    assertEquals("aa", StrUtils.repeat("a", 2));
  }


}