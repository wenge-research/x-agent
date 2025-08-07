package com.wg.appframe.core.constant.enums;

import lombok.Getter;

@Getter
public enum YesNoEnum {

    /**
     * 是
     */
    YES("1", "是"),

    /**
     * 否
     */
    NO("0", "否"),
    ;

    /**
     * 码值
     */
    private final String code;

    /**
     * 名称
     */
    private final String name;

    YesNoEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
