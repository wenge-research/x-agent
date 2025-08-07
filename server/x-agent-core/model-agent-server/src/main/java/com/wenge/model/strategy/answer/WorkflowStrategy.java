package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.service.ComponentService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.workflow.component.dto.WorkflowRunStatus;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.DOCUMENT_LIBRARY;

@Service(AnswerStrategyContant.WORKFLOW_STRATEGY)
@Slf4j
public class WorkflowStrategy implements AnswerStrategy {

    @Autowired
    private ComponentService componentService;


    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        // MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
        // String answer = StringConstant.BLANK;
        //
        // answer = fromDocumentLibrary(dto, contextList);
        // log.info("===>documentStrategy:{}", answer);
        // answerData.setAnswer(answer);
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.WORKFLOW_STRATEGY);
        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        // 判断是否开启问答库
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            step.setCostTime(System.currentTimeMillis() - start);
            return content;
        }
        step.setStep(AnswerStrategyContant.WORKFLOW_STEP);
        contextList.add(step);

        try {
            List<Component> components = componentService.effectiveComponent(dto.getApplicationId());
            if (CollectionUtils.isEmpty(components)) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }
            List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(components.get(0).getComponentId()));

            ComponentDto componentDto = componentDtoList.get(0);
            List<ComponentNodeDto> nodes = componentDto.getNodes();
            if (CollectionUtils.isEmpty(nodes)) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }
            Optional<ComponentNodeDto> any = nodes.stream().filter(p -> p.getNodeType().equals(ComponentNodeEnum.START.getCode())).findAny();
            if (!any.isPresent()) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }

            ComponentNodeDto startNode = any.get();

            List<MetaParam> paramList = startNode.getOutput();
            if (CollectionUtils.isEmpty(paramList)) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }

            any = nodes.stream().filter(p -> p.getNodeType().equals(ComponentNodeEnum.END.getCode())).findAny();
            if (!any.isPresent()) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }
            ComponentNodeDto endNode = any.get();
            List<MetaParam> endInputList = endNode.getInput();
            if (CollectionUtils.isEmpty(endInputList)) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }

            List<String> variableList = paramList.stream()
                    .filter(p -> MetaParamEnum.STRING.getName().equals(p.getValueType()))
                    .map(MetaParam::getVariable)
                    .collect(Collectors.toList());

            JSONObject flowParam = new JSONObject();
            for (String variable : variableList) {
                flowParam.set(variable, dto.getContent());
            }
            RunComponentNodeParam param = new RunComponentNodeParam();
            param.setComponentId(componentDto.getComponentId());
            param.setDialogueId(dto.getDialogueId());
            param.setInputs(flowParam);
            param.setClientId(IdUtil.simpleUUID());
            Map<String, Object> stringObjectMap = componentService.runFlow(param, WorkflowRunStatus.DEBUG, true);
            if (null == stringObjectMap || stringObjectMap.isEmpty()) {
                step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                step.setCostTime(System.currentTimeMillis() - start);
                return content;
            }
            List<String> endVariableList = endInputList.stream().map(MetaParam::getVariable).collect(Collectors.toList());
            stringObjectMap.entrySet().removeIf(entry -> !endVariableList.contains(entry.getKey()));

            List<Object> valueList = new ArrayList<>(stringObjectMap.values());
            List<String> list = valueList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
            content.setContentList(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            step.setCostTime(System.currentTimeMillis() - start);
        }
        return content;
    }


    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非文档库的结果
        if (null == step || !step.getStep().equals(DOCUMENT_LIBRARY)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        List<String> list = (List<String>) step.getResult();
        return null;
    }


    /**
     * 必要条件
     * @return
     */
    public BoolQueryBuilder prerequisite(List<String> knowldegeIdList) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        List<QueryBuilder> must = bool.must();
        must.add(QueryBuilders.termsQuery("knowledgeId.keyword", knowldegeIdList));
        return bool;
    }

    /**
     * 全文搜索
     * @param dto
     * @param mustCondition
     */
    public void fullTextSearch(DialogueByStreamParam dto, BoolQueryBuilder mustCondition) {
        List<QueryBuilder> must = mustCondition.must();
        must.add(QueryBuilders.matchQuery("content", dto.getContentTemp()));
    }


    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setContentTemp(scoreDataParam.getQuestion());
        dto.setClientId(scoreDataParam.getClientId());
        StepEntity step = new StepEntity();
        // List<FileData> fileDataList = getFileDataList(dto, step);
        return null;
    }

}
