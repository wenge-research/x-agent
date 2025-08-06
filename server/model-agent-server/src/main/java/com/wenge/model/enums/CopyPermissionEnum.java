package com.wenge.model.enums;

import lombok.Getter;

/**
 * 复制按钮权限
 */
@Getter
public enum CopyPermissionEnum {

    /**
     * 风险
     */
    YES("是", 0),

    /**
     * 模糊
     */
    NO("否", 1),

    ;

    private final String name;
    private final Integer code;

    private CopyPermissionEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
}
