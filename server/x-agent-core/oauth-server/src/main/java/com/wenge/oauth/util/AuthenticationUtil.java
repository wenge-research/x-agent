package com.wenge.oauth.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.enums.UserStatusEnum;
import com.wg.appframe.core.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;

@Slf4j
public class AuthenticationUtil {

    public static void checkAccount(OauthUser user) {
        String status = user.getStatus();
        UserStatusEnum statusEnum = UserStatusEnum.getDescByCode(status);
        if (null == statusEnum) {
            return;
        }
        switch (statusEnum) {
            case UNACTIVATED:
                log.info("用户未激活:{}", user.getAccountName());
                throw new BadCredentialsException("用户未激活");
            case LOCKED:
                log.info("用户已锁定:{}", user.getAccountName());
                throw new BadCredentialsException("用户已锁定");
            case DEREGISTERED:
                log.info("用户已注销:{}", user.getAccountName());
                throw new BadCredentialsException("用户已注销");
            default:
                break;
        }

        LocalDateTime now = LocalDateTimeUtil.now();
        String expireTime = user.getExpireTime();
        if (StringUtils.isNotBlank(expireTime)) {
            LocalDateTime parse = LocalDateTimeUtil.parse(expireTime, DateUtil.DEFAULT_FORMAT);
            if (parse.isBefore(now)) {
                log.info("用户已过期:{}", user.getAccountName());
                throw new BadCredentialsException("用户已过期");
            }
        }
    }

}
