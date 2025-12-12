package com.bajiezu.cloud.common.constants;

import com.bajiezu.cloud.common.type.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 全局操作类型枚举
 */
@AllArgsConstructor
@Getter
public enum OperateTypeEnum implements ArrayValuable<Integer> {

    SAVE(1, "保存"),
    SUBMIT(2, "提交");


    public static final Integer[] ARRAYS = Arrays.stream(values()).map(OperateTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer type;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
