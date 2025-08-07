package com.wenge.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;

// 默认R_720P,分辨率信息,填枚举值R_1080P、R_720P，默认720P&1M-2M码率，1080P支持3M-6M码率
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BitrateEnum {

    R720P("R_720P","1M-2M"),
    R1080P("R_1080P","3M-6M");

    @EnumValue
    @JsonValue
    private final String code; // 分辨率
    private final String codeRate; // 码率

    BitrateEnum(String code, String codeRate) {
        this.code = code;
        this.codeRate = codeRate;
    }

    public String getCode() {
        return code;
    }

    public String getCodeRate() {
        return codeRate;
    }
}
