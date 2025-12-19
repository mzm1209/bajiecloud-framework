package com.bajiezu.cloud.common.config;

import com.bajiezu.cloud.common.oss.AliyunOss;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Data
public class OssConfig {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket}")
    private String bucket;

    @Value("${aliyun.oss.roleArn}")
    private String roleArn;

    @Value("${aliyun.oss.cdn}")
    private String cdn;

    @Bean
    public AliyunOss getAliyunOss() {
        log.info("aliyun oss init");
        return new AliyunOss(endpoint, accessKeyId, accessKeySecret, roleArn, bucket, cdn);
    }
}