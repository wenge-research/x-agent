package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum AppPluginEnum {

    /**
     * 对话体验
     */
    EXPERIENCE("experience", "对话体验", "功能"),

    /**
     * 安全拦截
     */
    INTERCEPTION("interception", "安全拦截", "功能"),

    /**
     * 问题建议
     */
    RECOMMENDATION("recommendation", "问题建议", "功能"),

    /**
     * 虚拟人
     */
    VIRTUAL("virtual", "虚拟人", "功能"),

    /**
     * 语音对话
     */
    VOICE("voice", "语音对话", "功能"),

    /**
     * 答案溯源
     */
    ANSWER_SOURCE("answerSource", "答案溯源", "功能"),

    /**
     * 禁用IP
     */
    DISABLE_IP("DisableIP", "禁用IP", "功能"),

    /**
     * 检索引擎
     */
    SEARCH_ENGINE("SearchEngine", "检索引擎", "功能"),

    /**
     * 答案润色
     */
    TOUCH_ANSWER("TouchAnswer", "答案润色", "功能"),

    /**
     * 场景设置
     */
    SCENESETTING("Scenesetting", "场景设置", "功能"),

    /**
     * 问数检索
     */
    WENSUH_FLAG("wenshuFlag", "问数检索", "功能"),

    /**
     * 输出结果评价
     */
    FEEDBACK("feedback", "输出结果评价", "功能"),

    /**
     * 快捷指令
     */
    QUICK_COMMAND("quickCommand", "快捷指令", "功能"),

    /**
     * 问题联想
     */
    ASSOCIATION("association", "问题联想", "功能"),

    /**
     * 模糊问题引导
     */
    VAGUE_QUESTION("vagueQuestion", "模糊问题引导", "功能"),

    ;

    private String code;
    private String name;
    private String group;

    AppPluginEnum(String code, String name, String group) {
        this.code = code;
        this.name = name;
        this.group = group;
    }
}
