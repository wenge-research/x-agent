package com.wenge.model.enums;

import lombok.Getter;

/**
 * 应用重排模型枚举
 */
@Getter
public enum ApplicationRearrangeModelEnum {

    //
    YAYI("yayi", "雅意模型重排"),

    VOLCENGINE("volcengine", "火山引擎模型重排"),
    ;

    private final String code;
    private final String desc;

    ApplicationRearrangeModelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
