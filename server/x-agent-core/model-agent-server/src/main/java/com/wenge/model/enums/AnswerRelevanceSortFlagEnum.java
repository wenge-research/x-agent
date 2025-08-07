package com.wenge.model.enums;

import lombok.Getter;

/**
 * 答案溯源相关度排序枚举
 */
@Getter
public enum AnswerRelevanceSortFlagEnum {

    REVERSE(0, "降序"),

    SORT(1, "升序"),

    ;
    private final int code;
    private final String desc;

    AnswerRelevanceSortFlagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
