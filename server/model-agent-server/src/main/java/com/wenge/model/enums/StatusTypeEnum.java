package com.wenge.model.enums;

import lombok.Getter;

/**
 * 状态类型
 */
@Getter
public enum StatusTypeEnum {

    /**
     * 启用
     */
    ENABLE(1, "1", "启用"),

    /**
     * 禁用
     */
    DISABLE(2, "2", "禁用"),
    ;

    private final Integer intType;
    private final String strType;
    private final String name;

    StatusTypeEnum(Integer intType, String strType, String name) {
        this.intType = intType;
        this.strType = strType;
        this.name = name;
    }

}
