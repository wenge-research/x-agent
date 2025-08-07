package com.wenge.model.enums;

import lombok.Getter;

/**
 * 文件类型枚举 区分文件类型 0文档、1音频、2图片、3视频
 */
@Getter
public enum DialogueSearchFileTypeEnum {

    SEARCH_All(-1, "全部"),
    SEARCH_FILE(0, "文档（含雅意知识库文档）"),
    SEARCH_AUDIO(1, "音频"),
    SEARCH_IMAGE(2, "图片"),
    SEARCH_VIDEO(3, "视频"),
    ;

    private final Integer code;
    private final String desc;

    DialogueSearchFileTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
