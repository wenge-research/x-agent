package com.wenge.model.enums;

import lombok.Getter;

/**
 * 应用商店发布枚举
 */
@Getter
public enum PublishAppStoreEnum {

    /**
     * 未发布
     */
    NO_PUBLISH(0, "未发布"),

    /**
     * 已发布
     */
    COMPLETE_PUBLIS(1, "已发布"),

    /**
     * 待审核
     */
    PUBLISH_CHECK(2, "待审核"),

    ;


    private int code;
    private String desc;

    PublishAppStoreEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
