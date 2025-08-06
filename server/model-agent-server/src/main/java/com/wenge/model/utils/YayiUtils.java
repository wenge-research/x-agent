package com.wenge.model.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.wenge.model.dto.param.CommonApiParam;
import com.wg.appframe.yayi.constants.StringConstans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/10 11:56
 */
@Slf4j
public class YayiUtils {

    public static List<Double> modelEncode(String query,String modelEncodeUri){
        if (StringUtils.isBlank(query)) {
            return Lists.newArrayList();
        }
        JSONObject param = new JSONObject();
        param.put("query", query);
        String post = "";
        for (int i = 0; i < 3; i++) {
            post = HttpUtil.post(modelEncodeUri, JSON.toJSONString(param));
            if (StringUtils.isNotBlank(post)) {
                break;
            }
        }
        if (StringUtils.isNotBlank(post) && JSONUtil.isTypeJSONObject(post)) {
            JSONObject jsonObject = JSON.parseObject(post);
            return jsonObject.getJSONArray("dataList").toJavaList(Double.class);
        }
        return ListUtil.toList();
    }

    /**
     * 调用雅意通用接口
     * @param param
     * @return
     */
    public static JSONObject commonApi(CommonApiParam param) {
        JSONObject bodyData = new JSONObject();
        try {
            // 创建POST请求
            cn.hutool.json.JSONObject detailParam = param.getParam();
            if (StringUtils.isNotBlank(detailParam.getStr("uri"))) {
                param.setUri(detailParam.getStr("uri"));
            }
            if (StringUtils.isNotBlank(detailParam.getStr("appKey"))) {
                param.setAppKey(detailParam.getStr("appKey"));
            }
            if (StringUtils.isNotBlank(detailParam.getStr("appSecret"))) {
                param.setAppSecret(detailParam.getStr("appSecret"));
            }
            String host = param.getUri();
            HttpRequest post = HttpUtil.createPost(host);
            post.setReadTimeout(600000);
            post.setConnectionTimeout(600000);
            post.timeout(600000);

            String uri = param.getUri();
            uri = uri.replace("https://yayi.wenge.com/saas-gateway", StringConstans.BLANK)
                    .replace("https://stg-yayi.wenge.com/saas-gateway", StringConstans.BLANK)
                    .replace("https://yayi.wenge.com/saas-gateway", StringConstans.BLANK)
                    // 兼容旧版本域名
                    .replace("https://tilake.wenge.com/saas-gateway", StringConstans.BLANK)
                    .replace("https://stg-tilake.wenge.com/saas-gateway", StringConstans.BLANK);
            // 设置雅意接口请求的头部参数
            setHeader(post, uri, param);

            // 封装13B接口入参
//            JSONObject param = getYarni13BParam(content);
            post.body(JSON.toJSONString(param.getParam()));

            // 请求雅意13B接口
            log.info("===>commonApi url:{},  param: {}", host, JSON.toJSONString(param));
            HttpResponse execute = post.execute();
            String body = execute.body();
            log.info("===>commonApi body: {}", body);

            if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
                bodyData = JSON.parseObject(body);
                log.info("===>commonApi bodyData: {}", JSON.toJSONString(bodyData));
                return bodyData;
            }
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return bodyData;
    }

    /**
     * 设置雅意13B接口请求的头部参数
     *
     * @param post
     * @throws Exception
     */
    public static void setHeader(HttpRequest post, String uri, CommonApiParam param) throws Exception {
        JSONObject header = param.getHeader();
        String date = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.PATTERN_1);
        post.header("Date", date);
        post.header("x-tilake-app-key", param.getAppKey());
        post.header("x-tilake-ca-signature-method", "HmacSHA256");
        post.header("x-tilake-ca-timestamp", System.currentTimeMillis() + "");
        post.header("x-tilake-ca-nonce", IdUtil.simpleUUID());
        post.header("x-tilake-ca-signature", signature(date, uri, header, param.getAppSecret()));
        post.header("Content-Type", header.getString("Content-Type"));
        post.header("Accept", header.getString("Accept"));
        header.forEach((head, value) -> post.header(head, value.toString()));
        JSONObject json = new JSONObject();
        List<String> accept = ListUtil.toList("Accept", "Accept-Encoding", "Accept-Language", "User-Agent");
        post.headers().forEach((k,v)->{
            if (!accept.contains(k)) {
                json.put(k, v.get(0));
            }
        });
        System.out.println("-->date:" + JSON.toJSONString(json));
    }

    /**
     * 雅意接口签名
     *
     * @param date
     * @return
     * @throws Exception
     */
    private static String signature(String date, String uri, JSONObject header, String appSecret) throws Exception {
        String stringToSign = "POST\n"
                + header.getString("Accept") + "\n"
                + header.getString("Content-Type") + "\n"
                + date + "\n"
                + uri;

        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        byte[] appSecretBytes = appSecret.getBytes(StandardCharsets.UTF_8);
        hmacSha256.init(new SecretKeySpec(appSecretBytes, 0, appSecretBytes.length, "HmacSHA256"));
        byte[] md5Result = hmacSha256.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(md5Result);
    }
}