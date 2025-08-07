package com.wenge.model.utils;

import cn.hutool.json.JSONObject;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.wenge.oauth.constants.AppConfigContant.LLM_LLM_MODEL_STRATEGY_MODEL_NAME;

@Component
@Slf4j
public class LlmGenerateUtil {

    @Autowired
    private Map<String, LlmStrategy> llmStrategyMap;

    @Autowired
    private LlmInfoService llmInfoService;

    /**
     * 生成答案
     * @param question
     * @param messagesList
     * @param step
     * @param longFlag
     * @return
     */
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        String llmParamModelId = StringConstant.BLANK;
        JSONObject stepParam = step.getParam();
        if (null != stepParam && stepParam.containsKey(AnswerStrategyContant.LLM_PARAM_MODEL_ID)) {
            llmParamModelId = (String) stepParam.getOrDefault(AnswerStrategyContant.LLM_PARAM_MODEL_ID, StringConstant.BLANK);
        }
        if (StringUtils.isBlank(llmParamModelId)) {
            llmParamModelId = applicationInfo.getModelId();
        }

        // 获取模型信息
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(llmParamModelId);
        if (null == llmStrategy) {
            return AnswerStrategyContant.NO_ANSWER_TEXT;
        }
        return llmStrategy.generate(question, messagesList, step, longFlag);
    }

    /**
     * 流式生成答案
     * @param question
     * @param messagesList
     * @param step
     * @param longFlag
     * @return
     */
    public String generate(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 获取模型信息

        String modelId = applicationInfo.getModelId();

        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(modelId);
        if (null == llmStrategy) {
            return AnswerStrategyContant.NO_ANSWER_TEXT;
        }
        return llmStrategy.generate(question, messagesList, step, longFlag, answerConsumer);
    }
    /**
     * 流式生成答案
     * @param question
     * @param messagesList
     * @param step
     * @param longFlag
     * @return
     */
    public String generateForFindAnswerByModel(String question, List<YayiMessage> messagesList, StepEntity step, boolean longFlag, Consumer<AnswerStepData> answerConsumer) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 获取模型信息

        String modelName = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), LLM_LLM_MODEL_STRATEGY_MODEL_NAME);
        String modelId = applicationInfo.getModelId();

        if (StringUtils.isNotBlank(modelName)) {
            LlmInfo byModelName = llmInfoService.getByModelName(modelName);
            if (null != byModelName) {
                modelId = byModelName.getModelId();
            }
        }

        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(modelId);
        if (null == llmStrategy) {
            return AnswerStrategyContant.NO_ANSWER_TEXT;
        }
        return llmStrategy.generate(question, messagesList, step, longFlag, answerConsumer);
    }

}
