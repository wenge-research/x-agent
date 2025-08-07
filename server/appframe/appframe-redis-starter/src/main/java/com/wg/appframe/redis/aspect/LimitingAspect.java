package com.wg.appframe.redis.aspect;


import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableList;
import com.wg.appframe.redis.annotation.RedisLimit;
import com.wg.appframe.redis.enums.LimitTypeEnum;
import com.wg.appframe.redis.exception.LimitingException;
import com.wg.appframe.redis.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description: 接口限流
 * @Author: CHENZHIWEI
 * @CreateTime: 2022-11-14 14:01:53
 */
@Component
@Aspect
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class LimitingAspect {

    /**
     * 注入RedisTemplate
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${appframe.limit.count:50}")
    private Integer count;
    @Value("${appframe.limit.period:2}")
    private Integer period;

    @Value("${appframe.limit.enable:true}")
    private boolean enable;

    @Value("${appframe.limit.prefix:}")
    private String prefix;

    @Pointcut("@annotation(com.wg.appframe.redis.annotation.RedisLimit)")
    public void requestPointCut() {}


    @Before("requestPointCut()")
    public void around(JoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Scheduled scheduled = method.getAnnotation(Scheduled.class);
        if (null != scheduled) {
            return;
        }
        RedisLimit annotation = method.getAnnotation(RedisLimit.class);
        // 判断是否需要限流
        if (null != annotation) {
            boolean limitFlag = annotation.enable();
            if (!limitFlag) {
                return;
            }
        } else if (!enable) {
            return;
        }
        String key;

        int periods = null != annotation ? 0 != annotation.period() ? annotation.period() : period : period;
        int counts = null != annotation ? 0 != annotation.count() ? annotation.count() : count : count;
        LimitTypeEnum limitType = null != annotation ? null != annotation.limitType() ? annotation.limitType() : LimitTypeEnum.URI : LimitTypeEnum.URI;
        switch (limitType) {
            case IP:
                key = RequestUtils.getIpAddress();
                break;
            case URI:
                key = RequestUtils.getUri();
                break;
            default:
                key = RequestUtils.getUri();
                break;
        }
        key = StrUtil.join(null != annotation ? StrUtil.isNotBlank(annotation.prefix()) ? annotation.prefix() : prefix : prefix, key);
        ImmutableList<String> keys = ImmutableList.of(key);
        try {
            String luaScript = buildLuaScript();
            DefaultRedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
            Number number = redisTemplate.execute(redisScript, keys, counts, periods);
            // log.info("Access try count is {} for name = {} and key = {}", number, name, key);
            // 0表示失败，被限制
            if (number != null && number.intValue() == 0) {
                throw new LimitingException("系统繁忙");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new LimitingException("系统繁忙");
        }
    }

    public String buildLuaScript(){
        return "redis.replicate_commands(); local listLen,time" +
                "\nlistLen = redis.call('LLEN', KEYS[1])" +
                // 不超过最大值，则直接写入时间
                "\nif listLen and tonumber(listLen) < tonumber(ARGV[1]) then" +
                "\nlocal a = redis.call('TIME');" +
                "\nredis.call('LPUSH', KEYS[1], a[1]*1000000+a[2])" +
                "\nelse" +
                // 取出现存的最早的那个时间，和当前时间比较，看是小于时间间隔
                "\ntime = redis.call('LINDEX', KEYS[1], -1)" +
                "\nlocal a = redis.call('TIME');" +
                "\nif a[1]*1000000+a[2] - time < tonumber(ARGV[2])*1000000 then" +
                // 访问频率超过了限制，返回0表示失败
                "\nreturn 0;" +
                "\nelse" +
                "\nredis.call('LPUSH', KEYS[1], a[1]*1000000+a[2])" +
                "\nredis.call('LTRIM', KEYS[1], 0, tonumber(ARGV[1])-1)" +
                "\nend" +
                "\nend" +
                "\nreturn 1;";
    }


}
