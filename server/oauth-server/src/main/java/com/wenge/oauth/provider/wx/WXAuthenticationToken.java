package com.wenge.oauth.provider.wx;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class WXAuthenticationToken extends AbstractAuthenticationToken {

    private Object credentials;
    private Object principal;
    private String applicationId;
    private String tenantId;

    public WXAuthenticationToken(Object principal, Object credentials, String applicationId, String tenantId) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.applicationId = applicationId;
        this.tenantId = tenantId;
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
