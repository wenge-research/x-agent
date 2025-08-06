package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AnswerRefParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service(AnswerStrategyContant.BUILT_IN)
@Slf4j
public class BuiltInStrategy implements AnswerStrategy{

    @Autowired
    private KnowledgeDataMapper knowledgeDataMapper;

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        StepEntity idStep = new StepEntity();
        idStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        idStep.setStep(BUILT_IN_STEP);
        try {
            // 先完整完全匹配
            contextList.add(idStep);
            KnowledgeData knowledgeData = getGetFromPolicyTerm(dto, idStep);
            idStep.setResult(ListUtil.toList(knowledgeData));
            // 没有数据，则设置为无法回答
            if (null == knowledgeData || StringUtils.isBlank(knowledgeData.getKnowledgeId())) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                idStep.setEndTime(DateUtil.getCurrentTime());
                idStep.setCostTime(System.currentTimeMillis() - start);
                return answerData;
            }
            String knowledgeContent = knowledgeData.obtainAnswer();
            // dto.setAnswer(knowledgeContent);
            answerData.setAnswer(knowledgeContent);
            AnswerRefParam answerRefParam = AnswerUtils.initAnswerRefByKnow(knowledgeData);
            answerData.setRefList(ListUtil.toList(answerRefParam));
            // 润色回答
            if (YesNoEnum.YES.getName().equals(knowledgeData.getPolishFlag())) {
                StepEntity modelStep = new StepEntity();
                modelStep.setStep(POLISH_STEP);
                contextList.add(modelStep);
                answerUtils.polishAnswer(answerData, dto, modelStep, start);
            } else {
                answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                // 这里直接推流
                dto.getAnswerConsumer().accept(answerData);
            }
        } catch (Exception e) {
            log.error("BuiltInStrategy error:{}", e.getMessage(), e);
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            idStep.setResult(ERROR_STEP + ":" + e.getMessage());
        } finally {
            idStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            idStep.setCostTime(System.currentTimeMillis() - start);
        }
        return answerData;
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, BUILT_IN, BUILT_IN_STEP, BUILT_IN_REF);
    }

    /**
     * 精确查询
     * @param question
     * @param step
     * @return
     */
    private KnowledgeData getGetFromPolicyTerm(DialogueByStreamParam dto, StepEntity step) {
        List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();
        if (CollectionUtils.isEmpty(knowldegeIdList)) {
            return null;
        }
        // 如果内置问题有结果，则直接返回
        if (null != dto.getBuiltInData()) {
            return dto.getBuiltInData();
        }
        if (StringUtils.isBlank(dto.getContentTemp())) {
            dto.setContentTemp(dto.getContent());
        }

        // 检索有效期内的数据
        String currentTime = DateUtil.getCurrentTime();
        LambdaEsQueryWrapper<KnowledgeData> wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                .notSelect(KnowledgeData::getContentDense, KnowledgeData::getTitleDense)
                //.eq(KnowledgeData::getSuggest, "推荐")
                .eq(KnowledgeData::getTitle, dto.getContentTemp())
                .eq(KnowledgeData::getDelStatus, "0")
                .in(KnowledgeData::getKnowledgeId, knowldegeIdList)
                .eq(KnowledgeData::getStatus, YesNoEnum.YES.getName())
                .and(and -> {
                    and.or(or -> {
                        or.le(KnowledgeData::getEffectiveStartTime, currentTime);
                    });
                    and.or(or -> {
                        or.not().exists(KnowledgeData::getEffectiveStartTime);
                    });
                })
                .and(and -> {
                    and.or(or -> {
                        or.not().exists(KnowledgeData::getEffectiveEndTime);
                    });
                    and.or(or -> {
                        or.gt(KnowledgeData::getEffectiveEndTime, currentTime);
                    });
                })
                .size(1);
        List<KnowledgeData> dataList = knowledgeDataMapper.selectList(wrapper);

        // 没有数据时，使用改写的内容
        if (CollectionUtils.isEmpty(dataList) && StringUtils.isNotBlank(dto.getContentTemp()) && !dto.getContentTemp().equals(dto.getContent())) {
            wrapper = EsWrappers.lambdaQuery(KnowledgeData.class)
                    .notSelect(KnowledgeData::getContentDense, KnowledgeData::getTitleDense)
                    //.eq(KnowledgeData::getSuggest, "推荐")
                    .eq(KnowledgeData::getTitle, dto.getContentTemp())
                    .eq(KnowledgeData::getDelStatus, "0")
                    .in(KnowledgeData::getKnowledgeId, knowldegeIdList)
                    .eq(KnowledgeData::getStatus, YesNoEnum.YES.getName())
                    .and(and -> {
                        and.or(or -> {
                            or.le(KnowledgeData::getEffectiveStartTime, currentTime);
                        });
                        and.or(or -> {
                            or.not().exists(KnowledgeData::getEffectiveStartTime);
                        });
                    })
                    .and(and -> {
                        and.or(or -> {
                            or.not().exists(KnowledgeData::getEffectiveEndTime);
                        });
                        and.or(or -> {
                            or.gt(KnowledgeData::getEffectiveEndTime, currentTime);
                        });
                    })
                    .size(1);
            dataList = knowledgeDataMapper.selectList(wrapper);
        }
        step.setResult(dataList);
        //String resultFromEs = "";
        if (CollectionUtils.isNotEmpty(dataList)) {
            dto.setBuiltInData(dataList.get(0));
            return dataList.get(0);
        }
        return null;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非内置问答场景的结果
        if (null == step || !step.getStep().equals(BUILT_IN_REF)) {
            return dataList;
        }
        DialogueStep primaryData = stepMap.get(BUILT_IN_STEP);
        if (null != primaryData) {
            Object refResult = primaryData.getResult();
            if (null == refResult) {
                return dataList;
            }
            List<SourceAnswerResult> sourceAnswer = answerUtils.getSourceAnswer(refResult, knowledgeData -> {
                SourceAnswerResult source = new SourceAnswerResult();
                source.setKnowledgeName(knowledgeData.getKnowledgeName());
                source.setRoute(ListUtil.toList(BUILT_IN_ROUTE));
                source.setText(knowledgeData.getTitle());

                source.setFileName(knowledgeData.getTitle());
                source.setFileLink(knowledgeData.getLink());
                source.setSourceType(2);
                return source;
            });
            dataList.addAll(sourceAnswer);
        }
        return dataList;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity idStep = new StepEntity();
        idStep.setStep(BUILT_IN_STEP);
        idStep.setCreateTime(DateUtil.getCurrentTime());
        KnowledgeContent answerData = new KnowledgeContent();
        contextList.add(idStep);
        try {
            // 先完整完全匹配
            //String question = StringUtils.isNotBlank(dto.getContentTemp()) ? dto.getContentTemp() : dto.getContent();
            KnowledgeData knowledgeData = getGetFromPolicyTerm(dto, idStep);
            // 记录内置问题的结果
            if (null != knowledgeData) {
                dto.setBuiltInData(knowledgeData);
                answerData.setContentList(ListUtil.toList(knowledgeData.obtainAnswer()));
            } else {
                // 如果没有找到内置问题结果，则记录初始化对象
                dto.setBuiltInData(new KnowledgeData());
            }
        } catch (Exception e) {
            log.error("BuiltInStrategy error:{}", e.getMessage(), e);
            idStep.setResult(ERROR_STEP + ":" + e.getMessage());
        }
        idStep.setEndTime(DateUtil.getCurrentTime());
        idStep.setCostTime(System.currentTimeMillis() - start);
        return answerData;
    }

}
