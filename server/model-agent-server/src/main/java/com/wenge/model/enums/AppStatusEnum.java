package com.wenge.model.enums;

import lombok.Getter;

/**
 * 应用状态枚举
 */
@Getter
public enum AppStatusEnum {

    /**
     * 公开发布
     */
    PUBLISH(1, "公开发布"),

    /**
     * 私有发布
     */
    PRIVATE(2, "私有发布"),

    /**
     * 未发布
     */
    UNPUBLISH(3, "未发布"),

    /**
     * 暂存
     */
    TEMPORARY(4, "暂存"),

    /**
     * 停用
     */
    DISABLE(5, "停用");

    private final int code;
    private final String desc;

    AppStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
