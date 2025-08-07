package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum UserCheckStatusEnum {

    /**
     * 待审核
     */
    WAITING("waiting", "待审核"),

    /**
     * 审核通过
     */
    PASS("pass", "审核通过"),

    /**
     * 审核不通过
     */
    REJECT("reject", "审核不通过")
    ;

    private String code;

    private String name;

    UserCheckStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
