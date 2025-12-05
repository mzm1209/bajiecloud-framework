package com.bajiezu.cloud.framework.security;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录用户信息
 */
@Data
@Accessors(chain = true)
public class LoginUser<T extends LoginInfoEntity> implements Serializable {

    /**
     * 用户编号
     */
    private Long id;
    /**
     * 用户类型
     * <p>
     * 关联 {@link com.bajie.cloud.common.constants.UserTypeEnum}
     */
    private Integer userType;

    /**
     * 登陆用户额外信息
     */
    private T loginInfo;

    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

    private String ip;

    private String token;

    /**
     * 合作商id
     */
    private Long partnerId;

    // ========== 上下文 ==========
    /**
     * 上下文字段，不进行持久化
     * <p>
     * 1. 用于基于 LoginUser 维度的临时缓存
     */
    @JsonIgnore
    private Map<String, Object> context;


    public String getUsername() {
        return loginInfo != null ? loginInfo.getUsername() : null;
    }

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public <R> R getContext(String key, Class<R> type) {
        return MapUtil.get(context, key, type);
    }

}
