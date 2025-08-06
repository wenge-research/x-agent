package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.wenge.model.entity.File;
import com.wenge.model.service.FileService;
import com.wenge.model.utils.Word2PdfUtils;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 媒体解析
 */
@RestController
@Api(tags = "知识库上传的word转pdf定时任务接口")
@RequestMapping("/agentTask/transWordToPdf")
@Slf4j
public class Word2PdfParserTask {

    @Autowired
    private WosUtil wosUtil;

    @Value("${task.trans.enable}")
    private boolean enable;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private FileService fileService;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisConstant.RUN_WORD2PDF, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_WORD2PDF);
            redisService.unlock(RedisConstant.LOCK_WORD2PDF);
        }
    }


    @RequestMapping("/transParser")
    // // @Scheduled(cron = "${task.trans.cron}")
    public void transParser() {
        if (!enable) {
            return;
        }
        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_WORD2PDF);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_WORD2PDF);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_WORD2PDF, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 获取所有上传的不含转换的pdf的word文件
            List<File> files = fileService.getWordFile();
            if (CollectionUtil.isEmpty(files)) {
                return;
            }

            log.info("*************** start-启动定时任务开始转换word到pdf");

            ThreadPoolExecutor pool = ExecutorBuilder.create().setCorePoolSize(20).setMaxPoolSize(200).setWorkQueue(new LinkedBlockingDeque<>()).build();
            files.forEach(file -> pool.execute(() -> trans(file)));

            while (pool.getActiveCount() != 0 && !pool.isShutdown() && !pool.isTerminated()) {
                ThreadUtil.sleep(500);
            }
            pool.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_WORD2PDF);
            redisService.unlock(RedisConstant.LOCK_WORD2PDF);
        }
    }

    private void trans(File file) {
        InputStream inputStream = null;
        try {
            String fileUrl = file.getFileUrl();
            String[] split = fileUrl.split("file/download\\?");
            if (split.length < 2) {
                return;
            }
            inputStream = wosUtil.getInput(bucket, split[1]);
            byte[] bytes = IoUtil.readBytes(inputStream);
            MultipartFile pdfFile = Word2PdfUtils.docToPdf(file.getFileName(), bytes);
            MinioInfoResult result = wosUtil.upload(bucket, "word2Pdf", pdfFile, true);
            file.setTransPdfUrl(result.getUrlPath());
            fileService.updateById(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                IoUtil.close(inputStream);
            }
        }

    }

}
