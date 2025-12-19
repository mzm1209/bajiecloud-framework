package com.bajiezu.cloud.common.oss;

import com.aliyuncs.auth.sts.AssumeRoleResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StsInfo extends AssumeRoleResponse.Credentials {
    private String bucket;
    private String endpoint;
    private String cdnDomain;
}