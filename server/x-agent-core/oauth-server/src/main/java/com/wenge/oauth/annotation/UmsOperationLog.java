package com.wenge.oauth.annotation;

import java.lang.annotation.*;

/**
 * @description: 用户操作记录-展示给用户看的接口信息（接口注解必须规范使用）
 *               自定义注解
 * @author: caohaifeng
 * @date: 2024/8/27 11:11
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UmsOperationLog {

    public static String[] logTypes = {"查阅-1", "新增-2", "修改-3", "删除-4", "导出-5", "导入-6", "其他-10"};

    /**
     * 操作描述（展示用户可以理解的描述）：
     *      例如：操作方式（查阅、新增、修改、删除等）+ 业务信息（用户列表、分析、对话日志等-[建议可以和页面名称保持一致]）+ 操作对象（指标信息、用户使用量等）
     *           查阅用户列表、新增用户、修改用户、删除用户
     *           查阅应用概览
     *           查阅应用分析-概览指示
     *           查阅应用分析-详细指标
     *           查阅应用分析-问题排行榜Top50
     **/
    public String description() default "--";

    /**
     * 操作类型：1查询、2新增、3修改、4删除、5导出、6导入、 10其他
     **/
    public int logType() default 10;


    /**
     * 所属模块：记录菜单表（oauth_menu）中菜单编码（menu_code）字段值
     **/
    public String belongModule() default "--";

    /**
     * 所属模块：记录菜单表（oauth_menu）中菜单编码（menu_name）字段值 --检索的时候使用
     **/
    public String belongModuleName() default "--";

    /**
     * 对象类型：应用
     *         知识库
     *         其他
     **/
    public String objectType() default "--";

    /**
     * 对象名称：如果objectType==应用，则从对应的接口拦截 applicationId，填充应用的名称
     *
     **/
    public String objectName() default "--";

    /**
     * 对象唯一ID：如果objectType==应用，则从对应的接口拦截 applicationId,填充applicationId
     *
     **/
    public String objectUuid() default "--";
}
