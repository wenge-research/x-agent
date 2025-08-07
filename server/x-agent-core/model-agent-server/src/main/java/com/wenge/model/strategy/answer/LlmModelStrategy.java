package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueMessage;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.LlmGenerateUtil;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.oauth.constants.AppConfigContant.USER_MESSAGE_PRE;

@Slf4j
@Service(AnswerStrategyContant.FIND_ANSWER_BY_MODEL)
public class LlmModelStrategy implements AnswerStrategy {

    @Autowired
    private LlmGenerateUtil llmGenerateUtil;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 模型自主回答
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getModelAnswerFlag())) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return answerData;
        }
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(step);
        step.setStep(AnswerStrategyContant.LLM_ANSWER);

        // List<YayiMessage> messagesList = Lists.newArrayList();
        // YayiMessage messages = new YayiMessage();
        // messages.setRole(SYSTEM_PROMPT_FIELD);
        String systemPrompt = applicationInfo.getSystemPromptResult();
        // messages.setContent(systemPrompt);
        // messagesList.add(messages);

        List<DialogueMessage> listMsg = dto.getListMsg();
        List<YayiMessage> messagesList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(listMsg)) {
            messagesList = listMsg.stream().map(p -> new YayiMessage(p.getRole(), p.getContent())).collect(Collectors.toList());
            messagesList.remove(messagesList.size() - 1);
            boolean anyMatch = messagesList.stream().anyMatch(p -> p.getRole().equals("system"));
            if (!anyMatch && StringUtils.isNotBlank(systemPrompt)) {
                messagesList.add(0, new DialogueMessage(SYSTEM_PROMPT_FIELD, systemPrompt));
            }
        } else {
            if (StringUtils.isNotBlank(systemPrompt)) {
                messagesList.add(0, new DialogueMessage(SYSTEM_PROMPT_FIELD, systemPrompt));
            }
        }
        String userMessagePre = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), USER_MESSAGE_PRE);
        if (StringUtils.isBlank(userMessagePre)) {
            userMessagePre = StringConstant.BLANK;
        }

        String typeFromModel = llmGenerateUtil.generateForFindAnswerByModel(userMessagePre + dto.getContentTemp(), messagesList, step, true, result -> {
            try {
                if (null == step.getFirstLlmTime()) {
                    step.setFirstLlmTime(System.currentTimeMillis() - start);
                }
                String answer = result.getAnswer();
                String reasoningContent = result.getReasoningContent();
                answerData.setStatus(result.getStatus());
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        });
        if (typeFromModel.equals(AnswerStrategyContant.NO_ANSWER_TEXT)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
        }
        answerData.setAnswer(typeFromModel);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return answerData;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非大模型发散场景的结果
        if (null == step || !step.getStep().equals(FIND_ANSWER_BY_MODEL_REF)) {
            return dataList;
        }
        SourceAnswerResult refData = new SourceAnswerResult();
        refData.setKnowledgeName(NO_KNOWLEDGE);
        refData.setText(NO_KNOWLEDGE);
        refData.setRoute(ListUtil.toList(LLM_ANSWER));
        dataList.add(refData);
        return dataList;
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, FIND_ANSWER_BY_MODEL, LLM_ANSWER, FIND_ANSWER_BY_MODEL_REF);
    }
}
