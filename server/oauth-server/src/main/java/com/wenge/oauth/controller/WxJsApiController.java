package com.wenge.oauth.controller;

import com.wenge.oauth.config.WxMpConfiguration;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.dto.param.WxSignatureParam;
import com.wenge.oauth.service.WxJsApiService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.net.URLEncoder;

import static com.wenge.oauth.constants.AppConfigContant.WECHAT_REDIRECT_URI;

/**
 * @author : smalljop
 * @description : 微信公众号网页
 * @create : 2020-12-02 13:40
 **/
@AllArgsConstructor
@RestController
@RequestMapping("/wx/jsapi/")
@Slf4j
public class WxJsApiController {


    @Autowired
    private WxJsApiService wxJsApiService;

    /**
     * 用户授权url
     *
     * @return hcah
     */
    @GetMapping("/authorization/url")
    public Result getAuthorizationUrl(@RequestParam(required = false) String url, String applicationId) {
        if (StringUtils.isBlank(url)) {
            url = AppConfigContant.getConfiguration(applicationId, WECHAT_REDIRECT_URI);
        }
        WxMpService wxService = WxMpConfiguration.wxMpService(applicationId);
        String appId = wxService.getWxMpConfigStorage().getAppId();
        String authorizationUrl = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect",
                appId,
                URLEncoder.encode(url));
        return Result.success(authorizationUrl);
    }


    /**
     * 根据code获取用户信息
     *
     * @param code
     * @return
     */
    @GetMapping("/authorization/user/info")
    public Result<WxOAuth2UserInfo> getUserByCode(@RequestParam @NotBlank String code, String applicationId) {
        WxOAuth2UserInfo userInfo = null;
        log.info("================ 》 获取微信用户信息code=" + code);
        try {
            WxMpService wxService = WxMpConfiguration.wxMpService(applicationId);
            WxOAuth2AccessToken accessToken = wxService.getOAuth2Service().getAccessToken(code);


            log.info("================ 》 获取微信用户信息 accessToken ： " + accessToken);
            userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, null);
            log.info("================ 》 获取微信用户信息 userInfo.nickname ： " + userInfo.getNickname());
            log.info("================ 》 获取微信用户信息 userInfo.openId ： " + userInfo.getOpenid());
            log.info("================ 》 获取微信用户信息 userInfo.headImgUrl ： " + userInfo.getHeadImgUrl());
        } catch (WxErrorException e) {
            log.info("================ 》 获取微信用户信息 异常 ： " + e.getMessage());
            return Result.fail("获取微信用户信息失败");
        }
        return Result.success(userInfo);
    }


    /**
     * 获取jsapi签名
     *
     * @param url 1
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/signature")
    public Result getSignature(@RequestParam String url, String applicationId) throws WxErrorException {
        WxMpService wxService = WxMpConfiguration.wxMpService(applicationId);
        WxJsapiSignature signature = wxService.createJsapiSignature(url);
        return Result.success(signature);
    }


    /**
     * 通过access_token方式获取签名
     * @param param
     * @return
     */
    @ApiOperation("通过access_token方式获取签名")
    @PostMapping("/signatureByAccessToken")
    public Result signatureByAccessToken(@RequestBody WxSignatureParam param) {
        return wxJsApiService.signatureByAccessToken(param);
    }

}
