package com.wenge.model.strategy.recommend;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Lists;
import com.wenge.model.dto.param.AssociateQuestionParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.RecommendQuestionParam;
import com.wenge.model.dto.result.AssociateQuestionResult;
import com.wenge.model.dto.result.RecommendQuestionResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.impl.DialogueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.QA_LARGEMODEL_STRATEGY;

@Service(QA_LARGEMODEL_STRATEGY)
public class LargeModelStrategy implements RecommendStrategy {

    @Autowired
    private DialogueServiceImpl dialogueService;

    @Override
    public List<RecommendQuestionResult> getRecommendQuestion(RecommendQuestionParam param) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setContent(param.getQuestion());
        dto.setApplicationId(applicationInfo.getApplicationId());
        Vector<StepEntity> contextList = new Vector<>();
        dialogueService.recommendedContent(dto, contextList, Boolean.FALSE);
        final JSONArray recommendedQuestions = dto.getRecommendedQuestions();
        if (CollectionUtil.isNotEmpty(recommendedQuestions)) {
            List<RecommendQuestionResult> collect = recommendedQuestions.stream().map(p -> {
                RecommendQuestionResult recommend = new RecommendQuestionResult();
                recommend.setCategory("大模型发散的推荐");
                recommend.setQuestion(p.toString());
                return recommend;
            }).collect(Collectors.toList());
            return collect;
        }
        return Lists.newArrayList();
    }

    @Override
    public List<AssociateQuestionResult> getAssociationQuestion(AssociateQuestionParam param) {
        List<String> associationContent = dialogueService.associationContent(param);
        if (CollectionUtil.isNotEmpty(associationContent)) {
            List<AssociateQuestionResult> collect = associationContent.stream().map(p -> {
                AssociateQuestionResult recommend = new AssociateQuestionResult();
                recommend.setQuestion(p);
                return recommend;
            }).collect(Collectors.toList());
            return collect;
        }
        return Lists.newArrayList();
    }


}
