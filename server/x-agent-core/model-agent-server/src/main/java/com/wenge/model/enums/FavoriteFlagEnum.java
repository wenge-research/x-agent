package com.wenge.model.enums;

import lombok.Getter;

/**
 * 收藏标识枚举 0：未收藏，1：已收藏
 */
@Getter
public enum FavoriteFlagEnum {

    /**
     * 未收藏
     */
    NOT_FAVORITE(0, "未收藏"),

    /**
     * 已收藏
     */
    FAVORITE(    1, "已收藏");

    private final int code;
    private final String desc;

    FavoriteFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
