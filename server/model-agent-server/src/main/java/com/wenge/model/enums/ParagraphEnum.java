package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum ParagraphEnum {
    TITLE("title","目录标题"),
    TABLE("table","表格"),
    TEXT("text","正文"),
    PIE("pie","饼图"),
    LINE("line","折线图"),
    BAR("bar","柱形图"),
    LINE_BAR("lineBar","折线图+柱形图"),
    IMAGE("image","插入图片"),
    ;

    private final String type;
    private final String name;

    ParagraphEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
