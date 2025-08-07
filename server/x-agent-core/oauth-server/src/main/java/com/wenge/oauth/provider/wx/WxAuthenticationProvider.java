package com.wenge.oauth.provider.wx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wenge.oauth.config.WxMpConfiguration;
import com.wenge.oauth.dto.param.UserBindingRoleParam;
import com.wenge.oauth.entity.ApplicationUser;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.enums.UserSourceEnum;
import com.wenge.oauth.enums.UserStatusEnum;
import com.wenge.oauth.enums.UserTypeEnum;
import com.wenge.oauth.service.ApplicationUserService;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.AuthenticationUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;


@Data
@Component
@Slf4j
public class WxAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Value("${appframe.oauth.pwErrorMax:5}")
    private Integer pwErrorMax;

    @Value("${appframe.oauth.duration:1800}")
    private Integer duration;

    @Autowired
    private ApplicationUserService applicationUserService;

    /**
     * 关芯群众用户角色
     */
    @Value("${appConfig.guanxin.h5.residentRoleCodes}")
    private List<String> residentRoleCodes;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WXAuthenticationToken wxAuthenticationToken = (WXAuthenticationToken) authentication;
        Object code = wxAuthenticationToken.getPrincipal();
        String applicationId = wxAuthenticationToken.getApplicationId();
        String tenantId = wxAuthenticationToken.getTenantId();
        // 根据code请求微信接口获取用户信息
        WxOAuth2UserInfo userInfo = null;
        log.info("================ 》 获取微信用户信息code=" + code);
        try {
            WxMpService wxService = WxMpConfiguration.wxMpService(applicationId);
            WxOAuth2AccessToken accessToken = wxService.getOAuth2Service().getAccessToken(code.toString());


            log.info("================ 》 获取微信用户信息 accessToken ： " + accessToken);
            userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, null);
            log.info("================ 》 获取微信用户信息 userInfo： " + userInfo);
            log.info("================ 》 获取微信用户信息 userInfo.nickname ： " + userInfo.getNickname());
            log.info("================ 》 获取微信用户信息 userInfo.openId ： " + userInfo.getOpenid());
            log.info("================ 》 获取微信用户信息 userInfo.headImgUrl ： " + userInfo.getHeadImgUrl());
        } catch (WxErrorException e) {
            log.info("================ 》 获取微信用户信息 异常 ： " + e.getMessage());
            throw new BadCredentialsException("获取微信用户信息失败");
        }

        /**
         * 测试
         */
        //WxOAuth2UserInfo userInfo = new WxOAuth2UserInfo();
        //userInfo.setOpenid("oGNpH1bxa1gN5dY33l5UpKTLN0vk");
        //userInfo.setNickname("whchatdefault");
        //userInfo.setSex(0);

        Wrappers<Object> wrappers = Wrappers.init()
                .where(OAUTH_USER.OPEN_ID.eq(userInfo.getOpenid()))
                .and(OAUTH_USER.SOURCE.eq(UserSourceEnum.WE_OFFICE.getCode()))
                .limit(1);

        OauthUser oauthUser = userService.getOne(wrappers);
        boolean firstLoginFlag = false;
        if (null == oauthUser) {
            firstLoginFlag = true;
            OauthUser user = OauthUser.create();
            user.setOpenId(userInfo.getOpenid());
            user.setUserName(userInfo.getNickname());

            user.setPassword("cc958716b6582c164a4c8a5c4bf8c886");
            user.setStatus(UserStatusEnum.NORMAL.getCode());
            user.setSex(userInfo.getSex() == null || userInfo.getSex() == 0 ? "未知": (userInfo.getSex() == 1 ? "男" : "女"));
            user.setPowerType(PowerTypeEnum.WECHAT_USER.getCode());
            user.setUserType(UserTypeEnum.RESIDENT.getCode());
            user.setTenantId(tenantId);
            user.setSource(UserSourceEnum.WE_OFFICE.getCode());
            userService.save(user);
            oauthUser = user;

            // 绑定应用
            bindingApplication(user, applicationId);

            // 绑定角色(针对中关村关芯项目)
            bindingRole(user);

            // 同步用户到关芯智巡
            log.info("开始 同步用户到关芯智巡....{}", oauthUser);
            userService.syncWorker(oauthUser);
            log.info("结束 同步用户到关芯智巡....{}", oauthUser);
        } else {
            // 验证账号状态
            AuthenticationUtil.checkAccount(oauthUser);
            if (StringUtils.isBlank(oauthUser.getPassword())) {
                firstLoginFlag = true;
            }
        }
        TokenUser tokenUser = BeanUtil.toBean(oauthUser, TokenUser.class);
        tokenUser.setFirstLoginFlag(firstLoginFlag);
        wxAuthenticationToken = new WXAuthenticationToken(tokenUser, null, applicationId, tenantId);
        tokenUser.setLonginWay(UserSourceEnum.WE_OFFICE);
        return wxAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WXAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 绑定角色
     * @param user
     */
    private void bindingRole(OauthUser user) {
        if (CollectionUtil.isNotEmpty(residentRoleCodes)) {

            UserBindingRoleParam param = new UserBindingRoleParam();
            param.setUserIdList(ListUtil.toList(user.getId() + StringConstant.BLANK));
            param.setRoleCodeList(ListUtil.toList(residentRoleCodes));
            userService.bindingRoleByCode(param);
        }
    }

    /**
     * 绑定应用
     */
    private void bindingApplication(OauthUser user, String applicationId) {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setApplicationId(applicationId);
        applicationUser.setUserId(user.getId() + "");
        applicationUserService.addApplicationUser(applicationUser);
    }

}
