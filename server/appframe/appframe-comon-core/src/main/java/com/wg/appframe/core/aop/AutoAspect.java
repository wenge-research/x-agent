/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.core.aop;

import com.alibaba.fastjson2.JSONObject;
import com.wg.appframe.core.handler.AopHandle;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AOP拦截controller
 *
 * @author chenzhiwei
 * @since 2022-09-07
 */
@Component
@Aspect
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "appframe.common", name = "enable", havingValue = "true")
public class AutoAspect {

    @Autowired(required = false)
    private Map<String, AopHandle> handleMap;

    //    @Pointcut("execution( * com.wenge..controller.*.*(..))")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void logPointCut() {
    }

    /**
     * 操作日志aop
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object ret = null;
        // 接口执行前拦截
        JSONObject param = new JSONObject();
        before(joinPoint, param);
        ret = joinPoint.proceed();
        after(joinPoint, ret, param);
        return ret;
    }

    /**
     * 前置
     *
     * @param joinPoint
     */
    private void before(ProceedingJoinPoint joinPoint, JSONObject param) {
        // 按顺序依次执行拦截器
        if (null != handleMap && handleMap.size() != 0) {
            // 按sort排序
            List<AopHandle> aopHandleList = handleMap.values().stream().sorted(Comparator.comparingInt(AopHandle::getSort)).collect(Collectors.toList());
            for (AopHandle aopHandle : aopHandleList) {
                aopHandle.before(joinPoint, param);
            }
        }
    }

    /**
     * 后置
     *
     * @param joinPoint
     * @param ret
     */
    private void after(ProceedingJoinPoint joinPoint, Object ret, JSONObject param) {
        // 按顺序依次执行拦截器
        if (null != handleMap && handleMap.size() != 0) {
            // 按sort排序
            List<AopHandle> aopHandleList = handleMap.values().stream().sorted(Comparator.comparingInt(AopHandle::getSort)).collect(Collectors.toList());
            for (AopHandle aopHandle : aopHandleList) {
                aopHandle.after(joinPoint, ret, param);
            }
        }
    }
}
