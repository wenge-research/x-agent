package com.wenge.model.enums;

import lombok.Getter;

/**
 * 用户问题门槛枚举
 */
@Getter
public enum DoorsillEnum {

    /**
     * 风险
     */
    RISK("风险", "rick"),

    /**
     * 模糊
     */
    BLUR("模糊", "blur"),

    /**
     * 正常
     */
    NORMAL("正常", "normal");
    ;

    private final String name;
    private final String code;

    private DoorsillEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
