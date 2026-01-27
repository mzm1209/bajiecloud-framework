package com.bajiezu.cloud.excel.export;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.excel.export.dto.ExportTaskAddDTO;
import com.bajiezu.cloud.excel.export.dto.ExportTaskUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "system-service")
@Tag(name = "RPC 服务 - 下载服务")
public interface ExportApi {

    String PREFIX = "system-service/system/download";

    @PostMapping(PREFIX + "/addTask")
    @Operation(summary = "创建下载任务")
    CommonResult<Long> addTask(@Valid @RequestBody ExportTaskAddDTO downloadTaskAddDTO);


    @PostMapping(PREFIX + "/updateTask")
    @Operation(summary = "更新下载任务")
    CommonResult<Boolean> updateTask(@Valid @RequestBody ExportTaskUpdateDTO downloadTaskUpdateDTO);
}