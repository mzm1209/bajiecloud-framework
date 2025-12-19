package com.bajiezu.cloud.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    String upload(MultipartFile file);

    String uploadWithFileOriginName(MultipartFile file);
}
