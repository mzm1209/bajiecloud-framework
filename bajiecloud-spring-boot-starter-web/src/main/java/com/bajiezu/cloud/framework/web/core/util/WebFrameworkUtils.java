package com.bajiezu.cloud.framework.web.core.util;

import com.bajiezu.cloud.common.web.cloud.constants.RpcConstants;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 专属于 web 包的工具类
 */
public class WebFrameworkUtils {

  private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";
  private static final String REQUEST_ATTRIBUTE_LOGIN_USER_TYPE = "login_user_type";

  private static final String REQUEST_ATTRIBUTE_COMMON_RESULT = "common_result";


  /**
   * 获得当前用户的编号，从请求中 注意：该方法仅限于 framework 框架使用！！！
   *
   * @param request 请求
   * @return 用户编号
   */
  public static Long getLoginUserId(HttpServletRequest request) {
    if (request == null) {
      return null;
    }
    return (Long) request.getAttribute(REQUEST_ATTRIBUTE_LOGIN_USER_ID);
  }


  public static Long getLoginUserId() {
    HttpServletRequest request = getRequest();
    return getLoginUserId(request);
  }


  public static void setCommonResult(ServletRequest request, CommonResult<?> result) {
    request.setAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT, result);
  }

  public static CommonResult<?> getCommonResult(ServletRequest request) {
    return (CommonResult<?>) request.getAttribute(REQUEST_ATTRIBUTE_COMMON_RESULT);
  }

  @SuppressWarnings("PatternVariableCanBeUsed")
  public static HttpServletRequest getRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes)) {
      return null;
    }
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
    return servletRequestAttributes.getRequest();
  }

  /**
   * 判断是否为 RPC 请求
   *
   * @param request 请求
   * @return 是否为 RPC 请求
   */
  public static boolean isRpcRequest(HttpServletRequest request) {
    return request.getRequestURI().startsWith(RpcConstants.RPC_API_PREFIX);
  }

  /**
   * 判断是否为 RPC 请求
   * <p>
   * 约定大于配置，只要以 Api 结尾，都认为是 RPC 接口
   *
   * @param className 类名
   * @return 是否为 RPC 请求
   */
  public static boolean isRpcRequest(String className) {
    return className.endsWith("Api");
  }

}
