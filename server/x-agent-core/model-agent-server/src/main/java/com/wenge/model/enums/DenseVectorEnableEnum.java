package com.wenge.model.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 向量模型启用标识
 */

@Getter
public enum DenseVectorEnableEnum {
    //
    DISABLE(0, "未启用"),

    ENABLE(1, "启用");

    @EnumValue
    private final Integer code;

    private final String name;

    DenseVectorEnableEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    @Override
    public String toString() {
        return this.getName();
    }
}
