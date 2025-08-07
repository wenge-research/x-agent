package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 获取稿件【国科大应用】
 */
// @RestController
// @RequestMapping("/guangxin")
@Slf4j
@Component
public class GkStoryScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 获取稿件
     */
    // @GetMapping("/story")
    @Scheduled(cron = "${task.GkStory.cron}")
    public void story() {
        String url = agentxHost + "/agentTask/guangxin/story";
        log.info("开始获取稿件");
        String s = HttpUtil.get(url);
        log.info("获取稿件结束{}", s);
    }

}
