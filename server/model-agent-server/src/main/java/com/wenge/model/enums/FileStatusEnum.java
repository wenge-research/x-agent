package com.wenge.model.enums;

import lombok.Getter;

/**
 * 文件状态枚举
 */
@Getter
public enum FileStatusEnum {

    UPLOADING(0, "上传中"),
    PARSING(1, "解析中"),
    SUCCESS(2, "解析成功"),
    FAIL(3, "解析失败"),
    UPLOAD_FAIL(4, "上传失败"),
    UPLOAD_SUCCESS(5, "上传成功"),
    DECRYPT_FAIL(6,"解密失败"),
    ;
    private Integer code;
    private String desc;

    FileStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
