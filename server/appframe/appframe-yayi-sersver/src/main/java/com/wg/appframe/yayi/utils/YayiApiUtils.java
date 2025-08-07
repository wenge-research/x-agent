package com.wg.appframe.yayi.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.YayiAutoConfigure;
import com.wg.appframe.yayi.config.YayiConfig;
import com.wg.appframe.yayi.entity.YayiSignature;
import com.wg.appframe.yayi.param.YayiParam;
import com.wg.appframe.yayi.result.YayiResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class YayiApiUtils {


    /**
     * 设置雅意接口请求的头部参数
     *
     * @param builder
     * @throws Exception
     */
    public static void setHeader(Request.Builder builder, String uri, YayiConfig yayiConfig, YayiParam param) throws Exception {
        // 优先使用参数中的配置
        String appSecret = StringUtils.isNotBlank(param.getAppSecret()) ? param.getAppSecret() : yayiConfig.getAppSecret();
        String appKey = StringUtils.isNotBlank(param.getAppKey()) ? param.getAppKey() : yayiConfig.getAppKey();
        // 设置接口请求的头部参数
        YayiSignature yayiSignature = new YayiSignature();
        yayiSignature.setRequestMethod(yayiConfig.getMethod());
        yayiSignature.setHost(yayiConfig.getHost());
        yayiSignature.setAppSecret(appSecret);

        builder.addHeader("x-tilake-app-key", appKey);
        builder.addHeader("x-tilake-ca-signature-method", yayiConfig.getSignatureMethod());
        builder.addHeader("x-tilake-ca-timestamp", System.currentTimeMillis() + "");
        builder.addHeader("x-tilake-ca-nonce", IdUtil.simpleUUID());
        String date = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd HH:mm:ss");

        yayiSignature.setAccept(yayiConfig.getAccept());
        yayiSignature.setContentType(yayiConfig.getContentType());
        yayiSignature.setSignatureMethod(yayiConfig.getSignatureMethod());
        builder.addHeader("x-tilake-ca-signature", signature(date, uri, yayiSignature));
        builder.addHeader("Date", date);
        builder.addHeader("Content-Type", yayiConfig.getContentType());
        builder.addHeader("Accept", yayiConfig.getAccept());

    }

    /**
     * 雅意接口签名
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static String signature(String date, String uri, YayiSignature signature) throws Exception {
        if (uri.contains("saas-gateway")) {
            int indexOf = uri.indexOf("saas-gateway");
            // 从saas-gatewa开始截取
            uri = uri.substring(indexOf + 12);
        }
        String stringToSign = signature.getRequestMethod()
                + "\n" + signature.getAccept()
                + "\n" + signature.getContentType()
                + "\n" + date
                + "\n" + uri.replace(signature.getHost(), "");

        Mac hmacSha256 = Mac.getInstance(signature.getSignatureMethod());
        byte[] appSecretBytes = signature.getAppSecret().getBytes(StandardCharsets.UTF_8);
        hmacSha256.init(new SecretKeySpec(appSecretBytes, 0, appSecretBytes.length, signature.getSignatureMethod()));
        byte[] md5Result = hmacSha256.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(md5Result);
    }

    /**
     * 调用雅意接口
     *
     * @return 请求响应
     */
    public static String invokeYaYiApi(Request.Builder builder, YayiParam yayiParam) {
        try {
            final String contentType = "application/json; charset=utf-8";

            final MediaType mediaType = MediaType.parse(contentType);
            final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(yayiParam));
            Request request = builder
                    .url(yayiParam.getUri())
                    .post(body)
                    .build();

            OkHttpClient okHttpClient = YayiAutoConfigure.getOkHttpClient(yayiParam);
            try (Response response = okHttpClient.newCall(request).execute();
                 ResponseBody responseBody = response.body()) {
                if (null != responseBody) {
                    return responseBody.string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("调用雅意大模型算法失败，url = {}", yayiParam.getUri(), e);
        }
        return "";
    }

    /**
     * 调用雅意大模型流式响应接口
     *
     * @param url       接口地址
     * @param param 请求体参数
     * @return 请求响应
     */
    public static void invokeYaYiStreamApi(Request.Builder builder, String url, YayiParam param, Consumer<YayiResult> consumer, Class<? extends YayiResult> cls, Map<String, String> resultMap) {
        if (null == param.getRetryTimes()) {
            param.setRetryTimes(1);
        }
        if (null == param.getRetryInterval()) {
            param.setRetryInterval(300);
        }
        try {
            final String contentType = "application/json; charset=utf-8";

            final MediaType mediaType = MediaType.parse(contentType);
            final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param));

            Request request = builder
                    .url(url)
                    .post(body)
                    .build();
            OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);
            getResultByStream(okHttpClient, request, consumer, cls, resultMap);
            //// 重试次数
            //for (int i = 0; i < param.getRetryTimes(); i++) {
            //    if (!"1".equals(resultMap.get("isRetry"))) {
            //        break;
            //    }
            //    log.info("===>invokeYaYiStreamApi 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒", i + 1, param.getRetryTimes(), param.getRetryInterval());
            //    // 重试等待时间
            //    ThreadUtil.safeSleep(param.getRetryInterval());
            //    getResultByStream(okHttpClient, request, consumer, cls, resultMap);
            //}
        } catch (Exception e) {
            log.error("调用雅意大模型算法失败，url = {}", url, e);
            throw new RuntimeException("调用雅意大模型算法失败");
        }
    }

    /**
     * 获取响应结果
     * @param okHttpClient
     * @param request
     * @param consumer
     * @param cls
     * @param resultMap
     */
    private static void getResultByStream(OkHttpClient okHttpClient, Request request, Consumer<YayiResult> consumer, Class<? extends YayiResult> cls, Map<String, String> resultMap) {
        YayiResult bean = new YayiResult();
        try (final Response response = okHttpClient.newCall(request).execute();
             final InputStream it = response.body().byteStream()) {
            final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
            responseByStream(bufferedReader, (results) -> {
                String msg = results.getMsg();
                if (msg.contains("QPS")) {
                    // 1为需要重试
                    resultMap.put("isRetry", "1");
                } else {
                    resultMap.put("isRetry", "0");
                    consumer.accept(results);
                }
            }, cls);
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(bean);
        }
    }

    private static void responseByStream(BufferedReader bufferedReader, Consumer<YayiResult> consumer, Class<? extends YayiResult> cls) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");
                    if (JSONUtil.isTypeJSON(line)) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        YayiResult bean = entries.toBean(cls);
                        consumer.accept(bean);
                    } else if (line.contains("QPS")) {
                        YayiResult bean = new YayiResult();
                        bean.setMsg("QPS不足");
                        consumer.accept(bean);
                    }
                }
            }
        }
    }

    /**
     * 获取结果
     * @param okHttpClient
     * @param request
     * @return
     */
    private static YayiResult getResult(OkHttpClient okHttpClient, Request request) {
        YayiResult result = new YayiResult();
        try (Response response = okHttpClient.newCall(request).execute();
             ResponseBody responseBody = response.body()) {
            if (null != responseBody) {
                String responses = responseBody.string();
                log.info("===>YAYI result body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    result = entries.toBean(YayiResult.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
