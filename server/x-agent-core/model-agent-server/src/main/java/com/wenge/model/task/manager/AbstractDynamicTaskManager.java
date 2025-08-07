package com.wenge.model.task.manager;

import cn.hutool.json.JSONUtil;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 抽象动态任务管理器
 */
@Slf4j
public abstract class AbstractDynamicTaskManager<T extends BaseTaskConfig> {


    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private RedisService redisService;

    protected final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    protected final Map<String, T> configMap = new ConcurrentHashMap<>();

    protected abstract boolean getEnable();

    public abstract Class<T> getSupportedType();

    /**
     * 判断是否支持指定的配置类型
     */
    public boolean supports(Class<?> configClass) {
        return getSupportedType().isAssignableFrom(configClass);
    }

    @PostConstruct
    public void init() {
        log.info("开始初始化定时任务列表");
        if (!getEnable()) {
            log.info("==>任务未开启，定时任务不加载");
            return;
        }
        initRedisKey();
        List<T> records = loadTasksFromDb();
        batchAddTask(records);
        log.info("结束初始化知识库api定时任务: {}", JSONUtil.toJsonStr(configMap));
    }

    /**
     * 初始化redis key
     */
    protected abstract void initRedisKey();

    /**
     * 从数据库加在数据
     *
     * @return
     */
    protected abstract List<T> loadTasksFromDb();

    /**
     * 批量添加任务
     *
     * @param records
     */
    public void batchAddTask(List<T> records) {
        for (T record : records) {
            if (isEnabled(record.getStatus())) {
                addTask(record);
            }
        }
    }

    /**
     * 添加任务
     *
     * @param record
     */
    public void addTask(T record) {
        if (taskMap.containsKey(getTaskId(record))) return;
        log.info("AbstractDynamicTaskManager 添加任务到调度中心: {}", getTaskId(record));
        String cron = record.getCrontab();
        Runnable runnable = createRunnable(record);
        ScheduledFuture<?> future = taskScheduler.schedule(runnable, new CronTrigger(cron));
        taskMap.put(getTaskId(record), future);
        configMap.put(getTaskId(record), record);
    }

    /**
     * 移除任务
     *
     * @param record
     */
    public void removeTask(T record) {
        ScheduledFuture<?> future = taskMap.get(getTaskId(record));
        if (future != null) {
            future.cancel(true);
            taskMap.remove(getTaskId(record));
        }
    }

    /**
     * 更新任务
     *
     * @param record
     */
    public void updateTask(T record) {
        removeTask(record);
        if (isEnabled(record.getStatus())) {
            addTask(record);
        }
    }

    /**
     * 刷新定时任务列表
     */
    public abstract void refreshTasks();

    /**
     * 业务代码
     *
     * @param config
     * @return
     */
    protected abstract Runnable createRunnable(T config);

    /**
     * 是否关闭
     *
     * @param status
     * @return
     */
    private boolean isEnabled(Integer status) {
        return 1 == status;
    }

    /**
     * 执行逻辑
     *
     * @param config
     */
    protected abstract void handler(T config);

    /**
     * 格式化下次执行时间
     *
     * @param time
     * @return
     */
    protected String formatNextTime(LocalDateTime time) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(time);
    }

    /**
     * 删除指定的key
     *
     * @param key
     */
    protected void safeDel(String key) {
        if (redisService.hasKey(key)) {
            redisService.del(key);
        }
    }

    /**
     * 根据业务类型获取任务id
     *
     * @param t
     * @return
     */
    protected abstract String getTaskId(T t);
}
