package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum DialoguFromTabEnum {

    /**
     * 日志分拨 tab页
     */
    ALLOCATE("allocate", "日志分拨"),

    ;


    private final String type;

    private final String name;

    DialoguFromTabEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

}
