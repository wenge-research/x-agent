package com.wenge.model.enums;

import lombok.Getter;

/**
 * 文件类型枚举
 */
@Getter
public enum FileTypeEnum {

    /**
     * 文件
     */
    FILE(0, "文件"),

    /**
     * 音频
     */
    AUDIO(1, "音频"),

    /**
     * 图片
     */
    IMAGE(2, "图片"),

    /**
     * 视频
     */
    VIDEO(3, "视频"),

    /**
     * 雅意知识库文档
     */
    YAYI_DOC(4, "雅意知识库文档"),
    ;
    private final int type;
    private final String name;

    FileTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }
}
