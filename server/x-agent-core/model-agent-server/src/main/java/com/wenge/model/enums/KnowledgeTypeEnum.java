package com.wenge.model.enums;

import lombok.Getter;

/**
 * 知识库类型枚举
 */
@Getter
public enum KnowledgeTypeEnum {
    Q_A("知识库数据", "1"),
    DOCUMENT("文档", "2"),
    URL_PARSE("URL解析", "3"),
    API_COLLECT("API采集", "4"),
    STRUCTURED_DATA("结构化数据", "5"),
    MULTIMEDIA("多媒体","6"),
    YAYI_KNOWLEDGE("雅意文档库","7"),
    ;

    private final String name;
    private final String code;

    KnowledgeTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
