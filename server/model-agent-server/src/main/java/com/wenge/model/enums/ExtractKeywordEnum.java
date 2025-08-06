package com.wenge.model.enums;


import com.alibaba.nacos.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提取参数枚举
 */

@Getter
@AllArgsConstructor
public enum ExtractKeywordEnum {

    /**
     * 枚举
     */
    UNKNOWN( 0, "未知"),
    ACTIVITY(1, "活动"),
    PRACTICE(2, "实践"),
    ;
    private final Integer code;

    private final String name;


    public static Integer getCodeByName(String name) {
        for (ExtractKeywordEnum value : ExtractKeywordEnum.values()) {
            if (StringUtils.equals(value.getName(), name)) {
                return value.getCode();
            }
        }
        return null;
    }

}
