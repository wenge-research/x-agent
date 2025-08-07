package com.wg.appframe.yayi.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.config.ZhipuChatConfig;
import com.wg.appframe.yayi.config.ZhipuConfig;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ZhipuParam;
import com.wg.appframe.yayi.result.ZhipuResult;
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
public class ZhipuStrategy implements Serializable {
    
    private static final long serialVersionUID = 7130254049258952840L;
    
    @Autowired(required = false)
    private ZhipuConfig zhipuConfig;

    @Autowired(required = false)
    private ZhipuChatConfig deepseekCompletionsConfig;

    public ZhipuResult run(String content, ZhipuParam param, Consumer<ZhipuResult> consumer) {
        ZhipuResult result = new ZhipuResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();

            // 封装接口入参
            param = getParam(content, param);

            builder.addHeader("Authorization", "Bearer " + zhipuConfig.getAppKey());

            // 请求 智普接口
            log.info("===>zhipu chat url:{}  ,  param: {}", deepseekCompletionsConfig.getUri(), JSONUtil.toJsonStr(param));

            try {
                final String contentType = "application/json; charset=utf-8";

                final MediaType mediaType = MediaType.parse(contentType);
                final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param));
                Request request = builder
                        .url(deepseekCompletionsConfig.getUri())
                        .post(body)
                        .build();
                OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);

                // 流式输出
                if (param.getStream()) {
                    if (null == consumer) {
                        return new ZhipuResult();
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
                            log.info("===>zhipu chat body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                JSONObject entries = JSONUtil.parseObj(responses);
                                return entries.toBean(ZhipuResult.class);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用zhipu chat大模型算法失败，url = {}", deepseekCompletionsConfig.getUri(), e);
                throw new RuntimeException("调用zhipu chat大模型算法失败");
            }
            return new ZhipuResult();

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<ZhipuResult> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (null != zhipuConfig.getLogEnabled() && zhipuConfig.getLogEnabled()) {
                log.info("===>siliconflow line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        ZhipuResult bean = entries.toBean((Class<? extends ZhipuResult>) ZhipuResult.class);
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
    private ZhipuParam getParam(String content, ZhipuParam param) {
        ZhipuParam paramTemp = new ZhipuParam();
        paramTemp.setRequestId(IdUtil.simpleUUID());

        List<YayiMessage> messagesList = ListUtil.toList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("user");
        messages.setContent(content);
        messagesList.add(messages);
        paramTemp.setMessages(messagesList);

        if (StringUtils.isNotBlank(deepseekCompletionsConfig.getModel())) {
            paramTemp.setModel(deepseekCompletionsConfig.getModel());
        }
        if (null != deepseekCompletionsConfig.getDoSample()) {
            paramTemp.setDo_sample(deepseekCompletionsConfig.getDoSample());
        }
        if (null != deepseekCompletionsConfig.getTemperature()) {
            paramTemp.setTemperature(deepseekCompletionsConfig.getTemperature());
        }

        if (null != deepseekCompletionsConfig.getTopP()) {
            paramTemp.setTop_p(deepseekCompletionsConfig.getTopP());
        }

        if (null != deepseekCompletionsConfig.getMaxTokens()) {
            paramTemp.setMax_tokens(deepseekCompletionsConfig.getMaxTokens());
        }

        if (StringUtils.isNotBlank(deepseekCompletionsConfig.getStop())) {
            paramTemp.setStop(deepseekCompletionsConfig.getStop());
        }
        if (null != deepseekCompletionsConfig.getToolChoice()) {
            paramTemp.setTool_choice(deepseekCompletionsConfig.getToolChoice());
        }

        if (StringUtils.isBlank(paramTemp.getUri())) {
            paramTemp.setUri(deepseekCompletionsConfig.getUri());
        }
        if (StringUtils.isBlank(param.getAppKey())) {
            paramTemp.setAppKey(zhipuConfig.getAppKey());
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
