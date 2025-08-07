/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wg.appframe.redis.handler;

import cn.hutool.json.JSONObject;
import com.wg.appframe.redis.exception.LimitingException;
import com.wg.appframe.redis.exception.RequestLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>
 * 全局异常处理
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-28
 */
@RestControllerAdvice
@Slf4j
public class RedisExceptionHandler  implements Ordered {
        
    /**
     * 幂等性异常
     */
    @ExceptionHandler({RequestLockException.class})
    public JSONObject handleRequestLockException(RequestLockException e) {
        JSONObject result = new JSONObject();
        result.set("code", "0000001");
        result.set("msg", e.getMessage());
        result.set("data", null);
        return result;
    }

    /**
     * 限流异常
     */
    @ExceptionHandler({LimitingException.class})
    public JSONObject handleLimitingException(LimitingException e) {
        JSONObject result = new JSONObject();
        result.set("code", "0000001");
        result.set("msg", e.getMessage());
        result.set("data", null);
        return result;
    }

    @Override
    public int getOrder() {
        return 98;
    }
}

