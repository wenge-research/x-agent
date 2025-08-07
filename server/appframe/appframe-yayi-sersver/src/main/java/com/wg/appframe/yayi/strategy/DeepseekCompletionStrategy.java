package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.DeepSeekConfig;
import com.wg.appframe.yayi.config.DeepseekCompletionsConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DeepseekCompletionParam;
import com.wg.appframe.yayi.result.DeepseekCompletionResult;
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
public class DeepseekCompletionStrategy implements Serializable {
    
    private static final long serialVersionUID = -5526023661930751621L;
    
    @Autowired(required = false)
    private DeepSeekConfig deepSeekConfig;

    @Autowired(required = false)
    private DeepseekCompletionsConfig deepseekCompletionsConfig;

    public DeepseekCompletionResult run(String content, DeepseekCompletionParam param, Consumer<DeepseekCompletionResult> consumer) {
        DeepseekCompletionResult result = new DeepseekCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            param = getParam(content, param);

            builder.addHeader("Authorization", "Bearer " + param.getAppKey());
            builder.addHeader("szc-api-key", param.getAppKey());

            // 请求 deepseekCompletions接口
            log.info("===>deepseekCompletions url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

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
                        return new DeepseekCompletionResult();
                    }

                    try (final Response response = okHttpClient.newCall(request).execute();
                         final InputStream it = response.body().byteStream()) {
                        final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
                        responseByStream(bufferedReader, consumer);
                    }
                } else {
                    try (Response response = okHttpClient.newCall(request).execute();
                         ResponseBody responseBody = response.body()) {
                        if (null != responseBody) {
                            String responses = responseBody.string();
                            log.info("===>deepseekCompletions body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                return entries.toBean(DeepseekCompletionResult.class);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用deepseekCompletions大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用deepseekCompletions大模型算法失败");
            }
            return new DeepseekCompletionResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<DeepseekCompletionResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (null != deepSeekConfig.getLogEnabled() && deepSeekConfig.getLogEnabled()) {
                log.info("===>deepseekCompletions line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        DeepseekCompletionResult bean = entries.toBean((Class<? extends DeepseekCompletionResult>) DeepseekCompletionResult.class);
                        DeepseekCompletionResult.GenerateMessage message = bean.getMessage();
                        if (null != message) {
                            bean.setChoices(ListUtil.toList());
                            DeepseekCompletionResult.GenerateChoice generateChoice = new DeepseekCompletionResult.GenerateChoice();
                            generateChoice.setDelta(bean.getMessage());
                            bean.getChoices().add(generateChoice);
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
    private DeepseekCompletionParam getParam(String content, DeepseekCompletionParam param) {
        DeepseekCompletionParam paramTemp = new DeepseekCompletionParam();

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (null != deepseekCompletionsConfig.getMaxTokens()) {
            paramTemp.setMax_tokens(deepseekCompletionsConfig.getMaxTokens());
        }
        if (null != deepseekCompletionsConfig.getTemperature()) {
            paramTemp.setTemperature(deepseekCompletionsConfig.getTemperature());
        }
        if (null != deepseekCompletionsConfig.getPresencePenalty()) {
            paramTemp.setPresence_penalty(deepseekCompletionsConfig.getPresencePenalty());
        }
        if (null != deepseekCompletionsConfig.getFrequencyPenalty()) {
            paramTemp.setFrequency_penalty(deepseekCompletionsConfig.getFrequencyPenalty());
        }
        if (null != deepseekCompletionsConfig.getTopP()) {
            paramTemp.setTop_p(deepseekCompletionsConfig.getTopP());
        }

        if (StringUtils.isNotBlank(deepseekCompletionsConfig.getStop())) {
            String[] split = deepseekCompletionsConfig.getStop().split(",");
            paramTemp.setStop(split);
        }

        if (StringUtils.isNotBlank(deepseekCompletionsConfig.getModel())) {
            paramTemp.setModel(deepseekCompletionsConfig.getModel());
        }
        paramTemp.setRetryTimes(1);
        if (null != deepseekCompletionsConfig.getRetryTimes()) {
            paramTemp.setRetryTimes(deepseekCompletionsConfig.getRetryTimes());
        }
        paramTemp.setRetryInterval(3000);
        if (null != deepseekCompletionsConfig.getRetryInterval()) {
            paramTemp.setRetryInterval(deepseekCompletionsConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(param.getUri())) {
            param.setUri(deepseekCompletionsConfig.getUri());
        }
        if (StringUtils.isBlank(param.getAppKey())) {
            param.setAppKey(deepSeekConfig.getAppKey());
        }

        if (StringUtils.isNotBlank(deepseekCompletionsConfig.getResponseFormat())) {
            DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
            responseFormat.setType(deepseekCompletionsConfig.getResponseFormat());
            paramTemp.setResponse_format(responseFormat);
        }

        paramTemp.setStream(param.getStream());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();

        // 推理模型不支持logprobs、top_logprobs 会报错
        if ("deepseek-reasoner".equals(param.getModel())) {
            param.setLogprobs(null);
            param.setTop_logprobs(null);

            DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
            responseFormat.setType("text");
            paramTemp.setResponse_format(responseFormat);
        }

        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            paramTemp.setMessages(messagesLists);
        }
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }


}
