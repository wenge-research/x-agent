package com.wenge.model.strategy.recommend;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AssociateQuestionParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.KnowledgeDataPageParam;
import com.wenge.model.dto.param.RecommendQuestionParam;
import com.wenge.model.dto.result.AssociateQuestionResult;
import com.wenge.model.dto.result.RecommendQuestionResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.entity.KnowledgeDataType;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.KnowledgeDataTypeService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.QA_QUESTION_STRATEGY;
import static com.wenge.model.entity.table.KnowledgeDataTypeTableDef.KNOWLEDGE_DATA_TYPE;

@Service(QA_QUESTION_STRATEGY)
public class QuestionStrategy implements RecommendStrategy {

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    KnowledgeDataTypeService knowledgeDataTypeService;

    @Autowired
    private KnowledgeDataMapper dataMapper;

    @Override
    public List<RecommendQuestionResult> getRecommendQuestion(RecommendQuestionParam param) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        StepEntity step = new StepEntity();
        step.setStep("推荐问答的问题");
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setSuggest("推荐");
        dto.setContentTemp(param.getQuestion());
        Integer recommendationNum = null == applicationInfo.getRecommendationNum() ? 3 : applicationInfo.getRecommendationNum();
        applicationInfo.setFilterNum(recommendationNum);
        Vector<StepEntity> contextList = new Vector<>();
        List<KnowledgeData> knowledgeData = answerUtils.byDense(dto, AnswerStrategyContant.TITLE_DENSE_FILED, applicationInfo.getQaTitleScore(), step, step, applicationInfo.getQaRangeTitleScore(), contextList);
        if (CollectionUtil.isNotEmpty(knowledgeData)) {
            // 限制问题数量
            Set<String> seenTitles = new LinkedHashSet<>();
            knowledgeData = knowledgeData.stream()
                    .filter(v -> StringUtils.isNotEmpty(v.getTitle()) && seenTitles.add(v.getTitle()))
                    .limit(recommendationNum)
                    .collect(Collectors.toList());
            List<RecommendQuestionResult> recommendQuestionResults = knowledgeData.stream().map(p -> {
                RecommendQuestionResult recommend = new RecommendQuestionResult();
                recommend.setQuestion(p.getTitle());
                recommend.setCategory("QA对问题的推荐");
                recommend.setAnswer(p.obtainAnswer());
                recommend.setLink(p.getLink());

                // 事项需要名称属于事项的才返回；19/20
                String category = p.getCategory();
                if (StringUtils.isNotBlank(category)) {
                    List<String> split = CollectionUtil.toList(category.split("/"));
                    List<KnowledgeDataType> knowledgeDataTypes = knowledgeDataTypeService.listByIds(split);
                    Map<Long, String> collect = knowledgeDataTypes.stream().collect(Collectors.toMap(KnowledgeDataType::getId, KnowledgeDataType::getType));
                    String typeName = collect.get(Long.parseLong(split.get(0)));
                    if (StringUtils.isNotBlank(typeName) && StringUtils.contains(typeName, "事项")) {
                        recommend.setCategory(collect.get(Long.parseLong(split.get(split.size() - 1))));
                    }
                }
                return recommend;
            }).collect(Collectors.toList());

            // 按照问题去重
            HashMap<String, RecommendQuestionResult> collect = recommendQuestionResults.stream().collect(Collectors.toMap(
                    RecommendQuestionResult::getQuestion,
                    p -> p,
                    (k1, k2) -> k1,
                    Maps::newHashMap
            ));
            return new ArrayList<>(collect.values());
        }

        return Lists.newArrayList();
    }

    @Override
    public List<AssociateQuestionResult> getAssociationQuestion(AssociateQuestionParam param) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        StepEntity step = new StepEntity();
        step.setStep("推荐问答的问题");
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setSuggest("推荐");
        dto.setContentTemp(param.getQuestion());
        Integer associationNum = null == applicationInfo.getAssociationNum() ? 3 : applicationInfo.getAssociationNum();
        applicationInfo.setFilterNum(associationNum);
        List<String> knowledgeIds = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();

        List<KnowledgeData> knowledgeData = getKnowledgeDataList(knowledgeIds, param.getQuestion());

        if (CollectionUtil.isNotEmpty(knowledgeData)) {
            // 限制问题数量
            Set<String> seenTitles = new LinkedHashSet<>();
            knowledgeData = knowledgeData.stream()
                    .filter(v -> StringUtils.isNotEmpty(v.getTitle()) && seenTitles.add(v.getTitle()))
                    .limit(associationNum)
                    .collect(Collectors.toList());
            List<AssociateQuestionResult> associateQuestionResults = knowledgeData.stream().map(p -> {
                AssociateQuestionResult recommend = new AssociateQuestionResult();
                recommend.setQuestion(p.getTitle());
                recommend.setAnswer(p.obtainAnswer());
                recommend.setLink(p.getLink());

                // 事项需要名称属于事项的才返回；19/20
                String category = p.getCategory();
                if (StringUtils.isNotBlank(category)) {
                    List<String> split = CollectionUtil.toList(category.split("/"));
                    List<KnowledgeDataType> knowledgeDataTypes = knowledgeDataTypeService.listByIds(split);
                    Map<Long, String> collect = knowledgeDataTypes.stream().collect(Collectors.toMap(KnowledgeDataType::getId, KnowledgeDataType::getType));
                    String typeName = collect.get(Long.parseLong(split.get(0)));
                    if (StringUtils.isNotBlank(typeName) && StringUtils.contains(typeName, "事项")) {
                        recommend.setCategory(collect.get(Long.parseLong(split.get(split.size() - 1))));
                    }
                }
                return recommend;
            }).collect(Collectors.toList());

            // 按照问题去重
            HashMap<String, AssociateQuestionResult> collect = associateQuestionResults.stream().collect(Collectors.toMap(
                    AssociateQuestionResult::getQuestion,
                    p -> p,
                    (k1, k2) -> k1,
                    Maps::newHashMap
            ));
            return new ArrayList<>(collect.values());
        }
        return Lists.newArrayList();
    }

    /**
     * 获取知识库列表
     * @param knowledgeIds
     * @param question
     * @return
     */
    private List<KnowledgeData> getKnowledgeDataList(List<String> knowledgeIds, String question) {
        LambdaEsQueryWrapper<KnowledgeData> wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                .notSelect(KnowledgeData::getContentDense,
                        KnowledgeData::getTitleDense,
                        KnowledgeData::getContentDense1024,
                        KnowledgeData::getTitleDense1024)
                .eq(KnowledgeData::getDelStatus, "0")
                .in(KnowledgeData::getKnowledgeId, knowledgeIds)
                .matchPhrasePrefixQuery(KnowledgeData::getTitle, question)
                .orderByDesc(KnowledgeData::getUpdateTime);

        return dataMapper.selectList(wrapper);
    }
}
