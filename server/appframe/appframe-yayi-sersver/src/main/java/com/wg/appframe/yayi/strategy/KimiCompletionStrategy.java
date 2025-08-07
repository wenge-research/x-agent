package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.KimiCompletionsConfig;
import com.wg.appframe.yayi.config.KimiConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.KimiCompletionParam;
import com.wg.appframe.yayi.result.KimiCompletionResult;
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
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class KimiCompletionStrategy implements Serializable {

    private static final long serialVersionUID = -7295880730053082706L;

    @Autowired(required = false)
    private KimiConfig kimiConfig;

    @Autowired(required = false)
    private KimiCompletionsConfig kimiCompletionsConfig;

    private final static List<String> RETRY_ERROR_LIST = ListUtil.toList("rate_limit_reached_error", "engine_overloaded_error");

    public KimiCompletionResult run(String content, KimiCompletionParam param, Consumer<KimiCompletionResult> consumer) {
        KimiCompletionResult result = new KimiCompletionResult();
        try {
            // 封装接口入参
            param = getParam(content, param);

            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            builder.addHeader("Authorization", "Bearer " + param.getAppKey());

            try {
                final String contentType = "application/json; charset=utf-8";

                final MediaType mediaType = MediaType.parse(contentType);
                final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param));
                Request request = builder
                        .url(param.getUri())
                        .post(body)
                        .build();
                OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);

                // 流式输出
                if (param.getStream()) {
                    if (null == consumer) {
                        return new KimiCompletionResult();
                    }
                    Map<String, String> resultMap = MapUtil.newHashMap();
                    log.info("===>kimiCompletions url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));
                    getResultByStream(okHttpClient, request, resultMap, consumer);
                    // 重试次数
                    for (int i = 0; i < param.getRetryTimes(); i++) {
                        if (!"1".equals(resultMap.get("isRetry"))) {
                            break;
                        }
                        log.info("===>kimiCompletions 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, param.getRetryTimes(), param.getRetryInterval(), JSONUtil.toJsonStr(result));
                        // 重试等待时间
                        ThreadUtil.safeSleep(param.getRetryInterval());
                        log.info("===>kimiCompletions url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));
                        getResultByStream(okHttpClient, request, resultMap, consumer);
                    }
                } else {
                    result = getResult(okHttpClient, request, param);
                    // 重试次数
                    for (int i = 0; i < param.getRetryTimes(); i++) {
                        if (null == result.getError()) {
                            break;
                        }
                        if (RETRY_ERROR_LIST.contains(result.getError().getType())) {
                            log.info("===>kimiCompletions 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i + 1, param.getRetryTimes(), param.getRetryInterval(), result.getError().getMessage());
                            // 重试等待时间
                            ThreadUtil.safeSleep(param.getRetryInterval());
                            result = getResult(okHttpClient, request, param);
                        }
                    }
                    return result;
                }
            } catch (Exception e) {
                log.error("调用kimiCompletions大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用kimiCompletions大模型算法失败");
            }
            return new KimiCompletionResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<KimiCompletionResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (null != kimiConfig.getLogEnabled() && kimiConfig.getLogEnabled()) {
                log.info("===>doubao line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (!line.contains("[DONE]") && JSONUtil.isTypeJSON(line)) {
                        //System.out.println("--pp>>" + line);
                        JSONObject entries = JSONUtil.parseObj(line);
                        KimiCompletionResult bean = entries.toBean((Class<? extends KimiCompletionResult>) KimiCompletionResult.class);
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
    private KimiCompletionParam getParam(String content, KimiCompletionParam param) {
        KimiCompletionParam paramTemp = new KimiCompletionParam();

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (null != kimiCompletionsConfig.getMaxTokens()) {
            paramTemp.setMax_tokens(kimiCompletionsConfig.getMaxTokens());
        }
        if (null != kimiCompletionsConfig.getTemperature()) {
            paramTemp.setTemperature(kimiCompletionsConfig.getTemperature());
        }
        if (null != kimiCompletionsConfig.getPresencePenalty()) {
            paramTemp.setPresence_penalty(kimiCompletionsConfig.getPresencePenalty());
        }
        if (null != kimiCompletionsConfig.getFrequencyPenalty()) {
            paramTemp.setFrequency_penalty(kimiCompletionsConfig.getFrequencyPenalty());
        }
        if (null != kimiCompletionsConfig.getTopP()) {
            paramTemp.setTop_p(kimiCompletionsConfig.getTopP());
        }

        if (StringUtils.isNotBlank(kimiCompletionsConfig.getStop())) {
            paramTemp.setStop(kimiCompletionsConfig.getStop());
        }

        if (StringUtils.isNotBlank(kimiCompletionsConfig.getModel())) {
            paramTemp.setModel(kimiCompletionsConfig.getModel());
        }

        if (StringUtils.isNotBlank(kimiCompletionsConfig.getResponseFormat())) {
            KimiCompletionParam.ResponseFormat responseFormat = new KimiCompletionParam.ResponseFormat();
            responseFormat.setType(kimiCompletionsConfig.getResponseFormat());
            paramTemp.setResponse_format(responseFormat);
        }

        paramTemp.setRetryTimes(1);
        if (null != kimiCompletionsConfig.getRetryTimes()) {
            paramTemp.setRetryTimes(kimiCompletionsConfig.getRetryTimes());
        }
        paramTemp.setRetryInterval(3000);
        if (null != kimiCompletionsConfig.getRetryInterval()) {
            paramTemp.setRetryInterval(kimiCompletionsConfig.getRetryInterval());
        }
        paramTemp.setStream(param.getStream());
        if (null != kimiCompletionsConfig.getN()) {
            paramTemp.setN(kimiCompletionsConfig.getN());
        }

        if (StringUtils.isBlank(param.getAppKey())) {
            paramTemp.setAppKey(kimiConfig.getAppKey());
        }

        if (StringUtils.isBlank(param.getUri())) {
            paramTemp.setUri(kimiCompletionsConfig.getUri());
        }

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
     * 获取结果
     *
     * @param okHttpClient
     * @param request
     * @return
     */
    private KimiCompletionResult getResult(OkHttpClient okHttpClient, Request request, KimiCompletionParam param) {
        KimiCompletionResult result = new KimiCompletionResult();
        log.info("===>kimiCompletions url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));
        try (Response response = okHttpClient.newCall(request).execute();
             ResponseBody responseBody = response.body()) {
            if (null != responseBody) {
                String responses = responseBody.string();
                log.info("===>kimiCompletions body: {}", responses);
                if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                    JSONObject entries = JSONUtil.parseObj(responses);
                    result = entries.toBean(KimiCompletionResult.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取结果
     * @param okHttpClient
     * @param request
     * @param resultMap
     * @param consumer
     */
    private void getResultByStream(OkHttpClient okHttpClient, Request request, Map<String, String> resultMap, Consumer<KimiCompletionResult> consumer) {
        KimiCompletionResult bean = new KimiCompletionResult();
        try (final Response response = okHttpClient.newCall(request).execute();
             final InputStream it = response.body().byteStream()) {
            final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
            responseByStream(bufferedReader, (results) -> {
                KimiCompletionResult.Error error = results.getError();
                if (null != error) {
                    resultMap.put("isRetry", "1");
                    //log.error("===>kimiCompletions error:{}", error.getMessage());
                } else {
                    resultMap.put("isRetry", "0");
                    //log.info("===>kimiCompletions results:{}", results.getId());
                    consumer.accept(results);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(bean);
        }
    }
}
