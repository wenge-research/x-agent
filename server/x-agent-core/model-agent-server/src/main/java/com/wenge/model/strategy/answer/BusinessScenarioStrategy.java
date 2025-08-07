package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.BusinessScenarioList;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.service.BusinessScenarioListService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.LlmGenerateUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service(AnswerStrategyContant.BUSINESS_SCENARIO)
@Slf4j
public class BusinessScenarioStrategy implements AnswerStrategy{

    @Autowired
    private LlmGenerateUtil llmGenerateUtil;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private BusinessScenarioListService businessScenarioService;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setStep(BUSINESS_SCENARIO_STEP);
        step.setPrompt(dto.getContentTemp());
        try {
            MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

            // 没有开启业务场景，则跳过
            if (!YesNoEnum.YES.getName().equals(applicationInfo.getBusinessScenarioEnable())) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return answerData;
            }
            contextList.add(step);

            // 获取业务场景 根据应用ID
            List<String> businessScenarioList = answerUtils.getBusinessScenarioList(applicationInfo.getApplicationId(), BUSINESS_SCENARIO_STEP);

            //使用和【讨论话题】一样的指令
            String subjectPrompt = applicationInfo.getSubjectPrompt();

            // 没有配置主题策略，则跳过
            if (StringUtils.isBlank(subjectPrompt) || CollectionUtils.isEmpty(businessScenarioList)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(BUSINESS_SCENARIO_NO_DATA);
                return answerData;
            }

            // 调用大模型，找到哪个场景
            String generateResult = generateResult(applicationInfo, dto, step, businessScenarioList);

            BusinessScenarioList businessScenario = getProcessing(generateResult, answerData, step, businessScenarioList);
            // 没有找到对应场景，则跳过
            if (null == businessScenario) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(generateResult + FIELD_SEPARATOR + BUSINESS_SCENARIO_NO_SCENE);
                return answerData;
            }

            String talkContent = businessScenario.getTalkContent();
            if (StringUtils.isBlank(talkContent)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(BUSINESS_SCENARIO_NO_WAY + FIELD_SEPARATOR + BUSINESS_SCENARIO_NO_WAY);
                return answerData;
            }else{
                answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                step.setResult(BUSINESS_SCENARIO_ANSWER + FIELD_SEPARATOR + talkContent);
                answerData.setAnswer(talkContent);
                //查询触发的业务场景是否有下级列表展示
                QueryWrapper queryWrapper = QueryWrapper.create();
                queryWrapper.where(" application_id = '" + applicationInfo.getApplicationId() + "'");
                queryWrapper.and(" business_scenari_pid = '" + businessScenario.getBusinessScenariId() + "'");
                queryWrapper.and(" delete_flag = 0 ");
                queryWrapper.and(" show_flag = '是' ");
                dto.setBusinessScenario(businessScenario);
                dto.setBusinessScenarioLists(businessScenarioService.getMapper().selectListByQuery(queryWrapper));
                dto.setBusinessScenarioScript(applicationInfo.getBusinessScenarioScript());
                dto.setBusinessScenarioUploadPicStatus(businessScenario.getBusinessScenarioUploadPicStatus());
                dto.getAnswerConsumer().accept(answerData);
                return answerData;
            }
        } catch (Exception e) {
            log.error("BusinessScenarioStrategy error:{}", e.getMessage(), e);
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            step.setResult(ERROR_STEP + ":" + e.getMessage());
        }
        return answerData;
    }

    /**
     * 提取场景号，解析器
     *
     * @param result
     * @return
     */
    private Integer indexParser(String result) {
        int index = -1;
        if (StringUtils.isNotBlank(result)) {
            if (result.contains("```")) {
                result = AnswerUtils.dealLLmJson(result);
            }
            result = result.replace("\n", StringConstant.BLANK).replace("\t", StringConstant.BLANK).replace("\t", StringConstant.BLANK);
            if (JSONUtil.isTypeJSONObject(result)) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (jsonObject.containsKey("场景")) {
                    String scene = jsonObject.getString("场景");
                    String regex = "【? *场景 *(\\d+)】?";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(scene);

                    if (matcher.find()) {
                        index = Integer.parseInt(matcher.group(1));
                        index = index - 1;
                    }
                }
            }
        }
        return index;
    }

    /**
     * 讨论话题，使用大模型判断话题
     *
     * @param applicationInfo
     * @param dto
     * @param step
     * @return
     */
    private String generateResult(ApplicationInfo applicationInfo, DialogueByStreamParam dto, StepEntity step, List<String> subjectTalkList) {
        String subjectPrompt = applicationInfo.getSubjectPrompt();
        String date = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy年MM月dd日");

        // 封装prompt
        subjectPrompt = subjectPrompt.
                replace(DATE_PLACEHOLDER, date)
                .replace(NUM_PLACEHOLDER, subjectTalkList.size() + StringConstant.BLANK);
        StringBuilder sb = new StringBuilder();
        int index = 1;
        // talk 格式为 话题:策略方法名 例如：在讨论学区:schoolMap
        for (String talk : subjectTalkList) {
            sb.append("\n\t【场景").append(index++).append("】：").append(talk.toString());
        }
        subjectPrompt = subjectPrompt.replace(SCENES_PLACEHOLDER, sb.toString());

        String question = dto.getQuestion();
        question = AnswerUtils.questionMark(question);
        subjectPrompt = subjectPrompt.replace(QUESTION_PLACEHOLDER, question);
        // 调用大模型，找到哪个场景
//        System.out.println("==>>>>>=" + generateResult);
        cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
        stepParam.set(LLM_JSON_FLAG, StringConstant.ONE);
        step.setParam(stepParam);
        return llmGenerateUtil.generate(subjectPrompt, null, step, false);
    }

    /**
     * 获取处理方式
     */
    private BusinessScenarioList getProcessing(String generateResult, AnswerStepData answerData, StepEntity step, List<String> businessScenarioList) {
        // 其他场景，则跳过
        if (generateResult.contains("其他场景")) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return null;
        }
        Integer indexs = indexParser(generateResult);
        step.setResult(step.getResult() + "==>" + indexs);
        // 没有找到场景，则跳过
        if (-1 == indexs) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return null;
        }

        String subject = businessScenarioList.get(indexs);

        // 没有找到处理方式，则跳过
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(" application_id = '" + applicationInfo.getApplicationId() + "'");
        queryWrapper.and(" hit_keywords = '" + subject + "'");
        queryWrapper.and(" delete_flag = 0 ");
        queryWrapper.and(" show_flag = '是' ");
        final List<BusinessScenarioList> businessScenarioLists = businessScenarioService.getMapper().selectListByQuery(queryWrapper);
        if (null == businessScenarioLists || CollectionUtils.isEmpty(businessScenarioLists)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return null;
        }
        return businessScenarioLists.get(0);
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非讨论话题的结果
        if (null == step || !step.getStep().equals(SUBJECT_TALK_REF)) {
            return dataList;
        }
        DialogueStep primaryData = stepMap.get(SUBJECT_STEP);
        SourceAnswerResult source = new SourceAnswerResult();
        source.setRoute(ListUtil.toList(AnswerStrategyContant.SUBJECT_STEP));
        String result = null == primaryData.getResult() ? StringConstant.BLANK : primaryData.getResult().toString();
        source.setText(result);
        dataList.add(source);
        return dataList;
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, SUBJECT_TALK, SUBJECT_STEP, SUBJECT_TALK_REF);
    }

}
