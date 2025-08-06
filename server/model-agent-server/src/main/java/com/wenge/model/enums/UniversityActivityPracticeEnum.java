package com.wenge.model.enums;

import lombok.Getter;

/**
 * 大学城实践、活动枚举类
 */
@Getter
public enum UniversityActivityPracticeEnum {

    /**
     * 实践
     */
    PRACTIVE(1, "实践"),

    /**
     * 活动
     */
    ACTIVITY(2, "活动"),

    /**
     * 其他
     */
    OTHER(3, "其他");


    private final int code;
    private final String desc;

    UniversityActivityPracticeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
