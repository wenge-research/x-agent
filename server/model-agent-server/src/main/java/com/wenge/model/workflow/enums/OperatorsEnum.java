package com.wenge.model.workflow.enums;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;

/**
 * 操作符枚举
 */
@Getter
@AllArgsConstructor
public enum OperatorsEnum {
    /* integer */
    GREATER("greater", ">", (a, b) -> {
        if (NumberUtil.isNumber(a) && NumberUtil.isNumber(b)) {
            return Integer.parseInt(a) > Integer.parseInt(b);
        }
        return false;
    }),
    GREATER_OR_EQUAL("greater_or_equal", "≥", (a, b) -> {
        if (NumberUtil.isNumber(a) && NumberUtil.isNumber(b)) {
            return Integer.parseInt(a) >= Integer.parseInt(b);
        }
        return false;
    }),
    LESS("less", "<", (a, b) -> {
        if (NumberUtil.isNumber(a) && NumberUtil.isNumber(b)) {
            return Integer.parseInt(a) < Integer.parseInt(b);
        }
        return false;
    }),
    LESS_OR_EQUAL("less_or_equal", "≤", (a, b) -> {
        if (NumberUtil.isNumber(a) && NumberUtil.isNumber(b)) {
            return Integer.parseInt(a) <= Integer.parseInt(b);
        }
        return false;
    }),
    EQUAL("equal", "=", (a, b) -> {
        if (NumberUtil.isNumber(a) && NumberUtil.isNumber(b)) {
            return Integer.parseInt(a) == Integer.parseInt(b);
        }
        return false;
    }),
    NOT_EQUAL("not_equal", "≠", (a, b) -> {
        if (NumberUtil.isNumber(a) && NumberUtil.isNumber(b)) {
            return Integer.parseInt(a) != Integer.parseInt(b);
        }
        return false;
    }),
    IS_NULL("is_null", "为空", (a, b) -> a == null),
    NOT_NULL("not_null", "不为空", (a, b) -> a != null),
    /* string */
    CONTAINS("contains", "包含", StringUtils::contains),
    NOT_CONTAINS("not_contains", "不包含", (a, b) -> !StringUtils.contains(a, b)),
    START_WITH("start_with", "开始是", StringUtils::startsWith),
    END_WITH("end_with", "结束是", StringUtils::endsWith),
    MATCH("match", "是", StringUtils::equals),
    NOT_MATCH("not_match", "不是", (a, b) -> !StringUtils.equals(a, b)),
    NOT_BLANK("not_blank", "不为空", (a, b) -> StringUtils.isNotBlank(a)),
    IS_BLANK("is_blank", "为空", (a, b) -> StringUtils.isBlank(a)),
    /* array[string] */
    ARRAY_CONTAINS("array_contains", "包含", (a, b) -> {
        if (JSONUtil.isTypeJSONArray(a)) {
            JSONArray array = JSONUtil.parseArray(a);
            return array.contains(b);
        }
        return false;
    }),
    ARRAY_NOT_CONTAINS("array_not_contains", "不包含", (a, b) -> {
        if (JSONUtil.isTypeJSONArray(a)) {
            JSONArray array = JSONUtil.parseArray(a);
            return !array.contains(b);
        }
        return false;
    }),
    ARRAY_IS_EMPTY("array_is_empty", "为空", (a, b) -> {
        if (StringUtils.isBlank(a)) {
            return true;
        }
        if (JSONUtil.isTypeJSONArray(a)) {
            JSONArray array = JSONUtil.parseArray(a);
            return CollectionUtil.isEmpty(array);
        }
        return false;
    }),
    ARRAY_IS_NOT_EMPTY("array_is_not_empty", "不为空", (a, b) -> {
        if (StringUtils.isBlank(a)) {
            return false;
        }
        if (JSONUtil.isTypeJSONArray(a)) {
            JSONArray array = JSONUtil.parseArray(a);
            return CollectionUtil.isNotEmpty(array);
        }
        return false;
    }),
    ARRAY_IS_MATCH("match", "是", (a, b) -> {
        if (StringUtils.isBlank(a) || StringUtils.isBlank(b)) {
            return false;
        }
        if (JSONUtil.isTypeJSONArray(a) && JSONUtil.isTypeJSONArray(a)) {
            JSONArray arrayA = JSONUtil.parseArray(a);
            JSONArray arrayB = JSONUtil.parseArray(a);
            return arrayA.equals(arrayB);
        }
        return false;
    }),

    ;
    @JsonValue
    @EnumValue
    private final String value;

    private final String desc;

    private final BiFunction<String, String, Boolean> function;

    public Boolean test(String left, String right) {
        return function.apply(left, right);
    }
}
