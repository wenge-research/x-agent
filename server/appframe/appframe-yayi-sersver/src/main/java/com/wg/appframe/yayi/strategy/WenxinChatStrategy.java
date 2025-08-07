package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.WenxinChatConfig;
import com.wg.appframe.yayi.config.WenxinConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.WenxinChatParam;
import com.wg.appframe.yayi.result.WenxinChatResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class WenxinChatStrategy implements Serializable {
    
    private static final long serialVersionUID = 79949363008168044L;
    
    @Autowired(required = false)
    private WenxinConfig wenxinConfig;

    @Autowired(required = false)
    private WenxinChatConfig wenxinChatConfig;

    private static String ACCESS_TOKEN = "";

    public WenxinChatResult run(String content, WenxinChatParam param, Consumer<WenxinChatResult> consumer) {
        WenxinChatResult result = new WenxinChatResult();
        try {
            // 封装接口入参
            param = getParam(content, param);

            // 获取token
            getToken(param);
            
            if (StringUtils.isBlank(ACCESS_TOKEN)) {
                return result;
            }

            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 请求 wenxinChat接口
            log.info("===>wenxinChat url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                final String contentType = "application/json; charset=utf-8";

                final MediaType mediaType = MediaType.parse(contentType);
                final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param));
                Request request = builder
                        .url(param.getUri() + "?access_token=" + ACCESS_TOKEN)
                        .post(body)
                        .build();
                OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);

                // 流式输出
                if (param.getStream()) {
                    if (null == consumer) {
                        return new WenxinChatResult();
                    }
                    try (final Response response = okHttpClient.newCall(request).execute();
                         final InputStream it = response.body().byteStream()) {
                        final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
                        responseByStream(content, param, bufferedReader, consumer);
                    }
                } else {
                    try (Response response = okHttpClient.newCall(request).execute();
                         ResponseBody responseBody = response.body()) {
                        if (null != responseBody) {
                            String responses = responseBody.string();
                            log.info("===>wenxinChat body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                WenxinChatResult chatResult = entries.toBean(WenxinChatResult.class);
                                // token过期
                                if ("110".equals(chatResult.getErrorCode())) {
                                    ACCESS_TOKEN = "";
                                    return run(content, param, consumer);
                                }
                                return chatResult;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用wenxinChat大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用wenxinChat大模型算法失败");
            }
            return new WenxinChatResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(String content, WenxinChatParam param, BufferedReader bufferedReader, Consumer<WenxinChatResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line)) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        WenxinChatResult bean = entries.toBean((Class<? extends WenxinChatResult>) WenxinChatResult.class);
                        // token过期
                        if ("110".equals(bean.getErrorCode())) {
                            ACCESS_TOKEN = "";
                            run(content, param, consumer);
                            return;
                        }
                        consumer.accept(bean);
                    }
                }
            }
        }
    }

    /**
     * 初始化雅意128K接口参数
     * @param content
     * @param param
     * @return
     */
    private WenxinChatParam getParam(String content, WenxinChatParam param) {
        WenxinChatParam paramTemp = new WenxinChatParam();

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (null != wenxinChatConfig.getTemperature()) {
            paramTemp.setTemperature(wenxinChatConfig.getTemperature());
        }
        if (null != wenxinChatConfig.getTopP()) {
            paramTemp.setTop_p(wenxinChatConfig.getTopP());
        }

        if (null != wenxinChatConfig.getPenaltyScore()) {
            paramTemp.setPenalty_score(wenxinChatConfig.getPenaltyScore());
        }

        if (null != wenxinChatConfig.getEnableSystemMemory()) {
            paramTemp.setEnable_system_memory(wenxinChatConfig.getEnableSystemMemory());
        }

        if (StringUtils.isNotBlank(wenxinChatConfig.getSystemMemoryId())) {
            paramTemp.setSystem_memory_id(wenxinChatConfig.getSystemMemoryId());
        }

        if (StringUtils.isNotBlank(wenxinChatConfig.getSystem())) {
            paramTemp.setSystem(wenxinChatConfig.getSystem());
        }

        if (CollectionUtil.isNotEmpty(wenxinChatConfig.getStop())) {
            paramTemp.setStop(wenxinChatConfig.getStop());
        }

        if (null != wenxinChatConfig.getDisableSearch()) {
            paramTemp.setDisable_search(wenxinChatConfig.getDisableSearch());
        }

        if (null != wenxinChatConfig.getEnableCitation()) {
            paramTemp.setEnable_citation(wenxinChatConfig.getEnableCitation());
        }

        if (null != wenxinChatConfig.getEnableTrace()) {
            paramTemp.setEnable_trace(wenxinChatConfig.getEnableTrace());
        }

        if (null != wenxinChatConfig.getMaxOutputTokens()) {
            paramTemp.setMax_output_tokens(wenxinChatConfig.getMaxOutputTokens());
        }

        if (StringUtils.isNotBlank(wenxinChatConfig.getResponseFormat())) {
            paramTemp.setResponse_format(wenxinChatConfig.getResponseFormat());
        }

        if (StringUtils.isNotBlank(wenxinChatConfig.getUserId())) {
            paramTemp.setUser_id(wenxinChatConfig.getUserId());
        }

        if (StringUtils.isBlank(param.getUri())) {
            paramTemp.setUri(wenxinChatConfig.getUri());
        }
        if (StringUtils.isBlank(param.getAppKey())) {
            paramTemp.setAppKey(wenxinConfig.getAppKey());
        }
        if (StringUtils.isBlank(param.getAppSecret())) {
            paramTemp.setAppSecret(wenxinConfig.getAppSecret());
        }

        paramTemp.setStream(param.getStream());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            paramTemp.setMessages(messagesLists);
        }
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

    /**
     * 获取token
     */
    private void getToken(WenxinChatParam param) {
        if (StringUtils.isNotBlank(ACCESS_TOKEN)) {
            return;
        }
        log.info("wenxinChat 重新获取token");
        String tokenResponse = HttpUtil.get("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + param.getAppKey() + "&client_secret=" + wenxinConfig.getAppSecret());
        if (StringUtils.isBlank(tokenResponse)) {
            return;
        }
        JSONObject tokenData = JSONUtil.parseObj(tokenResponse);
        String accessToken = tokenData.getStr("access_token");
        if (StringUtils.isNotBlank(accessToken)) {
            ACCESS_TOKEN = accessToken;
        }
    }
}
