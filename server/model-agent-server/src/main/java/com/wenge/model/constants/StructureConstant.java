package com.wenge.model.constants;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/12 10:34
 */
public interface StructureConstant {

    // 未解析
    Integer PARSER_BEGIN = 0;
    // 正在解析
    Integer PARSER_ING = 1;
    // 解析成功
    Integer PARSER_SUCCESS = 2;
    // 解析失败
    Integer PARSER_FAIL = 3;

    // 字段未勾选
    Integer FIELD_UNCHECK = 1;
    // 字段已勾选
    Integer FIELD_CHECK = 2;

    Integer DATA_SOURCE = 1;
    Integer EXCEL = 2;

    // 未启用
    Integer NOT_ENABLED = 0;
    // 已启用
    Integer ENABLED = 1;

    // 永久有效时间
    String PERMANENT_VALID_DATE = "2099-12-31 00:00:00";

}
