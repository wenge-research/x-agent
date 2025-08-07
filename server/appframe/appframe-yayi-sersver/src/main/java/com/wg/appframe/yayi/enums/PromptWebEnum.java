package com.wg.appframe.yayi.enums;

public enum PromptWebEnum {

    ONE("1", "one"),
    TWO("2", "two"),
    THREE("3", "three"),
    FOUR("4", "four"),
    FIVE("5", "five"),
    SIX("6", "six"),
    SEVEN("7", "seven"),
    EIGHT("8", "eight"),
    NINE("9", "nine"),
    TEN("10", "ten"),
    ;


    private String code;
    private String name;

    PromptWebEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(String code) {
        PromptWebEnum[] values = PromptWebEnum.values();
        for (PromptWebEnum value : values) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return code;
    }
}
