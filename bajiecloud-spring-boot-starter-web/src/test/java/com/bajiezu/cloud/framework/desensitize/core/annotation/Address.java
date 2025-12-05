package com.bajiezu.cloud.framework.desensitize.core.annotation;

import com.bajiezu.cloud.framework.desensitize.core.DesensitizeTest;
import com.bajiezu.cloud.framework.desensitize.core.base.annotation.DesensitizeBy;
import com.bajiezu.cloud.framework.desensitize.core.handler.AddressHandler;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.*;

/**
 * 地址
 * <p>
 * 用于 {@link DesensitizeTest} 测试使用
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@DesensitizeBy(handler = AddressHandler.class)
public @interface Address {

    String replacer() default "*";

}
