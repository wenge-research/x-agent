package com.wenge.oauth.provider.userpassword;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.enums.UserSourceEnum;
import com.wenge.oauth.enums.UserStatusEnum;
import com.wenge.oauth.enums.UserTypeEnum;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;


@Data
@Component
public class WgAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Value("${appframe.oauth.pwErrorMax:5}")
    private Integer pwErrorMax;

    @Value("${appframe.oauth.duration:1800}")
    private Integer duration;

    @Autowired
    private OauthDeptService oauthDeptService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WgAuthenticationToken wgAuthenticationToken = (WgAuthenticationToken) authentication;
        Object credentials = wgAuthenticationToken.getCredentials();
        Object principal = wgAuthenticationToken.getPrincipal();

        Wrappers<Object> wrappers = Wrappers.init()
                .where(OAUTH_USER.ACCOUNT_NAME.eq(principal.toString()))
                .and(OAUTH_USER.USER_TYPE.in(UserTypeEnum.GOV.getCode(), UserTypeEnum.GOV_STREET.getCode(), UserTypeEnum.GOV_COMMUNITY.getCode()))
                .and(OAUTH_USER.POWER_TYPE.ne(PowerTypeEnum.WECHAT_USER.getCode()))
                .limit(1);

        OauthUser oauthUser = userService.getOne(wrappers);
        TokenUser user = BeanUtil.toBean(oauthUser, TokenUser.class);
        if (null == user) {
            throw new BadCredentialsException("用户或密码错误");
        }

        if (UserStatusEnum.DEREGISTERED.getCode().equals(user.getStatus())) {
            throw new BadCredentialsException("用户或密码错误");
        }

        Integer pwErrorNum = user.getPwErrorNum();
        if (null == pwErrorNum) {
            pwErrorNum = 0;
        }
        LocalDateTime now = LocalDateTimeUtil.now();

        String password = user.getPassword();
        if (!credentials.toString().equals(password)) {
//        if (!password.equals(credentials.toString())) {
            OauthUser.create()
                    .setPwErrorNum(++pwErrorNum)
                    .where(OAUTH_USER.ID.eq(user.getId()))
                    .update();

            // 锁定账号,30分钟
            if (pwErrorNum >= pwErrorMax) {
                OauthUser.create()
                        .setStatus(UserStatusEnum.LOCKED.getCode())
                        .setLockTime(LocalDateTimeUtil.format(now.plusMinutes(duration), DateUtil.DEFAULT_FORMAT))
                        .where(OAUTH_USER.ID.eq(user.getId()))
                        .update();
                throw new BadCredentialsException("账号已锁定");
            }
            throw new BadCredentialsException("用户或密码错误");
        }

        String passwordExpiredTime = user.getPasswordExpiredTime();
        if (StringUtils.isNotBlank(passwordExpiredTime)) {
            LocalDateTime parse = LocalDateTimeUtil.parse(passwordExpiredTime, DateUtil.DEFAULT_FORMAT);
            if (parse.isBefore(now)) {
                throw new BadCredentialsException("密码已过期");
            }
        }

        String expireTime = user.getExpireTime();
        if (StringUtils.isNotBlank(expireTime)) {
            LocalDateTime parse = LocalDateTimeUtil.parse(expireTime, DateUtil.DEFAULT_FORMAT);
            if (parse.isBefore(now)) {
                throw new BadCredentialsException("用户已过期");
            }
        }

        String status = user.getStatus();
        if (UserStatusEnum.LOCKED.getCode().equals(status)) {
            // 锁定时间到期需解锁
            String lockTime = user.getLockTime();
            if (StringUtils.isNotBlank(lockTime)) {
                LocalDateTime parse = LocalDateTimeUtil.parse(lockTime, DateUtil.DEFAULT_FORMAT);
                if (parse.isAfter(now)) {
                    throw new BadCredentialsException("用户已锁定");
                } else {
                    OauthUser.create()
                            .setStatus(UserStatusEnum.NORMAL.getCode())
                            .setPwErrorNum(0)
                            .where(OAUTH_USER.ID.eq(user.getId()))
                            .update();
                }
            }
        }

        // 成功登录,密码错误次数清零
        OauthUser.create()
                        .setStatus(UserStatusEnum.NORMAL.getCode())
                        .setPwErrorNum(0)
                        .setLockTime(null)
                        .where(OAUTH_USER.ID.eq(user.getId()))
                        .update();
        //UpdateChain.of(User.class)
        //        .set(OAUTH_USER.STATUS, 1)
        //        .set(OAUTH_USER.PW_ERROR_NUM, 0)
        //        .set(OAUTH_USER.LOCK_TIME, null)
        //        .where(OAUTH_USER.ID.eq(user.getId()))
        //        .update();

        OauthDept deptDetail = oauthDeptService.getDeptDetail(user.getDeptId());
        if (null != deptDetail) {
            user.setDeptName(deptDetail.getDeptName());
        }
        wgAuthenticationToken = new WgAuthenticationToken(user, null);
        user.setLonginWay(UserSourceEnum.PASSWORD);
        return wgAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WgAuthenticationToken.class.isAssignableFrom(authentication);
    }



}
