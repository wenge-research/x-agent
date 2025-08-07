package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum DialoguAuditStatusEnum {

    /**
     * 待审核
     */
    WAIT_AUDIT("0", "待审核"),

    /**
     * 审核通过
     */
    AUDIT_PASS("1", "审核通过"),

    /**
     * 审核不通过
     */
    AUDIT_NOT_PASS("2", "审核不通过"),

    /**
     * 不处理
     */
    NOT_DISPOSE("3", "不处理"),

    ;

    private final String code;

    private final String name;

    DialoguAuditStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
