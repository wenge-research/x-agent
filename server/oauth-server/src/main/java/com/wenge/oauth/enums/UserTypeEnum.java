package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum UserTypeEnum {

    /**
     * 群众，微信用户选择的类型
     */
    RESIDENT("resident", "群众"),

    /**
     * 工作人员-街道工作人员，微信用户选择的类型
     */
    STAFF_STREET("staff-street", "工作人员-街道工作人员"),

    /**
     * 工作人员-社区工作人员，微信用户选择的类型
     */
    STAFF_COMMUNITY("staff-community", "工作人员-社区工作人员"),

    /**
     * 政务人员，后台管理创建的类型
     */
    GOV("gov", "政务人员"),

    /**
     * 政务人员-街道工作人员，微信用户选择的类型
     */
    GOV_STREET("gov-street", "政务人员-街道工作人员"),

    /**
     * 政务人员-社区工作人员，微信用户选择的类型
     */
    GOV_COMMUNITY("gov-community", "政务人员-社区工作人员");
    ;
    private final String code;
    private final String name;

    UserTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
