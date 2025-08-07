//package com.wg.appframe.yayi.strategy;
//
//import cn.hutool.core.thread.ThreadUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.wg.appframe.yayi.config.DoubaoCompletionsConfig;
//import com.wg.appframe.yayi.config.DoubaoConfig;
//import com.wg.appframe.yayi.entity.DoubaoRequestBody;
//import com.wg.appframe.yayi.entity.YayiMessage;
//import com.wg.appframe.yayi.param.DoubaoCompletionParam;
//import com.wg.appframe.yayi.result.DoubaoCompletionResult;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Serializable;
//import java.util.List;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
//@Slf4j
//public class DoubaoCompletionStrategy implements Serializable {
//
//    private static final long serialVersionUID = 1106889480112342217L;
//
//    @Autowired(required = false)
//    private DoubaoConfig doubaoConfig;
//
//    @Autowired(required = false)
//    private DoubaoCompletionsConfig doubaoCompletionsConfig;
//
//
//    /**
//     * 非流式回答
//     * @param content
//     * @return
//     */
//    public DoubaoCompletionResult run(String content, DoubaoCompletionParam param) {
//        try {
//            DoubaoRequestBody doubaoRequestBody = buildRequestBody(param.getMessages(), Boolean.FALSE);
//            log.info("===>doubaoCompletions doubaoConfig:{}  ,  chatCompletionRequest: {}", JSONUtil.toJsonStr(doubaoConfig), JSONUtil.toJsonStr(doubaoRequestBody));
//            return invoke(doubaoRequestBody, null);
//        } catch (Exception e) {
//            log.error("调用豆包 chat大模型算法失败，url = {}", doubaoConfig.getHost(), e);
//            throw new RuntimeException("调用豆包 chat大模型算法失败");
//        }
//    }
//
//    private DoubaoRequestBody buildRequestBody(List<YayiMessage> messages, Boolean stream) {
//        List<DoubaoRequestBody.MessageParam> list = messages.stream().map(message -> {
//            DoubaoRequestBody.MessageParam param = new DoubaoRequestBody.MessageParam();
//            param.setRole(message.getRole());
//            param.setContent(message.getContent());
//            return param;
//        }).collect(Collectors.toList());
//        return DoubaoRequestBody.builder()
//                .model(doubaoCompletionsConfig.getModel())
//                .messages(list)
//                .temperature(doubaoCompletionsConfig.getTemperature())
//                .top_p(doubaoCompletionsConfig.getTopP())
//                .stream(stream)
//                .max_tokens(doubaoCompletionsConfig.getMaxTokens())
//                .presence_penalty(doubaoCompletionsConfig.getPresencePenalty())
//                .frequency_penalty(doubaoCompletionsConfig.getFrequencyPenalty())
//                .logprobs(doubaoCompletionsConfig.getLogprobs())
//                .top_logprobs(doubaoCompletionsConfig.getTopLogprobs())
////                .repetition_penalty(doubaoCompletionsConfig.getRepetitionPenalty())
////                .n(doubaoCompletionsConfig.getN())
//                .stop(doubaoCompletionsConfig.getStop())
//                .build();
//    }
//
//    /**
//     * 流式回答
//     * @param content
//     * @param param
//     * @param consumer
//     * @return
//     */
//    public void run(String content, DoubaoCompletionParam param, Consumer<DoubaoCompletionStreamResult> consumer) {
//        if (consumer == null) {
//            log.info("===>doubaoCompletions consumer is null");
//            return;
//        }
//        try {
//            DoubaoRequestBody doubaoRequestBody = buildRequestBody(param.getMessages(), Boolean.TRUE);
//            log.info("===>doubaoCompletions doubaoConfig:{}  ,  chatCompletionRequest: {}", JSONUtil.toJsonStr(doubaoConfig), JSONUtil.toJsonStr(doubaoRequestBody));
//            invoke(doubaoRequestBody, consumer);
//        } catch (Exception e) {
//            log.error("调用豆包 chat大模型算法失败，url = {}", doubaoConfig.getHost(), e);
//            throw new RuntimeException("调用豆包 chat大模型算法失败");
//        }
//    }
//
//    private DoubaoCompletionResult invoke(DoubaoRequestBody requestBody, Consumer<DoubaoCompletionStreamResult> consumer) {
//        okhttp3.OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);
//        final Request.Builder builder = new Request.Builder();
//        builder.url(doubaoConfig.getHost())
//                .post(RequestBody.create(MediaType.get("application/json"), JSONUtil.toJsonStr(requestBody)))
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer " + doubaoConfig.getApiKey());
//        int i = 1;
//        do {
//            try (Response response = okHttpClient.newCall(builder.build()).execute()) {
//                ResponseBody body = response.body();
//                if (body == null) {
//                    sleep(i, "豆包返回空数据");
//                    continue;
//                }
//                if (requestBody.getStream()) {
//                    // 流式输出
//                    InputStream it = body.byteStream();
//                    final BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
//                    if (responseByStream(bufferedReader, consumer)){
//                        return new DoubaoCompletionResult();
//                    } else {
//                        sleep(i, "流式输出失败");
//                    }
//                } else {
//                    // 非流式输出
//                    String result = body.string();
//                    log.info("===>doubaoCompletions response: {}", result);
//                    if (StringUtils.isNotBlank(result) && JSONUtil.isTypeJSONObject(result)) {
//                        JSONObject entries = JSONUtil.parseObj(result);
//                        return entries.toBean(DoubaoCompletionResult.class);
//                    } else {
//                        sleep(i, result);
//                    }
//                }
//            } catch (Exception e) {
//                sleep(i, e.getMessage());
//                e.printStackTrace();
//            }
//        } while (i++ < doubaoCompletionsConfig.getRetryTimes());
//        throw new RuntimeException("重试次数已用完, 请检查网络连接是否正常");
//    }
//
//    private void sleep(int i, String result) {
//        ThreadUtil.safeSleep(doubaoCompletionsConfig.getRetryInterval());
//        log.info("===>doubaoCompletions 第{}次重试 ,总重试次数:{} ,重试间隔:{}毫秒, error:{}", i, doubaoCompletionsConfig.getRetryTimes(), doubaoCompletionsConfig.getRetryInterval(), JSONUtil.toJsonStr(result));
//    }
//
//    private static boolean responseByStream(BufferedReader bufferedReader, Consumer<DoubaoCompletionStreamResult> consumer) throws Exception {
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            if (org.apache.commons.lang3.StringUtils.isNotBlank(line)) {
//                if (org.apache.commons.lang3.StringUtils.isNotBlank(line)) {
//                    line = line.replace("data:", "");
//
//                    if (!line.contains("[DONE]") && JSONUtil.isTypeJSON(line)) {
//                        JSONObject entries = JSONUtil.parseObj(line);
//                        DoubaoCompletionStreamResult bean = entries.toBean((Class<? extends DoubaoCompletionStreamResult>) DoubaoCompletionStreamResult.class);
//                        consumer.accept(bean);
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//}
