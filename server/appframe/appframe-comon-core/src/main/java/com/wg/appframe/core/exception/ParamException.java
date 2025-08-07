package com.wg.appframe.core.exception;

import com.wg.appframe.core.constant.ResultCodeBase;
import lombok.Getter;

/**
 * @Description: 参数校验异常
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-12-17 11:50:59
 */
@Getter
public class ParamException extends RuntimeException{

    private static final long serialVersionUID = -2091479822286880810L;

    private final String code;

    private final String message;

    private final ResultCodeBase resultCodeBase;

    public ParamException(ResultCodeBase resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.resultCodeBase = resultCode;
    }
}
