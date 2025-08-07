/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.handler;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.exception.ParamException;
import com.wg.appframe.core.utils.ExceptionUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.dto.results.EmptyResult;
import com.wg.appframe.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class GlobalExceptionHandler implements ResponseBodyAdvice<Object>, Ordered {

    /**
     * 返回状态码:405
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return Result.error(ResultCodeEnum.BAD_REQUEST);
    }

    /**
     * 返回状态码:415
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public Result handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return Result.error(ResultCodeEnum.BAD_REQUEST);
    }

    /**
     * 捕捉校验异常(MethodArgumentNotValidException)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException e) {
        log.error("==>MethodArgumentNotValidException,message:{}", ExceptionUtil.getStackTraceInfo(e));
        return Result.error(ResultCodeEnum.BAD_REQUEST.getCode(),
                e.getBindingResult().getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining("; ")));
    }

    /**
     * 捕捉404异常
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handle(NoHandlerFoundException e) {
        log.error("==>NoHandlerFoundException,message:{}", ExceptionUtil.getStackTraceInfo(e));
        return Result.error(ResultCodeEnum.NOT_FOUND);
    }

    ///**
    // * 捕捉其他所有异常
    // */
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //@ExceptionHandler(Exception.class)
    //public Result handle(HttpServletRequest request, Exception ex) {
    //    log.error("==>INTERNAL_SERVER_ERROR,message:{}", ExceptionUtil.getStackTraceInfo(ex));
    //    return Result.error();
    //}


    /**
     * 捕捉其他所有自定义异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(GlobalException.class)
    public Result handle(GlobalException e) {
        log.error("==>GlobalException,message:{}", ExceptionUtil.getStackTraceInfo(e));
        return Result.error(e.getCode() + "", e.getMessage());
    }

    /**
     * 获取状态码
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 获取校验错误信息
     */
    private Map<String, Object> getValidError(List<FieldError> fieldErrors) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<String> errorList = new ArrayList<String>();
        StringBuffer errorMsg = new StringBuffer("校验异常(ValidException):");
        for (FieldError error : fieldErrors) {
            errorList.add(error.getField() + "-" + error.getDefaultMessage());
            errorMsg.append(error.getField() + "-" + error.getDefaultMessage() + ".");
        }
        map.put("errorList", errorList);
        map.put("errorMsg", errorMsg);
        return map;
    }


    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return !methodParameter.getGenericParameterType().equals(Result.class);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter,
                                  MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (methodParameter.getGenericParameterType().equals(String.class)) {
            return JSONObject.toJSONString(Result.success(o));
        }
        return o;
    }

    /**
     * 捕捉校验异常(ConstraintViolationException)
     */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public Result validException(ConstraintViolationException e) {
        log.error("==>ConstraintViolationException,message:{}", ExceptionUtil.getStackTraceInfo(e));
        return Result.error(ResultCodeEnum.BAD_REQUEST.getCode(),
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; ")));
    }

    /**
     * 参数验证异常
     *
     * @param e 参数验证异常
     * @return 返回
     */
    @ExceptionHandler(ParamException.class)
    public Result<EmptyResult> handle(ParamException e) {
        log.error("==> 参数验证异常,message:{}", e.getMessage());
        return Result.fail(e.getResultCodeBase());
    }

    @Override
    public int getOrder() {
        return 99;
    }
}

