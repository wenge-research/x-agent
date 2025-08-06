package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum UserSourceEnum {

    /**
     * 平台系统
     */
    SYSTEM("system", "平台系统"),

    /**
     * 手机验证码
     */
    SMS("sms", "手机验证码"),

    /**
     * 账号密码
     */
    PASSWORD("password", "账号密码"),

    /**
     * 企业微信
     */
    WE_COM("WeCom", "企业微信"),

    /**
     * 微信公众号
     */
    WE_OFFICE("weOffice", "微信公众号"),
    ;


    private final String code;
    private final String name;

    UserSourceEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
