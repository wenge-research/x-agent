package com.wenge.model.enums;

import lombok.Getter;

/**
 * 步骤状态枚举
 */
@Getter
public enum StepStatusEnum {

    // 状态，1：正在检索答案，2：正在回答，3：回答完成，4：无法回答，5：最终输出答案，每个步骤都会按这个顺序进行切换状态
    /**
     * 1：正在检索答案，初始状态，每个步骤都会经过这个这步骤
     */
    ANSWER_SEARCH(1, "正在检索答案"),

    /**
     * 2：正在回答，有正常答案才会有这个状态
     */
    ANSWER_PROCESS(2, "正在回答"),

    /**
     * 3：回答完成，和4【无法回答】互补，要么【回答完成】，要么【无法回答】
     */
    ANSWER_COMPLETE(3, "回答完成"),

    /**
     * 4：无法回答，和3【回答完成】互补，要么【无法回答】，要么【回答完成】，无法回答状态一般会跳过当前步骤
     */
    ANSWER_FAIL(4, "无法回答"),

    /**
     * 5：最终输出答案，最多有一个步骤有这个状态，当都无法回答时，就不存在这个状态，当有几个步骤有正常答案时，其中只有一个步骤有这状态
     */
    ANSWER_FINAL(5, "最终输出答案"),

    ;
    private final Integer code;
    private final String desc;

    StepStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static StepStatusEnum getByCode(Integer code) {
        for (StepStatusEnum statusEnum : values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }
}
