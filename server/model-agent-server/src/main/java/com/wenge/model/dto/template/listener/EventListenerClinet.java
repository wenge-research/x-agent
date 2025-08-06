package com.wenge.model.dto.template.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.dto.param.ApplicationEvaluationParamAsync;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.template.DatasetDTO;
import com.wenge.model.entity.*;
import com.wenge.model.mapper.ApplicationDatasetMapper;
import com.wenge.model.mapper.ApplicationEvaluationMapper;
import com.wenge.model.mapper.ApplicationInfoMapper;
import com.wenge.model.service.*;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.JsonUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.wenge.model.entity.table.ApplicationDatasetTableDef.APPLICATION_DATASET;
import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;
import static com.wenge.model.entity.table.LlmInfoTableDef.LLM_INFO;

@Component
@EnableAsync
@Slf4j
public class EventListenerClinet {
    @Autowired
    private ApplicationDatasetMapper applicationDatasetMapper;
    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;
    @Autowired
    private AnswerUtils answerUtils;
    @Autowired
    private LlmInfoService llmInfoService;
    @Autowired
    private ApplicationDatasetService applicationDatasetService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEvaluationMapper applicationEvaluationMapper;
    @Autowired
    private DialogueServiceImpl dialogueServiceImpl;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private KnowledgeInfoService knowledgeInfoService;
    @Async
    @EventListener
    public void onApplicationEvent1(ApplicationEvaluationParamAsync event) {
        long startTime = System.currentTimeMillis();
        ApplicationEvaluationParamAsync applicationEvaluationParam = (ApplicationEvaluationParamAsync) event;
        Wrappers<Object> wrappers1 = Wrappers.init()//makeType
                .and(applicationEvaluationParam.getDatasetId() != null, APPLICATION_DATASET.DATASET_ID.eq(applicationEvaluationParam.getDatasetId()));
        List<ApplicationDataset> applicationDataset = applicationDatasetMapper.selectListByQuery(wrappers1);
        try {
                int number2=0;//ai响应出错会有3次重试机会
                int totalSize = applicationDataset.size();//总数量
                int pici=(totalSize/5)+1;//批次数量
                String answerConsumer = applicationEvaluationParam.getCueWord();
                QueryWrapper wrapper = Wrappers.init()
                        //.and(org.apache.commons.lang3.StringUtils.isNotBlank(applicationEvaluationParam.getApplicationCode()), APPLICATION_INFO.APPLICATION_CODE.eq(applicationEvaluationParam.getApplicationCode()))
                        .and(StringUtils.isNotEmpty(applicationEvaluationParam.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationEvaluationParam.getApplicationId()));
                List<ApplicationInfo> listApp = applicationInfoMapper.selectListByQuery(wrapper);
                ApplicationInfo applicationInfo = new ApplicationInfo();
                if (CollectionUtils.isNotEmpty(listApp) && !listApp.isEmpty()) {
                    applicationInfo = listApp.get(0);
                }
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                //调用智能体生成答案
                if (CollectionUtils.isNotEmpty(applicationDataset)) {
                    Iterator<ApplicationDataset> iterator=applicationDataset.iterator();
                    while (iterator.hasNext()) {
                        ApplicationDataset appDataset = iterator.next();
                        //封装数据
                        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                        DialogueByStreamParam dialogueByStreamParam = new DialogueByStreamParam();
                        dialogueByStreamParam.setClientId(UUID.randomUUID().toString());
                        dialogueByStreamParam.setClientType("PC");
                        dialogueByStreamParam.setApplicationId(applicationInfo.getApplicationId());
                        List<KnowledgeInfo> knowledgeInfos = knowledgeInfoService.effectiveKnowledge(applicationInfo.getApplicationId());
                        List<String> knowledgeIdList = new ArrayList<>();
                        for (KnowledgeInfo knowledgeInfo : knowledgeInfos) {
                            knowledgeIdList.add(knowledgeInfo.getKnowledgeId());
                        }
                        dialogueByStreamParam.setKnowledgeIdList(knowledgeIdList);
                        dialogueByStreamParam.setContent(appDataset.getInput());
                        dialogueByStreamParam.setQuestion(appDataset.getInput());
                        Conversation conversation = new Conversation();
                        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
                        if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                            OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                            if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                                conversation.setTenantId(tokenUserInfo.getTenantId());
                            }
                            if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                                conversation.setCreateUser(oauthUser.getAccountName());
                                conversation.setUpdateUser(oauthUser.getAccountName());
                            }
                        }
                        conversation.setName(appDataset.getInput());
                        conversation.setCreateTime(DateUtil.getCurrentTime());
                        conversation.setUpdateTime(DateUtil.getCurrentTime());
                        conversation.setConversationId(IdUtil.simpleUUID());
                        dialogueByStreamParam.setConversationId(conversation.getConversationId());
                        conversationService.save(conversation);
                        dialogueServiceImpl.dialogueByStreamEvaluation(dialogueByStreamParam);
                        String answer = dialogueByStreamParam.getAnswer();
                        answer=answer.replace(" ", "").replace("\n", "").replace("\r", "");
                        appDataset.setOutput(answer);
                    }
                }
                // 获取模型信息
                applicationInfo = new ApplicationInfo();
                // 获取模型信息
                Wrappers<Object> wrappers2 = Wrappers.init()
                        .where(LLM_INFO.MODEL_ID.eq(applicationEvaluationParam.getLlmInfoId()))
                        .limit(1);
                LlmInfo llmInfo11 = llmInfoService.getOne(wrappers2);
                if (null == llmInfo11) {
                    log.warn("未找到模型：{}", applicationEvaluationParam.getLlmInfoId());
                }
                applicationInfo.setLlmInfo(llmInfo11);
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                //分批次测评
                pici=(totalSize/5)+1;
                for (int i=1; i<=pici;i++) {
                    StringBuilder totalString = new StringBuilder();
                    for (int j=(i-1)*5; j<i*5;j++) {
                        if(j>=totalSize){
                            break;
                        }
                        totalString.append("question_id=").append(applicationDataset.get(j).getQuestionId()).append(",")
                                .append("session_id=").append(applicationDataset.get(j).getSessionId()).append(",")
                                .append("query_id=").append(applicationDataset.get(j).getQueryId()).append(",")
                                .append("input=").append(applicationDataset.get(j).getInput()).append(",")
                                .append("output=").append(applicationDataset.get(j).getOutput()).append(",")
                                .append("reference_output=").append(applicationDataset.get(j).getReferenceOutput()).append(",");
                    }
                    //拼接输出格式
                    totalString.append(answerConsumer).append("请严格按照Json格式\n" +
                            "[{\n" +
                            "    \"question_id\": \"\",\n" +
                            "    \"query_id\": \"\",\n" +
                            "    \"session_id\": \"\",\n" +
                            "    \"input\": \"\",\n" +
                            "    \"reference_output\": \"\",\n" +
                            "    \"output\": \"\",\n" +
                            "    \"score\": \"\",\n" +
                            "    \"score_reason\": \"\"\n" +
                            "}]\n" +
                            "返回");
//                    totalString.append(answerConsumer).append(",生成评分,按100分制打分,持续相同处理,响应json格式严格不变!!!");
                    // 分批生成答案，评测
                    if (BeanUtil.isNotEmpty(llmInfo11)) {
                        // 模型生成答案
                        String bigModeString = answerUtils.getGenerateCommon(null, totalString.toString(), new StepEntity(), null, null);
                        //log.info("第"+i+"批次,测评ai响应数据2: " + bigModeString);
                        if(bigModeString.trim().equals("无法回答")||StringUtils.isEmpty(bigModeString)){
                            if(number2<3){
                                i--;
                            }
                            number2++;
                            continue;
                        }
                        int count = 0;
                        int index = 0;
                        String sub="]";
                        // 使用 indexOf 方法查找 sub 在 str 中的每次出现
                        while ((index = bigModeString.indexOf(sub, index)) != -1) {
                            count++;
                            index += sub.length(); // 移动索引到当前找到的子字符串后面
                            if(count>1) break;
                        }
                        if(count>1){
                            if(number2<3){
                                i--;
                            }
                            number2++;
                            continue;
                        }
                        int startIndex1 = bigModeString.indexOf("[");
                        int endIndex1 = bigModeString.lastIndexOf("]");
                        if (startIndex1 != -1 && endIndex1 != -1) { // 确保找到了关键字
                            bigModeString = bigModeString.substring(startIndex1, endIndex1 + 1);
                        } else {
                            Result.fail("大模式出错");
                        }
                        bigModeString=bigModeString.replace(" ", "").replace("\n", "").replace("\r", "");
                        List<DatasetDTO> datasetDTOS = JsonUtil.jsonToList(bigModeString, DatasetDTO.class);
                        Iterator<ApplicationDataset> iterator2 = applicationDataset.iterator();
                        while (iterator2.hasNext()) {
                            ApplicationDataset data_DTO = iterator2.next();
                            for (DatasetDTO dto : datasetDTOS) {
                                if (data_DTO.getQuestionId().equals(dto.getQuestion_id())) {
                                    data_DTO.setScore(dto.getScore());
                                    data_DTO.setScoreReason(dto.getScore_reason());
                                }
                            }
                        }
                    }
                }
                applicationDatasetService.updateBatch(applicationDataset);
                ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
                BeanUtil.copyProperties(applicationEvaluationParam, applicationEvaluation);
                //数据隔离
                TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
                if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                    OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                    if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                        applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                        applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                        applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getDeptId())) {
                        applicationEvaluation.setDeptId(oauthUser.getDeptId());
                    }
                    if (ObjectUtil.isNotEmpty(oauthUser.getId())) {
                        applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
                    }
                }
                //计算测评成比例
                int i=0;
                while (i<totalSize) {
                    if(StringUtils.isNotEmpty(applicationDataset.get(i).getInput())&&StringUtils.isNotEmpty(applicationDataset.get(i).getScoreReason())){
                        i++;
                    }
                }
                float success_rate=Float.valueOf(i)/ Float.valueOf(totalSize);
                long endTime = System.currentTimeMillis();
                StringBuilder s = new StringBuilder(String.valueOf((endTime - startTime) / 1000)).append("s");
                applicationEvaluation.setId(applicationEvaluationParam.getId());
                applicationEvaluation.setRunningFrequently(String.valueOf(s));
                //applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
                applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
                applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
                applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
                applicationEvaluation.setEvaluationStatus(1);
                applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
                applicationEvaluation.setDeleteFlag(0);
                applicationEvaluation.setRunningFrequently(String.valueOf(s));
                applicationEvaluation.setTotalNumber(totalSize);
                applicationEvaluation.setSuccessNumber(i);
                applicationEvaluation.setSuccessRate(success_rate*100+"%");
                applicationEvaluationMapper.update(applicationEvaluation);
        } catch (Exception e) {
            log.error("解析失败，原因: " + e.getMessage());
            int totalSize=applicationDataset.size();
            //计算测评成比例
            int i=0;
            Iterator<ApplicationDataset> iterator = applicationDataset.iterator();
            while (iterator.hasNext()) {
                ApplicationDataset next = iterator.next();
                if(StringUtils.isNotEmpty(next.getInput())&&StringUtils.isNotEmpty(next.getScoreReason())){
                    i++;
                }else {
                    next.setErrorMessage("ai响应数据格式不对，导致解析失败");
                }
            }
            float success_rate=Float.valueOf(i)/ Float.valueOf(totalSize);
            long endTime = System.currentTimeMillis();
            StringBuilder s = new StringBuilder(String.valueOf((endTime - startTime) / 1000)).append("s");
            ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
            BeanUtil.copyProperties(applicationEvaluationParam, applicationEvaluation);
            //数据隔离
            TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
            if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                    applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
                }
                if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                    applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                    applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
                }
                if (StringUtils.isNotEmpty(oauthUser.getDeptId())) {
                    applicationEvaluation.setDeptId(oauthUser.getDeptId());
                }
                if (ObjectUtil.isNotEmpty(oauthUser.getId())) {
                    applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
                }
            }
            applicationDatasetService.updateBatch(applicationDataset);
            //StringBuilder s = new StringBuilder(10).append("s");
            //applicationEvaluation.setRunningFrequently(String.valueOf(s));
            applicationEvaluation.setId(applicationEvaluationParam.getId());
            applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
            applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
            applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
            applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
            applicationEvaluation.setEvaluationStatus(3);
            applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
            applicationEvaluation.setDeleteFlag(0);
            applicationEvaluation.setRunningFrequently(String.valueOf(s));
            applicationEvaluation.setTotalNumber(totalSize);
            applicationEvaluation.setSuccessNumber(i);
            applicationEvaluation.setSuccessRate(success_rate*100+"%");
            applicationEvaluationMapper.update(applicationEvaluation);
        }
    }
    /*@Async
    @EventListener
    public void onApplicationEvent1(ApplicationEvaluationParamAsync event) {
        long startTime = System.currentTimeMillis();
        ApplicationEvaluationParamAsync applicationEvaluationParam = (ApplicationEvaluationParamAsync) event;
        Wrappers<Object> wrappers1 = Wrappers.init()//makeType
                .and(applicationEvaluationParam.getDatasetId() != null, APPLICATION_DATASET.DATASET_ID.eq(applicationEvaluationParam.getDatasetId()));
        List<ApplicationDataset> applicationDataset = applicationDatasetMapper.selectListByQuery(wrappers1);
        try {
                int number1=0;//ai响应出错会有3次重试机会
                int number2=0;//ai响应出错会有3次重试机会
                int totalSize = applicationDataset.size();//总数量
                int pici=(totalSize/5)+1;//批次数量
                String answerConsumer = applicationEvaluationParam.getCueWord();
                QueryWrapper wrapper = Wrappers.init()
                        //.and(org.apache.commons.lang3.StringUtils.isNotBlank(applicationEvaluationParam.getApplicationCode()), APPLICATION_INFO.APPLICATION_CODE.eq(applicationEvaluationParam.getApplicationCode()))
                        .and(StringUtils.isNotEmpty(applicationEvaluationParam.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationEvaluationParam.getApplicationId()));
                List<ApplicationInfo> listApp = applicationInfoMapper.selectListByQuery(wrapper);
                ApplicationInfo applicationInfo = new ApplicationInfo();
                if (CollectionUtils.isNotEmpty(listApp) && !listApp.isEmpty()) {
                    applicationInfo = listApp.get(0);
                }
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                //分批次调用大模型生成答案
                if (CollectionUtils.isNotEmpty(applicationDataset)) {
                    for (int i=1; i<=pici;i++) {
                        StringBuilder questionBuilder = new StringBuilder();
                        for (int j=(i-1)*5; j<i*5;j++) {
                            if(j>=totalSize){
                                break;
                            }
                            questionBuilder.append("question_id=").append(applicationDataset.get(j).getQuestionId()).append(",")
                                    .append("input=").append(applicationDataset.get(j).getInput());
                        }
                        questionBuilder.append("#角色 你是一个问答专家，擅长对用户传入的问题生成答案.#输入：{{input}} 代表问题{{output}}代表答案，请严格按照Json格式[{\\\"question_id\\\":\\\"\\\",\\\"input\\\":\\\"\\\",\\\"output\\\":\\\"\\\"}}]返回").append(",持续相同处理,响应json格式严格不变！！！");;
                        String generateCommon = answerUtils.getGenerateCommon(null, questionBuilder.toString(), new StepEntity(), null, null);
                        //dialogueServiceImpl.dialogueByStream()
                        //log.info("第"+i+"批次,测评ai响应数据1: " + generateCommon);
                        if(generateCommon.trim().equals("无法回答")){
                            if(number1<3){
                                i--;
                            }
                            number1++;
                            continue;
                        }
                        int count = 0;
                        int index = 0;
                        String sub="]";
                        // 使用 indexOf 方法查找 sub 在 str 中的每次出现
                        while ((index = generateCommon.indexOf(sub, index)) != -1) {
                            count++;
                            index += sub.length(); // 移动索引到当前找到的子字符串后面
                            if(count>1) break;
                        }
                        if(count>1){
                            if(number1<3){
                                i--;
                            }
                            number1++;
                            continue;
                        }
                        int startIndex = generateCommon.indexOf("[");
                        int endIndex = generateCommon.lastIndexOf("]");
                        if (startIndex != -1 && endIndex != -1) { // 确保找到了关键字
                            generateCommon = generateCommon.substring(startIndex, endIndex + 1);
                        } else {
                            Result.fail("大模式出错");
                        }
                        generateCommon=generateCommon.replace(" ", "").replace("\n", "").replace("\r", "");
                        List<DatasetDTO> datasetDTOS = JsonUtil.jsonToList(generateCommon, DatasetDTO.class);
                        Iterator<ApplicationDataset> iterator2 = applicationDataset.iterator();
                        while (iterator2.hasNext()) {
                            ApplicationDataset data_DTO = iterator2.next();
                            for (DatasetDTO dto : datasetDTOS) {
                                if (data_DTO.getQuestionId().equals(dto.getQuestion_id())) {
                                    data_DTO.setOutput(dto.getOutput());
                                }
                            }
                        }
                    }
                }
                // 获取模型信息
                applicationInfo = new ApplicationInfo();
                // 获取模型信息
                Wrappers<Object> wrappers2 = Wrappers.init()
                        .where(LLM_INFO.MODEL_ID.eq(applicationEvaluationParam.getLlmInfoId()))
                        .limit(1);
                LlmInfo llmInfo11 = llmInfoService.getOne(wrappers2);
                if (null == llmInfo11) {
                    log.warn("未找到模型：{}", applicationEvaluationParam.getLlmInfoId());
                }
                applicationInfo.setLlmInfo(llmInfo11);
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                //分批次测评
                pici=(totalSize/5)+1;
                for (int i=1; i<=pici;i++) {
                    StringBuilder totalString = new StringBuilder();
                    for (int j=(i-1)*5; j<i*5;j++) {
                        if(j>=totalSize){
                            break;
                        }
                        totalString.append("question_id=").append(applicationDataset.get(j).getQuestionId()).append(",")
                                .append("session_id=").append(applicationDataset.get(j).getSessionId()).append(",")
                                .append("query_id=").append(applicationDataset.get(j).getQueryId()).append(",")
                                .append("input=").append(applicationDataset.get(j).getInput()).append(",")
                                .append("output=").append(applicationDataset.get(j).getOutput()).append(",")
                                .append("reference_output=").append(applicationDataset.get(j).getReferenceOutput()).append(",");
                    }
                    totalString.append(answerConsumer).append(",生成评分,按100分制打分,持续相同处理,响应json格式严格不变!!!");
                    // 分批生成答案，评测
                    if (BeanUtil.isNotEmpty(llmInfo11)) {
                        // 模型生成答案
                        String bigModeString = answerUtils.getGenerateCommon(null, totalString.toString(), new StepEntity(), null, null);
                        //log.info("第"+i+"批次,测评ai响应数据2: " + bigModeString);
                        if(bigModeString.trim().equals("无法回答")){
                            if(number2<3){
                                i--;
                            }
                            number2++;
                            continue;
                        }
                        int count = 0;
                        int index = 0;
                        String sub="]";
                        // 使用 indexOf 方法查找 sub 在 str 中的每次出现
                        while ((index = bigModeString.indexOf(sub, index)) != -1) {
                            count++;
                            index += sub.length(); // 移动索引到当前找到的子字符串后面
                            if(count>1) break;
                        }
                        if(count>1){
                            if(number2<3){
                                i--;
                            }
                            number2++;
                            continue;
                        }
                        int startIndex1 = bigModeString.indexOf("[");
                        int endIndex1 = bigModeString.lastIndexOf("]");
                        if (startIndex1 != -1 && endIndex1 != -1) { // 确保找到了关键字
                            bigModeString = bigModeString.substring(startIndex1, endIndex1 + 1);
                        } else {
                            Result.fail("大模式出错");
                        }
                        bigModeString=bigModeString.replace(" ", "").replace("\n", "").replace("\r", "");
                        List<DatasetDTO> datasetDTOS = JsonUtil.jsonToList(bigModeString, DatasetDTO.class);
                        Iterator<ApplicationDataset> iterator2 = applicationDataset.iterator();
                        while (iterator2.hasNext()) {
                            ApplicationDataset data_DTO = iterator2.next();
                            for (DatasetDTO dto : datasetDTOS) {
                                if (data_DTO.getQuestionId().equals(dto.getQuestion_id())) {
                                    data_DTO.setScore(dto.getScore());
                                    data_DTO.setScoreReason(dto.getScore_reason());
                                }
                            }
                        }
                    }
                }
                applicationDatasetService.updateBatch(applicationDataset);
                ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
                BeanUtil.copyProperties(applicationEvaluationParam, applicationEvaluation);
                //数据隔离
                TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
                if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                    OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                    if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                        applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                        applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                        applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getDeptId())) {
                        applicationEvaluation.setDeptId(oauthUser.getDeptId());
                    }
                    if (ObjectUtil.isNotEmpty(oauthUser.getId())) {
                        applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
                    }
                }
                //计算测评成比例
                int i=0;
                while (i<totalSize) {
                    if(StringUtils.isNotEmpty(applicationDataset.get(i).getInput())&&StringUtils.isNotEmpty(applicationDataset.get(i).getScoreReason())){
                        i++;
                    }
                }
                float success_rate=Float.valueOf(i)/ Float.valueOf(totalSize);
                long endTime = System.currentTimeMillis();
                StringBuilder s = new StringBuilder(String.valueOf((endTime - startTime) / 1000)).append("s");
                applicationEvaluation.setId(applicationEvaluationParam.getId());
                applicationEvaluation.setRunningFrequently(String.valueOf(s));
                //applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
                applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
                applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
                applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
                applicationEvaluation.setEvaluationStatus(1);
                applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
                applicationEvaluation.setDeleteFlag(0);
                applicationEvaluation.setRunningFrequently(String.valueOf(s));
                applicationEvaluation.setTotalNumber(totalSize);
                applicationEvaluation.setSuccessNumber(i);
                applicationEvaluation.setSuccessRate(success_rate*100+"%");
                applicationEvaluationMapper.update(applicationEvaluation);
        } catch (Exception e) {
            log.error("解析失败，原因: " + e.getMessage());
            int totalSize=applicationDataset.size();
            //计算测评成比例
            int i=0;
            Iterator<ApplicationDataset> iterator = applicationDataset.iterator();
            while (iterator.hasNext()) {
                ApplicationDataset next = iterator.next();
                if(StringUtils.isNotEmpty(next.getInput())&&StringUtils.isNotEmpty(next.getScoreReason())){
                    i++;
                }else {
                    next.setErrorMessage("ai响应数据格式不对，导致解析失败");
                }
            }
            float success_rate=Float.valueOf(i)/ Float.valueOf(totalSize);
            long endTime = System.currentTimeMillis();
            StringBuilder s = new StringBuilder(String.valueOf((endTime - startTime) / 1000)).append("s");
            ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
            BeanUtil.copyProperties(applicationEvaluationParam, applicationEvaluation);
            //数据隔离
            TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
            if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                    applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
                }
                if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                    applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                    applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
                }
                if (StringUtils.isNotEmpty(oauthUser.getDeptId())) {
                    applicationEvaluation.setDeptId(oauthUser.getDeptId());
                }
                if (ObjectUtil.isNotEmpty(oauthUser.getId())) {
                    applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
                }
            }
            applicationDatasetService.updateBatch(applicationDataset);
            //StringBuilder s = new StringBuilder(10).append("s");
            //applicationEvaluation.setRunningFrequently(String.valueOf(s));
            applicationEvaluation.setId(applicationEvaluationParam.getId());
            applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
            applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
            applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
            applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
            applicationEvaluation.setEvaluationStatus(3);
            applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
            applicationEvaluation.setDeleteFlag(0);
            applicationEvaluation.setRunningFrequently(String.valueOf(s));
            applicationEvaluation.setTotalNumber(totalSize);
            applicationEvaluation.setSuccessNumber(i);
            applicationEvaluation.setSuccessRate(success_rate*100+"%");
            applicationEvaluationMapper.update(applicationEvaluation);
        }
    }*/
    /*@Async
    @EventListener
    public void onApplicationEvent(ApplicationEvaluationParamAsync event) {
        try {
            if (event instanceof ApplicationEvaluationParamAsync) {
                ApplicationEvaluationParamAsync applicationEvaluationParam = (ApplicationEvaluationParamAsync) event;
                long startTime = System.currentTimeMillis();
                Wrappers<Object> wrappers1 = Wrappers.init()//makeType
                        .and(applicationEvaluationParam.getDatasetId() != null, APPLICATION_DATASET.DATASET_ID.eq(applicationEvaluationParam.getDatasetId()));
                List<ApplicationDataset> applicationDataset = applicationDatasetMapper.selectListByQuery(wrappers1);
                StringBuilder stringBuilder = new StringBuilder();
                StringBuilder questionBuilder = new StringBuilder();
                if (CollectionUtils.isNotEmpty(applicationDataset)) {
                    for (ApplicationDataset appDataset : applicationDataset) {
                        questionBuilder.append("question_id=").append(appDataset.getQuestionId()).append(",")
                                .append("input=").append(appDataset.getInput());
                    }
                    questionBuilder.append("#角色 你是一个问答专家，擅长对用户传入的问题生成答案.#输入：{{input}} 代表问题{{output}}代表答案，请严格按照Json格式[{\\\"question_id\\\":\\\"\\\",\\\"input\\\":\\\"\\\",\\\"output\\\":\\\"\\\"}}]返回");

                }
                String answerConsumer = applicationEvaluationParam.getCueWord();
                QueryWrapper wrapper = Wrappers.init()
                        //.and(org.apache.commons.lang3.StringUtils.isNotBlank(applicationEvaluationParam.getApplicationCode()), APPLICATION_INFO.APPLICATION_CODE.eq(applicationEvaluationParam.getApplicationCode()))
                        .and(StringUtils.isNotEmpty(applicationEvaluationParam.getApplicationId()), APPLICATION_INFO.APPLICATION_ID.eq(applicationEvaluationParam.getApplicationId()));
                List<ApplicationInfo> listApp = applicationInfoMapper.selectListByQuery(wrapper);
                ApplicationInfo applicationInfo = new ApplicationInfo();
                if (CollectionUtils.isNotEmpty(listApp) && !listApp.isEmpty()) {
                    applicationInfo = listApp.get(0);
                }
                DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                String generateCommon = answerUtils.getGenerateCommon(null, questionBuilder.toString(), new StepEntity(), null, null);
                int startIndex = generateCommon.indexOf("[");
                int endIndex = generateCommon.lastIndexOf("]");
                if (startIndex != -1 && endIndex != -1) { // 确保找到了关键字
                    generateCommon = generateCommon.substring(startIndex, endIndex + 1);
                } else {
                    Result.fail("大模式出错");
                }
                long endTime = System.currentTimeMillis();
                List<DatasetDTO> datasetDTOS = JsonUtil.jsonToList(generateCommon, DatasetDTO.class);
                Iterator<ApplicationDataset> iterator = applicationDataset.iterator();
                while (iterator.hasNext()) {
                    ApplicationDataset datasetDTO = iterator.next();
                    for (DatasetDTO dto : datasetDTOS) {
                        if (datasetDTO.getQuestionId().equals(dto.getQuestion_id())) {
                            datasetDTO.setOutput(dto.getOutput());
                        }
                    }
                    stringBuilder.append(",").append("question_id=").append(datasetDTO.getQuestionId()).append(",")
                            .append("session_id=").append(datasetDTO.getSessionId()).append(",")
                            .append("query_id=").append(datasetDTO.getQueryId()).append(",")
                            .append("input=").append(datasetDTO.getInput()).append(",")
                            .append("output=").append(datasetDTO.getOutput()).append(",")
                            .append("reference_output=").append(datasetDTO.getReferenceOutput()).append(",");
                }
                stringBuilder.append(answerConsumer);
                // 获取模型信息
                LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationEvaluationParam.getLlmInfoId());
                if (null == llmStrategy) {
                    Result.fail("大模式为空");
                }
                // 生成答案
                if (BeanUtil.isNotEmpty(llmStrategy)) {
                    applicationInfo = new ApplicationInfo();
                    // 获取模型信息
                    Wrappers<Object> wrappers2 = Wrappers.init()
                            .where(LLM_INFO.MODEL_ID.eq(applicationEvaluationParam.getLlmInfoId()))
                            .limit(1);
                    LlmInfo llmInfo11 = llmInfoService.getOne(wrappers2);
                    if (null == llmInfo11) {
                        log.warn("未找到模型：{}", applicationEvaluationParam.getLlmInfoId());
                    }
                    applicationInfo.setLlmInfo(llmInfo11);
                    DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                    // 模型生成答案
                    String bigModeString = answerUtils.getGenerateCommon(null, stringBuilder.toString(), new StepEntity(), null, null);
                    int startIndex1 = bigModeString.indexOf("[");
                    int endIndex1 = bigModeString.lastIndexOf("]");
                    if (startIndex1 != -1 && endIndex1 != -1) { // 确保找到了关键字
                        bigModeString = bigModeString.substring(startIndex1, endIndex1 + 1);
                    } else {
                        Result.fail("大模式出错");
                    }
                    datasetDTOS = JsonUtil.jsonToList(bigModeString, DatasetDTO.class);
                    Iterator<ApplicationDataset> iterator2 = applicationDataset.iterator();
                    while (iterator2.hasNext()) {
                        ApplicationDataset datasetDTO = iterator2.next();
                        for (DatasetDTO dto : datasetDTOS) {
                            if (datasetDTO.getQuestionId().equals(dto.getQuestion_id())) {
                                datasetDTO.setScore(dto.getScore());
                                datasetDTO.setScoreReason(dto.getScore_reason());
                            }
                        }
                    }
                }
                applicationDatasetService.updateBatch(applicationDataset);
                ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
                BeanUtil.copyProperties(applicationEvaluationParam, applicationEvaluation);
                //数据隔离
                TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
                if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                    OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                    if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                        applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                        applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                        applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
                    }
                    if (StringUtils.isNotEmpty(oauthUser.getDeptId())) {
                        applicationEvaluation.setDeptId(oauthUser.getDeptId());
                    }
                    if (ObjectUtil.isNotEmpty(oauthUser.getId())) {
                        applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
                    }
                }
                StringBuilder s = new StringBuilder(String.valueOf((endTime - startTime) / 1000)).append("s");
                applicationEvaluation.setId(applicationEvaluationParam.getId());
                applicationEvaluation.setRunningFrequently(String.valueOf(s));
                //applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
                applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
                applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
                applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
                applicationEvaluation.setEvaluationStatus(1);
                applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
                applicationEvaluation.setDeleteFlag(0);
                applicationEvaluationMapper.update(applicationEvaluation);
            }
        }catch (Exception e) {
            ApplicationEvaluationParamAsync applicationEvaluationParam = (ApplicationEvaluationParamAsync) event;
            ApplicationEvaluation applicationEvaluation = new ApplicationEvaluation();
            BeanUtil.copyProperties(applicationEvaluationParam, applicationEvaluation);
            //数据隔离
            TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
            if (null != tokenUserInfo && ObjectUtil.isNotEmpty(tokenUserInfo) && ObjectUtil.isNotEmpty(tokenUserInfo.getId())) {
                OauthUser oauthUser = userService.getById(tokenUserInfo.getId());
                if (StringUtils.isNotEmpty(tokenUserInfo.getTenantId())) {
                    applicationEvaluation.setTenantId(tokenUserInfo.getTenantId());
                }
                if (StringUtils.isNotEmpty(oauthUser.getAccountName())) {
                    applicationEvaluation.setCreateUser(oauthUser.getAccountName());
                    applicationEvaluation.setUpdateUser(oauthUser.getAccountName());
                }
                if (StringUtils.isNotEmpty(oauthUser.getDeptId())) {
                    applicationEvaluation.setDeptId(oauthUser.getDeptId());
                }
                if (ObjectUtil.isNotEmpty(oauthUser.getId())) {
                    applicationEvaluation.setUserId(String.valueOf(oauthUser.getId()));
                }
            }
            //StringBuilder s = new StringBuilder(10).append("s");
            //applicationEvaluation.setRunningFrequently(String.valueOf(s));
            applicationEvaluation.setId(applicationEvaluationParam.getId());
            applicationEvaluation.setCreateTime(DateUtil.getCurrentTime());
            applicationEvaluation.setUpdateTime(DateUtil.getCurrentTime());
            applicationEvaluation.setApplicationId(applicationEvaluationParam.getApplicationId());
            applicationEvaluation.setEvaluationTime(DateUtil.getCurrentTime());
            applicationEvaluation.setEvaluationStatus(3);
            applicationEvaluation.setEvaluationRules(applicationEvaluationParam.getCueWord());
            applicationEvaluation.setDeleteFlag(0);
            applicationEvaluationMapper.update(applicationEvaluation);
        }
    }*/
}
