package com.wenge.model.event;

import com.wenge.model.task.manager.BaseTaskConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Slf4j
@Getter
public class TaskManageEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public enum ChangeType {
        CREATE, UPDATE, DELETE
    }

    private final BaseTaskConfig taskConfig;
    private final ChangeType changeType;

    public TaskManageEvent(Object source, BaseTaskConfig taskConfig, ChangeType changeType) {
        super(source);
        this.taskConfig = taskConfig;
        this.changeType = changeType;
        log.info("推送任务事件通知，任务配置: {}", taskConfig);
    }

    public BaseTaskConfig getTaskConfig() {
        return taskConfig;
    }

    public ChangeType getChangeType() {
        return changeType;
    }
}
