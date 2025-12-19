package com.bajiezu.cloud.common.oss;

import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Slf4j
public final class AliyunOss {

    private final String endpoint;

    private final String accessKeyId;

    private final String accessKeySecret;

    private final String roleArn;

    private final String bucket;

    private final String cdn;

    public AliyunOss(String endpoint, String accessKeyId, String accessKeySecret,
                     String roleArn, String bucket, String cdn) {
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.roleArn = roleArn;
        this.bucket = bucket;
        this.cdn = cdn;
    }

    public AssumeRoleResponse getSTS() throws ClientException {
        String roleSessionName = "session-name";
        String policy = """
                {
                    "Version": "1",\s
                    "Statement": [
                        {
                            "Action": [
                                "oss:*"
                            ],\s
                            "Resource": [
                                "acs:oss:*:*:*"\s
                            ],\s
                            "Effect": "Allow"
                        }
                    ]
                }""";
        try {
            // 构造default profile（参数留空，无需添加region ID）
            IClientProfile profile = DefaultProfile.getProfile("cn-shanghai", accessKeyId, accessKeySecret);
            // 用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysProtocol(ProtocolType.HTTPS);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setPolicy(policy); // Optional
            return client.getAcsResponse(request);
        } catch (Exception e) {
            log.error("STS Request Failed", e);
            throw e;
        }
    }

    public StsInfo getStsInfo() throws ClientException {
        AssumeRoleResponse assumeRoleResponse = getSTS();
        AssumeRoleResponse.Credentials credentials = assumeRoleResponse.getCredentials();
        StsInfo stsInfo = new StsInfo();
        BeanUtils.copyProperties(credentials, stsInfo);
        stsInfo.setBucket(bucket);
        stsInfo.setEndpoint(endpoint);
        return stsInfo;
    }

    private String fileKey(File file, String originFileKey) {
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        String fileDir = "bjz/" + dateStr + "/";

        // 上传文件。<yourLocalFile>由本地文件路径加文件名包括后缀组成，例如/users/local/myfile.txt。
        String fileKey;
        if (StringUtils.isNotBlank(originFileKey)) {
            fileKey = fileDir + originFileKey;
        } else {
            fileKey = fileDir + UUID.randomUUID().toString().replace("-", "") + "-" + file.getName();
        }
        return fileKey;
    }

    public String uploadFile(File file, String originFileKey) {
        String fileKey = fileKey(file, originFileKey);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.putObject(bucket, fileKey, file);
        // 关闭OSSClient
        ossClient.shutdown();

        // 手动拼接链接url
        return "https://" + cdn + '/' + fileKey;
    }


    public String uploadFile(String folder, String fileName, InputStream file) {
        String fileKey = folder + '/' + fileName;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ossClient.putObject(bucket, fileKey, file);
        // 关闭OSSClient
        ossClient.shutdown();

        // 手动拼接链接url
        return "https://" + cdn + '/' + fileKey;
    }

    public String uploadFileWithOriginFileName(File file, String originFileKey, String originFileName) {
        String fileKey = fileKey(file, originFileKey);

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentDisposition("attachment;filename=\"" + originFileName + "\"");
        ossClient.putObject(bucket, fileKey, file, objectMetadata);

        // 关闭OSSClient
        ossClient.shutdown();

        // 手动拼接链接url
        return "https://" + cdn + '/' + fileKey;
    }
}
