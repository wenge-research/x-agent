package com.wenge.model.strategy.answer;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.*;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.mapper.es.LlmConversationMapper;
import com.wenge.model.service.*;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wenge.model.workflow.entity.MetaParam;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.model.workflow.enums.DirectionEnum;
import com.wg.appframe.core.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service(AnswerStrategyContant.COMPONENT)
@Slf4j
public class ComponentStrategy implements AnswerStrategy {

    @Autowired
    private ApplicationPluginDataService applicationPluginDataService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentNodeService componentNodeService;

    @Autowired
    private DialogueRecordService dialogueRecordService;

    @Autowired
    private LlmConversationMapper conversationMapper;

    @Autowired
    private LlmInfoService llmInfoService;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData stepData) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 执行组件获取结果
        AppConfigListParam param = new AppConfigListParam();
        param.setApplicationId(applicationInfo.getApplicationId());
        Result<List<ApplicationPluginData>> dataList = applicationPluginDataService.getApplicationPluginDataList(param);
        if (CollectionUtils.isEmpty(dataList.getData())) {
            // 未配置插件
            stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return stepData;
        }
        List<ApplicationPluginData> data = dataList.getData();
        // 只执行四个插件，并获取结果
        ThreadPoolExecutor pool = ExecutorBuilder.create().setCorePoolSize(4).setMaxPoolSize(4).setWorkQueue(new LinkedBlockingDeque<>()).build();
        List<Future<String>> futures = data.subList(0, 4).stream().map(pluginData -> pool.submit(() -> {
            // 查询到开始节点的变量名 根据对话记录填充变量 放在json里面
            RunComponentNodeParam nodeParam = getRunComponentNodeParam(pluginData, dto.getConversationId());
            // 获取组件结果
//            Result<String> result = componentService.run(nodeParam);
            Result<String> result = Result.success("");
            // 组件结果处理
            return result.getData();
        })).collect(Collectors.toList());

        String answer = "";
        for (Future<String> future : futures) {
            try {
                String s = future.get();
                if (StrUtil.isBlank(s)) {
                    continue;
                }
                answer = s;
            } catch (ExecutionException | InterruptedException e) {
                log.error("执行组件出错", e);
            }
        }
        if (StrUtil.isBlank(answer)) {
            stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return stepData;
        }
        // 封装结果
        stepData.setAnswer(answer);
        stepData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
        return stepData;
    }

    private RunComponentNodeParam getRunComponentNodeParam(ApplicationPluginData pluginData, String conversationId) {
        RunComponentNodeParam nodeParam = new RunComponentNodeParam();
        nodeParam.setComponentId(pluginData.getPluginId());
        List<MetaParam> metaParams = componentNodeService.selectParamByComponentId(pluginData.getPluginId(), ComponentNodeEnum.START, DirectionEnum.OUTPUT);
        ComponentNode componentNode = componentNodeService.selectByNodeId(metaParams.get(0).getNodeId());
        String nodeName = componentNode.getNodeName();
        // 大模型根据参数名称，从历史对话记录中总结参数值
        LambdaEsQueryWrapper<LlmConversation> wrapper = EsWrappers.lambdaQuery(LlmConversation.class)
                .eq(LlmConversation::getConversationId, conversationId)
                .orderByDesc(LlmConversation::getCreateTime)
                .limit(1);
        LlmConversation llmConversation = conversationMapper.selectOne(wrapper);
        List<DialogueMessage> messageList = llmConversation.getMessageList();
        List<String> variable = metaParams.stream().map(MetaParam::getVariable).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put(nodeName, getValue(variable, messageList));
        nodeParam.setInputs(JSONUtil.parseObj(map));
        return nodeParam;
    }

    /**
     * 从历史对话中获取参数值
     *
     * @param variable
     * @param messageList
     * @return
     */
    private JSONObject getValue(List<String> variable, List<DialogueMessage> messageList) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationInfo.getModelId());
        String generate = llmStrategy.generate("根据文本信息，为下列变量名填充合适的变量值，如果没有合适的值，则填充空字符串\"\"，以json格式输出。变量名："
                + String.join(",", variable) +
                " 文本内容：" + messageList.stream().map(message -> message.getQuestion() + " " + message.getContent()).collect(Collectors.joining("；")), new ArrayList<>(), new StepEntity(), false);
        return JSONUtil.parseObj(generate);
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        return AnswerStrategy.super.getContent(dto, contextList);
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        return AnswerStrategy.super.sourceAnswer(step, stepMap);
    }

    @Override
    public List<SourceAnswerResult> notAnswer(List<DialogueStep> dialogueSteps, boolean interceptFlag) {
        return AnswerStrategy.super.notAnswer(dialogueSteps, interceptFlag);
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerStrategy.super.answerRef(dialogueId, contextList);
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        return AnswerStrategy.super.getScoreData(scoreDataParam);
    }
}
