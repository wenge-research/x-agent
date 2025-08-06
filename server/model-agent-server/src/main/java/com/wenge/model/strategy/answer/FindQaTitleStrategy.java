package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AnswerRefParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service(AnswerStrategyContant.FIND_QA_TITLE)
@Slf4j
public class FindQaTitleStrategy implements AnswerStrategy{

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        // 设置步骤节点
        step.setStep(AnswerStrategyContant.FIND_QA_TITLE_STEP);
        StepEntity rerankStepModel = new StepEntity();
        rerankStepModel.setStep(AnswerStrategyContant.FIND_QA_TITLE_ANSWER);
        // rerankStepModel.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        try {
            // 获取应用
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

            // 判断是否开启问答库
            if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return answerData;
            }

            // 收集上下文
            contextList.add(step);
            contextList.add(rerankStepModel);

            // 获取问答库问答分数
            Float qaTitleScore = applicationInfo.getQaTitleScore();
            if (null == qaTitleScore) {
                qaTitleScore = 0F;
            }

            Float qaRangeTitleScore = applicationInfo.getQaRangeTitleScore();
            if (qaRangeTitleScore == null) {
                qaRangeTitleScore = 0F;
            }
            // 检索问答库，使用原问题检索
            List<KnowledgeData> knowledgeDataList = answerUtils.byDense(dto, AnswerStrategyContant.TITLE_DENSE_FILED, qaTitleScore, step, rerankStepModel, qaRangeTitleScore, contextList);
            // 判断是否获取到答案，如果没有则返回
            if (CollectionUtils.isEmpty(knowledgeDataList)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                rerankStepModel.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                rerankStepModel.setCostTime(System.currentTimeMillis() - start);
                return answerData;
            }

            // 设置回答
            KnowledgeData knowledgeData = knowledgeDataList.get(0);
            answerData.setAnswer(knowledgeData.obtainAnswer());

            AnswerRefParam answerRefParam = AnswerUtils.initAnswerRefByKnow(knowledgeData);
            answerData.setRefList(ListUtil.toList(answerRefParam));
            // 润色回答
            if (YesNoEnum.YES.getName().equals(knowledgeData.getPolishFlag())) {
                StepEntity modelStep = new StepEntity();
                modelStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                modelStep.setStep(POLISH_STEP);
                contextList.add(modelStep);
                answerUtils.polishAnswer(answerData, dto, modelStep, start);
            } else {
                answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                // 这里直接推流
                dto.getAnswerConsumer().accept(answerData);
            }
        } catch (Exception e) {
            log.error("QA title error:{}", e.getMessage(), e);
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            step.setResult(ERROR_STEP + ":" + e.getMessage());
        }
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        rerankStepModel.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        rerankStepModel.setCostTime(System.currentTimeMillis() - start);
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        KnowledgeContent knowledgeContent = new KnowledgeContent();
        knowledgeContent.setModule(FIND_QA_TITLE);

        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        // 设置步骤节点
        step.setStep(AnswerStrategyContant.FIND_QA_TITLE_STEP);
        StepEntity stepModel = new StepEntity();
        stepModel.setStep(AnswerStrategyContant.FIND_QA_TITLE_ANSWER);
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 获取问答库问答分数
        Float qaTitleScore = applicationInfo.getQaTitleScore();
        if (null == qaTitleScore) {
            qaTitleScore = 0F;
        }

        Float qaRangeTitleScore = applicationInfo.getQaRangeTitleScore();
        if (qaRangeTitleScore == null) {
            qaRangeTitleScore = 0F;
        }
        List<KnowledgeData> knowledgeDataList = answerUtils.byDense(dto, AnswerStrategyContant.TITLE_DENSE_FILED, qaTitleScore, step, stepModel, qaRangeTitleScore, contextList);
        // 判断是否获取到答案，如果没有则返回
        if (CollectionUtils.isEmpty(knowledgeDataList)) {
            step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            step.setCostTime(System.currentTimeMillis() - start);
            return knowledgeContent;
        }
        // 取前n条数据
        Integer filterNum = applicationInfo.getFilterNum();
        // 按照分数倒序，取前num
        List<KnowledgeData> knowledgeData = knowledgeDataList.stream()
                .sorted(Comparator.comparing(KnowledgeData::getScore).reversed()).collect(Collectors.toList())
                .subList(0, filterNum > knowledgeDataList.size() ? knowledgeDataList.size() : filterNum);

        ArrayList<String> contentLists = new ArrayList<>();
        for (int i = 0; i < knowledgeData.size(); i++) {
            contentLists.add(FIND_QA_TITLE + REARRANGE_SPLIT_CHAR + i + REARRANGE_SPLIT_CHAR + knowledgeData.get(i).getTitle() + "\n" + knowledgeData.get(i).getContent());
        }
        knowledgeContent.setContentList(contentLists);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return knowledgeContent;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非检索问答库【问题】的结果
        if (null == step || !step.getStep().equals(FIND_QA_TITLE_REF)) {
            return dataList;
        }
        DialogueStep primaryData = stepMap.get(FIND_QA_TITLE_STEP);
        if (null != primaryData) {
            List<SourceAnswerResult> answerResults = answerUtils.qaTitleRef(primaryData);
            dataList.addAll(answerResults);
        }
        return dataList;
    }

    @Override
    public List<SourceAnswerResult> notAnswer(List<DialogueStep> dialogueSteps, boolean interceptFlag) {
        List<SourceAnswerResult> results = Lists.newArrayList();
        // 拦截
        if (interceptFlag) {
            return results;
        }
        Optional<DialogueStep> any = dialogueSteps.stream()
                .filter(p -> p.getStep().equals(FIND_QA_TITLE_STEP))
                .findAny();
        // QA的问题
        if (any.isPresent()) {
            DialogueStep dialogueStep = any.get();
            SourceAnswerResult result = new SourceAnswerResult();
            result.setAnswerFlag("否");
            result.setText(null == dialogueStep.getResult() ? NO_KNOWLEDGE : dialogueStep.getResult().toString().replace("[]", NO_KNOWLEDGE));
            result.setRoute(ListUtil.toList(FIND_QA_TITLE_STEP));
            results.add(result);
        }
        return results;
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, FIND_QA_TITLE, FIND_QA_TITLE_STEP, FIND_QA_TITLE_REF);
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setContentTemp(scoreDataParam.getQuestion());
        dto.setClientId(scoreDataParam.getClientId());
        StepEntity step = new StepEntity();
        StepEntity stepModel = new StepEntity();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        List<KnowledgeData> knowledgeDataList = answerUtils.byDense(dto, AnswerStrategyContant.TITLE_DENSE_FILED, applicationInfo.getQaTitleScore(), step, stepModel, applicationInfo.getQaRangeTitleScore(), new Vector<>());

        return knowledgeDataList.stream().map(p -> {
            ScoreDataResult result = new ScoreDataResult();
            result.setTitle(p.getTitle());
            result.setContent(p.getContent());
            result.setEsScore(p.getScore());
            result.setRearrangeScore(p.getRangeScore());
            return result;
        }).collect(Collectors.toList());
    }
}
