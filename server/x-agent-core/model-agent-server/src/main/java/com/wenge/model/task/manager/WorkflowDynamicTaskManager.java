package com.wenge.model.task.manager;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.TriggerConfigParam;
import com.wenge.model.service.ComponentService;
import com.wenge.model.service.TriggerConfigService;
import com.wenge.model.workflow.component.dto.WorkflowRunStatus;
import com.wenge.model.workflow.entity.TriggerConfig;
import com.wenge.oauth.constants.RedisConstant;
import com.wg.appframe.core.bean.Result;
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

@Component("workflowDynamicTaskManager")
@Slf4j
public class WorkflowDynamicTaskManager extends AbstractDynamicTaskManager<TriggerConfig> {

    @Value("${task.workflowTrigger.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TriggerConfigService triggerConfigService;

    @Autowired
    private ComponentService componentService;

    @Override
    protected boolean getEnable() {
        return enable;
    }

    @Override
    protected void initRedisKey() {
        String value = redisService.get(RedisConstant.RUN_SYNC_WORKFLOW_TRIGGER_RUNNING, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_SYNC_WORKFLOW_TRIGGER_RUNNING);
            redisService.unlock(RedisConstant.LOCK_SYNC_WORKFLOW_TRIGGER_RUNNING);
        }
    }

    @Override
    protected List<TriggerConfig> loadTasksFromDb() {
        TriggerConfigParam param = new TriggerConfigParam();
        param.setPageNo(1);
        param.setPageSize(9999);
        param.setStatus(1); // 启用状态
        Result<Page<TriggerConfig>> pageResult = triggerConfigService.queryAll(param);
        if (pageResult == null || pageResult.getData() == null) {
            return Lists.newArrayList();
        }
        return pageResult.getData().getRecords();
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    protected Runnable createRunnable(TriggerConfig config) {
        return () -> {
            String cron = config.getCrontab();
            CronExpression cronExpression = CronExpression.parse(cron);
            String runKey = RedisConstant.RUN_SYNC_WORKFLOW_TRIGGER_RUNNING + config.getTriggerId();
            String lockKey = RedisConstant.LOCK_SYNC_WORKFLOW_TRIGGER_RUNNING + config.getTriggerId();
            try {
                // 加锁
                if (!redisService.lock(lockKey)) {
                    log.info("任务 {} 获取锁失败，跳过执行", config.getTriggerId());
                    return;
                }

                // 检查是否已有运行标记
                if (redisService.hasKey(runKey)) {
                    log.info("任务 {} 正在执行，跳过本次执行", config.getTriggerId());
                    return;
                }

                String localIp = NetUtil.getLocalhostStr();
                String timestamp = String.valueOf(System.currentTimeMillis());
                redisService.set(runKey, localIp + "_" + timestamp, 60 * 60 * 24 * 7);
                handler(config);
            } catch (Exception e) {
                log.error("工作流执行失败", e);
            } finally {
                safeDel(runKey);
                redisService.unlock(lockKey);
            }
            // 更新下次执行时间
            LocalDateTime nextTime = cronExpression.next(LocalDateTime.now(ZoneId.systemDefault()));
            String nextTimeString = formatNextTime(nextTime);
            redisService.set(RedisKey.WORKFLOW_DYNAMIC_TASK + config.getTriggerId(), nextTimeString);
            log.info("触发器 id: {}, 下一次执行时间: {}", config.getTriggerId(), nextTimeString);
        };
    }

    @Override
    protected void handler(TriggerConfig config) {
        String redisKey = RedisKey.WORKFLOW_DYNAMIC_HANDLER_TASK + config.getTriggerId();
        if (redisService.hasKey(redisKey)) {
            // 检查是否已有运行标记
            if (redisService.hasKey(redisKey)) {
                log.info("触发器任务 {} 正在执行，跳过本次执行", config.getTriggerId());
                return;
            }
        }
        RunComponentNodeParam param = new RunComponentNodeParam();
        param.setComponentId(config.getComponentId());
        param.setInputs(config.getConfig());
        param.setClientId(IdUtil.simpleUUID());
        componentService.runFlow(param, WorkflowRunStatus.DEBUG, true);
        safeDel(redisKey);
    }

    @Override
    protected String getTaskId(TriggerConfig config) {
        return "triggerConfig_" + config.getId();
    }

    @Override
    public Class<TriggerConfig> getSupportedType() {
        return TriggerConfig.class;
    }
}
