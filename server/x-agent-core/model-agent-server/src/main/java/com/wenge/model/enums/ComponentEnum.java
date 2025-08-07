package com.wenge.model.enums;

public enum ComponentEnum {

    /**
     * 插件(基于 api)
     */
    PLUGIN_API("1", "插件(基于 api)"),

    /**
     * 普通工作流
     */
    WORKFLOW("2", "普通工作流"),

    /**
     * 普通对话流
     */
    DIALOGUE("3", "普通对话流"),

    /**
     * 应用的工作流
     */
    REFERENCE_WORKFLOW("4", "应用的工作流"),

    /**
     * 应用的对话流
     */
    DIALOGUE_APP("5", "应用的对话流"),

    /**
     * 插件(基于代码)
     */
    CODE_PLUGIN("6", "插件(基于代码)")

    ;


    private String type;
    private String name;

    ComponentEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
