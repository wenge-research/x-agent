package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 媒体解析
 */
// @RestController
// @Api(tags = "多媒体文件解析接口")
// @RequestMapping("/medium")
@Slf4j
@Component
public class MediumAnalysisScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;


    // @RequestMapping("/mediumAnalysis")
    @Scheduled(cron = "${task.media.cron}")
    public void mediumAnalysis() {
        String url = agentxHost + "/agentTask/medium/mediumAnalysis";
        log.info("开始执行媒体解析任务");
        String s = HttpUtil.get(url);
        log.info("结束执行媒体解析任务{}", s);
     }

    // @RequestMapping("/imageAnalysis")
    @Scheduled(cron = "${task.image.cron}")
    public void imageAnalysis() {
        String url = agentxHost + "/agentTask/medium/imageAnalysis";
        log.info("开始执行图片解析任务");
        String s = HttpUtil.get(url);
        log.info("结束执行图片解析任务{}", s);
    }

}
