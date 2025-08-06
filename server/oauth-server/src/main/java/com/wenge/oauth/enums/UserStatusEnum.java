package com.wenge.oauth.enums;

import lombok.Getter;

@Getter
public enum UserStatusEnum {

    /**
     * 未激活
     */
    UNACTIVATED("0", "未激活"),

    /**
     * 正常
     */
    NORMAL("1", "正常"),

    /**
     * 锁定
     */
    LOCKED("2", "锁定"),

    /**
     * 删除
     */
    DELETED("3", "删除"),

    /**
     * 注销
     */
    DEREGISTERED("4", "注销")
    ;

    private final String code;

    private final String desc;

    UserStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserStatusEnum getDescByCode(String code) {
        for (UserStatusEnum e : UserStatusEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

}
