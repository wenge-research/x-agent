package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文档解析任务
 */
// @RestController
// @RequestMapping("/document")
@Slf4j
@Component
public class DocumentScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;


    /**
     * 解析文档
     */
    // @RequestMapping("/documentAnalysis")
    @Scheduled(cron = "${task.document.cron}")
    public void documentAnalysis() {
        String url = agentxHost + "/agentTask/document/documentAnalysis";
        log.info("开始解析文档");
        String s = HttpUtil.get(url);
        log.info("文档解析完成{}", s);
    }

}
