package com.bajie.cloud.common.web.biz.system.oauth2;

import com.bajie.cloud.common.web.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.bajie.cloud.common.web.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.bajie.cloud.common.web.cloud.constants.RpcConstants;
import com.bajie.cloud.common.web.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = RpcConstants.SYSTEM_NAME)
@Tag(name = "RPC 服务 - OAuth2.0 令牌")
public interface OAuth2TokenCommonApi {
    @Data
    public class OAuth2AccessTokenCheckRespDTO implements Serializable {

        @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        private Long userId;

        @Schema(description = "用户类型，参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer userType;

        @Schema(description = "用户信息", example = "{\"nickname\": \"芋道\"}")
        private Map<String, String> userInfo;

        @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long tenantId;

        @Schema(description = "授权范围的数组", example = "user_info")
        private List<String> scopes;

        @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime expiresTime;

    }
    String PREFIX = RpcConstants.SYSTEM_PREFIX + "/oauth2/token";

    /**
     * 校验 Token 的 URL 地址，主要是提供给 Gateway 使用
     */
    @SuppressWarnings("HttpUrlsUsage")
    String URL_CHECK = "http://" + RpcConstants.SYSTEM_NAME + PREFIX + "/check";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建访问令牌")
    CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(@Valid @RequestBody OAuth2AccessTokenCreateReqDTO reqDTO);

    @GetMapping(PREFIX + "/check")
    @Operation(summary = "校验访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, example = "tudou")
    CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(@RequestParam("accessToken") String accessToken);

    @DeleteMapping(PREFIX + "/remove")
    @Operation(summary = "移除访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, example = "tudou")
    CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(@RequestParam("accessToken") String accessToken);

    @PutMapping(PREFIX + "/refresh")
    @Operation(summary = "刷新访问令牌")
    @Parameters({
        @Parameter(name = "refreshToken", description = "刷新令牌", required = true, example = "haha"),
        @Parameter(name = "clientId", description = "客户端编号", required = true, example = "yudaoyuanma")
    })
    CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(@RequestParam("refreshToken") String refreshToken,
                                                              @RequestParam("clientId") String clientId);

}