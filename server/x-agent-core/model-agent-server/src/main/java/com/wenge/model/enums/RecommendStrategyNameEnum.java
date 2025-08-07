package com.wenge.model.enums;

import lombok.Getter;

/**
 * 智能问答推荐策略名称枚举
 */
@Getter
public enum RecommendStrategyNameEnum {

    /**
     * QA问题
     */
    QA_QUESTION("qa-question", "QA问题"),

    /**
     * QA内容
     */
    QA_CONTENT("qa-content", "QA内容"),

    /**
     * 知识库
     */
    KNOWLEDGE("knowledge", "知识库"),

    /**
     * 大模型
     */
    QA_LARGEMODEL("qa-largemodel", "大模型"),

    ;

    private String code;
    private String desc;

    RecommendStrategyNameEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
