package com.wenge.model.enums;

import lombok.Getter;

/**
 * 复制应用标识枚举类 0-从应用里面复制 2-从应用商店里面复制
 */
@Getter
public enum CopyAppFlagEnum {
    //
    FROM_APP_PAGE(0, "从应用里面复制"),

    FROM_APP_STORE_PAGE(1, "从应用商店里面复制"),
    ;
    private final int type;
    private final String name;

    CopyAppFlagEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
