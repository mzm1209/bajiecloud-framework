package com.bajiezu.cloud.framework.security.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.bajiezu.cloud.framework.security.context.LoginUserContext;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 安全服务工具类
 */
public class SecurityFrameworkUtils {

    /**
     * HEADER 认证头 value 的前缀
     */
    public static final String AUTHORIZATION_BEARER = "Bearer";

    /**
     * Cookie 认证 的 Cookie 名字
     */
    public static final String COOKIE_NAME = "BAJIEZU_SECURITY_TOKEN";

    public static final String TOKEN_PARAMETER_NAME = "security_token";

    public static final String LOGIN_USER_HEADER = "login-user-token";

    public static final Set<String> NOT_NEED_LOGIN_PATHS = Set.of(
            "/",
            "/actuator/*",
            "/swagger-ui.html",
            "/swagger-ui/*",
            "/v3/api-docs/*",
            "/webjars/*",
            "/swagger-resources/*",
            "/login",
            "/error"
    );


    private SecurityFrameworkUtils() {
    }

    public static Set<String> rewriteNotNeedLoginPaths(String contextPath) {
        if (Objects.equals("/", contextPath)) {
            return NOT_NEED_LOGIN_PATHS;
        } else {
            return NOT_NEED_LOGIN_PATHS.stream().map(path -> contextPath + path)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * 从请求中，获得认证 Token
     *
     * @param request       请求
     * @param headerName    认证 Token 对应的 Header 名字
     * @param parameterName 认证 Token 对应的 Parameter 名字
     * @return 认证 Token
     */
    public static String getToken(HttpServletRequest request,
                                  String headerName, String parameterName) {
        //1. 从 Cookie 中获得认证 Token
        Cookie tokenCookie = JakartaServletUtil.getCookie(request, COOKIE_NAME);
        if (tokenCookie != null && StrUtil.isNotBlank(tokenCookie.getValue())) {
            return tokenCookie.getValue();
        }
        String token = (String) request.getSession().getAttribute(COOKIE_NAME);
        if (StringUtil.isNotBlank(token)) {
            return token;
        }

        // 2. 获得 Token。优先级：Header > Parameter
        token = request.getHeader(headerName);
        if (StrUtil.isEmpty(token)) {
            token = request.getParameter(parameterName);
        }
        if (!StringUtils.hasText(token)) {
            return null;
        }
        // 2. 去除 Token 中带的 Bearer
        int index = token.indexOf(AUTHORIZATION_BEARER + " ");
        return index >= 0 ? token.substring(index + 7).trim() : token;
    }


    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    @Nullable
    public static LoginUser<?> getLoginUser() {
        return LoginUserContext.getLoginUser();
    }


}
