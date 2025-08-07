/*
 * Copyright 2022- 2022 Wenge Group Holding Ltd.
 */

package com.wg.appframe.redis.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 避免重复提交注解
 * </p>
 *
 * @author yangyunjun
 * @since 2022-05-30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AvoidRepeat {
    /**
     * 超时时间
     *
     * @return 返回
     */
    int lockTime();


    /**
     * redis 锁key的
     *
     * @return redis 锁key
     */
    String lockKey() default "";
}
