package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum UserCancelStateEnum {

    /**
     * 未注销
     */
    VALID("0", "有效"),

    /**
     * 注销
     */
    INVALID("1", "无效")
    ;

    private final String code;

    private final String desc;

    UserCancelStateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
