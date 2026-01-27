package com.bajiezu.cloud.excel.export.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Schema(description = "下载任务更新参数")
@Data
public class ExportTaskUpdateDTO {

    @Schema(description = "任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "文件路径")
    private String filePath;

    /**
     * @see com.bajiezu.cloud.excel.enums.ExportStatusEnum
     */
    @Schema(description = "任务状态")
    @NotNull(message = "任务状态不能为空")
    private Integer status;

    @Schema(description = "更新时间")
    @NotNull(message = "更新时间不能为空")
    private Date updateTime;

    @Schema(description = "失败原因")
    private String failReason;
}
