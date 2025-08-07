package com.wenge.model.task.manager;

public interface BaseTaskConfig {

    String getTaskId();
    String getCrontab();
    Integer getStatus();
}
