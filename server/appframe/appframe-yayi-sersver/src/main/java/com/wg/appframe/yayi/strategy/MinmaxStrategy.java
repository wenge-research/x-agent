package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.MinimaxConfig;
import com.wg.appframe.yayi.config.MinmaxCompletionsConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.MinmaxCompletionParam;
import com.wg.appframe.yayi.result.MinmaxCompletionResult;
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
public class MinmaxStrategy implements Serializable {

    private static final long serialVersionUID = -6645357340348510226L;
    
    @Autowired(required = false)
    private MinimaxConfig minimaxConfig;

    @Autowired(required = false)
    private MinmaxCompletionsConfig minmaxCompletionsConfig;

    public MinmaxCompletionResult run(String content, MinmaxCompletionParam param, Consumer<MinmaxCompletionResult> consumer) {
        MinmaxCompletionResult result = new MinmaxCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            param = getParam(content, param);
            if (null == param.getStream()) {
                param.setStream(null != consumer);
            }
            builder.addHeader("Authorization", "Bearer " + param.getAppKey());

            // 请求 minmaxCompletions接口
            log.info("===>minmaxCompletions url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

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
                        return new MinmaxCompletionResult();
                    }

                    try (final Response response = okHttpClient.newCall(request).execute();
                         final InputStream it = response.body().byteStream()) {
                        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
                        responseByStream(bufferedReader, consumer);
                    }
                } else {
                    try (Response response = okHttpClient.newCall(request).execute();
                         ResponseBody responseBody = response.body()) {
                        if (null != responseBody) {
                            String responses = responseBody.string();
                            log.info("===>minmaxCompletions body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                return entries.toBean(MinmaxCompletionResult.class);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用minmaxCompletions大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用minmaxCompletions大模型算法失败");
            }
            return new MinmaxCompletionResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<MinmaxCompletionResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (null != minimaxConfig.getLogEnabled() && minimaxConfig.getLogEnabled()) {
                log.info("===>minmax line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        MinmaxCompletionResult bean = entries.toBean((Class<? extends MinmaxCompletionResult>) MinmaxCompletionResult.class);
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
    private MinmaxCompletionParam getParam(String content, MinmaxCompletionParam param) {
        MinmaxCompletionParam paramTemp = new MinmaxCompletionParam();

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (StringUtils.isNotBlank(minmaxCompletionsConfig.getModel())) {
            paramTemp.setModel(minmaxCompletionsConfig.getModel());
        }
        if (null != minmaxCompletionsConfig.getTemperature()) {
            paramTemp.setTemperature(minmaxCompletionsConfig.getTemperature());
        }
        if (null != minmaxCompletionsConfig.getMax_tokens()) {
            paramTemp.setMax_tokens(minmaxCompletionsConfig.getMax_tokens());
        }

        if (null != minmaxCompletionsConfig.getMask_sensitive_info()) {
            paramTemp.setMask_sensitive_info(minmaxCompletionsConfig.getMask_sensitive_info());
        }

        if (null != minmaxCompletionsConfig.getTop_p()) {
            paramTemp.setTop_p(minmaxCompletionsConfig.getTop_p());
        }

        paramTemp.setRetryTimes(1);
        if (null != minmaxCompletionsConfig.getRetryTimes()) {
            paramTemp.setRetryTimes(minmaxCompletionsConfig.getRetryTimes());
        }
        paramTemp.setRetryInterval(3000);
        if (null != minmaxCompletionsConfig.getRetryInterval()) {
            paramTemp.setRetryInterval(minmaxCompletionsConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(paramTemp.getUri())) {
            paramTemp.setUri(minmaxCompletionsConfig.getUri());
        }
        if (StringUtils.isBlank(paramTemp.getAppKey())) {
            paramTemp.setAppKey(minimaxConfig.getApiKey());
        }

        // 根据输入的文本字数token数量，来确定使用哪个模型
        paramTemp.setStream(param.getStream());

        CopyOptions copyOptions = new CopyOptions();
        copyOptions.ignoreNullValue();
        List<YayiMessage> messagesLists = param.getMessages();
        BeanUtil.copyProperties(param, paramTemp, copyOptions);
        if (CollectionUtil.isNotEmpty(messagesLists)) {
            paramTemp.setMessages(messagesLists);
        }

        // 选择模型
        // setModel(paramTemp, content);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }
}
