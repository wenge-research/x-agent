package com.wg.appframe.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GlobalLog {

    /**
     * 是否开启入参日志
     *
     * @return
     */
    boolean paramEnable() default true;

    /**
     * 是否开启出参日志
     *
     * @return
     */
    boolean resultEnable() default true;
}
