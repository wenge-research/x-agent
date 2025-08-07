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
// @Api(tags = "知识库url解析接口")
// @RequestMapping("/url")
@Slf4j
@Component
public class KnowledgeUrlParserScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    // @RequestMapping("/urlParser")
    @Scheduled(cron = "${task.url.cron}")
    public void urlParser() {
        String url = agentxHost + "/agentTask/url/urlParser";
        log.info("开始执行urlParser任务");
        String s = HttpUtil.get(url);
        log.info("结束执行urlParser任务{}", s);
    }

}
