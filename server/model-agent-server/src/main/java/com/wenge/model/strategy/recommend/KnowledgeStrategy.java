package com.wenge.model.strategy.recommend;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AssociateQuestionParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.RecommendQuestionParam;
import com.wenge.model.dto.result.AssociateQuestionResult;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.RecommendQuestionResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.answer.AnswerStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.QuestionParam;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.QuestionResult;
import com.wg.appframe.yayi.result.RearrangeResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.QA_KNOWLEDGE_STRATEGY;


@Service(QA_KNOWLEDGE_STRATEGY)
public class KnowledgeStrategy implements RecommendStrategy {

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private YayiServer yayiServer;

    @Value("${aiCommand.questionByAnswer}")
    private String questionByAnswerCommand;

    @Override
    public List<RecommendQuestionResult> getRecommendQuestion(RecommendQuestionParam param) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        Vector<StepEntity> contextList = new Vector<>();
        dto.setContentTemp(param.getQuestion());
        dto.setApplicationId(param.getApplicationId());
        Vector<KnowledgeContent> contentList = answerUtils.getContentList(dto, contextList, Collections.emptyList());
        if (CollectionUtils.isEmpty(contentList)) {
            return Lists.newArrayList();
        }

        // 封装数据集，按照模块进行排序
        List<String> collect = contentList.stream().flatMap(p -> {
            List<String> contents = p.getContentList();
            if (CollectionUtils.isEmpty(contents)) {
                return null;
            }
            return contents.stream();
        }).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return Lists.newArrayList();
        }

        // 调用重排能力
        RearrangeResult rearrange = getRearrange(collect, param.getQuestion());
        if (null == rearrange) {
            return Lists.newArrayList();
        }
        RearrangeResult.RearrangeData data = rearrange.getData();
        return getRecommendQuestionByContent(data, collect);
    }

    /**
     * 对文本进行重排
     * @param question
     * @return
     */
    private RearrangeResult getRearrange(List<String> collect, String question) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (String content : collect) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            articles.setPara(content);
            articlesList.add(articles);
        }
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        RearrangeParam rearrangeParam = new RearrangeParam();
        rangeContent.setArticles(articlesList);
        rearrangeParam.setContent(rangeContent);
        rangeContent.setQuery(question);
        // 调用重排能力
        return answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
    }

    /**
     *  根据内容进行推荐
     * @param data
     * @param collect
     * @return
     */
    private List<RecommendQuestionResult> getRecommendQuestionByContent(RearrangeResult.RearrangeData data, List<String> collect) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        Integer recommendationNum = null == applicationInfo.getRecommendationNum() ? 3 : applicationInfo.getRecommendationNum();
        Float rangeContentScore = null == applicationInfo.getRangeContentScore() ? 0.77F : applicationInfo.getRangeContentScore();
        if (null == data) {
            return Lists.newArrayList();
        }
        List<Integer> indexList = data.getIndex_list();
        List<RecommendQuestionResult> results = Lists.newArrayList();
        int size = indexList.size();
        List<BigDecimal> resScoresList = data.getRes_scores_list();
        if (CollectionUtils.isNotEmpty(indexList)) {
            for (int i = 0; i < size; i++) {
                if (results.size() >= recommendationNum) {
                    break;
                }
                BigDecimal score = resScoresList.get(i);
                if (score.compareTo(BigDecimal.valueOf(rangeContentScore)) < 0) {
                    continue;
                }
                String text = collect.get(i);
                RecommendQuestionResult result = new RecommendQuestionResult();
                QuestionParam questionParam = new QuestionParam();
                QuestionParam.Content content = new QuestionParam.Content();
                questionParam.setContent(content);
                content.setModel("yayi");
                content.setQuestions_num(1);
                QuestionResult question = yayiServer.question(text, questionParam);
                QuestionResult.QuestionData questionData = question.getData();
                if (CollectionUtils.isEmpty(questionData.getQuestions_list())) {
                    continue;
                }
                String questionText = questionData.getQuestions_list().get(0);
                String answer = questionData.getAnswers_list().get(0);
                result.setAnswer(answer);
                result.setQuestion(questionText);
                result.setCategory("知识库的推荐");
                results.add(result);
            }
        }
        return results;
    }

    @Override
    public List<AssociateQuestionResult> getAssociationQuestion(AssociateQuestionParam param) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        Vector<StepEntity> contextList = new Vector<>();
        dto.setContentTemp(param.getQuestion());
        dto.setApplicationId(param.getApplicationId());
//        Vector<KnowledgeContent> contentList = answerUtils.getContentList(dto, contextList, Collections.emptyList());
//        if (CollectionUtils.isEmpty(contentList)) {
//            return Lists.newArrayList();
//        }
//        // 封装数据集，按照模块进行排序
//        List<String> collect = contentList.stream().flatMap(p -> {
//            List<String> contents = p.getContentList();
//            if (CollectionUtils.isEmpty(contents)) {
//                return null;
//            }
//            return contents.stream();
//        }).distinct().collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(collect)) {
//            return Lists.newArrayList();
//        }
        ApplicationContext context = CoreContextProvider.getContext();
        AnswerStrategy strategy = (AnswerStrategy) context.getBean(AnswerStrategyContant.KNOWLEDGE_CONTENT);
        List<String> contentList = strategy.getContent(dto, contextList).getContentList();
        // 调用重排能力
        RearrangeResult rearrange = getRearrange(contentList, param.getQuestion());
        if (null == rearrange) {
            return Lists.newArrayList();
        }
        RearrangeResult.RearrangeData data = rearrange.getData();
        return getAssociateQuestionByContent(data, contentList);
    }

    private List<AssociateQuestionResult> getAssociateQuestionByContent(RearrangeResult.RearrangeData data, List<String> collect) {

        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        Integer associationNum = null == applicationInfo.getAssociationNum() ? 3 : applicationInfo.getAssociationNum();
//        Float rangeContentScore = null == applicationInfo.getRangeContentScore() ? 0.77F : applicationInfo.getRangeContentScore();
        Float rangeContentScore = 0.4f;
        if (null == data) {
            return Lists.newArrayList();
        }
        List<Integer> indexList = data.getIndex_list();
        List<AssociateQuestionResult> results = Lists.newArrayList();
        int size = indexList.size();
        List<BigDecimal> resScoresList = data.getRes_scores_list();
        if (CollectionUtils.isNotEmpty(indexList)) {
            for (int i = 0; i < size; i++) {
                if (results.size() >= associationNum) {
                    break;
                }
                BigDecimal score = resScoresList.get(i);
                if (score.compareTo(BigDecimal.valueOf(rangeContentScore)) < 0) {
                    continue;
                }
                String text = collect.get(i);
                String questionByAnswerCommand = this.questionByAnswerCommand;
                questionByAnswerCommand = questionByAnswerCommand.replace("{answer}", text);
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                String generateCommon = answerUtils.getGenerateCommon(null,
                        questionByAnswerCommand,
                        new StepEntity(), null, null);
                String question;
                if (JSONUtil.isTypeJSON(generateCommon)) {
                    JSONObject entries = JSONUtil.parseObj(generateCommon);
                    question = entries.getStr("question");
                } else {
                    continue;
                }
                AssociateQuestionResult result = new AssociateQuestionResult();
                result.setAnswer(text);
                result.setQuestion(question);
                results.add(result);
            }
        }
        return results;
    }



}
