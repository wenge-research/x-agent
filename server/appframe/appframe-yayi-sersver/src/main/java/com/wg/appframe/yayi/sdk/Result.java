package com.wg.appframe.yayi.sdk;

import lombok.Data;

/**
 * 简易结果返回类
 **/
@Data
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    // 带参数的构造方法
    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功的静态方法
    public static <T> Result<T> success(T data) {
        return new Result<>("200", "成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>("200", "成功", null);
    }

    // 失败的静态方法
    public static <T> Result<T> failure(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> failure(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
}
