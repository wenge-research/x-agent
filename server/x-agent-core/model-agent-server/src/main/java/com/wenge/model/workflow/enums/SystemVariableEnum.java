package com.wenge.model.workflow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变量池中的系统变量枚举
 */
@AllArgsConstructor
@Getter
public enum SystemVariableEnum {
    QUERY("query", MetaParamEnum.STRING),
    FILES("files", MetaParamEnum.ARRAY_FILE),
    CONVERSATION_ID("conversation_id", MetaParamEnum.STRING),
    USER_ID("user_id", MetaParamEnum.STRING),
    DIALOGUE_COUNT("dialogue_count", MetaParamEnum.INTEGER),

    ;
    private final String variable;

    private final MetaParamEnum type;

}
