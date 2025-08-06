package com.wenge.model.enums;

import lombok.Getter;

/**
 * 网页配置类型枚举
 */
@Getter
public enum WebsiteTypeEnum {

    SHOW_BUT("1","渲染按钮"),
    WEB_SEARCH("2","检索网站")
    ;

    private final String code;
    private final String desc;

    WebsiteTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
