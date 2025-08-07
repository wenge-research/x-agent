package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum SearchWayEnum {

    /**
     * 混合检索
     */
    MIXED("mixed", "混合检索"),

    /**
     * 全文检索
     */
    FULL_TEXT("fullText", "全文检索"),

    /**
     * 语义检索
     */
    SEMANTIC("semantic", "语义检索")

    ;
    private String searchWay;
    private String searchWayDesc;
    SearchWayEnum(String searchWay, String searchWayDesc) {
        this.searchWay = searchWay;
        this.searchWayDesc = searchWayDesc;
    }
}
