package com.wenge.model.enums;


import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum MultimediaEnum {
    FILE(0, "file", Lists.newArrayList()),
    AUDIO(1, "audio", Lists.newArrayList("mp3", "wav")),
    IMAGE(2, "image", Lists.newArrayList("jpg", "jpeg", "png")),
    VIDEO(3, "video", Lists.newArrayList("mp4")),

    ;
    private final Integer code;

    private final String name;

    private final List<String> fileType;


    public static MultimediaEnum getByFileType(String type) {
        for (MultimediaEnum value : MultimediaEnum.values()) {
            if (value.getFileType().contains(type)) {
                return value;
            }
        }
        return null;
    }
}
