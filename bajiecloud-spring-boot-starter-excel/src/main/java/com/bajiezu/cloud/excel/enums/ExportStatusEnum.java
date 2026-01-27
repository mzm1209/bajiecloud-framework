package com.bajiezu.cloud.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExportStatusEnum {


    PROCESSING(1, "处理中"),

    FAILED(2, "已失败"),

    SUCCESS(3, "已完成");

    private final Integer status;

    private final String desc;

}