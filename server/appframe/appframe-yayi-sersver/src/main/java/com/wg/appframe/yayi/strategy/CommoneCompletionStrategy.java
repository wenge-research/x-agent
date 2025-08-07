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
public class CommoneCompletionStrategy implements Serializable {

    private static final long serialVersionUID = 232579929530716468L;

    @Value("${appframe.commonCompletions.logEnabled:false}")
    private Boolean logEnabled;

    public CommoneCompletionResult run(CommoneCompletionParam param, Consumer<CommoneCompletionResult> consumer) {
        CommoneCompletionResult result = new CommoneCompletionResult();
        try {
            // 创建POST请求
            final Request.Builder builder = new Request.Builder();
            // 封装接口入参
            // param = getParam(content, param);

            builder.addHeader("Authorization", "Bearer " + param.getAppKey());

            // 请求 CommoneCompletionStrategy接口
            log.info("===>CommoneCompletionStrategy url:{}  ,  param: {}", param.getUri(), JSONUtil.toJsonStr(param));

            try {
                final String contentType = "application/json; charset=utf-8";

                final MediaType mediaType = MediaType.parse(contentType);
                final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param.getLlmParam()));
                Request request = builder
                        .url(param.getUri())
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
                            // log.info("===>CommoneCompletionStrategy body: {}", responses);
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
                log.info("===>CommoneCompletionStrategy line:{}", line);
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

}
