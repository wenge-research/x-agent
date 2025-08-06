package com.wenge.model.enums;

import lombok.Getter;

/**
 * 智能问答溯源文档链接类型枚举
 */
@Getter
public enum DocLinkTypeEnum {

    /**
     * 原始文件链接
     */
    SOURCE_LINK("1", "原始文件链接"),

    /**
     * 网页链接
     */
    WEB_LINK("2", "网页链接"),

    ;

    private final String code;
    private final String desc;

    DocLinkTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
