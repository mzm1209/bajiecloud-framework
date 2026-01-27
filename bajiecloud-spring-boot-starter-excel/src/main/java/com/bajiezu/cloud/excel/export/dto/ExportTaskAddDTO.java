package com.bajiezu.cloud.excel.export.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Schema(description = "下载任务添加参数")
@Data
public class ExportTaskAddDTO {

    @Schema(description = "文件名称")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    @Schema(description = "下载来源")
    @NotNull(message = "下载来源不能为空")
    private Integer source;

    /**
     * @see com.bajiezu.cloud.excel.enums.ExportStatusEnum
     */
    @Schema(description = "下载状态 1:处理中 2:已失败 3:已完成")
    @NotNull(message = "下载状态不能为空")
    private Integer status;

    @Schema(description = "合作商id")
    @NotNull(message = "合作商id不能为空")
    private Long partnerId;

    @Schema(description = "创建时间")
    @NotNull(message = "创建时间不能为空")
    private Date createTime;

    @Schema(description = "创建人id")
    @NotNull(message = "创建人不能为空")
    private Long creatorId;

    @Schema(description = "扩展字段, 可以存储下载时传入的请求参数")
    private String extJson;
}
