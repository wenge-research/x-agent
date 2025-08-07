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
// @Api(tags = "知识库上传的word转pdf定时任务接口")
// @RequestMapping("/transWordToPdf")
@Slf4j
@Component
public class Word2PdfParserScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;


    // @RequestMapping("/transParser")
    @Scheduled(cron = "${task.trans.cron}")
    public void transParser() {
        String url = agentxHost + "/agentTask/transWordToPdf/transParser";
        log.info("开始执行任务：{}", url);
        String s = HttpUtil.get(url);
        log.info("任务执行完毕：{},{}", url, s);
    }


}
