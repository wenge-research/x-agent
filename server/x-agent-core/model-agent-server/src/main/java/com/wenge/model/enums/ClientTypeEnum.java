package com.wenge.model.enums;

import lombok.Getter;

@Getter
public enum ClientTypeEnum {

    /**
     * PC
     */
    PC("PC", "网页端"),

    /**
     * H5
     */
    H5("H5", "移动端H5")
    ;

    private String code;

    private String name;

    ClientTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
