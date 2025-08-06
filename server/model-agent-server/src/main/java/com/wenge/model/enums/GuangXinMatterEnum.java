package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum GuangXinMatterEnum {

    /**
     * 参数不全
     */
    PARAM_NOT_FULL("PARAM_NOT_FULL", "参数不全"),

    /**
     * 未开始
     */
    NOT_START("NOT_START", "未开始"),

    /**
     * 正在识别
     */
    WAITING("WAITING", "正在识别"),

    /**
     * 识别失败
     */
    FAIL("FAIL", "识别失败"),

    /**
     * 识别成功
     */
    SUCCESS("SUCCESS", "识别成功"),

    /**
     * 通知中
     */
    NOTIFYING("NOTIFYING", "通知中"),

    /**
     * 通知成功
     */
    NOTIFY_SUCCESS("NOTIFY_SUCCESS", "通知成功"),

    /**
     * 通知失败
     */
    NOTIFY_FAIL("NOTIFY_FAIL", "通知失败"),
    ;

    private final String code;

    private final String desc;

    GuangXinMatterEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
