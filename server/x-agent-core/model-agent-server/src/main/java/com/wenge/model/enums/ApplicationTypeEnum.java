package com.wenge.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationTypeEnum {

    /**
     * 工作流应用
     */
    WORKFLOW("workflow"),

    /**
     * 对话流应用
     */
    DIALOGUE("dialogue"),

    /**
     * 多智能体
     */
    MULTI_AGENT("multi_agent"),

    /**
     * 工作流创建出来的应用
     */
    WORKFLOW_APPLICATION("workflow_application")
    ;
    private final String name;
}
