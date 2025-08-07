/*
 * Copyright 2021-2021 Wenge Group Holding Ltd.
 */

package com.wenge.oauth.holder;


import com.wenge.oauth.entity.TokenUser;

/**
 * <p>
 *  服务上下文
 * </p>
 *
 * @author yangyunjun
 * @since 2021-07-08
 */
public class AppContextHolder {

    /**
     * 获取Token用户信息
     *
     * @return 返回
     */
    public static TokenUser getTokenUserInfo() {
        TokenUser tokenOauthUserInfo = ContextHolders.getTokenUserInfo();
        if (null == tokenOauthUserInfo) {
            tokenOauthUserInfo = new TokenUser();
        }
        return tokenOauthUserInfo;
    }

    /**
     * 获取Token用户信息
     *
     * @return 返回
     */
    public static TokenUser getTokenUserInfo(String token) {
        TokenUser tokenOauthUserInfo = ContextHolders.getTokenUserInfo(token);
        if (null == tokenOauthUserInfo) {
            tokenOauthUserInfo = new TokenUser();
        }
        return tokenOauthUserInfo;
    }

    /**
     * 获取Token用户账号
     *
     * @return 返回
     */
    public static String getAccountName() {
        TokenUser tokenOauthUserInfo = ContextHolders.getTokenUserInfo();
        if (null == tokenOauthUserInfo) {
            tokenOauthUserInfo = new TokenUser();
        }
        return tokenOauthUserInfo.getAccountName();
    }
}
