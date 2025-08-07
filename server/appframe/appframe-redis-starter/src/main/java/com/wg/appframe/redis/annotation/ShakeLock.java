package com.wg.appframe.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ShakeLock {

    /**
     * 幂等性前缀
     * @return
     */
    String prefix() ;

    /**
     * 幂等性分隔符
     * @return
     */
    String delimiter() default "&";

    /**
     * 幂等性过期时间单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 幂等性过期时间
     * @return
     */
    long expire() default 3000L;
}
