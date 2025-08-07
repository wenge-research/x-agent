package com.wenge.model.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import com.wg.appframe.core.constant.StringConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetaParamEnum {
    /**
     * json类型
     */
    OBJECT("object"),

    /**
     * 字符串
     */
    STRING("string"),

    /**
     * 整数
     */
    INTEGER("integer"),

    /**
     * 整数
     */
    NUMBER("number"),

    /**
     * 布尔
     */
    BOOLEAN("boolean"),

    /**
     * 引用
     */
    REFERENCE("reference"),

    /**
     * 数组类型
     */
    ARRAY_OBJ("array[object]"),

    /**
     * 数组类型
     */
    ARRAY_INT("array[integer]"),

    /**
     * 数组类型
     */
    ARRAY_NUMBER("array[number]"),

    /**
     * 数组类型
     */
    ARRAY_BOOL("array[boolean]"),

    /**
     * 数组类型
     */
    ARRAY_STR("array[string]"),

    /**
     * 文件类型
     */
    FILE("file"),

    /**
     * 文件列表
     */
    ARRAY_FILE("array[file]"),

    /**
     * 空值类型，允许前端传空，运行时不应当有该类型
     */
    NULL(StringConstant.BLANK),

    ;
    @EnumValue
    @JsonValue
    private final String name;

    /**
     * 传入数组类型，获取其非数组的类型
     * @param type
     * @return
     */
    public static MetaParamEnum getNotArrayType(MetaParamEnum type) {
        return MetaParamEnum.valueOf(type.getName().replace("array[", StringConstant.BLANK).replace("]", StringConstant.BLANK).toUpperCase());
    }

    /**
     * 传入非数组类型，获取其数组的类型
     * @param type
     * @return
     */
    public static MetaParamEnum getArrayType(MetaParamEnum type) {
        if (null == type) {
            throw new RuntimeException("参数类型不能为空");
        }
        String typeName = type.getName();
        String newType = StringConstant.BLANK;
        // 如何引入的数据类型是数组，那么这里返回对象数组
        if (typeName.contains("array[") && typeName.contains("]")) {
            if (typeName.contains("object")) {
                return MetaParamEnum.ARRAY_OBJ;
            } else if (typeName.contains("string")) {
                return MetaParamEnum.ARRAY_STR;
            } else if (typeName.contains("integer")) {
                return MetaParamEnum.ARRAY_INT;
            } else if (typeName.contains("number")) {
                return MetaParamEnum.ARRAY_NUMBER;
            } else if (typeName.contains("boolean")) {
                return MetaParamEnum.ARRAY_BOOL;
            } else if (typeName.contains("file")) {
                return MetaParamEnum.ARRAY_FILE;
            }
        } else {
            // 如果是基础类型，那么返回对应的数组类型
            newType = "array[" + type.getName() + "]";
            for (MetaParamEnum value : values()) {
                if (value.getName().contains(newType)) {
                    return value;
                }
            }
        }
        throw new RuntimeException("不支持的参数类型" + newType);
    }

    public static MetaParamEnum getType(String type) {
        for (MetaParamEnum value : values()) {
            if (value.getName().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
