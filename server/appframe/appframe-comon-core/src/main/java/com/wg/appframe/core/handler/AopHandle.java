package com.wg.appframe.core.handler;

import com.alibaba.fastjson2.JSONObject;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * aop拦截器
 */
public interface AopHandle {

    /**
     * 拦截器实现
     *
     * @param joinPoint
     * @return
     */
    void before(ProceedingJoinPoint joinPoint, JSONObject param);

    /**
     * 接口执行后拦截
     *
     * @param joinPoint
     * @param ret
     */
    void after(ProceedingJoinPoint joinPoint, Object ret, JSONObject param);

    /**
     * 拦截器排序
     * @return
     */
    int getSort();
}
