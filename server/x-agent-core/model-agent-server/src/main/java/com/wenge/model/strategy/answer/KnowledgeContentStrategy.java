package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.common.collect.Maps;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.*;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

@Service(AnswerStrategyContant.KNOWLEDGE_CONTENT)
@Slf4j
public class KnowledgeContentStrategy implements AnswerStrategy{

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        // 获取日志id
        String answer = StringConstant.BLANK;

        answer = answerUtils.fromVectorLibrary(dto, contextList);
        log.info("===>knowledgeContentStrategy:{}", answer);
        answerData.setAnswer(answer);
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.KNOWLEDGE_CONTENT);

        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 判断是否开启问答库
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            return content;
        }
        contextList.add(step);
        step.setStep(AnswerStrategyContant.KNOWLEDGE_CONTENT_STEP);
        List<KnowledgeData> knowledgeData = answerUtils.getKnowledgeData(dto, step);
        // 数据格式为  内容[额外信息]
        List<String> collect = knowledgeData.stream().map(p -> p.getTitle() + "\n" + p.getContent() + PREFIX_SYMBOL + p.getKnowledgeId() + SUFFIX_SYMBOL).distinct().collect(Collectors.toList());
        content.setContentList(collect);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return content;
    }


    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非向量库的结果
        if (null == step || !step.getStep().equals(KNOWLEDGE_CONTENT)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        List<String> list = (List<String>) step.getResult();
        return knowledgeRef(list);
    }


    /**
     * 向量库 格式： 内容[知识库id]
     * @param list
     * @return
     */
    private List<SourceAnswerResult> knowledgeRef(List<String> list) {
        List<String> knowledgeIdList = list.stream().map(p -> {
            SourceAnswerResult sourceAnswerResult = new SourceAnswerResult();
            sourceAnswerResult.setKnowledgeName(StringConstant.BLANK);
            int indexOf = p.indexOf(PREFIX_SYMBOL);
            return p.substring(indexOf + PREFIX_SYMBOL.length(), p.length() - PREFIX_SYMBOL.length());
        }).distinct().collect(Collectors.toList());
        Wrappers<Object> wrappers = Wrappers.init()
                .where(KNOWLEDGE_INFO.KNOWLEDGE_ID.in(knowledgeIdList));
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.list(wrappers);
        HashMap<String, KnowledgeInfo> knowledgeMap = knowledgeInfoList.stream().collect(Collectors.toMap(
                KnowledgeInfo::getKnowledgeId,
                p -> p,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
        return list.stream().map(p -> {
            String[] split1 = p.split(AnswerStrategyContant.REARRANGE_SPLIT_CHAR);
            // 提取原始内容
            String text = split1[split1.length - 1];
            if (text.contains(PREFIX_SYMBOL)) {
                text = text.split(PREFIX_SYMBOL)[0];
            }
            SourceAnswerResult sourceAnswerResult = new SourceAnswerResult();
            sourceAnswerResult.setKnowledgeName(StringConstant.BLANK);
            int indexOf = p.indexOf(PREFIX_SYMBOL);
            String knowledgeId = p.substring(indexOf + PREFIX_SYMBOL.length(), p.length() - PREFIX_SYMBOL.length());
            sourceAnswerResult.setKnowledgeName(knowledgeMap.get(knowledgeId).getKnowledgeName());
            sourceAnswerResult.setRoute(ListUtil.toList(AnswerStrategyContant.KNOWLEDGE_CONTENT_ROUTE));
            sourceAnswerResult.setText(text);
            return sourceAnswerResult;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setContentTemp(scoreDataParam.getQuestion());
        dto.setClientId(scoreDataParam.getClientId());
        StepEntity step = new StepEntity();
        List<KnowledgeData> knowledgeData = answerUtils.getKnowledgeData(dto, step);

        return knowledgeData.stream().map(p -> {
            ScoreDataResult result = new ScoreDataResult();
            result.setTitle(p.getTitle());
            result.setContent(p.getContent());
            result.setEsScore(p.getScore());
            result.setRearrangeScore(p.getRangeScore());
            return result;
        }).collect(Collectors.toList());
    }
}
