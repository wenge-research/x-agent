package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum DialogueVerifyStatusEnum {

    /**
     * 待核实
     */
    WAIT_VERIFY("0", "待核实"),

    /**
     * 已核实修改
     */
    VERIFY_MODIFY("1", "已核实修改"),

    /**
     * 已核实正确
     */
    VERIFY_CORRECT("2", "已核实正确"),

    /**
     * 恶意问题
     */
    MALICIOUS_QUESTION("3", "恶意问题"),

    /**
     * 不处置
     */
    NOT_DISPOSE("4", "不处置"),

    /**
     * 待重新核实
     */
    WAIT_REVERIFY("5", "待重新核实"),

    ;

    private final String code;

    private final String name;

    DialogueVerifyStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}