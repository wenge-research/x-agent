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
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.api.GeneralServer;
import com.wg.appframe.yayi.config.GeneralConfig;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.result.DoubaoCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service(LlmConstant.OTHER)
public class GeneralStrategy implements LlmStrategy {

    @Autowired
    private GeneralServer generalServer;


    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        return generate(question, messagesList, step, false, null);
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

        GeneralConfig generalConfig = modelParam.toBean(GeneralConfig.class);
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

        if (CollectionUtils.isEmpty(generalConfig.getMessages())) {
            generalConfig.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        String result = "";
        step.setPrompt(generalConfig);

        // 是否有消费者，消费大模型推流消息
        if (answerConsumer != null) {
            generalConfig.setStream(true);
            result = runStream(question, generalConfig, answerConsumer, step);
        } else {
            generalConfig.setStream(false);
            result = generalServer.generateString(question, generalConfig);
            List<YayiMessage> messages1 = generalConfig.getMessages();
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
     * @param generalConfig
     * @param answerConsumer
     */
    private String runStream(String question, GeneralConfig generalConfig, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        // 大模型推流
        generalServer.generateStream(question, generalConfig, resultData -> {
            //System.out.println("=>>>>" + JSON.toJSONString(resultData));
            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            List<DoubaoCompletionResult.Choice> choices = resultData.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            DoubaoCompletionResult.Choice generateChoice = resultData.getChoices().get(0);
            DoubaoCompletionResult.ChoiceDelta message = generateChoice.getDelta();
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
            if (null != content) {
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

    @Override
    public List<YayiMessage.ToolCall> generateTools(String question, List<YayiMessage> messagesList, StepEntity step) {
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
                return Lists.newArrayList();
            }
            jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(llmInfo));
        }
        JSONObject modelParam = new JSONObject();
        jsonObject.forEach((k, v) -> {
            modelParam.put(StrUtil.toUnderlineCase(k), v);
            modelParam.put(k, v);
        });

        log.info("doubaoCompletionParam:{}", modelParam);

        GeneralConfig generalConfig = modelParam.toBean(GeneralConfig.class);
        if (CollectionUtils.isEmpty(messagesList)) {
            messagesList = Lists.newArrayList();
        }
        List<YayiMessage> messagess = messagesList.stream().map(p -> {
            YayiMessage messages = new YayiMessage();
            messages.setRole(p.getRole());
            if (!"user".equals(p.getRole()) && !"system".equals(p.getRole()) && !"tool".equals(p.getRole())) {
                messages.setRole("assistant");
            }
            messages.setContent(p.getContent());
            messages.setTool_calls(p.getTool_calls());
            return messages;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(generalConfig.getMessages())) {
            generalConfig.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        step.setPrompt(generalConfig);
        generalConfig.setMaxTokens(1024);

        // 从step中获取llmParamTool，设置插件
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_TOOL)) {
            List<LlmTool> toolList = param.getBeanList(AnswerStrategyContant.LLM_PARAM_TOOL, LlmTool.class);
            if (CollectionUtils.isNotEmpty(toolList)) {
                List<LlmTool> tools = toolList.stream().map(p -> BeanUtil.toBean(p, LlmTool.class)).collect(Collectors.toList());
                generalConfig.setTools(tools);
            }
        }

        // 是否有消费者，消费大模型推流消息
        generalConfig.setStream(false);
        DoubaoCompletionResult generate = generalServer.generate(question, generalConfig);
        List<DoubaoCompletionResult.Choice> choices = generate.getChoices();
        DoubaoCompletionResult.Choice generateChoice = choices.get(0);
        String finishReason = generateChoice.getFinish_reason();
        if ("tool_calls".equals(finishReason)) {
            DoubaoCompletionResult.Message message = generateChoice.getMessage();
            // 转换对象适应策略
            List<DoubaoCompletionResult.MessageToolCall> toolCalls = message.getTool_calls();
            List<YayiMessage.ToolCall> totalCallResult = BeanUtil.copyToList(toolCalls, YayiMessage.ToolCall.class);
            step.setResult(totalCallResult);
            return totalCallResult;
        }
        return Lists.newArrayList();
    }
}
