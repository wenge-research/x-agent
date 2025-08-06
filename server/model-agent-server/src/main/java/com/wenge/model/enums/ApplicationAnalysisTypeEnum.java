package com.wenge.model.enums;

import lombok.Getter;


/**
 * @author: caohaifeng
 * @date: 2024/8/12 11:51
 * @Description:  应用统计维度类型枚举类
 * @Version:1.0
 **/
@Getter
public enum ApplicationAnalysisTypeEnum {

    /**
     * 总使用量
     */
    USAGE_TOTAL(1, "USAGE_TOTAL", "总使用量"),

    /**
     * 总使用用户数
     */
    USAGE_USER_TOTAL(2, "USAGE_USER_TOTAL", "总使用用户数"),

    /**
     * 总新增使用用户数
     */
    USAGE_ADD_USER_TOTAL(3, "USAGE_ADD_USER_TOTAL", "总新增使用用户数"),

    /**
     * 使用用户平均日存率
     */
    USAGE_USER_DAY_RETENTION_RATE(4, "USAGE_USER_DAY_RETENTION_RATE", "使用用户平均日存率"),

    /**
     * 访问量
     */
    VISIT_TOTAL(5, "VISIT_TOTAL", "访问量"),



    ;


    private final int code;
    private final String type;
    private final String desc;

    ApplicationAnalysisTypeEnum(int code, String type, String desc) {
        this.code = code;
        this.type = type;
        this.desc = desc;
    }

}
