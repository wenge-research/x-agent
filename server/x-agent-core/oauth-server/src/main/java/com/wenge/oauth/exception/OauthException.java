package com.wenge.oauth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OauthException extends AuthenticationException {

    private static final long serialVersionUID = 5536321371380076360L;

    private final String code;

    private final String message;

    public OauthException(String code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    public OauthException(String msg, Throwable cause, String code, String message) {
        super(msg, cause);
        this.code = code;
        this.message = message;
    }

    public OauthException(String msg, String code, String message) {
        super(msg);
        this.code = code;
        this.message = message;
    }
}
