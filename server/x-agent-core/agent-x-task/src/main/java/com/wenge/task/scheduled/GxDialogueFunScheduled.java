package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 关芯客服对话功能划分
 */
// @RestController
// @RequestMapping("/document")
@Slf4j
@Component
public class GxDialogueFunScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 关芯客服对话功能划分
     */
    // @RequestMapping("/funDivision")
    @Scheduled(cron = "${task.document.cron}")
    public void funDivision() {
        String url = agentxHost + "/agentTask/document/funDivision";
        log.info("GxDialogueFunScheduled funDivision");
        String s = HttpUtil.get(url);
        log.info("GxDialogueFunScheduled funDivision end{}", s);
    }
}
