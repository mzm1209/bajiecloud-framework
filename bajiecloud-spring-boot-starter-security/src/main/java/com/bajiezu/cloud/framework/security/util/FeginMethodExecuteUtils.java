package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.common.web.exception.ErrorCode;
import com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants;
import com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeginMethodExecuteUtils {

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

  public static <T> T execute(Supplier<CommonResult<T>> supplier, boolean ignoreAuth,
      ErrorCode errorCode, String message) {
    return execute(supplier, ignoreAuth, true, errorCode, message);
  }


  public static <T> T execute(Supplier<CommonResult<T>> supplier, boolean ignoreAuth,
      boolean checkNotNull,
      ErrorCode errorCode, String message) {
    try {
      CommonResult<T> result;
      if (ignoreAuth) {
        result = RemoteMethodInvokeUtils.invokeWithSecurityToken(supplier);
      } else {
        result = supplier.get();
      }
      if (!result.isSuccess()) {
        log.error("execute feign method error, result: {}", result);
        throw ServiceExceptionUtil.exception(errorCode, message, result.getMsg());
      }
      T resultData = result.getData();
      if (checkNotNull && resultData == null) {
        log.error("execute feign method error, result data is null, result: {}", result);
        throw ServiceExceptionUtil.exception(errorCode, message, "result data is null");
      }
      return result.getData();
    } catch (Exception e) {
      log.error("execute feign method error,", e);
      throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.FEGIN_EXECUTE_ERROR);
    }
  }
}
