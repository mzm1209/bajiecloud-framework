package com.bajiezu.cloud.framework.web.core.handler;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.FORBIDDEN;
import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.METHOD_NOT_ALLOWED;
import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.NOT_FOUND;
import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.UNAUTHORIZED;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.bajiezu.cloud.common.util.servlet.ServletUtils;
import com.bajiezu.cloud.common.web.exception.ServiceException;
import com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.framework.web.core.util.WebFrameworkUtils;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.util.concurrent.UncheckedExecutionException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * 全局异常处理器，将 Exception 翻译成 CommonResult + 对应的异常编号
 */
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 忽略的 ServiceException 错误提示，避免打印过多 logger
   */
  public static final Set<String> IGNORE_ERROR_MESSAGES = Collections.singleton("无效的刷新令牌");


  /**
   * 处理所有异常，主要是提供给 Filter 使用 因为 Filter 不走 SpringMVC 的流程，但是我们又需要兜底处理异常，所以这里提供一个全量的异常处理过程，保持逻辑统一。
   *
   * @param request 请求
   * @param ex      异常
   * @return 通用返回
   */
  public CommonResult<?> allExceptionHandler(HttpServletRequest request, Throwable ex) {
    if (ex instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
      return missingServletRequestParameterExceptionHandler(missingServletRequestParameterException);
    }
    if (ex instanceof MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
      return methodArgumentTypeMismatchExceptionHandler(methodArgumentTypeMismatchException);
    }
    if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
      return methodArgumentNotValidExceptionExceptionHandler(methodArgumentNotValidException);
    }
    if (ex instanceof BindException bindException) {
      return bindExceptionHandler(bindException);
    }
    if (ex instanceof ConstraintViolationException constraintViolationException) {
      return constraintViolationExceptionHandler(constraintViolationException);
    }
    if (ex instanceof ValidationException validationException) {
      return validationException(validationException);
    }
    if (ex instanceof MaxUploadSizeExceededException maxUploadSizeExceededException) {
      return maxUploadSizeExceededExceptionHandler(maxUploadSizeExceededException);
    }
    if (ex instanceof NoHandlerFoundException noHandlerFoundException) {
      return noHandlerFoundExceptionHandler(noHandlerFoundException);
    }
    if (ex instanceof HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
      return httpRequestMethodNotSupportedExceptionHandler(httpRequestMethodNotSupportedException);
    }
    if (ex instanceof HttpMediaTypeNotSupportedException mediaTypeNotSupportedException) {
      return httpMediaTypeNotSupportedExceptionHandler(mediaTypeNotSupportedException);
    }
    if (ex instanceof ServiceException serviceException) {
      return serviceExceptionHandler(serviceException);
    }
    if (ex instanceof AccessDeniedException ade) {
      return accessDeniedExceptionHandler(request, ade);
    }
    if (ex instanceof IllegalArgumentException illegalArgumentException) {
      return handleIllegalArgumentException(illegalArgumentException);
    }
    return defaultExceptionHandler(request, ex);
  }

  /**
   * 处理 SpringMVC 请求参数缺失
   * <p>
   * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
   */
  @ExceptionHandler(value = MissingServletRequestParameterException.class)
  public CommonResult<?> missingServletRequestParameterExceptionHandler(
      MissingServletRequestParameterException ex) {
    log.warn("[missingServletRequestParameterExceptionHandler]", ex);
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求参数缺失:%s", ex.getParameterName()));
  }

  /**
   * 处理 SpringMVC 请求参数类型错误
   * <p>
   * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public CommonResult<?> methodArgumentTypeMismatchExceptionHandler(
      MethodArgumentTypeMismatchException ex) {
    log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求参数类型错误:%s", ex.getMessage()));
  }

  /**
   * 处理 SpringMVC 参数校验不正确
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public CommonResult<?> methodArgumentNotValidExceptionExceptionHandler(
      MethodArgumentNotValidException ex) {
    log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
    // 获取 errorMessage
    String errorMessage = null;
    FieldError fieldError = ex.getBindingResult().getFieldError();
    if (fieldError == null) {
      // 组合校验，参考自 https://t.zsxq.com/3HVTx
      List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
      if (CollUtil.isNotEmpty(allErrors)) {
        errorMessage = allErrors.get(0).getDefaultMessage();
      }
    } else {
      errorMessage = fieldError.getDefaultMessage();
    }
    // 转换 CommonResult
    if (StrUtil.isEmpty(errorMessage)) {
      return CommonResult.error(BAD_REQUEST);
    }
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求参数不正确:%s", errorMessage));
  }

  /**
   * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
   */
  @ExceptionHandler(BindException.class)
  public CommonResult<?> bindExceptionHandler(BindException ex) {
    log.warn("[handleBindException]", ex);
    FieldError fieldError = ex.getFieldError();
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
  }

  /**
   * 处理 SpringMVC 请求参数类型错误
   * <p>
   * 例如说，接口上设置了 @RequestBody 实体中 xx 属性类型为 Integer，结果传递 xx 参数类型为 String
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @SuppressWarnings("PatternVariableCanBeUsed")
  public CommonResult<?> methodArgumentTypeInvalidFormatExceptionHandler(
      HttpMessageNotReadableException ex) {
    log.warn("[methodArgumentTypeInvalidFormatExceptionHandler]", ex);
    if (ex.getCause() instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
      return CommonResult.error(BAD_REQUEST.getCode(),
          String.format("请求参数类型错误:%s", invalidFormatException.getValue()));
    }
    if (StrUtil.startWith(ex.getMessage(), "Required request body is missing")) {
      return CommonResult.error(BAD_REQUEST.getCode(), "请求参数类型错误: request body 缺失");
    }
    return defaultExceptionHandler(ServletUtils.getRequest(), ex);
  }

  /**
   * 处理 Validator 校验不通过产生的异常
   */
  @ExceptionHandler(value = ConstraintViolationException.class)
  public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
    log.warn("[constraintViolationExceptionHandler]", ex);
    ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求参数不正确:%s", constraintViolation.getMessage()));
  }

  /**
   * 处理 Dubbo Consumer 本地参数校验时，抛出的 ValidationException 异常
   */
  @ExceptionHandler(value = ValidationException.class)
  public CommonResult<?> validationException(ValidationException ex) {
    log.warn("[constraintViolationExceptionHandler]", ex);
    // 无法拼接明细的错误信息，因为 Dubbo Consumer 抛出 ValidationException 异常时，是直接的字符串信息，且人类不可读
    return CommonResult.error(BAD_REQUEST);
  }

  /**
   * 处理上传文件过大异常
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public CommonResult<?> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex) {
    return CommonResult.error(BAD_REQUEST.getCode(), "上传文件过大，请调整后重试");
  }

  /**
   * 处理 SpringMVC 请求地址不存在
   * <p>
   * 注意，它需要设置如下两个配置项： 1. spring.mvc.throw-exception-if-no-handler-found 为 true 2.
   * spring.mvc.static-path-pattern 为 /statics/**
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public CommonResult<?> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
    log.warn("[noHandlerFoundExceptionHandler]", ex);
    return CommonResult.error(NOT_FOUND.getCode(),
        String.format("请求地址不存在:%s", ex.getRequestURL()));
  }

  /**
   * 处理 SpringMVC 请求方法不正确
   * <p>
   * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public CommonResult<?> httpRequestMethodNotSupportedExceptionHandler(
      HttpRequestMethodNotSupportedException ex) {
    log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
    return CommonResult.error(METHOD_NOT_ALLOWED.getCode(),
        String.format("请求方法不正确:%s", ex.getMessage()));
  }

  /**
   * 处理 SpringMVC 请求的 Content-Type 不正确
   * <p>
   * 例如说，A 接口的 Content-Type 为 application/json，结果请求的 Content-Type 为 application/octet-stream，导致不匹配
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public CommonResult<?> httpMediaTypeNotSupportedExceptionHandler(
      HttpMediaTypeNotSupportedException ex) {
    log.warn("[httpMediaTypeNotSupportedExceptionHandler]", ex);
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求类型不正确:%s", ex.getMessage()));
  }

  /**
   * 处理 Spring Security 权限不足的异常
   * <p>
   * 来源是，使用 @PreAuthorize 注解，AOP 进行权限拦截
   */
  @ExceptionHandler(value = AccessDeniedException.class)
  public CommonResult<?> accessDeniedExceptionHandler(HttpServletRequest req,
      AccessDeniedException ex) {
    log.warn("[accessDeniedExceptionHandler][userId({}) 无法访问 url({})]",
        WebFrameworkUtils.getLoginUserId(),
        req.getRequestURL(), ex);
    return CommonResult.error(FORBIDDEN);
  }

  /**
   * 处理 Guava UncheckedExecutionException
   * <p>
   * 例如说，缓存加载报错，可见 <a href="https://t.zsxq.com/UszdH">https://t.zsxq.com/UszdH</a>
   */
  @ExceptionHandler(value = UncheckedExecutionException.class)
  public CommonResult<?> uncheckedExecutionExceptionHandler(HttpServletRequest req,
      UncheckedExecutionException ex) {
    return allExceptionHandler(req, ex.getCause());
  }

  /**
   * 处理业务异常 ServiceException
   * <p>
   * 例如说，商品库存不足，用户手机号已存在。
   */
  @ExceptionHandler(value = ServiceException.class)
  public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
    // 不包含的时候，才进行打印，避免 ex 堆栈过多
    if (!IGNORE_ERROR_MESSAGES.contains(ex.getMessage())) {
      // 即使打印，也只打印第一层 StackTraceElement，并且使用 warn 在控制台输出，更容易看到
      try {
        StackTraceElement[] stackTraces = ex.getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
          if (ObjUtil.notEqual(stackTrace.getClassName(), ServiceExceptionUtil.class.getName())) {
            log.warn("[serviceExceptionHandler]\n\t{}", stackTrace);
            break;
          }
        }
      } catch (Exception ignored) {
        // 忽略日志，避免影响主流程
      }
    }
    return CommonResult.error(ex.getCode(), ex.getMessage());
  }

  /**
   * 处理系统异常，兜底处理所有的一切
   */
  @ExceptionHandler(value = Exception.class)
  public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
    // 特殊：如果是 ServiceException 的异常，则直接返回
    if (ex.getCause() != null && ex.getCause() instanceof ServiceException) {
      return serviceExceptionHandler((ServiceException) ex.getCause());
    }

    log.error("[defaultExceptionHandler]", ex);
    // 返回 ERROR CommonResult
    return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public CommonResult<?> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("illegal argument exception:", ex);
    return CommonResult.error(BAD_REQUEST.getCode(),
        String.format("请求参数错误:%s", ex.getMessage()));
  }

  /*@ExceptionHandler(AccessDeniedException.class)
  public CommonResult<?> handleAccessDeniedException(AccessDeniedException e) {
    return CommonResult.error(FORBIDDEN.getCode(), "没有访问权限");
  }*/

  @ExceptionHandler(BadCredentialsException.class)
  public CommonResult<?> handleBadCredentialsException(BadCredentialsException e) {
    return CommonResult.error(UNAUTHORIZED.getCode(), "用户名或密码错误");
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public CommonResult<?> handleExpiredJwtException(ExpiredJwtException e) {
    return CommonResult.error(UNAUTHORIZED.getCode(), "请重新登陆");
  }

  @ExceptionHandler(SignatureException.class)
  public CommonResult<?> handleSignatureException(SignatureException e) {
    return CommonResult.error(UNAUTHORIZED.getCode(), "请重新登陆");
  }


}
