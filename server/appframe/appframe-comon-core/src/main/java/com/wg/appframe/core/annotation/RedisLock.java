package com.wg.appframe.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RedisLock {

    /**
     * 指定reids的key
     * @return
     */
    String redisKey() default "";

    /**
     * 释放时间，秒 s 单位
     */
    long lockTime() default 5;

}
