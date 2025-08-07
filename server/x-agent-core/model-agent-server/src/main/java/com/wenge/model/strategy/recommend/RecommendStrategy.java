package com.wenge.model.strategy.recommend;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.wenge.model.dto.param.AssociateQuestionParam;
import com.wenge.model.dto.param.RecommendQuestionParam;
import com.wenge.model.dto.result.AssociateQuestionResult;
import com.wenge.model.dto.result.RecommendQuestionResult;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.mapper.es.DialogueStepMapper;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.oauth.constants.AppConfigContant.RECOMMENDED_SOURCE_VAGUE_ENABLE;
import static com.wenge.oauth.constants.AppConfigContant.VAGUE_GUIDE_MODEL_ID;

public interface RecommendStrategy {

    /**
     * 推荐问题
     * @param param
     * @return
     */
    List<RecommendQuestionResult> getRecommendQuestion(RecommendQuestionParam param);

    /**
     * 联想问题
     * @param param
     * @return
     */
    List<AssociateQuestionResult> getAssociationQuestion(AssociateQuestionParam param);

    /**
     * 从模糊问题引导中获取推荐问题
     *
     * @param param
     * @return
     */
    default List<RecommendQuestionResult> recommendQuestionByVague(RecommendQuestionParam param, DialogueStepMapper dialogueStepMapper) {
        // 先从模糊问题引导找推荐问题，如果有就推荐
        String vagueEnable = AppConfigContant.getConfiguration(param.getApplicationId(), RECOMMENDED_SOURCE_VAGUE_ENABLE);
        if (YesNoEnum.YES.getName().equals(vagueEnable) && StringUtils.isNotBlank(param.getDialogueId())) {
            LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                    .eq(DialogueStep::getDialogueId, param.getDialogueId());

            long start = System.currentTimeMillis();
            List<DialogueStep> dialogueSteps = Lists.newArrayList();
            // 这里在等待，等待大模型引导结果，最大等待3秒
            while (System.currentTimeMillis() - start <= 3000) {
                dialogueSteps = dialogueStepMapper.selectList(wrapper);
                if (CollectionUtil.isNotEmpty(dialogueSteps)) {
                    break;
                }
            }

            // 获取引导结果
            if (CollectionUtil.isEmpty(dialogueSteps)) {
                return Lists.newArrayList();
            }

            List<DialogueStep> vagueLlmResult = dialogueSteps.stream()
                    .filter(p -> p.getStep().equals(VAGUE_LLM_RESULT) || p.getStep().equals(KNOWLEDGE_QA_CHANGE_CONTENT_RECOMMENDED)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(vagueLlmResult)) {
                // 获取引导结果具体内容
                DialogueStep dialogueStep = vagueLlmResult.get(0);
                Object result = dialogueStep.getResult();

                // 获取配置的引导的大模型
                String vagueGuideModelId = AppConfigContant.getConfiguration(param.getApplicationId(), VAGUE_GUIDE_MODEL_ID);
                StepEntity stepEntity = new StepEntity();
                cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
                if (StringUtils.isNotBlank(vagueGuideModelId)) {
                    stepParam.set(LLM_PARAM_MODEL_ID, vagueGuideModelId);
                }
                ApplicationContext context = CoreContextProvider.getContext();
                AnswerUtils answerUtils = context.getBean(AnswerUtils.class);
                stepEntity.setParam(stepParam);

                // 生成结果
                String generateCommon = answerUtils.getGenerateCommon(result.toString(), StringConstant.BLANK, stepEntity, ListUtil.toList(), "请从提取准备的问题，不要改变问题的内容，返回 json 格式，例如[\"如何办理身份证\"]");
                if (StringUtils.isNotBlank(generateCommon)) {
                    generateCommon = generateCommon
                            .replace(JSON_START, StringConstant.BLANK)
                            .replace(JSON_END, StringConstant.BLANK);

                    // 返回结果
                    if (JSONUtil.isTypeJSONArray(generateCommon)) {
                        List<String> list = JSONUtil.toList(generateCommon, String.class);
                        return list.stream().map(p -> {
                            RecommendQuestionResult recommendQuestionResult = new RecommendQuestionResult();
                            recommendQuestionResult.setQuestion(p.replace("。", StringConstant.BLANK));
                            recommendQuestionResult.setCategory("模糊引导的推荐");
                            return recommendQuestionResult;
                        }).collect(Collectors.toList());
                    }
                }
            }
        }
        return Lists.newArrayList();
    }
}
