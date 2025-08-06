package com.wenge.model.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DirectionEnum {
    INPUT(0),
    OUTPUT(1);

    @EnumValue
    @JsonValue
    private final Integer value;
}
