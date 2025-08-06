package com.wenge.model.strategy.llmStrategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;
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
import com.wg.appframe.yayi.api.KimiServer;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.KimiCompletionParam;
import com.wg.appframe.yayi.result.KimiCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service(LlmConstant.KIMI)
@Slf4j
public class KimiStrategy implements LlmStrategy{

    @Autowired
    private KimiServer kimiServer;

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        return runGenerate(question, messagesList, step, null);
    }

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        return runGenerate(question, messagesList, step, answerConsumer);
    }

    /**
     *  生成答案
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

        KimiCompletionParam kimiCompletionParam = modelParam.toBean(KimiCompletionParam.class);
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

        if (CollectionUtils.isEmpty(kimiCompletionParam.getMessages())) {
            kimiCompletionParam.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        String result = "";
        step.setPrompt(kimiCompletionParam);

        // 从step中获取llmJson属性，如果llmJson设置1则需要返回json格式
        if (null != param) {
            Object llmJsonFlag = param.getOrDefault(AnswerStrategyContant.LLM_JSON_FLAG, StringConstant.BLANK);
            if (StringConstant.ONE.equals(llmJsonFlag)) {
                KimiCompletionParam.ResponseFormat responseFormat = new KimiCompletionParam.ResponseFormat();
                responseFormat.setType("json_object");
                kimiCompletionParam.setResponse_format(responseFormat);
            }
        }

        // 是否有消费者，消费大模型推流消息
        if (answerConsumer != null) {
            kimiCompletionParam.setStream(true);
            result = runStream(question, kimiCompletionParam, answerConsumer, step);
        } else {
            kimiCompletionParam.setStream(false);
            result = kimiServer.generateString(question, kimiCompletionParam);
            List<YayiMessage> messages1 = kimiCompletionParam.getMessages();
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
     * @param kimiCompletionParam
     * @param answerConsumer
     */
    private String runStream(String question, KimiCompletionParam kimiCompletionParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        // 大模型推流
        kimiServer.generateStream(question, kimiCompletionParam, resultData -> {
            //System.out.println("=>>>>" + JSON.toJSONString(resultData));
            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            List<KimiCompletionResult.GenerateChoice> choices = resultData.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            KimiCompletionResult.GenerateChoice generateChoice = resultData.getChoices().get(0);
            KimiCompletionResult.GenerateMessage delta = generateChoice.getDelta();
            if (null == delta) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            String finishReason = generateChoice.getFinish_reason();
            String content = delta.getContent();
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

    /**
     *  生成答案
     * @param question
     * @param messagesList
     * @param step
     * @return
     */
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
            modelParam.set(StrUtil.toUnderlineCase(k), v);
            modelParam.set(k, v);
        });

        KimiCompletionParam kimiCompletionParam = modelParam.toBean(KimiCompletionParam.class);
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

        if (CollectionUtils.isEmpty(kimiCompletionParam.getMessages())) {
            kimiCompletionParam.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        step.setPrompt(kimiCompletionParam);

        // 从step中获取llmParamTool，设置插件
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_TOOL)) {
            List<LlmTool> toolList = param.getBeanList(AnswerStrategyContant.LLM_PARAM_TOOL, LlmTool.class);
            if (CollectionUtils.isNotEmpty(toolList)) {
                List<KimiCompletionParam.Tool> tools = toolList.stream().map(p -> BeanUtil.toBean(p, KimiCompletionParam.Tool.class)).collect(Collectors.toList());
                kimiCompletionParam.setTools(tools);
            }
        }

        // 从step中获取llmJson属性，如果llmJson设置1则需要返回json格式
        if (null != param) {
            Object llmJsonFlag = param.getOrDefault(AnswerStrategyContant.LLM_JSON_FLAG, StringConstant.BLANK);
            if (StringConstant.ONE.equals(llmJsonFlag)) {
                KimiCompletionParam.ResponseFormat responseFormat = new KimiCompletionParam.ResponseFormat();
                responseFormat.setType("json_object");
                kimiCompletionParam.setResponse_format(responseFormat);
            }
        }

        kimiCompletionParam.setStream(false);
        KimiCompletionResult kimiCompletionResult = kimiServer.generate(question, kimiCompletionParam);
        List<KimiCompletionResult.GenerateChoice> choices = kimiCompletionResult.getChoices();
        KimiCompletionResult.GenerateChoice generateChoice = choices.get(0);
        if ("tool_calls".equals(generateChoice.getFinish_reason())) {
            KimiCompletionResult.GenerateMessage message = generateChoice.getMessage();
            step.setResult(message.getTool_calls());
            return message.getTool_calls();
        }
        return Lists.newArrayList();
    }
}
