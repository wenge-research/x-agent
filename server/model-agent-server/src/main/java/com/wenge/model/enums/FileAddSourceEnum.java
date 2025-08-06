package com.wenge.model.enums;

import lombok.Getter;

/**
 * 文件添加方式
 */
@Getter
public enum FileAddSourceEnum {

    //
    UPLOAD(0, "上传"),

    PULL(1, "同步拉取"),
    ;
    private final int code;
    private final String name;

    FileAddSourceEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
