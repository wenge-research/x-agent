package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum ParserUrlStatusEnum {
    BEGIN(1,"未解析"),
    PARSERING(2,"正在解析"),
    DONE(3,"解析完成"),
    PARSER_FAIL(4,"解析失败"),
    ;

    private Integer code;
    private String desc;

    ParserUrlStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
