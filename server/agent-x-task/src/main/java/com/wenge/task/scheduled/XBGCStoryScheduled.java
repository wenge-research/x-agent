package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 获取稿件【西部国传问答】
 */
// @RestController
// @RequestMapping("/xibuguochuan")
@Slf4j
@Component
public class XBGCStoryScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 获取稿件 定时任务建议每天凌晨执行一次
     */
    // @GetMapping("/story")
    @Scheduled(cron = "${task.XBGCStory.cron}")
    public void story() {
        String url = agentxHost + "/agentTask/xibuguochuan/story";
        log.info("获取稿件【西部国传问答】");
        String s = HttpUtil.get(url);
        log.info("获取稿件【西部国传问答】结束{}", s);
    }

}
