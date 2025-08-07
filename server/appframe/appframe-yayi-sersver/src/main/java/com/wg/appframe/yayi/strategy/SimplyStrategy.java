package com.wg.appframe.yayi.strategy;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wg.appframe.yayi.param.CommoneCompletionParam;
import com.wg.appframe.yayi.result.CommoneCompletionResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
@Component
public class SimplyStrategy implements Serializable {

    private static final long serialVersionUID = 7568679267573023781L;


    @Value("${appframe.simply.logEnabled:false}")
    private Boolean logEnabled;

    public CommoneCompletionResult run(CommoneCompletionParam param, Consumer<CommoneCompletionResult> consumer) {
        CommoneCompletionResult result = new CommoneCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            // param = getParam(content, param);

            builder.addHeader("Authorization", "Bearer " + param.getAppKey());

            String uri = param.getUri();
            clearField(param);
            // 请求 CommoneCompletionStrategy接口
            log.info("===>SimplyStrategy url:{}  ,  param: {}", uri, JSONUtil.toJsonStr(param));
            try {
                final String contentType = "application/json; charset=utf-8";

                final MediaType mediaType = MediaType.parse(contentType);
                final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param.getLlmParam()));
                Request request = builder
                        .url(uri)
                        .post(body)
                        .build();
                OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);

                // 流式输出
                if (param.getStream()) {
                    if (null == consumer) {
                        return result;
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
                            log.info("===>SimplyStrategy body: {}", responses);
                            if (StringUtils.isNotBlank(responses) && JSONUtil.isTypeJSONObject(responses)) {
                                responses(responses);
                                JSONObject entries = JSONUtil.parseObj(responses);
                                if (entries.containsKey("message")) {
                                    String delta = entries.getStr("message");
                                    JSONObject content = new JSONObject();
                                    content.set("delta", JSONUtil.parseObj(delta));
                                    JSONArray choices = new JSONArray();
                                    choices.add(content);
                                    entries.set("choices", choices);
                                }
                                result.setLlmResult(entries);
                                return result;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                log.error("调用CommoneCompletionStrategy大模型算法失败，url = {}", param.getUri(), e);
                throw new RuntimeException("调用CommoneCompletionStrategy大模型算法失败");
            }
            return result;

        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return result;
    }

    private void responseByStream(BufferedReader bufferedReader, Consumer<CommoneCompletionResult> consumer) throws Exception {
        CommoneCompletionResult result = new CommoneCompletionResult();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (logEnabled) {
                log.info("===>Simply line:{}", line);
            }
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replaceFirst("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        JSONObject entries = JSONUtil.parseObj(line);
                        if (entries.containsKey("message")) {
                            JSONObject delta = entries.getJSONObject("message");
                            JSONObject content = new JSONObject();
                            content.set("delta", delta);
                            JSONArray choices = new JSONArray();
                            choices.add(content);
                            entries.set("choices", choices);
                        }
                        result.setLlmResult(entries);
                        consumer.accept(result);
                    }
                }
            }
        }
    }

    private JSONObject responses(String responses) {
        JSONObject entries = JSONUtil.parseObj(responses);
        if (entries.containsKey("message")) {
            JSONObject delta = entries.getJSONObject("message");
            JSONObject content = new JSONObject();
            content.set("delta", delta);
            JSONArray choices = new JSONArray();
            choices.add(content);
            entries.set("choices", choices);
        }
        return entries;
    }
    
    /**
     * 清除字段
     * @param param
     */
    private void clearField(CommoneCompletionParam param) {
        JSONObject llmParam = param.getLlmParam();
        llmParam.remove("app_key");
        llmParam.remove("appKey");
        llmParam.remove("model_code");
        llmParam.remove("modelCode");
        llmParam.remove("frequencyPenalty");
        llmParam.remove("id");
        llmParam.remove("maxTokens");
        llmParam.remove("model_id");
        llmParam.remove("modelId");
        llmParam.remove("model_name");
        llmParam.remove("modelName");
        llmParam.remove("presencePenalty");
        llmParam.remove("retry_interval");
        llmParam.remove("retryInterval");
        llmParam.remove("retry_times");
        llmParam.remove("retryTimes");
        llmParam.remove("status");
        llmParam.remove("topP");
        llmParam.remove("uri");
        llmParam.remove("manufacturer");
        llmParam.remove("default_config");
        llmParam.remove("defaultConfig");
        llmParam.remove("delete_flag");
        llmParam.remove("deleteFlag");
        llmParam.remove("create_time");
        llmParam.remove("createTime");
        llmParam.remove("create_user");
        llmParam.remove("createUser");
        llmParam.remove("update_time");
        llmParam.remove("updateTime");
        llmParam.remove("update_user");
        llmParam.remove("updateUser");
        llmParam.remove("app_secret");
        llmParam.remove("appSecret");
        llmParam.remove("connect_timeout");
        llmParam.remove("connectTimeout");



    }

}
