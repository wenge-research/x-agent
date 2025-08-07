package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum SmartAgentApiTypeEnum {

    LLM("llm","LLM大模型"),
    GXZX("gxzx","关芯智巡"),
    DIALOGUE("dialogue","api流式对话"),
    WORKFLOW_STREAM("workflow_stream","工作流流式对话"),
    WORKFLOW_STRING("workflow_string","工作流非流式对话"),
    MCP_SERVER("mcp_server","mcp 服务"),
    ;

    private final String code;
    private final String desc;

    SmartAgentApiTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
