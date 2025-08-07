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
import com.wg.appframe.yayi.api.DeepseekServer;
import com.wg.appframe.yayi.entity.LlmTool;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.DeepseekCompletionParam;
import com.wg.appframe.yayi.result.DeepseekCompletionResult;
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

@Service(LlmConstant.DEEPSEEK)
@Slf4j
public class DeepseekStrategy implements LlmStrategy{

    @Autowired
    private DeepseekServer deepseekServer;

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
        });

        DeepseekCompletionParam deepseekCompletionParam = modelParam.toBean(DeepseekCompletionParam.class);
        //DeepseekCompletionParam deepseekCompletionParam = new DeepseekCompletionParam();
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

        if (CollectionUtils.isEmpty(deepseekCompletionParam.getMessages())) {
            deepseekCompletionParam.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole(USER_PROMPT_FIELD);
        messagess.add(messages);
        String result = StringConstant.BLANK;
        step.setPrompt(deepseekCompletionParam);

        // // 从step中获取llmJson属性，如果llmJson设置1则需要返回json格式
        // if (null != param && param.containsKey(AnswerStrategyContant.LLM_JSON_FLAG)) {
        //     Object llmJsonFlag = param.getOrDefault(AnswerStrategyContant.LLM_JSON_FLAG, StringConstant.BLANK);
        //     if (StringConstant.ONE.equals(llmJsonFlag)) {
        //         DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
        //         responseFormat.setType("json_object");
        //         deepseekCompletionParam.setResponse_format(responseFormat);
        //     }
        // }

        // 从step中获取llmParamTool，设置插件
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_TOOL)) {
            List<LlmTool> toolList = param.getBeanList(AnswerStrategyContant.LLM_PARAM_TOOL, LlmTool.class);
            if (CollectionUtils.isNotEmpty(toolList)) {
                DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
                responseFormat.setType("text");
                deepseekCompletionParam.setResponse_format(responseFormat);
                List<DeepseekCompletionParam.Tool> tools = toolList.stream().map(p -> BeanUtil.toBean(p, DeepseekCompletionParam.Tool.class)).collect(Collectors.toList());
                deepseekCompletionParam.setTools(tools);
            }
        }

        // 是否有消费者，消费大模型推流消息
        if (answerConsumer != null) {
            deepseekCompletionParam.setStream(true);
            result = runStream(question, deepseekCompletionParam, answerConsumer, step);
            System.out.println("大模型[推流消息]输出消耗token：" + step.getStep() + " 消耗token数：" + step.getTokenTotal());
        } else {
            deepseekCompletionParam.setStream(false);
            DeepseekCompletionResult deepseekCompletionResult = deepseekServer.generate(question, deepseekCompletionParam);
            result =  deepseekCompletionResult.getChoices().get(0).getMessage().getContent();
            // 处理 think 标签
            result = dealThink(result);
            List<YayiMessage> messages1 = deepseekCompletionParam.getMessages();
            YayiMessage messages2 = messages1.get(messages1.size() - 1);
            messages2.setContent(question);
            if (StringUtils.isBlank(result)) {
                result = AnswerStrategyContant.NO_ANSWER_TEXT;
            }
            step.setResult(result);
            //使用的token
            try {
                step.setTokenTotal(Integer.parseInt(deepseekCompletionResult.getUsage().getTotal_tokens()));
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
     * @param deepseekCompletionParam
     * @param answerConsumer
     */
    private String runStream(String question, DeepseekCompletionParam deepseekCompletionParam, Consumer<AnswerStepData> answerConsumer, StepEntity step) {
        // 大模型正文内容
        StringBuilder stream = new StringBuilder();
        // StringBuilder contentStream = new StringBuilder();
        // 大模型推理的内容
        StringBuilder reasoningContentStream = new StringBuilder();
        // 大模型全部输出的内容
        StringBuilder allStream = new StringBuilder();
        step.setResult(StringConstant.BLANK);

        // 大模型推流
        deepseekServer.generateStream(question, deepseekCompletionParam, resultData -> {
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

            // 获取大模型返回结果数据
            List<DeepseekCompletionResult.GenerateChoice> choices = resultData.getChoices();
            String doneReason = resultData.getDone_reason();
            if (CollectionUtils.isEmpty(choices)) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            DeepseekCompletionResult.GenerateChoice generateChoice = choices.get(0);
            DeepseekCompletionResult.GenerateMessage delta = generateChoice.getDelta();
            if (null == delta) {
                AnswerUtils.notify(stepData, answerConsumer);
                return;
            }
            // 获取结束的标记
            String finishReason = generateChoice.getFinish_reason();
            String content = null;
            String reasoningContent = null;
            List<YayiMessage.ToolCall> toolCalls = delta.getTool_calls();
            if (CollectionUtils.isNotEmpty(toolCalls)) {
                // 从工具中获取正文
                YayiMessage.ToolCall toolCall = toolCalls.get(0);
                if (null != toolCall) {
                    YayiMessage.Function function = toolCall.getFunction();
                    if (null != function) {
                        content = function.getArguments().toString();
                    }
                }
            } else {
                // 获取大模型的推理内容，大模型是逐字推送的，获取的reasoningContent是当前大模型推送的一个字或则是一个词
                reasoningContent = delta.getReasoning_content();
                // 获取大模型的正文内容，大模型是逐字推送的，获取的content是当前大模型推送的一个字或则是一个词
                content = delta.getContent();
            }

            // 收集大模型所有推送的内容
            if (null != reasoningContent) {
                allStream.append(reasoningContent);
            }

            // 收集大模型所有推送的内容
            if (null != content) {
                allStream.append(content);
            }

            // 处理大模型推流数据
            dealContent(reasoningContent, content, allStream, reasoningContentStream, stream);

            // 推流结束
            if ("stop".equals(finishReason) || "stop".equals(doneReason)) {
                stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                // 使用的token
                try {
                    step.setTokenTotal(Integer.parseInt(resultData.getUsage().getTotal_tokens()));
                } catch (Exception e) {
                    log.error("获取不到token消耗量");
                    e.printStackTrace();
                }
            } else if ("tool_calls".equals(finishReason)) {
                stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                // 使用的token
                try {
                    step.setTokenTotal(Integer.parseInt(resultData.getUsage().getTotal_tokens()));
                } catch (Exception e) {
                    log.error("获取不到token消耗量");
                    e.printStackTrace();
                }
            } else {
                stepData.setStatus(StepStatusEnum.ANSWER_PROCESS);
            }
            stepData.setAnswer(stream.toString());
            stepData.setReasoningContent(reasoningContentStream.toString());
            if (!"\\".equals(reasoningContent) && !"\\".equals(content)) {
                answerConsumer.accept(stepData);
            }
            step.setResult(allStream.toString());
        });
        return allStream.toString();
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

        DeepseekCompletionParam deepseekCompletionParam = modelParam.toBean(DeepseekCompletionParam.class);
        //DeepseekCompletionParam deepseekCompletionParam = new DeepseekCompletionParam();
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

        if (CollectionUtils.isEmpty(deepseekCompletionParam.getMessages())) {
            deepseekCompletionParam.setMessages(messagess);
        }
        YayiMessage messages = new YayiMessage();
        messages.setContent(question);
        messages.setRole("user");
        messagess.add(messages);
        step.setPrompt(deepseekCompletionParam);
        deepseekCompletionParam.setMax_tokens(1024);

        // 从step中获取llmParamTool，设置插件
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_PARAM_TOOL)) {
            List<LlmTool> toolList = param.getBeanList(AnswerStrategyContant.LLM_PARAM_TOOL, LlmTool.class);
            if (CollectionUtils.isNotEmpty(toolList)) {
                List<DeepseekCompletionParam.Tool> tools = toolList.stream().map(p -> BeanUtil.toBean(p, DeepseekCompletionParam.Tool.class)).collect(Collectors.toList());
                deepseekCompletionParam.setTools(tools);
            }
        }

        // 从step中获取llmJson属性，如果llmJson设置1则需要返回json格式
        if (null != param && param.containsKey(AnswerStrategyContant.LLM_JSON_FLAG)) {
            Object llmJsonFlag = param.getOrDefault(AnswerStrategyContant.LLM_JSON_FLAG, StringConstant.BLANK);
            if (StringConstant.ONE.equals(llmJsonFlag)) {
                DeepseekCompletionParam.ResponseFormat responseFormat = new DeepseekCompletionParam.ResponseFormat();
                responseFormat.setType("json_object");
                deepseekCompletionParam.setResponse_format(responseFormat);
            }
        }

        // 是否有消费者，消费大模型推流消息
        deepseekCompletionParam.setStream(false);
        DeepseekCompletionResult generate = deepseekServer.generate(question, deepseekCompletionParam);
        List<DeepseekCompletionResult.GenerateChoice> choices = generate.getChoices();
        DeepseekCompletionResult.GenerateChoice generateChoice = choices.get(0);
        String finishReason = generateChoice.getFinish_reason();
        if ("tool_calls".equals(finishReason)) {
            DeepseekCompletionResult.GenerateMessage message = generateChoice.getMessage();
            step.setResult(message.getTool_calls());
            return message.getTool_calls();
        }
        return Lists.newArrayList();
    }

    /**
     * 处理大模型返回的流式数据
     * @param reasoningContent 推理内容，每次新增的词，大模型一般会返回一个词
     * @param content 正文，每次新增的词，大模型一般会返回一个词
     * @param contentStream 拼接content内容
     * @param reasoningContentStream 推理内容，拼接reasoningContent内容
     * @param stream 最终输出给 answer 的 内容
     */
    public static void dealContent(String reasoningContent, String content, StringBuilder contentStream, StringBuilder reasoningContentStream, StringBuilder stream) {
        // if (null != content) {
        //     contentStream.append(content);
        // }

        if (contentStream.toString().contains("<think>")) {
            // </think>是 推理结束标志
            // ["<think>\n好的，用户问的是“你是谁”，我需要根据知识库里的内容来回答。首先看知识库1，里面明确说“我是智能问答助理，为用户提供贴心的服务，不能和用户闲聊”。所以用户的问题属于询问身份，应该直接引用这个信息。知识库2提到不能透露大模型的信息，但这里的问题不涉及模型，所以不用考虑。接下来按照任务步骤，提取有效信息，分析用户意图是确认身份，然后总结答案。需要添加问候和感谢语，保持友好简洁。最后生成大纲，结构要符合要求。确保不编造信息，只使用知识库内容。回答应该礼貌，符合政务客服的风格。\n","\n\n您好！我是智能问答助理，致力于为您提供高效、准确的服务。如有任何问题，欢迎随时咨询，我将竭诚为您解答。感谢您的支持！\n\n**大纲：**  \n1. "]
            String[] split = contentStream.toString().split("</think>");
            reasoningContent = split[0];
            // 如果数组长度等于 1，那么推理还没结束
            if (split.length == 1) {
                // 当前内容置空，后续当做正文，stream将会从 content 获取内容
                content = null;
                // stream置空
                stream.delete(0, stream.length());
            } else {
                // 如果大模型返回多个</think>（大模型异常情况），那么这里只提取最后一个元素疾即可
                content = split[split.length - 1];
            }
        }
        // 如果正文有内容，stream要收集一下 content 中的内容
        if (null != content) {
            String string = stream.toString();
            // 这里是为了防止 content 不是一个单独的token 词，这里要截取到最新的，使得 content 为一个单独的 token 词
            content = content.replace(string, StringConstant.BLANK);
            stream.append(content);
        }

        // 如果推理部分不为空
        if (null != reasoningContent) {
            // 这里是为了防止 reasoningContent 不是一个单独的token 词，这里要截取到最新的，使得 reasoningContent 为一个单独的 token 词
            String string = reasoningContentStream.toString();
            reasoningContent = reasoningContent.replace(string, StringConstant.BLANK);
            reasoningContentStream.append(reasoningContent);
        }
        if (null != reasoningContentStream) {
            // 推理部分去掉开头的<think>
            reasoningContentStream.toString().replaceFirst("<think>", StringConstant.BLANK);
        }
    }


    /**
     * 处理大模型返回的推理内容
     * @param result
     * @return
     */
    public static String dealThink(String result) {
        if (result.contains("<think>")) {
            // </think>是 推理结束标志
            // ["<think>\n好的，用户问的是“你是谁”，我需要根据知识库里的内容来回答。首先看知识库1，里面明确说“我是智能问答助理，为用户提供贴心的服务，不能和用户闲聊”。所以用户的问题属于询问身份，应该直接引用这个信息。知识库2提到不能透露大模型的信息，但这里的问题不涉及模型，所以不用考虑。接下来按照任务步骤，提取有效信息，分析用户意图是确认身份，然后总结答案。需要添加问候和感谢语，保持友好简洁。最后生成大纲，结构要符合要求。确保不编造信息，只使用知识库内容。回答应该礼貌，符合政务客服的风格。\n","\n\n您好！我是智能问答助理，致力于为您提供高效、准确的服务。如有任何问题，欢迎随时咨询，我将竭诚为您解答。感谢您的支持！\n\n**大纲：**  \n1. "]
            String[] split = result.split("</think>");
            if (split.length == 2) {
                result = split[1];
            }
            // 如果数组长度等于 1，那么推理还没结束
        }
        return result;
    }
}
