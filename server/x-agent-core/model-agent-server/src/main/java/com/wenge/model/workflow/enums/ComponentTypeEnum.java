package com.wenge.model.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ComponentTypeEnum {
    /**
     * API插件
     */
    PLUGIN(1),

    WORKFLOW(2),

    ITERATION(3),
    /**
     * 应用创建的工作流
     */
    APPLICATION(4),

    /**
     * 应用的对话流
     */
    APPLICATION_FLOW(5),

    /**
     * 代码插件
     */
    CODE(6),

    /**
     * 应用的多智能体
     */
    APPLICATION_MULTI_AGENT(7),

    TEST(99);
    @EnumValue
    @JsonValue
    private final Integer code;

    public static ComponentTypeEnum getByCode(Integer code) {
        for (ComponentTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
