package com.wenge.model.task.manager;

import cn.hutool.core.net.NetUtil;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.KnowledgeApi;
import com.wenge.model.service.KnowledgeApiService;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.wenge.model.entity.table.KnowledgeApiTableDef.KNOWLEDGE_API;

@Component("nowledgeApiDynamicTaskManager")
@Slf4j
public class KnowledgeApiDynamicTaskManager extends AbstractDynamicTaskManager<KnowledgeApi> {

    @Value("${task.callApi.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;

    @Autowired
    private KnowledgeApiService knowledgeApiService;

    @Override
    protected boolean getEnable() {
        return enable;
    }

    @Override
    protected void initRedisKey() {
        String value = redisService.get(RedisConstant.RUN_SYNC_CALL_API_RUNNING, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_SYNC_CALL_API_RUNNING);
            redisService.unlock(RedisConstant.LOCK_SYNC_CALL_API_RUNNING);
        }
    }

    @Override
    protected List<KnowledgeApi> loadTasksFromDb() {
        Wrappers<Object> queryWrapper = Wrappers.init()
                .where(KNOWLEDGE_API.STATUS.eq("1"));

        return knowledgeApiService.list(queryWrapper);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    protected Runnable createRunnable(KnowledgeApi config) {
        return () -> {
            String cron = config.getCrontab();
            CronExpression cronExpression = CronExpression.parse(cron);
            String runKey = RedisConstant.RUN_SYNC_CALL_API_RUNNING + config.getKnowledgeApiId();
            String lockKey = RedisConstant.LOCK_SYNC_CALL_API_RUNNING + config.getKnowledgeApiId();

            try {
                // 加锁
                if (!redisService.lock(lockKey)) {
                    log.info("任务 {} 获取锁失败，跳过执行", config.getKnowledgeApiId());
                    return;
                }

                // 检查是否已有运行标记
                if (redisService.hasKey(runKey)) {
                    log.info("任务 {} 正在执行，跳过本次执行", config.getKnowledgeApiId());
                    return;
                }
                String localIp = NetUtil.getLocalhostStr();
                String timestamp = String.valueOf(System.currentTimeMillis());
                redisService.set(runKey, localIp + "_" + timestamp, 60 * 60 * 24 * 7);
                handler(config);
            } catch (Exception e) {
                log.error("知识库API拉取任务执行失败", e);
            }finally {
                safeDel(runKey);
                redisService.unlock(lockKey);
            }

            // 更新下次执行时间
            LocalDateTime nextTime = cronExpression.next(LocalDateTime.now(ZoneId.systemDefault()));
            String nextTimeString = formatNextTime(nextTime);
            redisService.set(RedisKey.KNOWLEDGE_API_DYNAMIC_TASK + config.getKnowledgeApiId(), nextTimeString);
            log.info("知识库API id is: {},corn表达式: {}, 下一次执行时间: {}",
                    config.getKnowledgeApiId(), cron, nextTime);
        };
    }

    @Override
    protected void handler(KnowledgeApi config) {
        String redisKey = RedisKey.KNOWLEDGE_API_HANDLER_TASK + config.getKnowledgeId();
        if (redisService.hasKey(redisKey)) {
            // 检查是否已有运行标记
            if (redisService.hasKey(redisKey)) {
                log.info("触发器任务 {} 正在执行，跳过本次执行", config.getKnowledgeApiId());
                return;
            }
        }
        // 处理相关业务
        knowledgeApiService.requestTask(config);
    }

    @Override
    protected String getTaskId(KnowledgeApi knowledgeApi) {
        return "knowledgeApi_" + knowledgeApi.getId();
    }

    @Override
    public Class<KnowledgeApi> getSupportedType() {
        return KnowledgeApi.class;
    }
}
