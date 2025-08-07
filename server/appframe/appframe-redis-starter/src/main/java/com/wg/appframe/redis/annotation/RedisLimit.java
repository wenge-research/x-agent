package com.wg.appframe.redis.annotation;


import com.wg.appframe.redis.enums.LimitTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RedisLimit {

    /**
     * 是否开启限流
     * @return
     */
    boolean enable() default true;

    /**
     *  前缀
     * @return
     */
    String prefix() default "";

    /**
     * 时间，单位秒
     * @return
     */
    int period();

    /**
     * 最多访问次数
     * @return
     */
    int count();

    /**
     * 限流方式
     * @return
     */
    LimitTypeEnum limitType() default LimitTypeEnum.URI;
}
