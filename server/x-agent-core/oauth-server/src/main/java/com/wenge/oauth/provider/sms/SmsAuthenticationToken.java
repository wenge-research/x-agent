package com.wenge.oauth.provider.sms;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    private Object credentials;
    private Object principal;
    private String codeKey;
    private String code;

    public SmsAuthenticationToken(Object principal, Object credentials, String codeKey, String code) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.codeKey = codeKey;
        this.code = code;
    }


    @Override
    public Object getCredentials() {
        return credentials;
    }


    @Override
    public Object getPrincipal() {
        return principal;
    }
}
