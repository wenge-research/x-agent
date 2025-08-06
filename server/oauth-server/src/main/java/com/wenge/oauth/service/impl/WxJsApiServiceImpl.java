package com.wenge.oauth.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.wenge.oauth.config.WxMpConfiguration;
import com.wenge.oauth.dto.param.WxSignatureParam;
import com.wenge.oauth.service.WxJsApiService;
import com.wenge.oauth.util.Sha1Util;
import com.wenge.oauth.util.WxSignatureUtil;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@Slf4j
public class WxJsApiServiceImpl implements WxJsApiService {
    @Value("${wx-official-signature.accessTokenApiUrl}")
    private String accessTokenApiUrl;

    @Value("${wx-official-signature.ticketApiUrl}")
    private String ticketApiUrl;


    @Override
    public Result signatureByAccessToken(WxSignatureParam param) {
        log.info("************* 开始通过access_token获取签名 **************");

        String url = param.getUrl();
        String applicationId = param.getApplicationId();
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url不能为空");
        }
        if (StringUtils.isEmpty(param.getApplicationId())) {
            throw new IllegalArgumentException("applicationId不能为空");
        }

        String accessToken;
        String jsTicket;
        String appId;
        String secret;
        try {
            WxMpService wxService = WxMpConfiguration.wxMpService(applicationId);
            appId = wxService.getWxMpConfigStorage().getAppId();
            secret = wxService.getWxMpConfigStorage().getSecret();

            // 获取access_token
            accessToken = WxSignatureUtil.getAccessToken(accessTokenApiUrl, appId, secret);
            if (StringUtils.isEmpty(accessToken)) {
                throw new RuntimeException("access_token获取失败");
            }

            // 获取jsTicket
            jsTicket = WxSignatureUtil.getJsTicket(ticketApiUrl, accessToken);

        } catch (Exception e) {
            log.info("通过access_token方式获取签名", e);
            return Result.success(new JSONObject());
        }

        String nonceStr = RandomStringUtils.randomNumeric(10);
        String timeStamp = Long.toString((new Date()).getTime() / 1000);

        // 对string1作sha1加密，字段名和字段值都采用原始值，不进行URL转义
        String str1 = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s", jsTicket, nonceStr, timeStamp, url);
        String sign = Sha1Util.hex(str1);
        JSONObject res = new JSONObject();
        res.put("appId", appId);
        res.put("timestamp", timeStamp);
        res.put("nonceStr", nonceStr);
        res.put("signature", sign);

        log.info("************* 结束access_token签名获取，结果为：" + res);

        return Result.success(res);
    }

}

