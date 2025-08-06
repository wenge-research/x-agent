package com.wenge.task.scheduled;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * <p>
 * 网信办数据 定时拉取
 * </p>
 */
@Slf4j
@Component
public class WangxinbanDataScheduled {

    @Value("${task.agentx.host}")
    private String agentxHost;

    /**
     * 每天早上七点执行一次拉取
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void crawlSchedule() {
        String url = agentxHost + "/agentTask/crawlSchedule";
        log.info("开始执行网信办数据定时任务");
        String s = HttpUtil.get(url);
        log.info("网信办数据定时任务执行完毕{}", s);
    }
}
