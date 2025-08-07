package com.wenge.model.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum EnableEnum {
    // 启用
    START(0, "启用"),

    // 禁用
    DISABLE(1, "禁用");

    @EnumValue
    private final Integer code;

    private final String name;

    EnableEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    @Override
    public String toString() {
        return this.getName();
    }
}
