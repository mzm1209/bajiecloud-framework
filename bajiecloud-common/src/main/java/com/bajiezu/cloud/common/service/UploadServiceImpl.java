package com.bajiezu.cloud.common.service;

import com.bajiezu.cloud.common.oss.AliyunOss;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static com.bajiezu.cloud.common.web.exception.constants.GlobalErrorCodeConstants.FILE_UPLOAD_ERROR;
import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;

@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    public static final String TEMP_DIR = "./tmp";
    @Resource
    private AliyunOss aliyunOss;

    @Override
    public String upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        log.info("upload fileName: {}", fileName);
        File fileDir = new File(TEMP_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        fileName = buildNewFileName(fileName);
        File tmpFile = new File(TEMP_DIR + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            if (!tmpFile.exists()) {
                tmpFile.createNewFile();
            }
            IOUtils.copy(file.getInputStream(), fos);
            return aliyunOss.uploadFile(tmpFile, fileName);
        } catch (IOException e) {
            log.error("upload file error: ", e);
            throw exception(FILE_UPLOAD_ERROR);
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    @Override
    public String uploadWithFileOriginName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        log.info("uploadWithFileOriginName, fileName: {}", fileName);

        File fileDir = new File(TEMP_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        fileName = buildNewFileName(fileName);
        File tmpFile = new File(TEMP_DIR + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
            if (!tmpFile.exists()) {
                tmpFile.createNewFile();
            }
            IOUtils.copy(file.getInputStream(), fos);
            return aliyunOss.uploadFileWithOriginFileName(tmpFile, fileName, file.getOriginalFilename());
        } catch (IOException e) {
            log.error("uploadWithFileOriginName error: ", e);
            throw exception(FILE_UPLOAD_ERROR);
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    private String buildNewFileName(final String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isNotBlank(extension)) {
            return UUID.randomUUID().toString() + "." + extension;
        }
        return UUID.randomUUID().toString();
    }
}
