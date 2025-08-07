package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum PowerTypeEnum {

    /**
     * 系统管理员
     */
    SYSTEM_ADMIN("0", "超级管理员"),

    /**
     * 普通管理员
     */
    NORMAL_ADMIN("1", "普通管理员"),

    /**
     * 普通用户
     */
    NORMAL_USER("2", "普通用户"),

    /**
     * 群众
     */
    WECHAT_USER("3", "群众没有任何权限")
    ;
    private final String code;
    private final String name;

    PowerTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
