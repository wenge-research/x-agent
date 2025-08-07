package com.wenge.model.event;

import com.wenge.model.task.manager.BaseTaskConfig;
import com.wenge.model.task.manager.TaskManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class TaskManageEventListener  {

    @Autowired
    private TaskManagerFactory taskManagerFactory;

    @EventListener
    public void onApplicationEvent(TaskManageEvent event) {
        BaseTaskConfig config = event.getTaskConfig();
        TaskManageEvent.ChangeType type = event.getChangeType();

        switch (type) {
            case CREATE:
                log.info("推送知识库事件通知，知识库信息: {}", config);
                taskManagerFactory.addTask(config);
                break;
            case UPDATE:
                log.info("收到编辑事件，更新任务: {}", config);
                taskManagerFactory.updateTask(config);
                break;
            case DELETE:
                log.info("收到删除事件，移除任务: {}", config);
                taskManagerFactory.removeTask(config);
                break;
            default:
                log.warn("未知事件类型: {}", type);
        }
    }
}
