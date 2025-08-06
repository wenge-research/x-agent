package com.wenge.model.schedule;

import cn.hutool.core.collection.CollectionUtil;
import com.wenge.model.entity.File;
import com.wenge.model.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * <p>
 * 重启服务时，扫描未完成的多媒体任务并重新执行
 * </p>
 */
@Slf4j
@Component
public class MediaScheduleService {

/**
     * 定时任务开关 本地local环境请勿启用
     */
    @Value("${schedule.retry.enable:false}")
    private boolean scheduleEnable;

    @Autowired
    private FileService fileService;


    /**
     * 重启服务时执行该任务
     */
    @PostConstruct
    public void retrySchedule() {
        if (scheduleEnable) {
            log.info("==>定时任务启动，检测上次服务停止前，是否有未完成的多媒体转为文本任务！");
            List<File> parsingMediumFiles = fileService.getParsingMediumFile();

            if (CollectionUtil.isNotEmpty(parsingMediumFiles)) {
                log.info("start==>检测到上次服务停止前，有未完成的多媒体转为文本任务，更新执行中的任务状态为初始状态！");
                fileService.updateStatusUploadSuccess(parsingMediumFiles);
                log.info("end==>检测到上次服务停止前，有未完成的多媒体转为文本任务，更新状态完成！");
            } else {
                log.info("==>检测结束，上次服务停止前，没有未完成的多媒体转为文本任务！");
            }
        } else {
            log.info("==>定时任务未启动，未检测上次服务停止前，是否有未完成的多媒体转为文本任务！");
        }
    }

}
