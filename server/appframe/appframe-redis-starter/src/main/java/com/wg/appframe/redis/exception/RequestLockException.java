package com.wg.appframe.redis.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequestLockException extends RuntimeException {

    public RequestLockException(String message) {
        super(message);
    }
}
