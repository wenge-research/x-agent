package com.wenge.oauth.annotation;

import java.lang.annotation.*;

/**
 * @description:  接口调用日志记录
 *               自定义注解
 * @author: caohaifeng
 * @date: 2024/8/27 11:11
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterfaceCallLog {
    public String description() default "--";
}
