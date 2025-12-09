package com.bajiezu.cloud.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件信息
 */
@Schema(description = "文件信息")
@Data
public class FileInfoVO {

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件访问地址")
    private String fileUrl;
}
