package com.wenge.model.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 节点类型枚举
 */
@Getter
public enum WorkFlowNodeEnum {
    // 自定义
    CUSTOM(1, "自定义"),

    // 节点管理
    NODE_MANAGE(2, "组件"),;


    @EnumValue
    private final Integer code;

    private final String name;

    WorkFlowNodeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
