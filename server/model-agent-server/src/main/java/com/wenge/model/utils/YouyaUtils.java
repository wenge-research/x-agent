package com.wenge.model.utils;

import cn.hutool.json.JSONUtil;
import com.wenge.model.dto.param.YouyaAddCatalogParam;
import com.wenge.model.dto.result.AddCatalogResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 调用优雅Api
 */
@Slf4j
@Service
public class YouyaUtils {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String youyaApiUrl;
    private static String youyaAddCatalogUri;
    private static String youyaAccessKey;
    private static String youyaSecretKey;
    private static String youyaCallback;

    @Value("${youya.access_key}")
    public void setYouyaAccessKey(String youyaAccessKey) {
        this.youyaAccessKey = youyaAccessKey;
    }

    @Value("${youya.secret_key}")
    public void setYouyaSecretKey(String youyaSecretKey) {
        this.youyaSecretKey = youyaSecretKey;
    }

    @Value("${youya.api_url}")
    public void setYouyaApiUrl(String youyaApiUrl) {
        this.youyaApiUrl = youyaApiUrl;
    }

    @Value("${youya.add_catalog_uri}")
    public void setYouyaAddCatalogUri(String youyaAddCatalogUri) {
        this.youyaAddCatalogUri = youyaAddCatalogUri;
    }

    @Value("${youya.callback}")
    public void setYouyaCallback(String youyaCallback) {
        this.youyaCallback = youyaCallback;
    }


    /**
     * 调用优雅创建编目api接口
     * @param youyaAddCatalogParam
     * @return
     */
    public static AddCatalogResult addCatalog(YouyaAddCatalogParam youyaAddCatalogParam) {

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(7200, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        long timestamp = System.currentTimeMillis() / 1000;
        // 构建请求体
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("url", youyaAddCatalogParam.getVideoUrl());
        paramMap.put("callbackUrl", youyaCallback);

        String paramStr = JSONUtil.toJsonStr(paramMap);
        // 构建签名字符串
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append("POST\n")
                .append(youyaAddCatalogUri).append("\n")
                .append(timestamp).append("\n")
                .append(paramStr).append("\n");

        // 构建请求
        Headers headers = createJSONHeaders(timestamp, stringToSign.toString());

        Request.Builder builder = new Request.Builder();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), paramStr);
        builder.url(youyaApiUrl + youyaAddCatalogUri).post(requestBody).headers(headers);

        Call call = httpClient.newCall(builder.build());
        Response response;
        String responseBody;
        try {
            long startTime = System.currentTimeMillis();
            response = call.execute();
            long endTime = System.currentTimeMillis();
            log.info("调用优雅创建编目接口入参:{}，耗时:{}ms",
                    paramStr, endTime - startTime);
            if (!response.isSuccessful()) {
                throw new RuntimeException("调用调用优雅创建编目接口失败");
            }
            responseBody = response.body().string();
            log.info(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JSONUtil.toBean(responseBody, AddCatalogResult.class);
    }

    private static Headers createJSONHeaders(long timestamp, String stringToSign) {
        String signature = HmacUtils.hmacSha256Hex(youyaSecretKey, stringToSign);

        return new Headers.Builder()
                .add("X-Api-Access-Key", youyaAccessKey)
                .add("X-Api-Timestamp", String.valueOf(timestamp))
                .add("X-Api-Signature", signature)
                .add("Content-Type", "application/json")
                .build();
    }
}
