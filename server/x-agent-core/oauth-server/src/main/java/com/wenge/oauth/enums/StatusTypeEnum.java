package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum StatusTypeEnum {

    /**
     * 启用
     */
    ENABLE(1, "1", "启用"),

    /**
     * 禁用
     */
    DISABLE(0, "0", "禁用"),
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