package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.common.web.exception.ErrorCode;
import com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants;
import com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class FeginMethodExecuteUtils {

  /**
   * 执行 feign 方法，忽略认证, 并检查返回结果是否成功 如果返回结果不是成功，会抛出异常
   *
   * @param supplier   feign 方法的 supplier
   * @param ignoreAuth 是否忽略认证
   * @param <T>        调用 feign 方法的返回结果类型
   * @return 调用 feign 方法的返回结果
   * @deprecated 请使用 {@link #execute(Supplier, boolean, ErrorCode, String)} 方法
   */
  @Deprecated
  public static <T> T execute(Supplier<CommonResult<T>> supplier, boolean ignoreAuth) {
    CommonResult<T> result;
    if (ignoreAuth) {
      result = RemoteMethodInvokeUtils.invokeWithSecurityToken(supplier);
    } else {
      result = supplier.get();
    }
    if (!result.isSuccess()) {
      log.error("execute feign method error, result: {}", result);
      throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.FEGIN_EXECUTE_ERROR,
          result.getMsg());
    }
    return result.getData();
  }

  /**
   * 执行 feign 方法，忽略认证, 并检查返回结果是否成功 如果返回结果不是成功，会抛出异常
   *
   * @param supplier   feign 方法的 supplier
   * @param ignoreAuth 是否忽略认证
   * @param errorCode  异常码
   * @param message    异常信息
   * @param <T>        调用 feign 方法的返回结果类型
   * @return 调用 feign 方法的返回结果
   */
  @Deprecated
  public static <T> T execute(Supplier<CommonResult<T>> supplier, boolean ignoreAuth,
      ErrorCode errorCode, String message) {
    return execute(supplier, ignoreAuth, true, errorCode, message);
  }

  /**
   * 执行 feign 方法，忽略认证, 并检查返回结果是否成功 如果返回结果不是成功，会抛出异常
   *
   * @param supplier     feign 方法的 supplier
   * @param ignoreAuth   是否忽略认证
   * @param checkNotNull 是否检查返回结果是否为 null
   * @param errorCode    异常码
   * @param message      异常信息
   * @param <T>          调用 feign 方法的返回结果类型
   * @return 调用 feign 方法的返回结果
   */
  @Deprecated
  public static <T> T execute(Supplier<CommonResult<T>> supplier, boolean ignoreAuth,
      boolean checkNotNull, ErrorCode errorCode, String message) {
    CommonResult<T> result;
    try {
      if (ignoreAuth) {
        result = RemoteMethodInvokeUtils.invokeWithSecurityToken(supplier);
      } else {
        result = supplier.get();
      }
    } catch (Exception e) {
      log.error("execute feign method error,", e);
      throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.FEGIN_EXECUTE_ERROR);
    }
    message = StringUtils.defaultIfBlank(message, errorCode.getMsg());
    if (!result.isSuccess()) {
      log.error("execute feign method error, result: {}", result);
      throw ServiceExceptionUtil.exception0(errorCode.getCode(), message, result.getMsg());
    }
    T resultData = result.getData();
    if (checkNotNull && resultData == null) {
      log.error("execute feign method error, result data is null, result: {}", result);
      throw ServiceExceptionUtil.exception0(errorCode.getCode(), message, "result data is null");
    }
    return result.getData();

  }
}
