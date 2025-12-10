package com.bajiezu.cloud.common.constants;

import com.bajiezu.cloud.common.type.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 通用枚举
 */
@Getter
@AllArgsConstructor
public enum IsDeletedEnum implements ArrayValuable<Integer> {

    NO(0, "未删除"),
    YES(1, "已删除");


    public static final Integer[] ARRAYS = Arrays.stream(values()).map(IsDeletedEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}