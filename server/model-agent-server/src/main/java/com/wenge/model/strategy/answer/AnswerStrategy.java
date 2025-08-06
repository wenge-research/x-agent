package com.wenge.model.strategy.answer;

import com.google.common.collect.Lists;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public interface AnswerStrategy {

    /**
     * 获取答案
     *
     * @param dto
     * @param contextList
     * @return
     */
    AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData stepData);

    /**
     * 获取知识库数据，以供大模型回答，这里是最原始的数据，没有经过重排过滤
     * @param dto
     * @param contextList
     * @return
     */
    default KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList){
        return null;
    }

    /**
     * 是当前步骤提供的答案，从这里获取来源答案
     * @param step
     * @param stepMap
     * @return
     */
    default List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        return Lists.newArrayList();
    };

    /**
     * 无法回答，查看各个策略的原始数据
     *
     * @param dialogueSteps
     * @param interceptFlag
     * @return
     */
    default List<SourceAnswerResult> notAnswer(List<DialogueStep> dialogueSteps, boolean interceptFlag) {
        return Lists.newArrayList();
    };

    /**
     * 提供检索原文依据
     * @param dialogueId
     * @param contextList
     * @return
     */
    default StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return new StepEntity();
    };

    /**
     * 通过问题查询知识库数据，用来排查分值
     * @param scoreDataParam
     * @return
     */
    default List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        return Lists.newArrayList();
    };
}
