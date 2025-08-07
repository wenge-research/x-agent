/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wenge.oauth.exception;

import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.dto.results.EmptyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 * 全局异常处理
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-28
 */
@RestControllerAdvice
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OauthExceptionHandler{

    /**
     * 参数验证异常
     *
     * @param e 参数验证异常
     * @return 返回
     */
    @ExceptionHandler(OauthException.class)
    public Result<EmptyResult> handle(OauthException e) {
        log.error("==> 未授权,message:{}", e.getMessage());
        return Result.fail(ResultCodeEnum.TOKEN_INVALID);
    }
}

