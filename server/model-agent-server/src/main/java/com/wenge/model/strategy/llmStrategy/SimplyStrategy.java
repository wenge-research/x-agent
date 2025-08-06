package com.wenge.model.strategy.llmStrategy;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.LlmConstant;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.api.CommoneLLServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.CommoneCompletionParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service(LlmConstant.SIMPLY)
@Slf4j
public class SimplyStrategy implements LlmStrategy {

    @Autowired
    private CommoneLLServer commoneLLServer;

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        return generate(question, messagesList, step, longFlag, null);
    }

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        // 从step中获取llmParam，该属性含有所有的大模型的参数
        JSONObject param = step.getParam();
        JSONObject jsonObject = null;
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_OBJECT)) {
            JSONObject llmParamObject = (JSONObject) param.getOrDefault(AnswerStrategyContant.LLM_PARAM_OBJECT, new JSONObject());
            if (!llmParamObject.isEmpty()) {
                jsonObject = llmParamObject;
            }
        }

        // 优先使用step中的llmParam，如果没有则使用llmInfo
        if (null == jsonObject) {
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            LlmInfo llmInfo = applicationInfo.getLlmInfo();
            if (null == llmInfo) {
                log.error("llmInfo is null");
                return StringConstant.BLANK;
            }
            jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(llmInfo));
        }
        JSONObject modelParam = new JSONObject();
        jsonObject.forEach((k, v) -> {
            modelParam.set(StrUtil.toUnderlineCase(k), v);
            modelParam.set(k, v);
        });

        CommoneCompletionParam llmParam = new CommoneCompletionParam();
        llmParam.setLlmParam(modelParam);
        llmParam.setAppKey(modelParam.getStr("app_key"));
        llmParam.setUri(modelParam.getStr("uri"));

        if (CollectionUtils.isEmpty(messagesList)) {
            messagesList = Lists.newArrayList();
        }
        List<YayiMessage> messagess = messagesList.stream().map(p -> {
            YayiMessage messages = new YayiMessage();
            messages.setRole(p.getRole());
            if (!"user".equals(p.getRole()) && !"system".equals(p.getRole())) {
                messages.setRole("assistant");
            }
            messages.setContent(p.getContent());
            return messages;
        }).collect(Collectors.toList());

        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        String result = "";
        step.setPrompt(modelParam);
        messagess = messagess.stream().filter(p -> StringUtils.isNotBlank(p.getContent())).collect(Collectors.toList());
        modelParam.set("messages", messagess);
        // 是否有消费者，消费大模型推流消息
        if (answerConsumer != null) {
            llmParam.setStream(true);
            result = runStream(llmParam, answerConsumer, step);
        } else {
            // commoneCompletionParam.setStream(false);
            // result = commoneLLServer.generateString(question, commoneCompletionParam);
            // List<YayiMessage> messages1 = commoneCompletionParam.getMessages();
            // YayiMessage messages2 = messages1.get(messages1.size() - 1);
            // messages2.setContent(question);
            // if (StringUtils.isBlank(result)) {
            //     result = AnswerStrategyContant.NO_ANSWER_TEXT;
            // }
            // step.setResult(result);
        }
        return result;
    }

    /**
     * 推流
     * @param commoneCompletionParam
     * @param answerConsumer
     */
    private String runStream(CommoneCompletionParam commoneCompletionParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        StringBuilder contentStream = new StringBuilder();
        StringBuilder reasoningContentStream = new StringBuilder();
        StringBuilder allStream = new StringBuilder();
        step.setResult(StringConstant.BLANK);
        commoneCompletionParam.setStrategy(LlmConstant.SIMPLY);
        // 大模型推流
        commoneLLServer.generate(commoneCompletionParam, resultData -> {
            //System.out.println("=>>>>" + JSON.toJSONString(resultData));
            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            JSONObject llmResult = resultData.getLlmResult();
            if (null == llmResult) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }

            tongyiqianwen(llmResult, answerConsumer, stepData, stream, contentStream, reasoningContentStream, allStream, step);
        });
        return step.getResult().toString();
    }

    private void tongyiqianwen(JSONObject llmResult, Consumer<AnswerStepData> answerConsumer, AnswerStepData stepData, StringBuilder stream, StringBuilder contentStream, StringBuilder reasoningContentStream, StringBuilder allStream, StepEntity step) {
        if (llmResult.containsKey("error")) {
            step.setResult(llmResult.getJSONObject("error"));
            log.info("LLM节点执行结果:{}", JSONUtil.toJsonStr(llmResult));
            AnswerUtils.notify(stepData, answerConsumer);
            return;
        }
        // 这里主要看各大模型返回的字段，这里是兼容通义千问
        if (!llmResult.containsKey("choices")) {
            AnswerUtils.notify(stepData, answerConsumer);
            return;
        }

        JSONArray choices = llmResult.getJSONArray("choices");
        if (CollectionUtils.isEmpty(choices)) {
            AnswerUtils.notify(stepData, answerConsumer);
            return;
        }

        List<JSONObject> choiceList = choices.toList(JSONObject.class);
        JSONObject choice = choiceList.get(0);
        if (!choice.containsKey("delta")) {
            AnswerUtils.notify(stepData, answerConsumer);
            return;
        }

        JSONObject delta = choice.getJSONObject("delta");
        String finishReason = choice.getStr("finish_reason");
        String content = delta.getStr("content");
        String reasoningContent = delta.getStr("reasoning_content");

        if (null != content) {
            allStream.append(content);
        }
        if (null != reasoningContent) {
            allStream.append(reasoningContent);
        }

        // 推流结束
        if ("stop".equals(finishReason)) {
            stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
            // 使用的token
            try {
                if (llmResult.containsKey("usage")) {
                    JSONObject usage = llmResult.getJSONObject("usage");
                    if (null != usage && usage.containsKey("usage")) {
                        step.setTokenTotal(usage.getInt("usage"));
                    }
                }
            } catch (Exception e) {
                log.error("获取不到token消耗量");
                e.printStackTrace();
            }
        } else {
            stepData.setStatus(StepStatusEnum.ANSWER_PROCESS);
        }
        DeepseekStrategy.dealContent(reasoningContent, content, contentStream, reasoningContentStream, stream);
        stepData.setAnswer(stream.toString());
        stepData.setReasoningContent(reasoningContentStream.toString());
        if (!"\\".equals(reasoningContent) && !"\\".equals(content)) {
            answerConsumer.accept(stepData);
        }
        step.setResult(allStream.toString());
    }
}
