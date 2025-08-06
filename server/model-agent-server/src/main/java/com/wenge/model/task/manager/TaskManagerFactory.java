package com.wenge.model.task.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskManagerFactory {

    private final List<AbstractDynamicTaskManager<?>> taskManagers;
    private final Map<Class<? extends BaseTaskConfig>, AbstractDynamicTaskManager<?>> managerMap = new HashMap<>();

    @Autowired
    public TaskManagerFactory(List<AbstractDynamicTaskManager<?>> taskManagers) {
        this.taskManagers = taskManagers;
    }

    @PostConstruct
    public void init() {
        for (AbstractDynamicTaskManager<?> manager : taskManagers) {
            Class<?> supportedType = manager.getSupportedType();
            if (BaseTaskConfig.class.isAssignableFrom(supportedType)) {
                managerMap.put((Class<? extends BaseTaskConfig>) supportedType, manager);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseTaskConfig> AbstractDynamicTaskManager<BaseTaskConfig> getManager(Class<T> configClass) {
        for (Map.Entry<Class<? extends BaseTaskConfig>, AbstractDynamicTaskManager<?>> entry : managerMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(configClass)) {
                return (AbstractDynamicTaskManager<BaseTaskConfig>) entry.getValue();
            }
        }
        return null;
    }

    public void addTask(BaseTaskConfig config) {
        execute(config, AbstractDynamicTaskManager::addTask);
    }

    public void updateTask(BaseTaskConfig config) {
        execute(config, AbstractDynamicTaskManager::updateTask);
    }

    public void removeTask(BaseTaskConfig config) {
        execute(config, AbstractDynamicTaskManager::removeTask);
    }

    private interface TaskExecutor<T extends BaseTaskConfig> {
        void execute(AbstractDynamicTaskManager<T> manager, T taskConfig);
    }

    private <T extends BaseTaskConfig> void execute(BaseTaskConfig config, TaskExecutor<BaseTaskConfig> executor) {
        AbstractDynamicTaskManager<BaseTaskConfig> manager = getManager(config.getClass());
        if (manager != null) {
            executor.execute(manager, config);
        } else {
            throw new IllegalArgumentException("未找到对应的任务管理器处理配置类型：" + config.getClass());
        }
    }
}