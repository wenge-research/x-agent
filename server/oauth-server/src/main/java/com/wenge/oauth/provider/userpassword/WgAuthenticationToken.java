package com.wenge.oauth.provider.userpassword;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class WgAuthenticationToken extends AbstractAuthenticationToken {

    private Object credentials;
    private Object principal;

    public WgAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
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
