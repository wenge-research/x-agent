package com.wenge.oauth.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <P>
 *
 * </p>
 *
 * @author zs
 * @date 2024/10/18
 */
@Slf4j
public class WxSignatureUtil {

    /**
     * config 签名ticket获取
     *
     * @param accessToken 微信认证token
     */
    public static String getJsTicket(String ticketApiUrl, String accessToken) throws Exception {
        if (StringUtils.isEmpty(ticketApiUrl) || StringUtils.isEmpty(accessToken)) {
            throw new IllegalArgumentException("ticketApiUrl,accessToken任意一个参数都不能为空");
        }

        String result = "";
        try {
            String utf8Code = String.valueOf(StandardCharsets.UTF_8);
            String url = ticketApiUrl + "?access_token=" + URLEncoder.encode(accessToken, utf8Code) + "&type=" + URLEncoder.encode("jsapi", utf8Code);
            try {
                result = HttpUtil.get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSON.parseObject(result);
        Integer errcode = jsonObject.getInteger("errcode");
        if (!Objects.equals(errcode, 0)) {
            throw new Exception("微信平台ticket获取失败，接口返回结果:" + jsonObject);
        }

        log.info("获取ticket成功，jsonObject：" + jsonObject);
        return jsonObject.getString("ticket");
    }


    /**
     * 获取access_token
     *
     * @return
     * @throws Exception
     */
    public static String getAccessToken(String accessTokenApiUrl, String appid, String secret) throws Exception {
        if (StringUtils.isEmpty(accessTokenApiUrl) || StringUtils.isEmpty(appid) || StringUtils.isEmpty(secret)) {
            throw new IllegalArgumentException("accessTokenApiUrl,appid,secret任意一个参数都不能为空");
        }

        String result = "";
        try {
            String utf8Code = String.valueOf(StandardCharsets.UTF_8);
            String url = accessTokenApiUrl + "?grant_type=" + URLEncoder.encode("client_credential", utf8Code) + "&appid=" +
                    URLEncoder.encode(appid, utf8Code) + "&secret=" + URLEncoder.encode(secret, utf8Code);
            try {
                result = HttpUtil.get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSON.parseObject(result);
        Integer errcode = jsonObject.getInteger("errcode");
        if (Objects.nonNull(errcode)) {
            throw new Exception("微信平台access_token获取失败，接口返回结果:" + jsonObject);
        }

        log.info("获取access_token成功，jsonObject：" + jsonObject);
        return jsonObject.getString("access_token");
    }


}
