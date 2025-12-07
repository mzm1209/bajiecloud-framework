package com.bajiezu.cloud.framework.web.core.handler;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.web.core.util.WebFrameworkUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应结果（ResponseBody）处理器
 * <p>
 * 不同于在网上看到的很多文章，会选择自动将 Controller 返回结果包上 {@link CommonResult}， 在 onemall 中，是 Controller 在返回时，主动自己包上
 * {@link CommonResult}。 原因是，GlobalResponseBodyHandler 本质上是 AOP，它不应该改变 Controller 返回的数据结构
 * <p>
 * 目前，GlobalResponseBodyHandler 的主要作用是，记录 Controller 的返回结果，
 */
@ControllerAdvice
public class GlobalResponseBodyHandler<T> implements ResponseBodyAdvice<CommonResult<T>> {

  @Override
  @SuppressWarnings("NullableProblems") // 避免 IDEA 警告
  public boolean supports(MethodParameter returnType, Class converterType) {
    if (returnType.getMethod() == null) {
      return false;
    }
    // 只拦截返回结果为 CommonResult 类型
    return returnType.getMethod().getReturnType() == CommonResult.class;
  }

  @Override
  public CommonResult<T> beforeBodyWrite(CommonResult<T> body, MethodParameter returnType,
      MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request, ServerHttpResponse response) {
    // 记录 Controller 结果
    WebFrameworkUtils.setCommonResult(((ServletServerHttpRequest) request).getServletRequest(),
        body);
    return body;
  }

}
