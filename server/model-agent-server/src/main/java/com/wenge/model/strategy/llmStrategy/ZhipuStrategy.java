package com.wenge.model.strategy.llmStrategy;

import cn.hutool.core.util.StrUtil;
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
import com.wg.appframe.yayi.api.ZhipuServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ZhipuParam;
import com.wg.appframe.yayi.result.ZhipuResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service(LlmConstant.ZHIPU)
@Slf4j
public class ZhipuStrategy implements LlmStrategy{

    @Autowired
    private ZhipuServer zhipuServer;

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        return runGenerate(question, messagesList, step, null);
    }

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        return runGenerate(question, messagesList, step, answerConsumer);
    }

    /**
     * 生成答案
     * @param question
     * @param messagesList
     * @param step
     * @param answerConsumer
     * @return
     */
    private String runGenerate(String question, List<YayiMessage> messagesList, StepEntity step, Consumer<AnswerStepData> answerConsumer) {
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

        ZhipuParam zhipuParam = modelParam.toBean(ZhipuParam.class);

        //ZhipuParam zhipuParam = new ZhipuParam();
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

        if (CollectionUtils.isEmpty(zhipuParam.getMessages())) {
            zhipuParam.setMessages(messagesList);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        //step.setPrompt(generate30BParam);
        String result = "";
        step.setPrompt(zhipuParam);
        //zhipuParam.setMax_tokens(question.length() + 512);

        // 是否有消费者，消费大模型推流消息
        if (answerConsumer != null) {
            zhipuParam.setStream(true);
            result = runStream(question, zhipuParam, answerConsumer, step);
        } else {
            zhipuParam.setStream(false);
            result = zhipuServer.generateString(question, zhipuParam);
            List<YayiMessage> messages1 = zhipuParam.getMessages();
            YayiMessage messages2 = messages1.get(messages1.size() - 1);
            messages2.setContent(question);
            if (StringUtils.isBlank(result)) {
                result = AnswerStrategyContant.NO_ANSWER_TEXT;
            }
            step.setResult(result);
        }

        return result;
    }


    /**
     * 推流
     * @param question
     * @param zhipuParam
     * @param answerConsumer
     */
    private String runStream(String question, ZhipuParam zhipuParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        // 大模型推流
        zhipuServer.generateStream(question, zhipuParam, resultData -> {
            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            List<ZhipuResult.GenerateChoice> choices = resultData.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            ZhipuResult.GenerateChoice generateChoice = resultData.getChoices().get(0);
            ZhipuResult.GenerateMessage message = generateChoice.getDelta();
            if (null == message) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            String finishReason = generateChoice.getFinish_reason();
            String content = message.getContent();
            // 推流结束
            if ("stop".equals(finishReason)) {
                stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
            } else {
                stepData.setStatus(StepStatusEnum.ANSWER_PROCESS);
            }
            if (StringUtils.isNotBlank(content)) {
                stream.append(content);
            }
            stepData.setAnswer(stream.toString());
            if (!"\\".equals(content)) {
                answerConsumer.accept(stepData);
            }
            step.setResult(stream.toString());
        });
        return stream.toString();
    }


}
