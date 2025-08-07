package com.wenge.oauth.holder;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.exception.OauthException;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.RequestUtil;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.wenge.oauth.constants.AppConfigContant.FREE_LOGIN_USER_NAME;
import static com.wenge.oauth.constants.AppConfigContant.getConfiguration;
import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;

@Slf4j
@Component
public class ContextHolders {

    /**
     * 当前租户
     */
    public static final String CURRENT_TENANT = "currentTenant";

    /**
     * 子租户
     */
    public static final String SUB_TENANT = "subTenants";

    /**
     * 操作人
     */
    private static final ThreadLocal<TokenUser> USER_INFO = new TransmittableThreadLocal<>();

    /**
     * Token
     */
    private static final ThreadLocal<String> ACCESS_TOKEN = new TransmittableThreadLocal<>();

    /**
     * 获取access_token
     *
     * @return 返回
     */
    public static String getAccessToken() {
        return ACCESS_TOKEN.get();
    }

    /**
     * 设置access token
     *
     * @param accessToken token
     */
    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN.set(accessToken);
    }

    /**
     * 获取Token用户信息
     *
     * @return 返回
     */
    public static TokenUser getTokenUserInfo() {
        getUserByToken(StringConstant.BLANK);
        return USER_INFO.get();
    }

    /**
     * 获取Token用户信息
     *
     * @return 返回
     */
    public static TokenUser getTokenUserInfo(String accessToken) {
        getUserByToken(accessToken);
        return USER_INFO.get();
    }

    /**
     *
     * @return
     */
    private static TokenUser getUserByToken(String accessToken) {
        TokenUser tokenOauthUserInfo = getUserByAccessToken(accessToken);
        if (null != tokenOauthUserInfo) {
            return tokenOauthUserInfo;
        }

        ApplicationContext context = CoreContextProvider.getContext();

        // 是否开启认证
        String property = context.getEnvironment().getProperty("appframe.token.check");
        if (StringUtils.isBlank(property) || "false".equals(property)) {
            return null;
        }
        // 非请求时，不需要认证
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            return null;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 有token是需要认证
        accessToken = RequestUtil.extractToken(request);

        String whiteStr = context.getEnvironment().getProperty("appframe.token.whiteList");
        if (StringUtils.isNotBlank(accessToken)) {
            setAccessToken(accessToken);
            RedisService redisService = context.getBean(RedisService.class);
            String token = RedisConstant.TOKEN + accessToken;

            tokenOauthUserInfo = redisService.getObject(token, TokenUser.class);
            if (null == tokenOauthUserInfo) {
                // 验证没通过时，判断是否白名单
                boolean passFlag = isPassFlag(whiteStr, request);
                // 验证没通过，而且也不在白名单内，则抛出异常
                if (!passFlag) {
                    throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "未授权");
                }
            }
            setTokenUserInfo(tokenOauthUserInfo);
            return tokenOauthUserInfo;
        } else {
            // 没有token时，需要判断是否在白名单内
            boolean passFlag = isPassFlag(whiteStr, request);
            // 验证没通过，而且也不在白名单内，则抛出异常
            if (!passFlag) {
                // 判断免登录
                TokenUser user = checkFreeLogin(request);
                if (null != user) {
                    return user;
                }
                throw new OauthException(ResultCodeEnum.TOKEN_INVALID.getCode(), "未授权");
            }
        }
        return null;
    }

    private static boolean isPassFlag(String whiteStr, HttpServletRequest request) {
        boolean passFlag = false;
        String url = request.getRequestURI();
        if (StringUtils.isNotBlank(whiteStr)) {
            String[] whiteList = whiteStr.split(",");
            for (String ignore : whiteList) {
                String httpUrl = ignore;
                if (ignore.contains("/**")) {
                    httpUrl = httpUrl.replace("/**", "");
                    if (url.contains(httpUrl)) {
                        passFlag = true;
                        break;
                    }
                }
                if (url.equals(httpUrl)) {
                    passFlag = true;
                    break;
                }
            }
        }
        // log.info("==>>是否通过白名单验证：{}，passFlag:{}", url, passFlag);
        return passFlag;
    }

    ///**
    // * 获取全部租户ID
    // *
    // * @return 返回
    // */
    //public static List<String> getAllTenantIds() {
    //    List<String> tenantIds = new ArrayList<>();
    //    String currentId = getCurrentTenantId();
    //    if (StringUtils.isNotBlank(currentId)) {
    //        tenantIds.add(currentId);
    //        tenantIds.addAll(getSubTenantIds());
    //    }
    //    return tenantIds;
    //}

    /**
     * 获取当前租户子租户
     *
     * @return 返回当前租户ID
     */
    //public static List<String> getSubTenantIds() {
    //    Map<String, String> tenantIdMap = getTenantId();
    //    List<String> subList = new ArrayList<>();
    //    // 默认租户id为 0-无查看数据权限
    //    String tenantId = "0";
    //    if (tenantIdMap != null && tenantIdMap.size() > 0) {
    //        tenantId = tenantIdMap.get(SUB_TENANT) != null ? tenantIdMap.get(SUB_TENANT) : "0";
    //        if (StringUtils.isNotBlank(tenantId)) {
    //            String[] subIds = tenantId.split(",");
    //            subList.addAll(Arrays.asList(subIds));
    //        }
    //    }
    //    return subList;
    //}

    ///**
    // * 获取当前租户id
    // *
    // * @return 返回当前租户ID
    // */
    //public static String getCurrentTenantId() {
    //    Map<String, String> tenantIdMap = getTenantId();
    //
    //    // 默认租户id为 0-无查看数据权限
    //    String tenantId = "0";
    //    if (tenantIdMap != null && tenantIdMap.size() > 0) {
    //        tenantId = tenantIdMap.get(CURRENT_TENANT) != null ? tenantIdMap.get(CURRENT_TENANT) : "0";
    //    }
    //
    //    return tenantId;
    //}

    /**
     * 设置Token用户信息
     *
     * @param oauthUser userInfo
     */
    public static void setTokenUserInfo(TokenUser oauthUser) {
        USER_INFO.set(oauthUser);
    }

    ///**
    // * 获取租户id
    // *
    // * @return 返回
    // */
    //public static Map<String, String> getTenantId() {
    //    return  getTokenUserInfo().getTenantId();
    //}

    ///**
    // * 设置租户ID
    // *
    // * @param tenantId 租户id
    // */
    //public static void setTenantId(Map<String, String> tenantId) {
    //    getTokenUserInfo().setTenantId(tenantId);
    //}

    /**
     * 删除token
     */
    public static void removeAccessToken() {
        ACCESS_TOKEN.remove();
    }

    /**
     * 删除用户Token信息
     */
    public static void removeTokenUserinfo() {
        USER_INFO.remove();
    }

    /**
     * 免登录
     * @param request
     */
    private static TokenUser checkFreeLogin(HttpServletRequest request) {
        String accountName = request.getHeader("Account-name");
        if (StringUtils.isBlank(accountName)) {
            return null;
        }
        String applicationId = request.getHeader("Application-id");
        if (StringUtils.isBlank(applicationId)) {
            return null;
        }
        String freeLoginUserName = getConfiguration(applicationId, FREE_LOGIN_USER_NAME);
        if (StringUtils.isBlank(freeLoginUserName)) {
            return null;
        }
        String[] split = freeLoginUserName.split(",");
        // 或者是免登录账号
        String redisKey = RedisConstant.TOKEN_ACCOUNT + accountName;
        ApplicationContext context = CoreContextProvider.getContext();
        RedisService redisService = context.getBean(RedisService.class);
        UserService userService = context.getBean(UserService.class);
        for (String name : split) {
            if (name.equals(accountName)) {
                log.info("用户名：" + name + "免登录");
                if (!redisService.hasKey(redisKey)) {
                    return redisService.getObject(redisKey, TokenUser.class);
                } else {
                    Wrappers<Object> wrappers = Wrappers.init()
                            .where(OAUTH_USER.ACCOUNT_NAME.eq(accountName))
                            .limit(1);
                    OauthUser oauthUser = userService.getOne(wrappers);
                    TokenUser user = BeanUtil.toBean(oauthUser, TokenUser.class);
                    if (null != user) {
                        redisService.set(redisKey, user, 60 * 60 * 24);
                        return user;
                    }
                }
            }
        }
        return null;
    }

    private static TokenUser getUserByAccessToken(String accessToken) {
        if (StringUtils.isNotBlank(accessToken)) {
            ApplicationContext context = CoreContextProvider.getContext();
            RedisService redisService = context.getBean(RedisService.class);
            String token = RedisConstant.TOKEN + accessToken;
            return redisService.getObject(token, TokenUser.class);
        }
        return null;
    }
}
