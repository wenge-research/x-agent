package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 定时拉取舆情
 */
@Slf4j
// @Controller
// @RequestMapping("/task")
@Component
public class WenhaiScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 拉取热点定时器
     */
    // @RequestMapping("/sentiment")
    // @Scheduled(cron = "${longyanGpt.sentiment.corn}")
    public void sentiment() {
        String url = agentxHost + "/agentTask/sentiment";
        log.info("拉取热点定时器");
        String s = HttpUtil.get(url);
        log.info("拉取热点定时器完成{}", s);
    }


}
