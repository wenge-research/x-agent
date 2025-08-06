package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum KnowledgeDataTypeEnum {
    wdd("问答对", "wdd"),
    wj("文件", "wj"),
    jgh("结构化", "jgh"),
    url("url链接", "url"),
    dmt("多媒体", "dmt"),
    ;

    private final String name;
    private final String code;

    KnowledgeDataTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
