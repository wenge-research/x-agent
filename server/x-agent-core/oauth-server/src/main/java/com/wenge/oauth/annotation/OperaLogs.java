package com.wenge.oauth.annotation;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author 参自若依调整 所有的参都非必填最简单用法直接@LogDiy 存数据库表USER_LOG_DIY
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperaLogs {
    /**
     * 日志title,如果注解没填title直接读@ApiOperation注解的value
     */
    public String title() default "";

    /**
     * 业务类型，如果为空直接读取@api的tag
     */
    public String businessType() default "";

    /**
     * 操作人类别:用于记录普通用户、管理员、对接等可能用到的人员类型标记
     */
    public String operatorType() default "";

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
}
