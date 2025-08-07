package com.wenge.model.workflow.component.file;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileType {
    DEFAULT("default"),
    IMAGE("image"),
    DOC("doc"),
    CODE("code"),
    PPT("ppt"),
    TXT("txt"),
    EXCEL("excel"),
    AUDIO("audio"),
    ZIP("zip"),
    VIDEO("video"),
    SVG("svg"),
    OTHER("other"),
    ;

    @JsonValue
    private final String type;

    public static FileType getFileType(String type) {
        for (FileType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
