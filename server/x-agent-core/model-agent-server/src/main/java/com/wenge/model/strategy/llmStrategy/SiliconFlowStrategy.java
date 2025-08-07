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
import com.wg.appframe.yayi.api.SiliconFlowServer;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DeepseekCompletionParam;
import com.wg.appframe.yayi.param.SiliconFlowCompletionParam;
import com.wg.appframe.yayi.result.SiliconFlowCompletionResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service(LlmConstant.SILICON_FLOW)
@Slf4j
public class SiliconFlowStrategy implements LlmStrategy{

    @Autowired
    private SiliconFlowServer siliconFlowServer;

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        return runGenerate(question, messagesList, step, null);
    }

    @Override
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        return runGenerate(question, messagesList, step, answerConsumer);
    }

    /**
     * 请求大模型
     *
     * @param question
     * @param messagesList
     * @param step
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
            if ("responseFormat".equals(k) && null != v) {
                DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
                responseFormat.setType(v.toString());
                modelParam.put(k, responseFormat);
                modelParam.put("response_format", responseFormat);
            }
            if ("stop".equals(k) && null != v) {
                String[] stopArray = v.toString().split(",");
                modelParam.set("stop", stopArray);
            }
        });

        SiliconFlowCompletionParam siliconFlowCompletionParam = modelParam.toBean(SiliconFlowCompletionParam.class);
        //SiliconFlowCompletionParam siliconFlowCompletionParam = new SiliconFlowCompletionParam();
        if (CollectionUtils.isEmpty(messagesList)) {
            messagesList = Lists.newArrayList();
        }
        List<YayiMessage> messagess = messagesList.stream().map(p -> {
            YayiMessage messages = new YayiMessage();
            messages.setRole(p.getRole());
            if (!USER_PROMPT_FIELD.equals(p.getRole()) && !SYSTEM_PROMPT_FIELD.equals(p.getRole()) && !"tool".equals(p.getRole())) {
                messages.setRole(ASSISTANT_ROLE);
            }
            messages.setContent(p.getContent());
            messages.setTool_calls(p.getTool_calls());
            return messages;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(siliconFlowCompletionParam.getMessages())) {
            siliconFlowCompletionParam.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole(USER_PROMPT_FIELD);
        messagess.add(messages);
        String result = StringConstant.BLANK;
        step.setPrompt(siliconFlowCompletionParam);

        // // 从step中获取llmJson属性，如果llmJson设置1则需要返回json格式
        // if (null != param && param.containsKey(AnswerStrategyContant.LLM_JSON_FLAG)) {
        //     Object llmJsonFlag = param.getOrDefault(AnswerStrategyContant.LLM_JSON_FLAG, StringConstant.BLANK);
        //     if (StringConstant.ONE.equals(llmJsonFlag)) {
        //         SiliconFlowCompletionParam.ResponseFormat responseFormat = new SiliconFlowCompletionParam.ResponseFormat();
        //         responseFormat.setType("json_object");
        //         siliconFlowCompletionParam.setResponse_format(responseFormat);
        //     }
        // }

        // 从step中获取llmParamTool，设置插件
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_TOOL)) {
            List<LlmTool> toolList = param.getBeanList(AnswerStrategyContant.LLM_PARAM_TOOL, LlmTool.class);
            if (CollectionUtils.isNotEmpty(toolList)) {
                DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
                responseFormat.setType("text");
                siliconFlowCompletionParam.setResponse_format(responseFormat);
                List<SiliconFlowCompletionParam.Tool> tools = toolList.stream().map(p -> BeanUtil.toBean(p, SiliconFlowCompletionParam.Tool.class)).collect(Collectors.toList());
                siliconFlowCompletionParam.setTools(tools);
            }
        }

        // 是否有消费者，消费大模型推流消息
        if (answerConsumer != null) {
            siliconFlowCompletionParam.setStream(true);
            result = runStream(question, siliconFlowCompletionParam, answerConsumer, step);
            System.out.println("大模型[推流消息]输出消耗token：" + step.getStep() + " 消耗token数：" + step.getTokenTotal());
        } else {
            siliconFlowCompletionParam.setStream(false);
            SiliconFlowCompletionResult siliconFlowCompletionResult = siliconFlowServer.generate(question, siliconFlowCompletionParam);
            result =  ((SiliconFlowCompletionResult.GenerateChoice)siliconFlowCompletionResult.getChoices().get(0)).getMessage().getContent();
            List<YayiMessage> messages1 = siliconFlowCompletionParam.getMessages();
            YayiMessage messages2 = messages1.get(messages1.size() - 1);
            messages2.setContent(question);
            if (StringUtils.isBlank(result)) {
                result = AnswerStrategyContant.NO_ANSWER_TEXT;
            }
            step.setResult(result);
            //使用的token
            try {
                step.setTokenTotal(Integer.parseInt(siliconFlowCompletionResult.getUsage().getTotal_tokens()));
            }catch (Exception e){
                log.error("获取不到token消耗量");
                e.printStackTrace();
            }
            System.out.println("大模型[直接]输出消耗token：" + step.getStep() + " 消耗token数：" + step.getTokenTotal());
        }
        return result;
    }

    /**
     * 推流
     *
     * @param question
     * @param siliconFlowCompletionParam
     * @param answerConsumer
     */
    private String runStream(String question, SiliconFlowCompletionParam siliconFlowCompletionParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        StringBuilder stream = new StringBuilder();
        StringBuilder contentStream = new StringBuilder();
        StringBuilder reasoningContentStream = new StringBuilder();
        step.setResult(StringConstant.BLANK);
        // 大模型推流
        siliconFlowServer.generateStream(question, siliconFlowCompletionParam, resultData -> {
            AnswerStepData stepData = new AnswerStepData();
            if (null == resultData) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            // 推流错误
            if (null != resultData.getError()) {
                String message = resultData.getError().getMessage();
                stepData.setErrorMessage(message);
                stream.append(message);
                return;
            }

            List<SiliconFlowCompletionResult.GenerateChoice> choices = resultData.getChoices();
            if (CollectionUtils.isEmpty(choices)) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            SiliconFlowCompletionResult.GenerateChoice generateChoice = choices.get(0);
            SiliconFlowCompletionResult.GenerateMessage delta = generateChoice.getDelta();
            if (null == delta) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }

            String finishReason = generateChoice.getFinish_reason();
            String content = delta.getContent();
            String reasoningContent = delta.getReasoning_content();

            // 推流结束
            if ("stop".equals(finishReason)) {
                stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
            } else {
                stepData.setStatus(StepStatusEnum.ANSWER_PROCESS);
            }

            // List<YayiMessage.ToolCall> toolCalls = resultData.getTool_calls();
            // if (CollectionUtils.isNotEmpty(toolCalls)) {
            //     YayiMessage.ToolCall toolCall = toolCalls.get(0);
            //     if (null != toolCall) {
            //         YayiMessage.Function function = toolCall.getFunction();
            //         if (null != function) {
            //             content = function.getArguments().toString();
            //         }
            //     }
            // } else {
            //     // 先获取思考过程
            //     reasoningContent = delta.getReasoning_content();
            //     content1 = delta.getContent();
            //     content = "";
            // }
            // String dealReasoningContent = dealReasoningContent(stream.toString());

            // 处理大模型推流内容
            DeepseekStrategy.dealContent(reasoningContent, content, contentStream, reasoningContentStream, stream);
            stepData.setAnswer(stream.toString());
            stepData.setReasoningContent(reasoningContentStream.toString());
            if (!"\\".equals(reasoningContent) && !"\\".equals(content)) {
                answerConsumer.accept(stepData);
            }
            step.setResult(stream.toString());
        });
        return step.getResult().toString();
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
            modelParam.set(StrUtil.toUnderlineCase(k), v);
            modelParam.set(k, v);
        });

        SiliconFlowCompletionParam siliconFlowCompletionParam = modelParam.toBean(SiliconFlowCompletionParam.class);
        //SiliconFlowCompletionParam siliconFlowCompletionParam = new SiliconFlowCompletionParam();
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

        if (CollectionUtils.isEmpty(siliconFlowCompletionParam.getMessages())) {
            siliconFlowCompletionParam.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        step.setPrompt(siliconFlowCompletionParam);
        siliconFlowCompletionParam.setMax_tokens(1024);

        // 从step中获取llmParamTool，设置插件
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_TOOL)) {
            List<LlmTool> toolList = param.getBeanList(AnswerStrategyContant.LLM_PARAM_TOOL, LlmTool.class);
            if (CollectionUtils.isNotEmpty(toolList)) {
                List<SiliconFlowCompletionParam.Tool> tools = toolList.stream().map(p -> BeanUtil.toBean(p, SiliconFlowCompletionParam.Tool.class)).collect(Collectors.toList());
                siliconFlowCompletionParam.setTools(tools);
            }
        }

        // 从step中获取llmJson属性，如果llmJson设置1则需要返回json格式
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_JSON_FLAG)) {
            Object llmJsonFlag = param.getOrDefault(AnswerStrategyContant.LLM_JSON_FLAG, StringConstant.BLANK);
            if (StringConstant.ONE.equals(llmJsonFlag)) {
                DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
                responseFormat.setType("json_object");
                siliconFlowCompletionParam.setResponse_format(responseFormat);
            }
        }

        // 是否有消费者，消费大模型推流消息
        siliconFlowCompletionParam.setStream(false);
        SiliconFlowCompletionResult generate = siliconFlowServer.generate(question, siliconFlowCompletionParam);
        List<SiliconFlowCompletionResult.GenerateChoice> choices = generate.getChoices();
        SiliconFlowCompletionResult.GenerateChoice generateChoice = choices.get(0);
        String finishReason = generateChoice.getFinish_reason();
        if ("tool_calls".equals(finishReason)) {
            step.setResult(generate.getTool_calls());
            return generate.getTool_calls();
        }
        return Lists.newArrayList();
    }
}
