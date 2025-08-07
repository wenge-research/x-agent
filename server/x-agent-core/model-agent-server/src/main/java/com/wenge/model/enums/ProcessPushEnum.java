package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum ProcessPushEnum {

    /**
     * 风险检测
     */
    RISK("risk", "1", "风险检测"),

    /**
     * 问题理解
     */
    UNDERSTANDING("understanding", "3", "问题理解"),

    /**
     * 检索知识库
     */
    SEARCH_KNOWLEDGE("searchKnowledge", "6", "检索知识库"),

    /**
     * mcp服务
     */
    MCP("mcp", "4", "mcp服务"),

    /**
     * 联网检索
     */
    NETWORK("network", "5", "联网检索"),
    ;

    private String code;
    private String stepIndex;
    private String progress;

    ProcessPushEnum(String code, String stepIndex, String progress) {
        this.code = code;
        this.stepIndex = stepIndex;
        this.progress = progress;
    }
}
