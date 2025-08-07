package com.wenge.model.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 节点执行状态
 */
@Getter
public enum WorkflowNodeExecutionStatus {
    /**
     * 初始化
     */
    INIT("init"),

    /**
     * 执行成功
     */
    SUCCESS("succeeded"),

    /**
     * 执行失败
     */
    FAIL("failed"),

    /**
     * 执行中
     */
    RUNNING("running"),

    /**
     * 不需要执行
     */
    NOT_RUN("not_run"),

    /**
     * 等待子节点执行
     */
    WAITING_NEXT("waiting_next"),
    ;

    @JsonValue
    private final String value;

    WorkflowNodeExecutionStatus(String value) {
        this.value = value;
    }
}