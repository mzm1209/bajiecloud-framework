package com.bajiezu.cloud.common.web.validation;

import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.validation.ValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MobileValidator implements ConstraintValidator<Mobile, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // 如果手机号为空，默认不校验，即校验通过
    if (StrUtil.isEmpty(value)) {
      return true;
    }
    // 校验手机
    return ValidationUtils.isMobile(value);
  }

}