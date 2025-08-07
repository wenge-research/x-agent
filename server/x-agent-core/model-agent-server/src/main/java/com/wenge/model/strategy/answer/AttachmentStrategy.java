package com.wenge.model.strategy.answer;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AttachmentListParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;


/**
 * 附件策略
 */
@Service(ATTACHMENT_STRATEGY)
@Slf4j
public class AttachmentStrategy implements AnswerStrategy {


    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(step);
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.ATTACHMENT_STRATEGY);
        step.setStep(ATTACHMENT_STEP);
        List<AttachmentListParam> attachmentParams = dto.getAttachmentList();
        step.setResult(attachmentParams);
        if ( null == attachmentParams || attachmentParams.isEmpty()) {
            step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            step.setCostTime(System.currentTimeMillis() - start);
            return content;
        }
        // 数据格式为  内容[额外信息]
        List<String> attachmentListParam = attachmentParams.stream()
                .map(AttachmentListParam::getText)
                .distinct().collect(Collectors.toList());
        content.setContentList(attachmentListParam);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return content;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非文档库的结果
        if (null == step || !step.getStep().equals(ATTACHMENT_STRATEGY)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        DialogueStep dialogueStep = stepMap.get(ATTACHMENT_STRATEGY);
        if (null == dialogueStep) {
            return Lists.newArrayList();
        }
        Object resultObj = dialogueStep.getResult();
        if (null == resultObj) {
            return Lists.newArrayList();
        }
        List<String> list = (List<String>) step.getResult();
        return list.stream().map(p -> {
                    AttachmentListParam bean = JSONUtil.toBean(p, AttachmentListParam.class);
                    SourceAnswerResult result = new SourceAnswerResult();
                    result.setText(bean.getText());
                    result.setFileLink(bean.getVideoUrl());
                    return result;
                })
                .collect(Collectors.toList());
    }
}
