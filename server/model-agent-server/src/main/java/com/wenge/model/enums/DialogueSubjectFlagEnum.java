package com.wenge.model.enums;

import lombok.Getter;

/**
 * 对话中“是否讨论话题"状态枚举
 */
@Getter
public enum DialogueSubjectFlagEnum {

    NO(0, "否"),
    YES(1, "是"),
    ;

    private final Integer code;

    private final String name;

    DialogueSubjectFlagEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
