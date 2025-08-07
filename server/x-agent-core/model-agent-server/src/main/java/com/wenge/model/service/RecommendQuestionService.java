package com.wenge.model.service;

import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.mapper.es.DialogueStepMapper;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface RecommendQuestionService {

    /**
     * 推荐问题
     * @param param
     * @return
     */
    Result<List<RecommendQuestionResult>> recommendQuestion(RecommendQuestionParam param);


    /**
     * 根据知识库QA推荐问题
     * @param param
     * @return
     */
    List<RecommendQuestionResult> recommendKnowledgeQAQuestion(RecommendQuestionParam param);

    /**
     * 查看答案依据来源
     * @param sourceAnswerParam
     * @return
     */
    Result<SourceAnswerFinalResult> sourceAnswer(SourceAnswerParam sourceAnswerParam);

    /**
     * 获取评分数据
     * @param scoreDataParam
     * @return
     */
    Result<List<ScoreDataResult>> getScoreData(ScoreDataParam scoreDataParam);

    /**
     * 查看问答全过程的步骤
     * @param stepParam
     * @return
     */
    Result<List<DialogueStep>> getStepByDialogId(StepByDialogIdParam stepParam);

    /**
     * 根据对话id列表查看问答全过程的重排步骤
     * @param dialogueIds
     * @return
     */
    List<DialogueStep> getRerrangeStepByDialogIdList(List<String> dialogueIds);

    Result<DialogueStep> getReviseQuestion(SourceAnswerParam sourceAnswerParam);

    /**
     * 大学城智能问答政策帮h5专用-根据大学城系统的用户id获取推荐问题(注：该接口需要开放白名单)
     * @param param
     * @return
     */
    Result<PolicyRecommendResult> getRecommendByUserInfo(UserInfoParam param);

    /**
     * 联想问题
     * @param param
     * @return
     */
    Result<List<AssociateQuestionResult>> associationQuestion(AssociateQuestionParam param);
}
