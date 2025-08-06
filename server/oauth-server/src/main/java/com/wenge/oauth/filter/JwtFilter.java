package com.wenge.oauth.filter;

import com.alibaba.fastjson2.JSON;
import com.wenge.oauth.config.WebSecurityConfig;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.ContextHolders;
import com.wenge.oauth.util.RequestUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.handler.CipherPreHandle;
import com.wg.appframe.core.utils.MDCTraceUtils;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Value("${appframe.token.check:false}")
    private boolean checkToken;

    @Value("${appframe.token.expired:1800000}")
    private Long expired;

    /**
     * 打印用户信息
     */
    @Value("${appframe.log.userKey:userId}")
    private String userKey;


    /**
     * 是否开启接口加密
     */
    @Value("${appframe.cipher.enable:false}")
    private boolean cipherEnable;

    @Autowired
    private RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!checkToken) {
            filterChain.doFilter(request, response);
            return;
        }

        // 携带token场景
        try {
            // 验证是否是加密请求
            boolean vulnerability = vulnerability(request, response);
            if (!vulnerability) {
                return;
            }
            //log.info("==>doFilterInternal");
            TokenUser oauthUser = ContextHolders.getTokenUserInfo();
            // 没有用户时，是不需要认证的
            if (null == oauthUser) {
                log.info("==>oauthUser.null");
                filterChain.doFilter(request, response);
                return;
            }
            String accessToken = RequestUtil.extractToken(request);

            String tokenKey = RedisConstant.TOKEN + accessToken;

            if (!redisService.hasKey(tokenKey)) {
                // token失效
                tokenInvalid(response);
            } else {
                // token续期
                // redisService.setExpire(tokenKey, oauthUser, expired / 1000);
                redisService.expire(tokenKey, (int) (expired / 1000));
                // 获取用户信息，并续期token
                String accountName = oauthUser.getAccountName();
                Long id = oauthUser.getId();
                MDC.put(MybatisFiledConstant.MDC_USER_NAME, accountName);
                MDC.put(MybatisFiledConstant.MDC_USER_ID, id + StringConstant.BLANK);
                MDC.put(MDCTraceUtils.LOG_USER_ID, accountName);
                MDC.put(MybatisFiledConstant.MDC_TENANT_ID, oauthUser.getTenantId());
                // 打印日志的时候，把用户名打印出来，这里指定user对象的字段，是指打印用户的指定字段
                MDC.put(userKey, oauthUser.getUserName());
                filterChain.doFilter(request, response);
            }
        } catch (Throwable e) {
            //log.info("==>doFilterInternal.found");
            log.error("parse token found error.", e);
            if (checkToken) {
                tokenInvalid(response);
            }
        } finally {
            MDC.clear();
            ContextHolders.removeTokenUserinfo();
            ContextHolders.removeAccessToken();
        }
    }

    private void tokenInvalid(HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(403);
        Result result = Result.fail(ResultCodeEnum.TOKEN_INVALID);
        response.getWriter().println(JSON.toJSONString(result));
    }

    /**
     * 拦截漏洞
     * @param request
     * @param response
     * @return
     */
    private boolean vulnerability(HttpServletRequest request, HttpServletResponse response) {
        // 验证是否是加密请求
        boolean paseFlag = true;
        if (cipherEnable) {
            String requestURI = request.getRequestURI();
            // 单独对登录接口进行拦截验证，其他接口在通用代码已拦截
            if (WebSecurityConfig.LOGIN_PATH.equals(requestURI)) {
                paseFlag = CipherPreHandle.checkCipher(request, response);
            }
        }

        return paseFlag;
    }
}
