package com.wg.appframe.redis.aspect;

import com.wg.appframe.redis.annotation.ShakeLock;
import com.wg.appframe.redis.exception.RequestLockException;
import com.wg.appframe.redis.utils.RequestKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Slf4j
@Component
@Aspect
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RequestLockAspect {

    @Autowired(required = false)
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.wg.appframe.redis.annotation.ShakeLock)")
    public void requestPointCut() {}

    @Before("requestPointCut()")
    public void interceptor(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ShakeLock requestLock = method.getAnnotation(ShakeLock.class);
        if (null == requestLock) {
            return;
        }
        if (StringUtils.isEmpty(requestLock.prefix())) {
            throw new RequestLockException("重复提交前缀不能为空");
        }
        // 获取自定义key
        final String lockKey = RequestKeyGenerator.getLockKey(joinPoint);
        // 使用Redisson分布式锁的方式判断是否重复提交
        RLock lock = redissonClient.getFairLock(lockKey);
        boolean isLocked = false;
        try {
            // 尝试抢占锁
            isLocked = lock.tryLock();
            // 没有拿到锁说明已经有了请求了
            if (!isLocked) {
                throw new RequestLockException("您的操作太快了,请稍后重试");
            }
            //拿到锁后设置过期时间
            lock.lock(requestLock.expire(), requestLock.timeUnit());
        } catch (Exception e) {
            throw new RequestLockException("您的操作太快了,请稍后重试");
        } finally {
            //释放锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
