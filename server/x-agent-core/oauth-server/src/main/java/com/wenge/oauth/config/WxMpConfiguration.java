package com.wenge.oauth.config;

import com.wenge.oauth.constants.AppConfigContant;
import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;

import static com.wenge.oauth.constants.AppConfigContant.*;

/**
 * wechat mp configuration
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Data
public class WxMpConfiguration {

    /**
     * 获取微信服务端配置
     * @param applicationId
     * @return
     */
    public static WxMpService wxMpService(String applicationId) {
        // todo:临时使用配置获取应用id，后续要删掉
        if (StringUtils.isBlank(applicationId)) {
            applicationId = AppConfigContant.getConfiguration(SYNC_GUANXIN_ZHIXUN_USER_APPID);
        }
        String secret = AppConfigContant.getConfiguration(applicationId, WECHAT_SECRET);
        String appId = AppConfigContant.getConfiguration(applicationId, WECHAT_APP_ID);
        String token = AppConfigContant.getConfiguration(applicationId, WECHAT_TOKEN);
        String aesKey = AppConfigContant.getConfiguration(applicationId, WECHAT_AES_KEY);
        if (StringUtils.isAnyBlank(secret, appId)) {
            throw new RuntimeException("公众号配置信息不全错误");
        }
        WxMpService service = new WxMpServiceImpl();
        WxMpDefaultConfigImpl wxMpDefaultConfig = new WxMpDefaultConfigImpl();
        wxMpDefaultConfig.setAppId(appId);
        wxMpDefaultConfig.setSecret(secret);
        wxMpDefaultConfig.setToken(token);
        wxMpDefaultConfig.setAesKey(aesKey);
        //service.setMultiConfigStorages(storage);
        service.setWxMpConfigStorage(wxMpDefaultConfig);
        return service;
    }

}
