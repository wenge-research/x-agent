package com.wg.appframe.core.aop;

import cn.hutool.core.thread.ThreadUtil;
import com.wg.appframe.core.bean.RedisLockDefinitionHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.lock.RedisLockHandle;
import com.wg.appframe.core.utils.IPUtils;
import com.wg.appframe.core.annotation.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 分布式锁，多台服务器同时运行同一段代码时，有且只有一台能够正常执行
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-11-14 14:02:17
 */
@Component
@Aspect
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnBean(value = {RedisLockHandle.class, IPUtils.class})
public class RedisLockAspect {

    @Autowired
    private IPUtils ipUtils;

    /**
     * 注入DistributedLock
     */
    @Autowired(required = false)
    private RedisLockHandle redisLockHandle;

    /**
     * 应用名称
     */
    @Value("${spring.application.name:demo}")
    private String appName;

    /**
     * 锁前缀
     */
    private static String LOCK_KEY = "redis_lock:";

    /**
     * 启动项目是释放锁
     */
    @PostConstruct
    public void releaseLock() {
        LOCK_KEY = "redis_lock-" + appName + "-";
        redisLockHandle.batchDel(LOCK_KEY);
    }

    // 扫描的任务队列
    private static ConcurrentLinkedQueue<RedisLockDefinitionHolder> holderList = new ConcurrentLinkedQueue();

    /**
     * 为业务锁续时间
     */
    private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("redisLock-schedule-pool").daemon(true).build());

    {

        // 两秒执行一次「续时」操作
        SCHEDULER.scheduleAtFixedRate(() -> {
            // 这里记得加 try-catch，否者报错后定时任务将不会再执行=-=
            Iterator<RedisLockDefinitionHolder> iterator = holderList.iterator();
            while (iterator.hasNext()) {
                RedisLockDefinitionHolder holder = iterator.next();

                // 判空
                if (holder == null) {
                    iterator.remove();
                    continue;
                }

                // 判断 key 是否还有效，无效的话进行移除
                // if (redisTemplate.opsForValue().get(holder.getBusinessKey()) == null) {
                if (redisLockHandle.get(holder.getBusinessKey()) == null) {
                    iterator.remove();
                    continue;
                }

                // 超时重试次数，超过时给线程设定中断
                // if (holder.getCurrentCount() > holder.getTryCount()) {
                //     holder.getCurrentTread().interrupt();
                //     iterator.remove();
                //     continue;
                // }

                // 判断是否进入最后三分之一时间
                long curTime = System.currentTimeMillis();
                boolean shouldExtend = (holder.getLastModifyTime() + holder.getModifyPeriod()) <= curTime;
                // 进入最后三分之一时间，重置有效期
                if (shouldExtend) {
                    holder.setLastModifyTime(curTime);
                    redisLockHandle.expire(holder.getBusinessKey(), holder.getLockTime(), TimeUnit.SECONDS);
                    // log.info("businessKey : [" + holder.getBusinessKey() + "], reset time");
                    // holder.setCurrentCount(holder.getCurrentCount() + 1);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Around("@annotation(com.wg.appframe.core.annotation.RedisLock)")
    public Object around(ProceedingJoinPoint pjp) {
        // 解析参数
        MethodSignature sign = (MethodSignature) pjp.getSignature();
        Method method = sign.getMethod();
        String methodName = method.getName();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String businessKey = redisLock.redisKey();
        String prefix = method.getDeclaringClass().getName() + "." + methodName;
        // 省略很多参数校验和判空
        if (StringUtils.isBlank(businessKey)) {
            businessKey = prefix;
        }

        businessKey = LOCK_KEY + businessKey;

        // String uniqueValue = UUID.randomUUID().toString();
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String ipPort = ipUtils.getIpPort();
        // 加锁
        Thread currentThread = Thread.currentThread();
        Object reuslt = Result.success();

        boolean isSuccess = redisLockHandle.setIfAbsent(businessKey, format + "  |  " + ipPort, redisLock.lockTime(), TimeUnit.SECONDS);
        // 未抢到锁，跳过执行
        if (!isSuccess) {
            log.info("===========未抢到锁，跳过当前程序：[{}]-[lock is taked]============", prefix);
            return reuslt;
        }
        try {
            // 加锁，并配置指定的过期时间，单位s秒
            redisLockHandle.expire(businessKey, redisLock.lockTime(), TimeUnit.SECONDS);

            // 将本次 Task 信息加入「延时」队列中
            holderList.add(new RedisLockDefinitionHolder(businessKey, redisLock.lockTime(), System.currentTimeMillis(), currentThread));

            // 线程被中断，抛出异常，中断此次请求
            if (currentThread.isInterrupted()) {
                log.info("===========执行异常 [{}] [You had been interrupted]============", prefix);
            }
            long start = System.currentTimeMillis();
            reuslt = pjp.proceed();
            long end = System.currentTimeMillis();
            if (end - start < redisLock.lockTime() * 1000) {
                ThreadUtil.safeSleep(redisLock.lockTime() * 1000 - (end - start));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("->>> has some error, please check again [{}]", prefix);
        } finally {
            // 请求结束后，强制删掉 key，释放锁
            if (null != redisLockHandle.get(businessKey)) {
                redisLockHandle.delete(businessKey);
                log.info("===========释放锁 [{}] [release the lock]==========", prefix);
            }
        }
        return reuslt;
    }

}
