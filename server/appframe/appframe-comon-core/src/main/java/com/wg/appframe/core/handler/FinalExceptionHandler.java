/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.handler;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.utils.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

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
public class FinalExceptionHandler implements ResponseBodyAdvice<Object>, Ordered {


    /**
     * 捕捉其他所有异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result handle(HttpServletRequest request, Exception ex) {
        log.error("==>INTERNAL_SERVER_ERROR,message:{}", ExceptionUtil.getStackTraceInfo(ex));
        return Result.error();
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return !methodParameter.getGenericParameterType().equals(Result.class);

    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (methodParameter.getGenericParameterType().equals(String.class)) {
            return JSONObject.toJSONString(Result.success(o));
        }
        return o;
    }

    @Override
    public int getOrder() {
        return 9999;
    }
}

