package com.bajiezu.cloud.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "记录唯一id请求对象")
@Data
public class LongIdReqVO {

    @Schema(description = "唯一id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "id不能为空")
    private Long id;
}
