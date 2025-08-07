package com.wenge.model.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 编程语言类型枚举
 */
@Getter
public enum CodeLanguageEnum {
    PYTHON(1, "Python"),

    JAVASCRIPT(2, "JavaScript");

    @EnumValue
    private final Integer code;

    private final String name;

    CodeLanguageEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
