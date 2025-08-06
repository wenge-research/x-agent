package com.wenge.model.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.net.NetUtil;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.GuangXinMatter;
import com.wenge.model.service.GuangXinMatterService;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 关芯智巡定时器
 */
@RestController
@RequestMapping("/agentTask/guangxin")
@Slf4j
public class GXTask {

    @Autowired
    private GuangxinTask guangxinTask;

    @Value("${task.guanxin.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GuangXinMatterService guangXinMatterService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisKey.GUANXI_MASTTER, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.GUANXI_MASTTER);
            redisService.unlock(RedisConstant.LOCK_GUAXIN);
        }
    }

    /**
     * 关芯智巡定时器
     */
    // @Scheduled(cron = "${task.guanxin.cron}")
    @RequestMapping("/guangxinTask")
    public void guangxinTask() {
        if (!enable) {
            return;
        }

        boolean lock = redisService.lock(RedisConstant.LOCK_GUAXIN);
        if (!lock) {
            log.info("==>任务未获取到锁，跳过当前任务");
            return;
        }

        String redisKey = RedisKey.GUANXI_MASTTER;
        if (redisService.hasKey(redisKey)) {
            log.info("关芯智巡定时器正在执行，本次跳过");
            return;
        }
        String localhostStr = NetUtil.getLocalhostStr();
        String currentDateString = com.wg.appframe.core.utils.DateUtil.getCurrentDateString();
        redisService.set(redisKey, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

        try {
            log.info("关芯智巡定时器开始执行");
            List<GuangXinMatter> notExecuteMatters = guangXinMatterService.getNotExecuteMatter();
            if (CollectionUtil.isEmpty(notExecuteMatters)) {
                log.info("没有需要执行的关芯智巡的事项");
                return;
            }

            notExecuteMatters.forEach(p -> guangxinTask.handleMatter(p));
        } catch (Exception e) {
            log.info("关芯智巡定时器异常,error:{}", e.getMessage(), e);
        } finally {
            redisService.del(redisKey);
            redisService.unlock(RedisConstant.LOCK_GUAXIN);
        }
        log.info("关芯智巡定时器结束");
    }


}
