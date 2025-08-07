package com.wenge.model.enums;

import lombok.Getter;

/**
 * 词库类型枚举
 */
@Getter
public enum InterceptTypeEnum {

    /**
     * 无法回答
     */
    NO_ANSWER("noAnswer", "无法回答"),

    /**
     * 替换词
     */
    REPLACE_WORD("replaceWord", "替换词"),

    /**
     * 学区判断
     */
    SCHOOL_JUDGE("schoolJudge", "学区判断"),

    /**
     * 禁用词
     */
    FORBIDDEN_WORD("forbiddenWord", "禁用词"),

    /**
     * 非学区话题
     */
    NO_SCHOOL("noSchool", "非学区话题"),

    /**
     * 拦截提问
     */
    INTERCEPT_QUESTION("interceptQuestion", "拦截提问"),

    /**
     * 单词白名单
     */
    WHITE_WORD("whiteWord", "单词白名单"),

    /**
     * 拦截问题全词
     */
    INTERCEPT_ALL_QUESTION("interceptAllQuestion", "拦截问题全词"),

    /**
     * 对话场景对比
     */
    DIALOGUE_SCENE("dialogueScene", "对话场景对比"),

    /**
     * 推荐机构
     */
    SUGGEST_ORG("suggestOrg", "推荐机构"),

    /**
     * 讨论话题
     */
    SUBJECT("subject", "讨论话题"),
    /**
     * 业务场景
     */
    BUSINESS_SCENARIO("businessScenario", "业务场景");
    ;

    private final String type;
    private final String name;

    InterceptTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

}
