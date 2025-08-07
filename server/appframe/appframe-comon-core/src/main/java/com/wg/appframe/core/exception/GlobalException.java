/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.exception;

import com.wg.appframe.core.constant.ResultCodeBase;
import lombok.Getter;

/**
 * <p>
 * 自定义全局异常
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-28
 */

@Getter
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = -1982580879674268460L;

    private final String  code;

    private final String message;

    private final ResultCodeBase resultCodeBase;

    public GlobalException(ResultCodeBase resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.resultCodeBase = resultCode;
    }

    public GlobalException(String code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
        this.resultCodeBase = new ResultCodeBase() {
            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getMsg() {
                return msg;
            }
        };
    }

}
