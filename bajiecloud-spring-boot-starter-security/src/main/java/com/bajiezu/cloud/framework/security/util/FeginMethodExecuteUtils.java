package com.bajiezu.cloud.framework.security.util;

import com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants;
import com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

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
      throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.FEGIN_EXECUTE_ERROR, result.getMsg());
    }
    return result.getData();
  }
}
