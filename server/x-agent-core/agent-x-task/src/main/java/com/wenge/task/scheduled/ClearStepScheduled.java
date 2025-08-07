package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// @RestController
// @RequestMapping("/step")
@Slf4j
@Component
public class ClearStepScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 清理步骤数据
     * 每隔10分钟清理一次
     */
    // @RequestMapping("/clear")
    @Scheduled(cron = "${task.stepclear.cron}")
    public void clearStep() {
        String url = agentxHost + "/agentTask/step/clear";
        log.info("开始清理step数据");
        String s = HttpUtil.get(url);
        log.info("清理step数据结束{}", s);
    }

    /**
     * 清理llm会话的数据
     * 每隔10分钟清理一次
     */
    // @RequestMapping("/clearerMessage")
    @Scheduled(cron = "${task.stepclear.cron}")
    public void clearerMessage() {
        log.info("开始清理llm会话数据");
        String url = agentxHost + "/agentTask/step/clearerMessage";
        String s = HttpUtil.get(url);
        log.info("清理llm会话数据结束{}", s);
    }
}
