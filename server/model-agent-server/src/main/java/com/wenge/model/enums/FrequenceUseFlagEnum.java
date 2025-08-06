package com.wenge.model.enums;

import lombok.Getter;

/**
 * 语音常用标识枚举
 */
@Getter
public enum FrequenceUseFlagEnum {

    /**
     * 非常用
     */
    frequence(0, "非常用"),

    /**
     * 常用
     */
    not_frequence(1, "常用"),

    ;
    private final int code;
    private final String desc;

    FrequenceUseFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
