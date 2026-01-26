package com.bajiezu.cloud.excel.export;

import cn.hutool.core.thread.NamedThreadFactory;
import com.bajiezu.cloud.common.oss.AliyunOss;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractExportService {

    public static final String EXPORT_FILE_TYPE = ".xlsx";
    public static final int TASK_STATUS_PROCESSING = 1; // 任务处理中
    public static final int TASK_STATUS_FAILED = 2;  // 任务处理失败
    public static final int TASK_STATUS_SUCCESS = 3; // 任务处理成功
    public static final String EXPORT_DIR = "./exportDir/";
    //执行异步生成导出文件的线程池
    private final ExecutorService executeService = new ThreadPoolExecutor(5, 5, 10,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(200),
            new NamedThreadFactory("ExportFileExecuteService", false));

    @Autowired
    private AliyunOss aliyunOss;

    @PostConstruct
    public void init() {
        File fileDir = new File(EXPORT_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /**
     * 执行下载流程的主方法
     *
     * @param userId    用户ID
     * @param partnerId 合作商ID
     * @param params    业务参数
     */
    public void executeDownload(Long userId, Long partnerId, Object params, Integer source) {
        String fileName = generateFileName();
        Long taskId = createDownloadTask(userId, partnerId, fileName, params, source);

        executeService.execute(() -> {
            int taskStatus = TASK_STATUS_SUCCESS;
            String failReason = "";
            String fileUrl = "";
            try {
                String filePath = createExportFile(fileName, params);
                fileUrl = uploadFileToAliyun(filePath, fileName);
            } catch (Exception e) {
                taskStatus = TASK_STATUS_FAILED;
                log.error("下载任务执行失败，taskId: {}, error: {}", taskId, e.getMessage(), e);
                failReason = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage().substring(0, 2000) : "下载任务执行失败";
            }
            updateDownloadTask(taskId, taskStatus, fileUrl, failReason);
        });
    }

    /**
     * 生成文件名 由各个业务来实现
     */
    protected abstract String generateFileName();

    /**
     * 创建下载任务
     *
     * @param userId    用户ID
     * @param partnerId 合作商ID
     * @param fileName  文件名
     * @param params    下载参数
     * @param source    来源
     * @return 任务ID
     */
    protected abstract Long createDownloadTask(Long userId, Long partnerId, String fileName, Object params, Integer source);

    /**
     * 创建导出文件
     *
     * @param fileName 文件路径
     * @param params   业务参数
     * @return 文件在本地的文件路径
     */
    protected abstract String createExportFile(String fileName, Object params);

    /**
     * 上传文件到阿里云
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 文件下载地址
     */
    private String uploadFileToAliyun(String filePath, String fileName) {
        log.info("开始上传文件到阿里云，filePath: {}", filePath);
        if (filePath == null) {
            return null;
        }
        return aliyunOss.uploadFile(new File(filePath), fileName);
    }

    /**
     * 更新下载任务
     *
     * @param taskId     任务ID
     * @param taskStatus 任务状态
     * @param fileUrl    文件下载地址
     * @param failReason 失败原因
     */
    protected abstract void updateDownloadTask(Long taskId, int taskStatus, String fileUrl, String failReason);
}
