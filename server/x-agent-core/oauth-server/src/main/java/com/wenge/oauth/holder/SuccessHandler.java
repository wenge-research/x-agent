package com.wenge.oauth.holder;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.entity.Menu;
import com.wenge.oauth.entity.OauthLoginLog;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.enums.UserSourceEnum;
import com.wenge.oauth.provider.qywx.QywxAuthenticationToken;
import com.wenge.oauth.provider.sms.SmsAuthenticationToken;
import com.wenge.oauth.provider.userpassword.WgAuthenticationToken;
import com.wenge.oauth.provider.wx.WXAuthenticationToken;
import com.wenge.oauth.service.MenuService;
import com.wenge.oauth.service.OauthLoginLogService;
import com.wenge.oauth.service.OauthTenantService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.redis.utils.RequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${spring.application.name:}")
    private String appName;

    @Value("${spring.application.cnName:}")
    private String cnName;

    @Autowired
    private RedisService redisService;

    @Value("${appframe.token.expired:1800000}")
    private Long expired;

    @Autowired
    private OauthLoginLogService oauthLoginLogService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private OauthTenantService oauthTenantService;

    /**
     * 登录页面认证成功业务逻辑(login success)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        JSONObject jsonObject = new JSONObject();
        Object principal = authentication.getPrincipal();
        TokenUser tokenUser = (TokenUser) principal;
        JSONObject userData = JSON.parseObject(JSON.toJSONString(tokenUser));
        userData.remove("password");
        jsonObject.put("user", userData);
        jsonObject.put("grantedAuthority", authentication.getAuthorities());
        jsonObject.put("systemLogo", oauthTenantService.getLogoById(tokenUser.getTenantId()));

        String token = tokenUser.getId() + "_" + IdUtil.simpleUUID();
        jsonObject.put("accessToken", token);
        String redisKey = RedisConstant.TOKEN + token;
        //TokenUser tokenUser = BeanUtil.toBean(oauthUser, TokenUser.class);
        if (tokenUser.getDeptId() == null) { //处理用户没有部门的时候 这里只需要将部门信息设置为其他 用户分析统计使用
            tokenUser.setDeptId("-1");
            tokenUser.setDeptName("其他");
        }
        redisService.set(redisKey, tokenUser, expired / 1000);

        String passwordExpiredTime = tokenUser.getPasswordExpiredTime();
        LocalDateTime now = LocalDateTimeUtil.now();
        Result success = Result.success(jsonObject);
        if (StringUtils.isNotBlank(passwordExpiredTime)) {
            LocalDateTime parse = LocalDateTimeUtil.parse(passwordExpiredTime, DateUtil.DEFAULT_FORMAT);
            if (parse.isBefore(now.plusDays(2))) {
                jsonObject.put("passwordExpiredTime", "登录成功，密码即将过期");
            }
        }
        StringParam userId = new StringParam();
        userId.setParam(tokenUser.getId() + "");
        // 超级管理员可以获取所有菜单
        if (PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenUser.getPowerType())) {
            List<Menu> adminPermission = menuService.getAdminPermission();
            jsonObject.put("permission", adminPermission);
        } else {
            // 获取用户菜单
            Result<List<Menu>> permissionResult = menuService.getUserPermission(userId);
            jsonObject.put("permission", permissionResult.getData());
        }

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(JSON.toJSONString(success));
        // 保存登录日志
        saveLog(tokenUser, authentication);
    }

    /**
     * 保存登录日志
     *
     * @param oauthUser
     */
    private void saveLog(TokenUser oauthUser, Authentication authentication) {
        String ipAddress = RequestUtils.getIpAddress();
        OauthLoginLog loginLog = new OauthLoginLog();
        loginLog.setLoginSystem(StringUtils.isNotBlank(cnName) ? cnName : StringUtils.isNotBlank(appName) ? appName : "服务系统");
        loginLog.setLoginIp(ipAddress);
        loginLog.setUserId(oauthUser.getId());
        String userName = StringUtils.isNotBlank(oauthUser.getAccountName()) ? oauthUser.getAccountName() : oauthUser.getUserName();
        loginLog.setAccountName(userName);
        loginLog.setOpera("登录");
        loginLog.setLoginTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT));
        // 设置登录方式
        if (authentication instanceof WgAuthenticationToken) {
            loginLog.setLoginWay(UserSourceEnum.SYSTEM.getCode());
        } else if (authentication instanceof QywxAuthenticationToken) {
            loginLog.setLoginWay(UserSourceEnum.WE_COM.getCode());
        } else if (authentication instanceof SmsAuthenticationToken) {
            loginLog.setLoginWay(UserSourceEnum.SMS.getCode());
        } else if (authentication instanceof WXAuthenticationToken) {
            loginLog.setLoginWay(UserSourceEnum.WE_OFFICE.getCode());
        }
        oauthLoginLogService.save(loginLog);
    }
}
