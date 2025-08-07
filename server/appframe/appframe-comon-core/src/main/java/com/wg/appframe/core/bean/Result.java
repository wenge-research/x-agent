/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.bean;

import cn.hutool.core.util.StrUtil;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.core.constant.ResultCodeBase;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.dto.results.EmptyResult;
import lombok.Getter;
import org.slf4j.MDC;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 结果返回类
 * </p>
 *
 * @author yangyunjun
 * @since 2021-06-21
 */
@Getter
public final class Result<T> implements Serializable {

    private static final long serialVersionUID = -8116799604125587170L;

    public static final String TRACE_ID = "traceId";

    public static final String SPAN_ID = "spanId";

    /**
     * 状态码，比如000000代表响应成功
     */
    private final String code;

    /**
     * 响应信息，用来说明响应情况
     */
    private final String msg;

    /**
     * 响应的具体数据
     */
    private T data;

    /**
     * 响应时间
     */
    private final String time;

    /**
     * traceId
     */
    private final String traceId;

    /**
     * traceId
     */
    private final String spanId;

    protected Result(ResultCodeBase resultCodeBase) {
        this(resultCodeBase.getCode(), resultCodeBase.getMsg());
    }

    protected Result(ResultCodeBase resultCodeBase, T data) {
        this(resultCodeBase);
        this.data = data;
    }

    protected Result(String code, String msg) {
        this.traceId = MDC.get(TRACE_ID);
        this.spanId = MDC.get(SPAN_ID);
        this.code = code;
        this.msg = msg;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtil.TIME_MILLISECOND));
    }

    public Result(String code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    /**
     * 成功
     *
     * @return
     */
    public static Result<EmptyResult> success() {
        EmptyResult emptyResult = new EmptyResult();
        return new Result<>(ResultCodeEnum.SUCCESS, emptyResult);
    }

    /**
     * 成功
     *
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultCodeEnum.SUCCESS, data);
    }

    /**
     * 业务异常
     *
     * @return
     */
    public static Result<EmptyResult> fail() {
        EmptyResult emptyResult = new EmptyResult();
        return new Result<>(ResultCodeEnum.BUSINESS_FAILED, emptyResult);
    }

    /**
     * 业务异常
     *
     * @return
     */
    public static Result<EmptyResult> fail(ResultCodeBase resultCodeBase) {
        EmptyResult emptyResult = new EmptyResult();
        return new Result<>(resultCodeBase, emptyResult);
    }

    /**
     * 业务异常
     *
     * @return
     */
    public static <T> Result<T> fail(ResultCodeBase resultCodeBase, String... params) {
        EmptyResult emptyResult = new EmptyResult();
        String resultCodeBaseMsg = resultCodeBase.getMsg();
        String massage = StrUtil.format(resultCodeBaseMsg, params);
        ResultCodeBase nowResultCode = new ResultCodeBase() {
            @Override
            public String getCode() {
                return resultCodeBase.getCode();
            }

            @Override
            public String getMsg() {
                return massage;
            }
        };
        return (Result<T>) new Result<>(nowResultCode, emptyResult);
    }



    /**
     * 业务异常
     *
     * @return
     */
    public static <T> Result<T> fail(String massage) {
        ResultCodeEnum businessFailed = ResultCodeEnum.BUSINESS_FAILED;
        EmptyResult emptyResult = new EmptyResult();
        ResultCodeBase nowResultCode = new ResultCodeBase() {
            @Override
            public String getCode() {
                return businessFailed.getCode();
            }

            @Override
            public String getMsg() {
                return massage;
            }
        };
        return (Result<T>) new Result<>(nowResultCode, emptyResult);
    }
    /**
     * 业务异常
     *
     * @return
     */
    public static <T> Result<T> fail(T data) {
        return new Result<T>(ResultCodeEnum.BUSINESS_FAILED, data);
    }

    /**
     * 系统报错异常
     *
     * @return
     */
    public static Result<EmptyResult> error() {
        EmptyResult emptyResult = new EmptyResult();
        return new Result<>(ResultCodeEnum.SYSTEM_ERROR, emptyResult);
    }

    /**
     * 系统报错异常
     *
     * @return
     */
    public static <T> Result<T> error(T data) {
        return new Result<T>(ResultCodeEnum.SYSTEM_ERROR, data);
    }

    /**
     * 系统报错异常
     *
     * @return
     */
    public static Result<EmptyResult> error(ResultCodeBase resultCodeBase) {
        EmptyResult emptyResult = new EmptyResult();
        return new Result<>(resultCodeBase, emptyResult);
    }

    /**
     * 系统报错异常
     *
     * @return
     */
    public static Result<EmptyResult> error(String code, String msg) {
        return new Result<>(code, msg);
    }

    /**
     * 自定义返回
     *
     * @return
     */
    public static <T> Result<T> result(ResultCodeBase resultCodeBase) {
        return new Result<T>(resultCodeBase);
    }

    /**
     * 返回空数据
     *
     * @return
     */
    public static Result<EmptyResult> empty() {
        EmptyResult emptyResult = new EmptyResult();
        return new Result<>(ResultCodeEnum.SUCCESS, emptyResult);
    }

    /**
     * 自定义返回
     *
     * @return
     */
    public static <T> Result<T> result(ResultCodeBase resultCodeBase, T data) {
        return new Result<T>(resultCodeBase, data);
    }

    public boolean isSuccess() {
        return ResultCodeEnum.SUCCESS.getCode().equals(code);
    }
}
