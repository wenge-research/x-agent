package com.wenge.model.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class IdempotentProcessor {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean checkIdempotent(String key) {
        String redisKey = "app:idempotent:" + key;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 1, TimeUnit.MINUTES));
    }
}
