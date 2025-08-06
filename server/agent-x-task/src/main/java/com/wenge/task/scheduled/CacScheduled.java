package com.wenge.task.scheduled;


import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 网信办
 */
@Slf4j
// @RestController
// @RequestMapping("/task")
@Component
public class CacScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 拉取网信办
     */
    // @GetMapping("/cac")
    @Scheduled(cron = "${task.cac.cron}")
    public void guide() {
        String url = agentxHost + "/agentTask/cac";
        log.info("==>网信办任务开始,api:{}", url);
        String response = HttpUtil.get(url);
        log.info("==>网信办任务结束:{}", response);
    }

}

