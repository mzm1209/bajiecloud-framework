package com.bajiezu.cloud.framework.security.service;

import cn.hutool.core.collection.CollectionUtil;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import lombok.AllArgsConstructor;

/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 */
@AllArgsConstructor
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    @Override
    public boolean hasPermission(String permission) {
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null || CollectionUtil.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        return loginUser.getPermissions().contains(permission);
    }
}
