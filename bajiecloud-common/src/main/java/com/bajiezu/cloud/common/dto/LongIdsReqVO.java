package com.bajiezu.cloud.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "批量操作的ID 请求实体")
@Data
public class LongIdsReqVO {

    @Schema(description = "批量操作的ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> ids;
}
