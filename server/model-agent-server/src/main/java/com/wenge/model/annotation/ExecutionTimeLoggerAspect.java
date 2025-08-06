package com.wenge.model.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeLoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeLoggerAspect.class);

    @Around("@annotation(logExecutionTime)") // 拦截带有@LogExecutionTime注解的方法
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 执行目标方法
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - startTime;

        // 获取注解上的自定义名称（如果未设置则使用方法名）
        String methodName = logExecutionTime.value().isEmpty()
                ? joinPoint.getSignature().getName()
                : logExecutionTime.value();

        logger.info("方法 [{}] 执行耗时: {} ms", methodName, duration);

        return result;
    }
}