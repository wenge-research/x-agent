package com.wg.appframe.core.constant.enums;

import lombok.Getter;

@Getter
public enum BooleanEnum {

    /**
     * 是
     */
    TRUE("1", "是", true),

    /**
     * 否
     */
    FALSE("0", "否", false),

    ;


    /**
     * 码值
     */
    private final String code;

    /**
     * 名称
     */
    private final String name;

    /**
     * 值
     */
    private final boolean value;

    BooleanEnum(String code, String name, boolean value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }
}
