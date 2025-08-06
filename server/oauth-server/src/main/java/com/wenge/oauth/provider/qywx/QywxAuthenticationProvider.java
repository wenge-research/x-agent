package com.wenge.oauth.provider.qywx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.dto.param.UserBindingRoleParam;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.Tenant;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.enums.UserSourceEnum;
import com.wenge.oauth.enums.UserStatusEnum;
import com.wenge.oauth.service.OauthTenantService;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.AuthenticationUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;
import static com.wenge.oauth.entity.table.TenantTableDef.TENANT;


@Data
@Component
@Slf4j
public class QywxAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private OauthTenantService oauthTenantService;

    @Autowired
    private RedisService redisService;

    @Value("${qywx.secret:}")
    private String secret;
    @Value("${qywx.appid:}")
    private String appid;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(7200, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * 获取access_token
     */
    private static String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
    /**
     * 获取access_token需要的参数corpid
     */
    // private static String CORPID = "ww96b49bb6ab748217";
    /**
     * 获取access_token需要的参数corpsecret
     */
    // private static String CORPSECRET = "KNK3fHHBQm2fI5WLqRleZqqAfKxUVaWIZ_EfeFnrgts";

    /**
     * 根据code获取userId
     */
    private static String GET_USERID_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
    /**
     * 根据userId获取用户信息
     */
    private static String GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        QywxAuthenticationToken wxAuthenticationToken = (QywxAuthenticationToken) authentication;
        Object code = wxAuthenticationToken.getPrincipal();
        String userId = null;
        String name = null;
        String token = getAccessToken();
        HttpUrl url = HttpUrl.parse(GET_USERID_URL).newBuilder()
                .addQueryParameter("access_token", token)
                .addQueryParameter("code", code.toString())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 根据code获取userId
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jo = JSONUtil.parseObj(response.body().string());
                userId = jo.getStr("UserId");
                HttpUrl url1 = HttpUrl.parse(GET_USER_INFO_URL).newBuilder()
                        .addQueryParameter("access_token", token)
                        .addQueryParameter("userid", userId)
                        .build();
                Request request1 = new Request.Builder()
                        .url(url1)
                        .build();
                // 根据userId获取用户信息
                Response response1 = client.newCall(request1).execute();
                if (response1.isSuccessful() && response1.body() != null) {
                    JSONObject jo1 = JSONUtil.parseObj(response1.body().string());
                    log.info("企业微信返回结果：{}", JSON.toJSONString(jo1));
                    name = jo1.getStr("name");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StringUtils.isAnyBlank(name, userId)) {
            throw new BadCredentialsException("获取企业微信用户信息失败");
        }
        // todo 暂时给固定租户
        Wrappers<Object> w = Wrappers.init()
                .where(TENANT.TENANT_NAME.eq("中科闻歌"))
                .and(TENANT.STATUS.eq(1))
                .limit(1);
        Tenant tenant = oauthTenantService.getOne(w);
        if (tenant == null) {
            throw new BadCredentialsException("请先配置租户");
        }

        // 通过企业微信的 id 查询用户，并且是企业微信注册的账号
        Wrappers<Object> wrappers = Wrappers.init()
                .where(OAUTH_USER.ACCOUNT_NAME.eq(userId))
                .and(OAUTH_USER.SOURCE.eq(UserSourceEnum.WE_COM.getCode()))
                .limit(1);
        OauthUser oauthUser = userService.getOne(wrappers);
        boolean firstLoginFlag = false;
        // 如果用户不存在，创建用户
        if (null == oauthUser) {
            firstLoginFlag = true;
            OauthUser user = OauthUser.create();
            user.setUserName(name);
            user.setAccountName(userId);
            // user.setPassword("cc958716b6582c164a4c8a5c4bf8c886");
            user.setStatus(UserStatusEnum.NORMAL.getCode());
            user.setPowerType(PowerTypeEnum.NORMAL_USER.getCode());
            user.setTenantId(tenant.getTenantId());
            user.setSource(UserSourceEnum.WE_COM.getCode());
            user.setOpenId(userId);
            userService.save(user);
            oauthUser = user;
            // 绑定角色
            bindingRole(user);
        } else {
            // 验证账号状态
            AuthenticationUtil.checkAccount(oauthUser);
            if (StringUtils.isBlank(oauthUser.getPassword())) {
                firstLoginFlag = true;
            }
        }
        if (StringUtils.isBlank(oauthUser.getTenantId())) {
            oauthUser.setTenantId(tenant.getTenantId());
            userService.saveOrUpdate(oauthUser);
        }
        TokenUser tokenUser = BeanUtil.toBean(oauthUser, TokenUser.class);
        tokenUser.setFirstLoginFlag(firstLoginFlag);
        wxAuthenticationToken = new QywxAuthenticationToken(tokenUser, null, null, null);
        tokenUser.setLonginWay(UserSourceEnum.WE_COM);
        return wxAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return QywxAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 获取企业微信access_token，并添加1h缓存
     *
     * @return access_token值
     */
    private String getAccessToken() {
        Object token = redisService.get(RedisConstant.QYWX_TOKEN);
        if (token == null) {
            HttpUrl url = HttpUrl.parse(GET_TOKEN_URL).newBuilder()
                    .addQueryParameter("corpid", appid)
                    .addQueryParameter("corpsecret", secret)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    JSONObject jo = JSONUtil.parseObj(response.body().string());
                    String accessToken = jo.getStr("access_token");
                    // 缓存1h
                    redisService.set(RedisConstant.QYWX_TOKEN, accessToken, 60 * 60);
                    return accessToken;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return token.toString();
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
        param.setRoleCodeList(ListUtil.toList("zkwg"));
        userService.bindingRoleByCode(param);
    }
}
