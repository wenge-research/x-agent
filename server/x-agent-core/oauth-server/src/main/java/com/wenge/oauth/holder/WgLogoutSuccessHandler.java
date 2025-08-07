package com.wenge.oauth.holder;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSON;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.entity.OauthLoginLog;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.exception.OauthException;
import com.wenge.oauth.service.OauthLoginLogService;
import com.wenge.oauth.util.RequestUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.redis.utils.RequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class WgLogoutSuccessHandler implements LogoutSuccessHandler {

    @Value("${spring.application.name:}")
    private String appName;

    @Value("${spring.application.cnName:}")
    private String cnName;


    @Autowired
    private RedisService redisService;

    @Autowired
    private OauthLoginLogService oauthLoginLogService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            TokenUser tokenOauthUserInfo = ContextHolders.getTokenUserInfo();
            if (null != tokenOauthUserInfo) {
                // 登出时，清空redis缓存
                String accessToken = RequestUtil.extractToken(request);
                String redisKey = RedisConstant.TOKEN + accessToken;
                redisService.del(redisKey);
                saveLog(tokenOauthUserInfo);
            }
        } catch (OauthException e) {
            System.out.println("====");
            logoutMsg(response);
        }
    }

    /**
     * 登出成功
     * @param response
     * @throws IOException
     */
    private void logoutMsg(HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        Result result = Result.success();
        response.getWriter().println(JSON.toJSONString(result));
    }


    /**
     * 保存登录日志
     *
     * @param oauthUser
     */
    private void saveLog(TokenUser oauthUser) {
        String ipAddress = RequestUtils.getIpAddress();
        OauthLoginLog loginLog = new OauthLoginLog();
        loginLog.setLoginSystem(StringUtils.isNotBlank(cnName) ? cnName : StringUtils.isNotBlank(appName) ? appName : "服务系统");
        loginLog.setLoginIp(ipAddress);
        loginLog.setUserId(oauthUser.getId());
        String userName = StringUtils.isNotBlank(oauthUser.getAccountName()) ? oauthUser.getAccountName() : oauthUser.getUserName();
        loginLog.setAccountName(userName);
        loginLog.setOpera("登出");
        loginLog.setLogoutTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
        oauthLoginLogService.save(loginLog);
    }
}
