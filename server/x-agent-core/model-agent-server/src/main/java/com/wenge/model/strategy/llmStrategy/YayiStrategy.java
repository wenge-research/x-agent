package com.wenge.model.strategy.llmStrategy;

import cn.hutool.core.bean.BeanUtil;
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
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.Generate128KParam;
import com.wg.appframe.yayi.param.Generate30BParam;
import com.wg.appframe.yayi.result.Generate128KResult;
import com.wg.appframe.yayi.result.Generate30BResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service(LlmConstant.YAYI)
@Slf4j
public class YayiStrategy implements LlmStrategy{

    @Autowired
    private YayiServer yayiServer;

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        return runGenerate(question, messagesList, step, longFlag, null);
    }

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        return runGenerate(question, messagesList, step, longFlag, answerConsumer);
    }

    /**
     * 生成答案
     *
     * @param question
     * @param messagesList
     * @param step
     * @param answerConsumer
     * @return
     */
    private String runGenerate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
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

        Generate30BParam generate30BParam = modelParam.toBean(Generate30BParam.class);

        if (CollectionUtils.isEmpty(messagesList)) {
            messagesList = Lists.newArrayList();
        }

        if (CollectionUtils.isEmpty(generate30BParam.getMessages())) {
            generate30BParam.setMessages(messagesList);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagesList.add(messages);
        String result = "";

        if (longFlag) {
            Generate128KParam generate128KParam = BeanUtil.copyProperties(generate30BParam, Generate128KParam.class);
            //question = getHtml(question);
            messages.setContent(question);
            List<YayiMessage> messages1 = BeanUtil.copyToList(messagesList, YayiMessage.class);
            generate128KParam.setMessages(messages1);
            generate128KParam.setMax_new_tokens(question.length() + 512);
            step.setPrompt(generate128KParam);

            if (answerConsumer != null) {
                generate128KParam.setStream(true);
                result = run128kStream(question, generate128KParam, answerConsumer, step);
            } else {
                generate128KParam.setStream(false);
                result = yayiServer.generate128KStr(question, generate128KParam);
                messages1 = generate30BParam.getMessages();
                YayiMessage messages2 = messages1.get(messages1.size() - 1);
                messages2.setContent(question);
                if (StringUtils.isBlank(result)) {
                    result = AnswerStrategyContant.NO_ANSWER_TEXT;
                }
                step.setResult(result);
            }
        } else {
            step.setPrompt(generate30BParam);
            generate30BParam.setMax_new_tokens(question.length() + 512);
            if (answerConsumer != null) {
                generate30BParam.setStream(true);
                result = run30BStream(question, generate30BParam, answerConsumer, step);
            } else {
                generate30BParam.setStream(false);
                result = yayiServer.generate30BStr(question, generate30BParam);
                List<YayiMessage> messages1 = generate30BParam.getMessages();
                YayiMessage messages2 = messages1.get(messages1.size() - 1);
                messages2.setContent(question);
                if (StringUtils.isBlank(result)) {
                    result = AnswerStrategyContant.NO_ANSWER_TEXT;
                }
                step.setResult(result);
            }
        }

        return result;
    }

    /**
     * 推流
     *
     * @param question
     * @param generate128KParam
     * @param answerConsumer
     */
    private String run128kStream(String question, Generate128KParam generate128KParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        // 大模型推流
        yayiServer.generate128KStream(question, generate128KParam, resultData -> {

            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            Generate128KResult.GenerateData data = resultData.getData();
            if (null == data) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            List<Generate128KResult.GenerateChoice> choices = data.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            Generate128KResult.GenerateChoice generateChoice = choices.get(0);
            Generate128KResult.GenerateMessage message = generateChoice.getMessage();
            if (null == message) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            String content = message.getContent();
            // 推流结束
            if ("finish".equals(message.getType())) {
                stream.append(content);
                stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
            } else {
                stepData.setStatus(StepStatusEnum.ANSWER_PROCESS);
            }
            stepData.setAnswer(content);
            if (!content.endsWith("\\")) {
                answerConsumer.accept(stepData);
            }
            step.setResult(content);
        });
        return stream.toString();
    }


    /**
     * 推流
     * @param question
     * @param generate30BParam
     * @param answerConsumer
     */
    private String run30BStream(String question, Generate30BParam generate30BParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        // 大模型推流
        yayiServer.generate30BStream(question, generate30BParam, resultData -> {
            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            Generate30BResult.GenerateData data = resultData.getData();
            if (null == data) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            List<Generate30BResult.GenerateChoice> choices = data.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            Generate30BResult.GenerateChoice generateChoice = choices.get(0);
            Generate30BResult.GenerateMessage message = generateChoice.getMessage();
            if (null == message) {
                stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            String content = message.getContent();
            // 推流结束
            if ("finish".equals(message.getType())) {
                stream.append(content);
                stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
            } else {
                stepData.setStatus(StepStatusEnum.ANSWER_PROCESS);
            }
            //if (StringUtils.isNotBlank(content)) {
            //    stream.append(content);
            //}
            stepData.setAnswer(content);
            answerConsumer.accept(stepData);
            step.setResult(content);
        });
        return stream.toString();
    }

}
