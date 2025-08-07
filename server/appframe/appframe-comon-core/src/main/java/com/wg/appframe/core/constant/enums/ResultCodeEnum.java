/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.constant.enums;

import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.ResultCodeBase;
import lombok.Getter;

/**
 * <p>
 * 返回结果枚举
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-28
 */
@Getter
public enum ResultCodeEnum implements ResultCodeBase {

    /**
     * 操作成功
     */
    SUCCESS("000000", "操作成功"),

    /**
     * 业务异常
     */
    BUSINESS_FAILED("0000001", "不符合规范"),

    /**
     * 内部服务器错误异常
     */
    INTERNAL_SERVER_ERROR("000002", "内部服务器错误异常"),

    /**
     * 系统繁忙
     */
    SYSTEM_SO_BUSY_ERROR("000003", "系统繁忙"),

    /**
     * 错误请求
     */
    BAD_REQUEST("000004", "错误请求异常"),

    /**
     * 请求头部缺少加密密文[cipher]
     */
    CIPHER_IS_BLANK("000005", "请求头部缺少加密密文[cipher]"),

    /**
     * 密文错误
     */
    CIPHER_IS_FAILED("000006", "密文错误"),

    /**
     * 请求体没有参数
     */
    BODY_IS_BLANK("000007", "请求体没有参数"),

    /**
     * 单参数只能接收一个字段
     */
    ONLY_ONE_PARAM("000008", "单参数只能接收一个字段"),

    /**
     * 只允许POST请求
     */
    ONLY_POST("000009", "只允许POST请求"),

    /**
     * POST的请求URL不能带有参数
     */
    POST_NOT_PARAM("000010", "POST的请求URL不能带有参数"),

    /**
     * 参数不能为空
     */
    PARAM_NOT_BLANK("000011", "参数不能为空"),

    /**
     * 参数不正确
     */
    PARAM_NOT_SUCCESS("000012", "参数不正确"),

    /**
     * 用户已过期
     */
    USER_NOT_TOKEN("000013", "用户已过期"),

    /**
     * 请求头部缺少加密密文[timestamp]
     */
    TIMESTAMP_IS_BLANK("000014", "请求头部缺少加密密文[timestamp]"),

    /**
     * 网络超时
     */
    TIMESTAMP_IS_TIMEOUT("000015", "网络超时"),

    /**
     * 请求URL不存在
     */
    NOT_FOUND("000016", "请求URL不存在异常"),

    /**
     * 入场类异常
     */
    PARAM_ERROR("000017", "传递的对象非[dto.params]包或不是以[Param]结尾的类"),

    /**
     * 必须返回一个对象
     */
    RESULT_ERROR("000017", "必须返回一个对象"),

    /**
     * 请返回指定的类型Result
     */
    RESULT_TYPE_ERROR("000018", "请返回指定的类型:" + Result.class.getTypeName()),

    /**
     * 返回结果不能为键值对
     */
    RESULT_NOT_MAP_ERROR("000019", "返回结果不能为键值对"),

    /**
     * 必须指定一个返回类型
     */
    RESULT_MUST_TYPE("000020", "必须指定一个返回类型"),

    /**
     * 返回结果必须继承[WGResult]
     */
    RESULT_MUST_WGRESULT("000021", "返回结果必须继承[WGResult]"),

    /**
     * 必须初始化返回对象data
     */
    RESULT_MUST_DATA("000022", "返回对象必须初始化data"),

    /**
     * 编码失败
     */
    ENCRYPT_ERROR("000023", "encrypt data found error"),

    TOKEN_INVALID("000024", "无效或者不存在Token"),

    /**
     * 最大分页数
     */
    MAX_PAGE_SIZE("000026", "最大分页数"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR("999999", "系统异常"),


    ;

    private final String code;

    private final String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}

