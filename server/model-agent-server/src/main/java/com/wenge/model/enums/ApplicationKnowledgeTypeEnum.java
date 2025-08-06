package com.wenge.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationKnowledgeTypeEnum {
    KNOWLEDGE("knowledge", "知识库"),
    WORKFLOW("workflow", "工作流");

    @EnumValue
    @JsonValue
    private final String type;

    private final String name;

}
