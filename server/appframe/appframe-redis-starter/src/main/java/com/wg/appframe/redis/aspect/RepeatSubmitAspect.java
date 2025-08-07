///*
// * Copyright 2022- 2022 Wenge Group Holding Ltd.
// */
//
//package com.wg.appframe.redis.aspect;
//
//import cn.hutool.core.util.StrUtil;
//import com.wg.appframe.core.aop.BaseAspect;
//import com.wg.appframe.core.exception.BizException;
//import com.wg.appframe.core.exception.DistributedLockException;
//import com.wg.appframe.core.lock.DistributedLock;
//import com.wg.appframe.core.lock.ILock;
//import com.wg.appframe.redis.annotation.AvoidRepeat;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * <p>
// * 防止重复提交切面
// * </p>
// *
// * @author yangyunjun
// * @since 2022-05-30
// */
//@Aspect
//@Component
//public class RepeatSubmitAspect extends BaseAspect {
//
//    /**
//     * 注入DistributedLock
//     */
//    @Autowired(required = false)
//    private DistributedLock locker;
//
//    /***
//     * 定义controller切入点拦截规则，拦截avoidRepeat注解的业务方法
//     */
//    @Pointcut("@annotation(avoidRepeat)")
//    public void pointCut(AvoidRepeat avoidRepeat) {
//    }
//
//    /**
//     * AOP分布式锁拦截
//     *
//     * @param joinPoint
//     * @return
//     * @throws Exception
//     */
//    @Around("pointCut(avoidRepeat)")
//    public Object repeatSubmit(ProceedingJoinPoint joinPoint, AvoidRepeat avoidRepeat) throws Throwable {
//        if (avoidRepeat == null) {
//            // 获取类上的注解
//            avoidRepeat = joinPoint.getTarget().getClass().getDeclaredAnnotation(AvoidRepeat.class);
//        }
//
//        if (avoidRepeat == null) {
//            return joinPoint.proceed();
//        }
//
//        String lockKey = avoidRepeat.lockKey();
//        if (locker == null) {
//            throw new DistributedLockException("==>DistributedLock is null");
//        }
//        if (StrUtil.isEmpty(lockKey)) {
//            throw new DistributedLockException("==>lockKey is null");
//        }
//
//        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer()
//                .getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
//
//        if (lockKey.contains("#")) {
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            // 获取方法参数值
//            Object[] args = joinPoint.getArgs();
//            lockKey = getValBySpEL(lockKey, methodSignature, args);
//        }
//
//        ILock lockObj = null;
//        try {
//            // 加锁
//            lockObj = locker.lock(lockKey, avoidRepeat.lockTime(), TimeUnit.SECONDS, true);
//            if (lockObj != null) {
//                return joinPoint.proceed();
//            } else {
//                throw new BizException("请勿重复提交");
//            }
//        } finally {
//            locker.unlock(lockObj);
//        }
//    }
//}
