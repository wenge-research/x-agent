package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum VariableStatusEnum {

    /**
     * 未初始化，默认状态，这个状态表示变量还未被初始化值，此时变量尚未有初始值
     */
    UNINITIALIZED("uninitialized", "未初始化"),

    /**
     * 初始化，表示变量已经初始化值，此时已有一个初始值，如果后续不再需要更新内容，则直接跳到COMPLETE状态
     */
    INITIALIZED("initialized", "初始化"),

    /**
     * 持续更新值，表示变量已经初始化值，但是后续可能还有新的值，此时变量在更新中
     */
    UPDATING("updating", "持续更新值"),

    /**
     * 完成赋值，表示变量已经初始化值，并且后续没有新的值，此时变量已经完成赋值，不会再改变内容
     */
    COMPLETE("complete", "完成赋值"),

    /**
     * 异常，表示变量在赋值过程中出现了异常，此时变量已经无法继续赋值，无法获取值
     */
    ERROR("error", "异常");
    ;

    private final String code;
    private final String desc;

    VariableStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VariableStatusEnum getByCode(String code) {
        for (VariableStatusEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
