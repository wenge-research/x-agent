package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.SiliconFlowCompletionsConfig;
import com.wg.appframe.yayi.config.SiliconflowConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DeepseekCompletionParam;
import com.wg.appframe.yayi.param.SiliconFlowCompletionParam;
import com.wg.appframe.yayi.result.SiliconFlowCompletionResult;
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
public class SiliconFlowCompletionStrategy implements Serializable {
    
    private static final long serialVersionUID = -5526023661930751621L;
    
    @Autowired(required = false)
    private SiliconflowConfig siliconflowConfig;

    @Autowired(required = false)
    private SiliconFlowCompletionsConfig siliconFlowCompletionsConfig;

    public SiliconFlowCompletionResult run(String content, SiliconFlowCompletionParam param, Consumer<SiliconFlowCompletionResult> consumer) {
        SiliconFlowCompletionResult result = new SiliconFlowCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            param = getParam(content, param);

            builder.addHeader("Authorization", "Bearer " + param.getAppKey());

            // 请求 deepseekCompletions接口
            log.info("===>siliconflowCompletions url:{},  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

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
                        return new SiliconFlowCompletionResult();
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
                            log.info("===>siliconflowCompletions body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                return entries.toBean(SiliconFlowCompletionResult.class);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用siliconflowCompletions大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用siliconflowCompletions大模型算法失败");
            }
            return new SiliconFlowCompletionResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<SiliconFlowCompletionResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (null != siliconflowConfig.getLogEnabled() && siliconflowConfig.getLogEnabled()) {
                log.info("===>siliconflow line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        SiliconFlowCompletionResult bean = entries.toBean((Class<? extends SiliconFlowCompletionResult>) SiliconFlowCompletionResult.class);
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
    private SiliconFlowCompletionParam getParam(String content, SiliconFlowCompletionParam param) {
        SiliconFlowCompletionParam paramTemp = new SiliconFlowCompletionParam();

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (StringUtils.isNotBlank(siliconFlowCompletionsConfig.getModel())) {
            paramTemp.setModel(siliconFlowCompletionsConfig.getModel());
        }

        if (null != siliconFlowCompletionsConfig.getPresencePenalty()) {
            paramTemp.setPresence_penalty(siliconFlowCompletionsConfig.getPresencePenalty());
        }

        if (null != siliconFlowCompletionsConfig.getFrequencyPenalty()) {
            paramTemp.setFrequency_penalty(siliconFlowCompletionsConfig.getFrequencyPenalty());
        }

        if (StringUtils.isNotBlank(siliconFlowCompletionsConfig.getStop())) {
            String[] split = siliconFlowCompletionsConfig.getStop().split(",");
            paramTemp.setStop(split);
        }

        if (null != siliconFlowCompletionsConfig.getTopP()) {
            paramTemp.setTop_p(siliconFlowCompletionsConfig.getTopP());
        }

        if (null != siliconFlowCompletionsConfig.getTopK()) {
            paramTemp.setTop_k(siliconFlowCompletionsConfig.getTopK());
        }

        if (null != siliconFlowCompletionsConfig.getTemperature()) {
            paramTemp.setTemperature(siliconFlowCompletionsConfig.getTemperature());
        }

        if (null != siliconFlowCompletionsConfig.getMaxTokens()) {
            paramTemp.setMax_tokens(siliconFlowCompletionsConfig.getMaxTokens());
        }
        paramTemp.setRetryTimes(1);
        if (null != siliconFlowCompletionsConfig.getRetryTimes()) {
            paramTemp.setRetryTimes(siliconFlowCompletionsConfig.getRetryTimes());
        }
        paramTemp.setRetryInterval(3000);
        if (null != siliconFlowCompletionsConfig.getRetryInterval()) {
            paramTemp.setRetryInterval(siliconFlowCompletionsConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(param.getUri())) {
            paramTemp.setUri(siliconFlowCompletionsConfig.getUri());
        }
        if (StringUtils.isBlank(param.getAppKey())) {
            paramTemp.setAppKey(siliconflowConfig.getAppKey());
        }

        if (StringUtils.isNotBlank(siliconFlowCompletionsConfig.getResponseFormat())) {
            DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
            responseFormat.setType(siliconFlowCompletionsConfig.getResponseFormat());
            paramTemp.setResponse_format(responseFormat);
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


}
