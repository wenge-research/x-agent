package com.wenge.oauth.filter;

import com.wenge.oauth.provider.qywx.QywxAuthenticationToken;
import com.wenge.oauth.provider.sms.SmsAuthenticationToken;
import com.wenge.oauth.provider.userpassword.WgAuthenticationToken;
import com.wenge.oauth.provider.wx.WXAuthenticationToken;
import com.wg.appframe.core.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class WgAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private AuthenticationManager authenticationManager;

    public WgAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        super(new AntPathRequestMatcher("/login", "POST"));
        setAuthenticationFailureHandler(failureHandler);
        setAuthenticationSuccessHandler(successHandler);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new HttpRequestMethodNotSupportedException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String username = request.getParameter("username");
        String mykey = request.getParameter("mykey");
        String loginType = request.getParameter("loginType");
        String code = request.getParameter("code");
        String applicationId = request.getParameter("applicationId");
        String tenantId = request.getParameter("tenantId");

        AbstractAuthenticationToken wgAuthenticationToken;

        if ("wx".equals(loginType)) {
            wgAuthenticationToken = new WXAuthenticationToken(code, null, applicationId, tenantId);
            wgAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));

        } else if ("qywx".equals(loginType)) {
            // 企业微信扫码登录
            wgAuthenticationToken = new QywxAuthenticationToken(code, null, null, null);
            wgAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        } else if ("portaltoken".equals(loginType)) {
            if (StringUtils.isBlank(username)) {
                username = "";
            }
            if (StringUtils.isBlank(mykey)) {
                mykey = "";
            }
            wgAuthenticationToken = new WgAuthenticationToken(username, mykey);
            wgAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
            log.info("这个请求是通过统一认证平台请求的 username:{}", username);
        } else if ("sms".equals(loginType)) {
            // 通过手机验证码登录

            // 手机号
            String phoneNum = request.getParameter("phoneNum");
            // 验证码缓存 key
            String codeKey = request.getParameter("codeKey");
            wgAuthenticationToken = new SmsAuthenticationToken(phoneNum, StringConstant.BLANK, codeKey, code);
            wgAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        } else {
            // 账号密码登录
            if (StringUtils.isBlank(username)) {
                username = "";
            }
            if (StringUtils.isBlank(mykey)) {
                mykey = "";
            }
            wgAuthenticationToken = new WgAuthenticationToken(username, mykey);
            wgAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        }
        return authenticationManager.authenticate(wgAuthenticationToken);
    }

}
