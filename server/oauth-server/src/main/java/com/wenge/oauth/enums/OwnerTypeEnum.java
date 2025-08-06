package com.wenge.oauth.enums;


import lombok.Getter;

/**
 * 模板归属，分为官方模板和个人模板
 */
@Getter
public enum OwnerTypeEnum {

    /**
     * 官方，一般在引用数据的场景
     */
    OFFICIAL("official", "官方"),

    /**
     * 授权，一般在引用数据的场景
     */
    GRANT("grant", "授权"),

    /**
     * 个人，一般在引用数据的场景
     */
    PERSONAL("personal", "个人"),

    /**
     * 个人/授权，一般在引用数据的场景
     */
    PERSONAL_GRANT("personalGrant", "个人/授权"),

    /**
     * 个人/授权/本租户，一般在菜单的首页列表场景，此场景为默认
     */
    PERSONAL_GRANT_TENANT("personalGrantTenant", "个人/授权/本租户"),

    /**
     *  所有
     */
    ALL("all", "所有，官方/个人/授权/本租户");


    private final String code;
    private final String desc;

    OwnerTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OwnerTypeEnum getByCode(String code) {
        for (OwnerTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}

