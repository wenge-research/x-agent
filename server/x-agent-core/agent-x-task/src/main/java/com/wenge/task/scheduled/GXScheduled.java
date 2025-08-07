package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 关芯智巡定时器
 */
// @RestController
// @RequestMapping("/guangxin")
@Slf4j
@Component
public class GXScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 关芯智巡定时器
     */
    @Scheduled(cron = "${task.guanxin.cron}")
    // @RequestMapping("/guangxinTask")
    public void guangxinTask() {
        String url = agentxHost + "/agentTask/guangxin/guangxinTask";
        log.info("关心智巡定时器开始执行");
        String s = HttpUtil.get(url);
        log.info("关心智巡定时器执行完毕{}", s);
    }


}
