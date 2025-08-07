package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.DoubaoCompletionsConfig;
import com.wg.appframe.yayi.config.DoubaoConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DoubaoCompletionParam;
import com.wg.appframe.yayi.param.DoubaoTokenizationParam;
import com.wg.appframe.yayi.result.DoubaoCompletionResult;
import com.wg.appframe.yayi.result.DoubaoTokenizationResult;
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
import java.util.stream.Collectors;

@Slf4j
public class DoubaoStrategy implements Serializable {
    
    private static final long serialVersionUID = -5526023661930751621L;
    
    @Autowired(required = false)
    private DoubaoConfig doubaoConfig;

    @Autowired(required = false)
    private DoubaoCompletionsConfig doubaoCompletionsConfig;

    public DoubaoCompletionResult run(String content, DoubaoCompletionParam param, Consumer<DoubaoCompletionResult> consumer) {
        DoubaoCompletionResult result = new DoubaoCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            param = getParam(content, param);

            builder.addHeader("Authorization", "Bearer " + param.getAppKey());

            // 请求 doubaoCompletions接口
            log.info("===>doubaoCompletions url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

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
                        return new DoubaoCompletionResult();
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
                            log.info("===>doubaoCompletions body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                return entries.toBean(DoubaoCompletionResult.class);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用doubaoCompletions大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用doubaoCompletions大模型算法失败");
            }
            return new DoubaoCompletionResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<DoubaoCompletionResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (null != doubaoConfig.getLogEnabled() && doubaoConfig.getLogEnabled()) {
                log.info("===>doubao line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        DoubaoCompletionResult bean = entries.toBean((Class<? extends DoubaoCompletionResult>) DoubaoCompletionResult.class);
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
    private DoubaoCompletionParam getParam(String content, DoubaoCompletionParam param) {

        DoubaoCompletionParam paramTemp = new DoubaoCompletionParam();

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (StringUtils.isNotBlank(doubaoCompletionsConfig.getModel())) {
            paramTemp.setModel(doubaoCompletionsConfig.getModel());
        }
        if (null != doubaoCompletionsConfig.getInclude_usage()) {
            DoubaoCompletionParam.StreamOptionsParam streamOptionsParam = new DoubaoCompletionParam.StreamOptionsParam();
            streamOptionsParam.setInclude_usage(doubaoCompletionsConfig.getInclude_usage());
            paramTemp.setStream_options(streamOptionsParam);
        }
        if (null != doubaoCompletionsConfig.getMaxTokens()) {
            paramTemp.setMax_tokens(doubaoCompletionsConfig.getMaxTokens());
        }
        if (StringUtils.isNotBlank(doubaoCompletionsConfig.getStop())) {
            String[] split = doubaoCompletionsConfig.getStop().split(",");
            paramTemp.setStop(split);
        }
        if (null != doubaoCompletionsConfig.getFrequencyPenalty()) {
            paramTemp.setFrequency_penalty(doubaoCompletionsConfig.getFrequencyPenalty());
        }

        if (null != doubaoCompletionsConfig.getPresencePenalty()) {
            paramTemp.setPresence_penalty(doubaoCompletionsConfig.getPresencePenalty());
        }
        if (null != doubaoCompletionsConfig.getTemperature()) {
            paramTemp.setTemperature(doubaoCompletionsConfig.getTemperature());
        }
        if (null != doubaoCompletionsConfig.getTopP()) {
            paramTemp.setTop_p(doubaoCompletionsConfig.getTopP());
        }

        if (null != doubaoCompletionsConfig.getLogprobs()) {
            paramTemp.setLogprobs(doubaoCompletionsConfig.getLogprobs());
        }
        if (null != doubaoCompletionsConfig.getTopLogprobs()) {
            paramTemp.setTop_logprobs(doubaoCompletionsConfig.getTopLogprobs());
        }
        if (null != doubaoCompletionsConfig.getLogit_bias()) {
            paramTemp.setLogit_bias(doubaoCompletionsConfig.getLogit_bias());
        }

        paramTemp.setRetryTimes(1);
        if (null != doubaoCompletionsConfig.getRetryTimes()) {
            paramTemp.setRetryTimes(doubaoCompletionsConfig.getRetryTimes());
        }
        paramTemp.setRetryInterval(3000);
        if (null != doubaoCompletionsConfig.getRetryInterval()) {
            paramTemp.setRetryInterval(doubaoCompletionsConfig.getRetryInterval());
        }

        if (StringUtils.isBlank(paramTemp.getUri())) {
            paramTemp.setUri(doubaoCompletionsConfig.getUri());
        }
        if (StringUtils.isBlank(paramTemp.getAppKey())) {
            paramTemp.setAppKey(doubaoConfig.getApiKey());
        }

        //if (StringUtils.isNotBlank(doubaoCompletionsConfig.getResponseFormat())) {
        //    DoubaoCompletionParam.ResponseFormat responseFormat = new DoubaoCompletionParam.ResponseFormat();
        //    responseFormat.setType(doubaoCompletionsConfig.getResponseFormat());
        //    paramTemp.setResponse_format(responseFormat);
        //}

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
        setModel(paramTemp, content);
        BeanUtil.copyProperties(paramTemp, param, copyOptions);
        return paramTemp;
    }

    /**
     * 设置模型
     *
     * @param param
     */
    private void setModel(DoubaoCompletionParam param, String content) {
        try {

            // 根据输入的文本字数token数量，来确定使用哪个模型
            String model = param.getModel();
            if (StringUtils.isBlank(model)) {
                model = doubaoCompletionsConfig.getModel();
            }

            if (StringUtils.isBlank(model)) {
                throw new RuntimeException("调用DoubaoChat大模型算法失败，缺少model配置");
            }

            // 收集所有的输入文本
            List<YayiMessage> messages = param.getMessages();
            String allText = "";
            if (CollectionUtil.isNotEmpty(messages)) {
                allText = messages.stream().map(YayiMessage::getContent).collect(Collectors.joining("；"));
            }
            allText += content;

            // 如果配置多个模型参数，例如：ep-20241022153706-98jv2|4096,ep-20241022153706-98jv2|2048，则按顺序自动选择最小的模型
            if (model.contains(",")) {
                String[] split = model.split(",");
                for (String str : split) {
                    String modelId = setModelDetail(str, allText, param);
                    if (StringUtils.isNotBlank(modelId)) {
                        param.setModel(modelId);
                        break;
                    }
                }
            } else if (model.contains("|")) {
                // 模型配置中，如果配置了模型和最大token数量，则自动选择最小的模型，例如：ep-20241022153706-98jv2|4096
                String modelId = setModelDetail(model, allText, param);
                if (StringUtils.isNotBlank(modelId)) {
                    param.setModel(modelId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置模型
     *
     * @param config
     * @param allText
     */
    private String setModelDetail(String config, String allText, DoubaoCompletionParam param) {
        if (config.contains("|")) {
            String[] split1 = config.split("\\|");
            String modelId = split1[0];
            String maxTokenStr = split1[1];
            if (NumberUtil.isInteger(maxTokenStr)) {
                int maxToken = Integer.parseInt(maxTokenStr);
                DoubaoTokenizationParam doubaoTokenizationParam = new DoubaoTokenizationParam();
                doubaoTokenizationParam.setModel(modelId);
                doubaoTokenizationParam.setText(ListUtil.toList(allText));
                HttpRequest post = HttpUtil.createPost("https://ark.cn-beijing.volces.com/api/v3/tokenization");
                post.header("Authorization", "Bearer " + param.getAppKey());
                post.body(JSONUtil.toJsonStr(doubaoTokenizationParam));
                String response = post.execute().body();
                if (StringUtils.isNotBlank(response)) {
                    DoubaoTokenizationResult bean = JSONUtil.parseObj(response).toBean(DoubaoTokenizationResult.class);
                    if (null != bean) {
                        String model = bean.getModel();
                        param.setModelName(model);
                        List<DoubaoTokenizationResult.Data> dataList = bean.getData();
                        if (CollectionUtil.isNotEmpty(dataList)) {
                            DoubaoTokenizationResult.Data data = dataList.get(0);
                            Integer totalTokens = data.getTotal_tokens();
                            if (null != totalTokens) {
                                if (totalTokens < maxToken) {
                                    return modelId;
                                }
                            }
                        }

                    }

                }
            }
        }
        return null;
    }
}
