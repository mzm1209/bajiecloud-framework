package com.bajiezu.cloud.common.dict;

import com.bajiezu.cloud.common.dict.dto.DictDataRespDTO;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "system-service", primary = false)
@Tag(name = "RPC 服务 - 字典数据")
public interface DictDataCommonApi {

    String PREFIX = "system-service/system/dict-data";

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得指定字典类型的字典数据列表")
    @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true)
    CommonResult<List<DictDataRespDTO>> getDictDataList(@RequestParam("dictType") String dictType);

}
