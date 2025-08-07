package com.wg.appframe.redis.exception;

public class LimitingException extends RuntimeException {

    public LimitingException(String message) {
        super(message);
    }
}
