package com.wenge.oauth.provider.sms;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.wenge.oauth.dto.param.UpdateTenantParam;
import com.wenge.oauth.dto.param.UserBindingRoleParam;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.Tenant;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.enums.StatusTypeEnum;
import com.wenge.oauth.enums.UserSourceEnum;
import com.wenge.oauth.enums.UserStatusEnum;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.service.OauthTenantService;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.AuthenticationUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wenge.oauth.constants.OauthContant.SMS_TENANT_PREFIX;
import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;


@Data
@Component
@Slf4j
public class SmsAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private OauthTenantService oauthTenantService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OauthDeptService oauthDeptService;

    @Value("${default.appNumLimit}")
    private Integer appNumLimit;

    @Value("${default.userNumLimit}")
    private Integer userNumLimit;

    @Value("${default.defaultPassword}")
    private String defaultPassword;

    @Value("${default.defaultLogoUrl}")
    private String defaultLogoUrl;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken wxAuthenticationToken = (SmsAuthenticationToken) authentication;
        Object phoneNum = wxAuthenticationToken.getPrincipal();
        String codeKey = wxAuthenticationToken.getCodeKey();
        String code = wxAuthenticationToken.getCode();
        if (StringUtils.isBlank(code)) {
            throw new BadCredentialsException("验证码不能为空");
        }
        if (null == phoneNum || StringUtils.isBlank(phoneNum.toString())) {
            throw new BadCredentialsException("手机号不能为空");
        }
        if (!redisService.hasKey(codeKey)) {
            throw new BadCredentialsException("验证码已过期");
        }
        String codeValue = redisService.get(codeKey, String.class);
        if (!code.equals(codeValue)) {
            throw new BadCredentialsException("验证码不正确");
        }

        // 非请求时，不需要认证
        // RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // if (null == requestAttributes) {
        //     return null;
        // }
        // ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // HttpServletRequest request = attributes.getRequest();

        // String clientIP = ServletUtil.getClientIP(request);
        // String ipKey = RedisConstant.LOGIN_SMS_IP + clientIP;

        OauthDept deptName = oauthDeptService.getDeptByDeptName("短信部门");
        if (null == deptName) {
            throw new BadCredentialsException("请先创建一级【短信部门】");
        }
        // 删除验证码
        redisService.del(codeKey);
        // redisService.del(ipKey);
        String tel = phoneNum.toString();

        // 通过手机号查询用户
        Wrappers<Object> telWrapper = Wrappers.init()
                .where(OAUTH_USER.PHONE.eq(tel))
                .limit(1);
        OauthUser oauthUser = userService.getOne(telWrapper);
        boolean firstLoginFlag = false;

        // 如果用户不存在，创建用户
        if (null == oauthUser) {
            firstLoginFlag = true;
            // Wrappers<Object> wrappers = Wrappers.init()
            //         .where(OAUTH_USER.ACCOUNT_NAME.likeRight(tel));
            // List<OauthUser> userList = userService.list(wrappers);
            // String userName = tel;
            // if (CollectionUtil.isNotEmpty(userList)) {
            //     log.info("手机号已经注册，使用新账号登录，手机号：{}", tel);
            //     // 使用手机号_6位随机数字拼接，当做账号名称
            //     userName = creatAccountName(userList, tel);
            // }
            OauthUser user = OauthUser.create();
            user.setUserName(tel);
            user.setAccountName(tel);
            user.setStatus(UserStatusEnum.NORMAL.getCode());
            user.setPowerType(PowerTypeEnum.NORMAL_USER.getCode());
            user.setSource(UserSourceEnum.SMS.getCode());
            user.setPhone(tel);
            user.setDeptId(deptName.getDeptId());
            userService.save(user);
            oauthUser = user;
            // 绑定角色
            bindingRole(user);
            // 绑定租户
            bindingTenant(user);
        } else {
            // 验证账号状态
            AuthenticationUtil.checkAccount(oauthUser);
            if (StringUtils.isBlank(oauthUser.getPassword())) {
                firstLoginFlag = true;
            }
        }
        TokenUser tokenUser = BeanUtil.toBean(oauthUser, TokenUser.class);
        tokenUser.setFirstLoginFlag(firstLoginFlag);
        wxAuthenticationToken = new SmsAuthenticationToken(tokenUser, null, null, null);
        tokenUser.setLonginWay(UserSourceEnum.SMS);
        return wxAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 绑定角色
     *
     * @param user
     */
    private void bindingRole(OauthUser user) {
        UserBindingRoleParam param = new UserBindingRoleParam();
        param.setUserIdList(ListUtil.toList(user.getId() + StringConstant.BLANK));
        // todo 暂时给固定角色，负责人确认后修改
        param.setRoleCodeList(ListUtil.toList("basic_user"));
        userService.bindingRoleByCode(param);
    }

    /**
     * 绑定租户
     * 1. 租户名：sms_手机号
     * 2. 如果不存在：则创建新的租户
     * @param user
     */
    private void bindingTenant(OauthUser user) {
        String tenantName = SMS_TENANT_PREFIX.concat(user.getPhone()) ;
        Tenant tenantByName = oauthTenantService.getTenantByName(tenantName);
        String tenantId = IdUtil.simpleUUID();
        if (Objects.isNull(tenantByName)) {
            tenantByName = Tenant.create()
                    .setTenantName(tenantName)
                    .setTenantId(tenantId)
                    .setStatus(StatusTypeEnum.ENABLE.getIntType())
                    .setFullName(tenantName)
                    .setAppNumLimit(appNumLimit)
                    .setUserNumLimit(userNumLimit)
                    .setContactsPhone(user.getPhone())
                    .setContacts(user.getUsername())
                    .setCreateUser(user.getUsername())
                    .setUpdateUser(user.getUsername())
                    .setDefaultPassword(defaultPassword)
                    .setLogoUrl(defaultLogoUrl);
            oauthTenantService.addOauthTenant(tenantByName);
        } else {
            tenantId = tenantByName.getTenantId();
        }
        user.setTenantName(tenantName);
        user.setTenantId(tenantId);
        UpdateTenantParam updateTenantParam = new UpdateTenantParam();
        updateTenantParam.setTenantId(tenantId);
        List<Long> uerIdList = ListUtil.toList(user.getId());
        updateTenantParam.setUserIdList(uerIdList);
        userService.updateTenant(updateTenantParam);
    }

    private String creatAccountName(List<OauthUser> userList, String tel) {
        List<String> accountNameList = userList.stream().map(OauthUser::getAccountName).distinct().collect(Collectors.toList());
        // 随机 6 位数
        String accountName;
        do {
            accountName = tel + "_" + RandomUtil.randomNumbers(6);
        } while (accountNameList.contains(accountName));

        return accountName;
    }
}
