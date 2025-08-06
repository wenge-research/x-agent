package com.wenge.model.task;

import cn.hutool.core.net.NetUtil;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.redis.service.RedisService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * 定时汇总知识库数据数据
 */
@RestController
@Api(tags = "知识库数据汇总")
@RequestMapping("/agentTask/analysis/task")
@Slf4j
public class KnowledgeDataAnalysisTask {


    @Value("${task.analysis.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisConstant.RUN_SYNC_KNOWLEDGE, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_SYNC_KNOWLEDGE);
            redisService.unlock(RedisConstant.LOCK_SYNC_KNOWLEDGE);
        }
    }


    @RequestMapping("/summaryData")
    // @Scheduled(cron = "${task.analysis.cron}")
    public void summaryData() {
//        if (!enable) {
//            return;
//        }
        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_SYNC_KNOWLEDGE);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }
            boolean hasKey = redisService.hasKey(RedisConstant.RUN_SYNC_KNOWLEDGE);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }
            log.info("*************** start-启动定时任务开始同步所有的知识库数据");
            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_SYNC_KNOWLEDGE, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("开始同步知识库数据");
            // 同步知识库数据
            knowledgeInfoService.summaryData();
            stopWatch.stop();
            log.info(stopWatch.getLastTaskName() + "耗时: " + stopWatch.getTotalTimeSeconds());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_SYNC_KNOWLEDGE);
            redisService.unlock(RedisConstant.LOCK_SYNC_KNOWLEDGE);
        }
    }
}
