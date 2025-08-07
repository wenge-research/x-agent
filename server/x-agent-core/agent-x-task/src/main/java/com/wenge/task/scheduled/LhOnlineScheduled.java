package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 龙华在线数据拉取
 */
@Slf4j
// @RestController
// @RequestMapping("/task")
@Component
public class LhOnlineScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 拉取龙华在线数据到知识库文件库
     */
    // @GetMapping("/report")
    @Scheduled(cron = "${task.lhonline.cron}")
    public void report() {
        String url = agentxHost + "/agentTask/report";
        log.info("拉取龙华在线数据到知识库文件库");
        String s = HttpUtil.get(url);
        log.info("拉取龙华在线数据到知识库文件库完成{}", s);
    }

    /**
     * 拉取龙华办事指南
     */
    // @GetMapping("/guide")
    @Scheduled(cron = "${task.guide.cron}")
    public void guide() {
        String url = agentxHost + "/agentTask/guide";
        log.info("拉取龙华办事指南");
        String s = HttpUtil.get(url);
        log.info("拉取龙华办事指南完成{}", s);
    }
}

