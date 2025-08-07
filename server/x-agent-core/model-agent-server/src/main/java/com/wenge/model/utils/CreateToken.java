package com.wenge.model.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

/**
 * 获取阿里token
 */
@Slf4j
public class CreateToken {
    private final static String TIME_ZONE = "GMT";
    private final static String FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final static String URL_ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    private static String token = null;
    private static long expireTime = 0;

    /**
     * 获取时间戳
     * 必须符合ISO8601规范，并需要使用UTC时间，时区为+0。
     */
    public static String getISO8601Time(Date date) {
        Date nowDate = date;
        if (null == date) {
            nowDate = new Date();
        }
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ISO8601);
        df.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));
        return df.format(nowDate);
    }

    /**
     * 获取UUID
     */
    public static String getUniqueNonce() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * URL编码
     * 使用UTF-8字符集按照RFC3986规则编码请求参数和参数取值。
     */
    public static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, URL_ENCODING).replace("+", "%20")
                .replace("*", "%2A").replace("%7E", "~") : null;
    }

    /***
     * 将参数排序后，进行规范化设置，组合成请求字符串。
     * @param queryParamsMap   所有请求参数
     * @return 规范化的请求字符串
     */
    public static String canonicalizedQuery(Map<String, String> queryParamsMap) {
        String[] sortedKeys = queryParamsMap.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);
        String queryString = null;
        try {
            StringBuilder canonicalizedQueryString = new StringBuilder();
            for (String key : sortedKeys) {
                canonicalizedQueryString.append("&")
                        .append(percentEncode(key)).append("=")
                        .append(percentEncode(queryParamsMap.get(key)));
            }
            queryString = canonicalizedQueryString.toString().substring(1);
            System.out.println("规范化后的请求参数串：" + queryString);
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 encoding is not supported.");
            e.printStackTrace();
        }
        return queryString;
    }

    /***
     * 构造签名字符串
     * @param method       HTTP请求的方法
     * @param urlPath      HTTP请求的资源路径
     * @param queryString  规范化的请求字符串
     * @return 签名字符串
     */
    public static String createStringToSign(String method, String urlPath, String queryString) {
        String stringToSign = null;
        try {
            StringBuilder strBuilderSign = new StringBuilder();
            strBuilderSign.append(method);
            strBuilderSign.append("&");
            strBuilderSign.append(percentEncode(urlPath));
            strBuilderSign.append("&");
            strBuilderSign.append(percentEncode(queryString));
            stringToSign = strBuilderSign.toString();
            System.out.println("构造的签名字符串：" + stringToSign);
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 encoding is not supported.");
            e.printStackTrace();
        }
        return stringToSign;
    }

    /***
     * 计算签名
     * @param stringToSign      签名字符串
     * @param accessKeySecret   阿里云AccessKey Secret加上与号&
     * @return 计算得到的签名
     */
    public static String sign(String stringToSign, String accessKeySecret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM_NAME);
            mac.init(new SecretKeySpec(
                    accessKeySecret.getBytes(ENCODING),
                    ALGORITHM_NAME
            ));
            byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));
            String signBase64 = DatatypeConverter.printBase64Binary(signData);
            System.out.println("计算的得到的签名：" + signBase64);
            String signUrlEncode = percentEncode(signBase64);
            System.out.println("UrlEncode编码后的签名：" + signUrlEncode);
            return signUrlEncode;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.toString());
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    /***
     * 发送HTTP GET请求，获取token和有效期时间戳。
     * @param queryString 请求参数
     */
    public static String processGETRequest(String url, String queryString) {

        /**
         * 设置HTTP GET请求
         * 1. 使用HTTP协议
         * 2. Token服务域名：nls-meta.cn-shanghai.aliyuncs.com
         * 3. 请求路径：/
         * 4. 设置请求参数
         */
//        String url = "http://nls-meta.cn-shanghai.aliyuncs.com";
        url = url + "/";
        url = url + "?" + queryString;
        System.out.println("HTTP请求链接：" + url);
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .get()
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            if (response.isSuccessful()) {
                log.info("本次请求阿里token接口返回：{}", result);
                JSONObject rootObj = JSON.parseObject(result);
                JSONObject tokenObj = rootObj.getJSONObject("Token");
                if (tokenObj != null) {
                    return tokenObj.getString("Id");
//                    expireTime = tokenObj.getLongValue("ExpireTime");
                } else {
                    System.err.println("提交获取Token请求失败: " + result);
                }
            } else {
                System.err.println("提交获取Token请求失败: " + result);
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("未获取到阿里token");
    }

}