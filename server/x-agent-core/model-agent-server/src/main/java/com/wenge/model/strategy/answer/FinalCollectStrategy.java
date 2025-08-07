package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.AnswerRefParam;
import com.wenge.model.dto.param.ArticleFilterParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.LlmInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.TokenReducer;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.RearrangeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.oauth.constants.AppConfigContant.*;

@Service(AnswerStrategyContant.FINAL_COLLECT)
@Slf4j
public class FinalCollectStrategy implements AnswerStrategy {

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private Map<String, AnswerStrategy> answerStrategyMap;

    @Autowired
    private LlmInfoService llmInfoService;


    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setStep(AnswerStrategyContant.FINAL_COLLECT_STEP);

        try {
            // 设置 汇总所有知识库-原始数据、互联网数据
            contextList.add(step);

            // 获取重排后的知识库和联网数据
            List<String> topDocumentLibrary = getnCntents(dto, contextList, step, start);

            // 防止知识库内容超过模型的最大token限制
            topDocumentLibrary = trimTheKnowledgeBaseContent(topDocumentLibrary);

            step.setResult(topDocumentLibrary);

            // 获取答案
            fromDocumentLibrary(dto, contextList, topDocumentLibrary, answerData, start);
        } catch (Exception e) {
            log.error("FinalCollectStrategy error:{}", e.getMessage(), e);
            if (!StepStatusEnum.ANSWER_FINAL.equals(answerData.getStatus())) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(ERROR_STEP + ":" + e.getMessage());
            }
        }
        return answerData;
    }



    /**
     * 修剪知识库内容
     * @param topDocumentLibrary
     * @return
     */
    private List<String> trimTheKnowledgeBaseContent(List<String> topDocumentLibrary) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String trimFlag = getConfiguration(applicationInfo.getApplicationId(), TRIM_KNN_CONTENT_FLAG);
        if (!YesNoEnum.YES.getName().equals(trimFlag)) {
            return topDocumentLibrary;
        }

        llmInfoService.getActiveLLm(applicationInfo.getModelId());
        LlmInfo llmInfo = applicationInfo.getLlmInfo();
        // 如果为空就不走

        if (null != llmInfo && (llmInfo.getMaxNewTokens() != null || llmInfo.getMaxTokens() != null)) {
            int maxToken = 0;
            if (llmInfo.getMaxNewTokens() == null) {
                maxToken = llmInfo.getMaxTokens();
            } else if (llmInfo.getMaxTokens() == null) {
                maxToken = llmInfo.getMaxNewTokens();
            } else {
                maxToken = llmInfo.getMaxNewTokens() > llmInfo.getMaxTokens() ? llmInfo.getMaxNewTokens() : llmInfo.getMaxTokens();
            }
            log.debug("原始知识库：{}", topDocumentLibrary);
            TokenReducer bean = CoreContextProvider.getContext().getBean(TokenReducer.class);
            topDocumentLibrary = bean.reduceTokenList(topDocumentLibrary, maxToken);
            log.debug("修剪知识库：{}", topDocumentLibrary);
        }
        return topDocumentLibrary;
    }


    /**
     * 从文档库获取答案
     *
     * @param dto
     * @param contextList
     * @return
     */
    private String fromDocumentLibrary(DialogueByStreamParam dto, List<StepEntity> contextList, List<String> vectorLibrary, AnswerStepData answerData, long start) {
        if (CollectionUtils.isNotEmpty(vectorLibrary)) {
            // 大模型回答的步骤
            StepEntity modelStep = new StepEntity();
            modelStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            contextList.add(modelStep);
            modelStep.setStep(AnswerStrategyContant.FINAL_COLLECT_ANSWER);

            StringBuilder builder = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            if (CollectionUtils.isEmpty(vectorLibrary)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return StringConstant.BLANK;
            }

            // 获取评分top数据，封装知识库
            String textDetail;
            List<JSONObject> dataList = Lists.newArrayList();
            JSONObject articleContent;
            boolean networkFlag = false;
            for (int i = 0; i < vectorLibrary.size(); i++) {
                // 判断是否为网络知识库，只要有一个计算来源于网络
                networkFlag = networkFlag || vectorLibrary.get(i).contains(NETWORK_STRATEGY);
                String[] split = vectorLibrary.get(i).split(REARRANGE_SPLIT_CHAR);
                // 获取内容，格式为 内容[额外信息]
                textDetail = split[2];
                if (textDetail.contains(PREFIX_SYMBOL)) {
                    int indexOf = textDetail.lastIndexOf(PREFIX_SYMBOL);
                    textDetail = textDetail.substring(0, indexOf);
                }
                builder.append("\n「 【知识库").append(i + 1).append("】是： ").append(textDetail).append("」");
                builder2.append("\n").append(textDetail);
                articleContent = new JSONObject();
                articleContent.put("article_content", textDetail);
                articleContent.put("serial_number", i + 1);
                dataList.add(articleContent);
            }
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            String appPromptTemplate = applicationInfo.getPromptTemplate();
            String appSystemPrompt = applicationInfo.getSystemPrompt();

            // 构建prompt
            String according = StringConstant.BLANK;
            String systemPrompt = buildSystemPrompt(dataList, builder, vectorLibrary.size());
            if (StringUtils.isNotBlank(systemPrompt)) {
                String userSystem = getConfiguration(applicationInfo.getApplicationId(), LLM_PROMPT_USER_FINAL_COLLECT_STRATEGY);
                String question = StringUtils.isNotBlank(dto.getNewQuestion()) ? dto.getNewQuestion() : dto.getContentTemp();
                according = userSystem.replace(QUESTION_PLACEHOLDER, question);
            } else if (StringUtils.isNotBlank(appPromptTemplate) && appPromptTemplate.contains("{content}") || StringUtils.isNotBlank(appSystemPrompt) && appSystemPrompt.contains("{content}")) {
                according = AnswerUtils.buildPrompt(builder, dto.getContentTemp(), vectorLibrary.size());
            } else {
                if (StringUtils.isNotBlank(appSystemPrompt)) {
                    appSystemPrompt = appSystemPrompt
                            .replace(DATES_PLACEHOLDER, DateUtil.getCurrentDateCn())
                            .replace(WEEK_PLACEHOLDER, DateUtil.getCurrentWeekCn());
                    systemPrompt = appSystemPrompt.replace(DATE_PLACEHOLDER, DateUtil.getDateForPrompt()) + "\n========================以下是相关资料" + builder2.toString();
                }
                according = dto.getContentTemp();
                if (StringUtils.isNotBlank(appPromptTemplate)) {
                    appPromptTemplate = appPromptTemplate
                            .replace(DATES_PLACEHOLDER, DateUtil.getCurrentDateCn())
                            .replace(WEEK_PLACEHOLDER, DateUtil.getCurrentWeekCn());
                    according = appPromptTemplate + "\n" + according;
                }
            }
            cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
            // 大模型是否需要json格式化
            stepParam.set(LLM_JSON_FLAG, StringConstant.ONE);
            modelStep.setParam(stepParam);

            List<SourceAnswerResult> datas = initSourceAnswerResult(vectorLibrary, contextList, answerStrategyMap);
            // 设置引用文章来源
            List<AnswerRefParam> refList = Lists.newArrayList();
            for (int i = 0; i < datas.size(); i++) {
                AnswerRefParam answerRefParam = new AnswerRefParam();
                answerRefParam.setUrl(datas.get(i).getFileLink());
                answerRefParam.setTranslationUrlMap(datas.get(i).getTranslationUrlMap());
                answerRefParam.setFileLanguageList(datas.get(i).getFileLanguageList());
                answerRefParam.setContent(datas.get(i).getText());
                answerRefParam.setTitle(datas.get(i).getFileName());
                answerRefParam.setIndex(i);
                answerRefParam.setModule(datas.get(i).getRoute());
                answerRefParam.setSourceType(datas.get(i).getSourceType());
                refList.add(answerRefParam);
            }
            // 根据 title 去重
            refList = refList.stream().distinct().collect(Collectors.toList());
            answerData.setRefList(refList);

            // 这里如果找到答案就直接做消费
            String conversationName = llmGetAnswer(dto, according, modelStep, answerData, systemPrompt, networkFlag, start);
            modelStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            modelStep.setCostTime(System.currentTimeMillis() - start);
            // 如果是高危，则切换大模型
            if (StringUtils.isNotBlank(conversationName) && conversationName.toLowerCase().contains(LLM_ERROR_RISK)) {
                String switchModelNames = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), RISK_SWITCH_LLM_MODEL_NAMES);
                if (StringUtils.isNotBlank(switchModelNames)) {
                    String[] modelNameSplit = switchModelNames.split(",");
                    for (String modelName : modelNameSplit) {
                        // 跳过当前默认大模型
                        if (null != applicationInfo.getLlmInfo() && "modelName".equals(applicationInfo.getLlmInfo().getModelName())) {
                            continue;
                        }
                        LlmInfo byModelName = llmInfoService.getByModelName(modelName);
                        if (null == byModelName) {
                            continue;
                        }
                        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
                        jsonObject.set(LLM_PARAM_MODEL_ID, byModelName.getModelId());
                        modelStep.setParam(jsonObject);
                        conversationName = llmGetAnswer(dto, according, modelStep, answerData, systemPrompt, networkFlag, start);
                        modelStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                        modelStep.setCostTime(System.currentTimeMillis() - start);
                        if (!conversationName.toLowerCase().contains(LLM_ERROR_RISK)) {
                            break;
                        }
                    }
                }
            }

            // modelStep.setResult(conversationName);
            log.info("[汇总调用大模型] 生成会话名称完成 conversationName = {}", conversationName);
            if (conversationName.equals(AnswerStrategyContant.NO_ANSWER_TEXT) || conversationName.contains(" tokens ")) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
            return conversationName;
        }
        answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
        return StringConstant.BLANK;
    }

    /**
     * 获取评分top数据
     *
     * @param content
     * @param contentList
     * @return
     */
    private Map<String, List<String>> getTopDocumentLibrary(String content, List<String> contentList, Vector<StepEntity> contextList) {
        long startTime = System.currentTimeMillis();
        // 根据余弦相似度降重
        contentList = answerUtils.reduceRepetitionBasedOnCos(contentList);
        log.debug("余弦相似度降重耗时:{}ms", System.currentTimeMillis() - startTime);
        Map<String, List<String>> map = new HashMap<>();
        // 保存原数据
        List<String> toYayiList = Lists.newArrayList();
        List<String> notToYayiList = Lists.newArrayList();
        List<String> rearrangeFilterResult = Lists.newArrayList();
        List<String> notRearrangeFilterResult = Lists.newArrayList();
        map.put(COLLECCT_REARRANGE_RESULT, toYayiList);
        map.put(COLLECCT_REARRANGE_FILTER_RESULT, rearrangeFilterResult);
        // 保存分数
        List<String> scoreList = Lists.newArrayList();
        List<String> notScoreList = Lists.newArrayList();
        map.put(COLLECCT_REARRANGE_SCORE, scoreList);
        RearrangeParam rearrangeParam = new RearrangeParam();
        // 调用雅意重排能力
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(content);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        String[] split = null;
        String textDetail = StringConstant.BLANK;
        for (String text : contentList) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            // 策略===>索引===>内容
            split = text.split(REARRANGE_SPLIT_CHAR);
            if (split.length < 3) {
                continue;
            }
            textDetail = split[2];
            if (textDetail.contains(PREFIX_SYMBOL)) {
                // 获取内容，格式为 内容[额外信息]
                int indexOf = textDetail.lastIndexOf(PREFIX_SYMBOL);
                textDetail = textDetail.substring(0, indexOf);
            }
            if (AnswerStrategyContant.ATTACHMENT_STRATEGY.equals(split[0])) {
                notToYayiList.add(text);
                notRearrangeFilterResult.add(textDetail);
                notScoreList.add("1");
                continue;
            }
            articles.setPara(textDetail);
            articlesList.add(articles);
        }
        rangeContent.setArticles(new ArrayList<>(articlesList));

        // 先重排
        rearrange(rangeContent, articlesList, rearrangeParam, contentList, toYayiList, scoreList, contextList);

        if (CollectionUtils.isNotEmpty(notToYayiList)) {
            toYayiList.addAll(notToYayiList);
        }

        if (CollectionUtils.isNotEmpty(notRearrangeFilterResult)) {
            rearrangeFilterResult.addAll(notRearrangeFilterResult);
        }

        if (CollectionUtils.isNotEmpty(notScoreList)) {
            scoreList.addAll(notScoreList);
        }

        // 通过大模型过滤文章
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String articleFilterEnable = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), LLM_PROMPT_SYSTEM_KNOWLEDGE_ARTICLE_ENABLE);
        if (CollectionUtils.isNotEmpty(contentList) && YesNoEnum.YES.getName().equals(articleFilterEnable)) {
            List<String> filterContent = filterContent(content, articlesList, toYayiList, contextList);
            rearrangeFilterResult.addAll(toYayiList);
            if (null != filterContent) {
                toYayiList.clear();
                toYayiList.addAll(filterContent);
            }
        }
        contentList.clear();
        return map;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非汇总场景的结果
        if (null == step || !step.getStep().equals(FINAL_COLLECT_REF)) {
            return dataList;
        }
        // 获取原始数据
        DialogueStep primaryData = stepMap.get(FINAL_COLLECT_STEP);
        if (null != primaryData) {
            List<SourceAnswerResult> collectRef = getCollectRef(primaryData, stepMap);
            dataList.addAll(collectRef);
        }
        return dataList;
    }


    /**
     * 汇总场景
     *
     * @param primaryData
     * @return
     */
    private List<SourceAnswerResult> getCollectRef(DialogueStep primaryData, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = com.google.common.collect.Lists.newArrayList();
        // 获取原始数据
        if (null != primaryData) {
            Object refResult = primaryData.getResult();
            if (null == refResult) {
                return dataList;
            }
            List<String> refList = JSON.parseArray(refResult.toString(), String.class);
            if (CollectionUtils.isNotEmpty(refList)) {
                Map<String, List<String>> refMap = refList.stream().collect(Collectors.groupingBy(p -> {
                    String[] split = p.split(REARRANGE_SPLIT_CHAR);
                    return split[0];
                }));
                refMap.forEach((k, list) -> {
                    AnswerStrategy answerStrategy = answerStrategyMap.get(k);
                    DialogueStep step = new DialogueStep();
                    step.setStep(k);
                    step.setResult(list);
                    List<SourceAnswerResult> sourceAnswerResults = answerStrategy.sourceAnswer(step, stepMap);
                    dataList.addAll(sourceAnswerResults);
                });
            }
        }
        return dataList;
    }

    @Override
    public List<SourceAnswerResult> notAnswer(List<DialogueStep> dialogueSteps, boolean interceptFlag) {
        List<SourceAnswerResult> results = Lists.newArrayList();
        // 拦截
        if (interceptFlag) {
            return results;
        }
        Optional<DialogueStep> any = dialogueSteps.stream()
                .filter(p -> p.getStep().equals(FINAL_COLLECT_STEP))
                .findAny();
        if (any.isPresent()) {
            DialogueStep dialogueStep = any.get();
            SourceAnswerResult result = new SourceAnswerResult();
            result.setAnswerFlag("否");
            result.setText(null == dialogueStep.getResult() ? NO_KNOWLEDGE : dialogueStep.getResult().toString());
            result.setRoute(ListUtil.toList(FINAL_COLLECT_STEP));
            results.add(result);
        }
        return results;
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, FINAL_COLLECT, FINAL_COLLECT_STEP, FINAL_COLLECT_REF);
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setClientId(scoreDataParam.getClientId());
        dto.setContentTemp(scoreDataParam.getQuestion());
        Vector<StepEntity> contextList = new Vector<>();
        Vector<KnowledgeContent> contentList = answerUtils.getContentList(dto, contextList, com.google.common.collect.Lists.newArrayList(scoreDataParam.getStrategyDetail()));
        // 封装数据集，按照模块进行排序
        List<String> collect = contentList.stream().flatMap(p -> {
            String module = p.getModule();
            List<String> contents = p.getContentList();
            if (CollectionUtils.isEmpty(contents)) {
                return null;
            }
            int size = contents.size();
            List<String> contentLists = Lists.newArrayList();
            for (int i = 0; i < size; i++) {
                contentLists.add(module + REARRANGE_SPLIT_CHAR + i + REARRANGE_SPLIT_CHAR + contents.get(i));
            }
            return contentLists.stream();
        }).distinct().collect(Collectors.toList());
        collect = collect.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, List<String>> mapResult = getTopDocumentLibrary(dto.getContentTemp(), collect, contextList);
        List<String> rearrangeScores = mapResult.get(COLLECCT_REARRANGE_SCORE);
        String articleFilterEnable = AppConfigContant.getConfiguration(scoreDataParam.getApplicationId(), LLM_PROMPT_SYSTEM_KNOWLEDGE_ARTICLE_ENABLE);
        List<String> resultReargenList = null;
        if (CollectionUtils.isNotEmpty(contentList) && YesNoEnum.YES.getName().equals(articleFilterEnable)) {
            resultReargenList = mapResult.get(COLLECCT_REARRANGE_FILTER_RESULT);
        } else {
            resultReargenList = mapResult.get(COLLECCT_REARRANGE_RESULT);
        }
        List<ScoreDataResult> results = new ArrayList<>();
        int index = 0;
        for (String content : resultReargenList) {
            String[] split = content.split(REARRANGE_SPLIT_CHAR);
            ScoreDataResult result = new ScoreDataResult();
            result.setTitle(split[0]);
            result.setContent(split[2]);
            result.setRearrangeScore(new BigDecimal(rearrangeScores.get(index++)));
            results.add(result);
        }
        return results;
    }

    /**
     * 获取最终答案
     *
     * @param dto
     * @param modelStep
     * @param answerData
     * @return
     */
    private String llmGetAnswer(DialogueByStreamParam dto, String according, StepEntity modelStep, AnswerStepData answerData, String systemPrompt, boolean networkFla, long start) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String fromNetworkContent = getConfiguration(applicationInfo.getApplicationId(), FROM_NETWORK_CONTENT);
        boolean simpleFlag = StringUtils.isNotBlank(systemPrompt) && systemPrompt.contains("========================以下是相关资料");
        JSONObject resultObj = new JSONObject();
        if (simpleFlag) {
            resultObj.put(ANSWER_FIELD, StringConstant.BLANK);
        }
        // 截取前100000字符，这里房子大模型超出最大token导致无法使用，如果各项目有问题，这里可以按应用个性配置
        if (systemPrompt.length() > 100000) {
            systemPrompt = systemPrompt.substring(0, 100000);
        }
        long llmStart = System.currentTimeMillis();
        return answerUtils.getGenerateCommon(according, StringConstant.BLANK, modelStep, dto.getListMsg(), result -> {
            try {
                // log.info("FinalCollectStrategy大模型耗时:{}", System.currentTimeMillis() - start);
                // 如果是高危，则跳过
                if (null == modelStep.getFirstLlmTime()) {
                    modelStep.setFirstLlmTime(System.currentTimeMillis() - llmStart);
                }
                if (StringUtils.isNotBlank(result.getErrorMessage())) {
                    if (result.getErrorMessage().toLowerCase().contains(LLM_ERROR_RISK)) {
                        return;
                    }
                }
                // String answer = result.getAnswer();
                String originalAnswer = result.getAnswer();
                String reasoningContent = result.getReasoningContent();
                if (simpleFlag) {
                    resultObj.put(ANSWER_FIELD, originalAnswer);
                    originalAnswer = JSONUtil.toJsonStr(resultObj);
                }
                answerData.setStatus(result.getStatus());
                if (StringUtils.isBlank(originalAnswer) && StringUtils.isBlank(reasoningContent)) {
                    return;
                }
                // answer = answerUtils.analyticAnswer(answer);
                String answer = AnswerUtils.analyticAnswer(originalAnswer);
                String outline = AnswerUtils.analyticOutline(originalAnswer);
                if (StringUtils.isBlank(answer) && StringUtils.isBlank(reasoningContent)) {
                    return;
                }
                if (StringUtils.isNotBlank(applicationInfo.getNotAnswer()) && applicationInfo.getNotAnswer().equals(answer)) {
                    answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                    return;
                }
                if (StepStatusEnum.ANSWER_COMPLETE.equals(answerData.getStatus()) && StringUtils.isNotBlank(fromNetworkContent) && networkFla) {
                    answer += fromNetworkContent;
                }
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                answerData.setOutline(outline);
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        }, systemPrompt);
    }

    /**
     * 过滤文章
     *
     * @return
     */
    private List<String> filterContent(String question, List<RearrangeParam.Articles> articlesList, List<String> contentList, Vector<StepEntity> contextList) {
        StepEntity filterStep = new StepEntity();
        filterStep.setStep(FINAL_COLLECT_FILTER);
        contextList.add(filterStep);
        if (CollectionUtils.isEmpty(articlesList)) {
            return Lists.newArrayList();
        }
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String articleFilterSystemPrompt = AppConfigContant.getConfiguration(LLM_PROMPT_SYSTEM_KNOWLEDGE_ARTICLE_FILTER);
        if (StringUtils.isBlank(articleFilterSystemPrompt)) {
            return null;
        }
        String dateForPrompt = DateUtil.getDateForPrompt();
        articleFilterSystemPrompt = articleFilterSystemPrompt.replace(DATES_PLACEHOLDER, dateForPrompt);

        // 获取大模型
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationInfo.getModelId());
        // 调用大模型，判别事项类型和指标问题
        List<YayiMessage> messagesList = Lists.newArrayList();
        if (StringUtils.isNotBlank(applicationInfo.getSystemPromptResult())) {
            messagesList.add(new YayiMessage(SYSTEM_PROMPT_FIELD, articleFilterSystemPrompt));
        }
        ArticleFilterParam filterParam = new ArticleFilterParam();
        filterParam.setOriginal_question(question);
        List<ArticleFilterParam.Article> articleList = Lists.newArrayList();
        filterParam.setArticle_list(articleList);
        for (int i = 0; i < articlesList.size(); i++) {
            articleList.add(new ArticleFilterParam.Article(i + 1, articlesList.get(i).getPara()));
        }
        String generate = llmStrategy.generate(JSON.toJSONString(filterParam), messagesList, filterStep, false);

        generate = AnswerUtils.dealLLmJson(generate);
        filterStep.setResult(generate);
        if (generate.contains("没有符合的文章")) {
            return Lists.newArrayList();
        }
        if (JSONUtil.isTypeJSONArray(generate)) {
            List<JSONObject> parseArray = JSON.parseArray(generate, JSONObject.class);
            List<Integer> leaveIndex = parseArray.stream().filter(p -> p.getString("筛选结果").equals("丢弃")).map(p -> p.getInteger("文章序号")).collect(Collectors.toList());
            List<Integer> fitIndex = parseArray.stream().filter(p -> p.getString("筛选结果").equals("符合") || p.getString("筛选结果").equals("完美")).map(p -> p.getInteger("文章序号")).collect(Collectors.toList());
            List<String> toYayiList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(fitIndex)) {
                for (int i = 0; i < contentList.size(); i++) {
                    if (fitIndex.contains(i + 1)) {
                        toYayiList.add(contentList.get(i));
                        contentList.set(i, contentList.get(i) + "(符合)");
                    }
                }
            } else {
                for (int i = 0; i < contentList.size(); i++) {
                    if (!leaveIndex.contains(i + 1)) {
                        toYayiList.add(contentList.get(i));
                    } else {
                        contentList.set(i, contentList.get(i) + "(丢弃)");
                    }
                }
            }
            return toYayiList;
        }
        filterStep.setResult(contentList);
        return Lists.newArrayList();
    }

    /**
     * 重排
     *
     * @param rangeContent
     * @param articlesList
     * @param rearrangeParam
     * @param contentList
     * @param toYayiList
     * @param scoreList
     * @return
     */
    private List<String> rearrange(RearrangeParam.Content rangeContent, List<RearrangeParam.Articles> articlesList, RearrangeParam rearrangeParam, List<String> contentList, List<String> toYayiList, List<String> scoreList, Vector<StepEntity> contextList) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (CollectionUtils.isEmpty(articlesList)) {
            return Lists.newArrayList();
        }
        long start = System.currentTimeMillis();
        // 重排并获取前几条
        StepEntity rearrangeStep = new StepEntity();
        rearrangeStep.setStep(FINAL_COLLECT_RANGE);
        rearrangeStep.setCreateTime(DateUtil.getCurrentTime());
        contextList.add(rearrangeStep);
        List<RearrangeParam.Articles> articlesTempList = Lists.newArrayList();
        rearrangeParam.setContent(rangeContent);
        // 调用重排能力
        rearrangeStep.setPrompt(rearrangeParam);
        RearrangeResult rearrange = answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
        rearrangeStep.setPrompt(rearrangeParam);
        rearrangeStep.setResult(rearrange);
        RearrangeResult.RearrangeData data = rearrange.getData();

        if (data!=null) {
            // 获取重排后的索引
            List<Integer> indexList = data.getIndex_list();
            List<BigDecimal> resScoresList = data.getRes_scores_list();
            if (CollectionUtils.isNotEmpty(indexList)) {
                int size = Math.min(contentList.size(), applicationInfo.getFilterNum());
                size = Math.min(resScoresList.size(), size);
                Float rangeContentScore = applicationInfo.getRangeContentScore();
                if (null == rangeContentScore) {
                    rangeContentScore = 0F;
                }
                for (int i = 0; i < size; i++) {
                    BigDecimal score = resScoresList.get(i);
                    if (score.compareTo(BigDecimal.valueOf(rangeContentScore)) >= 0) {
                        toYayiList.add(contentList.get(indexList.get(i)));
                        articlesTempList.add(articlesList.get(indexList.get(i)));
                        scoreList.add(score.toPlainString());
                    }
                }
            }
        }
        articlesList.clear();
        articlesList.addAll(articlesTempList);
        rearrangeStep.setCostTime(System.currentTimeMillis() - start);
        rearrangeStep.setEndTime(DateUtil.getCurrentTime());
        return toYayiList;
    }

    /**
     * 构建system prompt
     *
     * @param builder
     * @param size
     * @return
     */
    public String buildSystemPrompt(List<JSONObject> dataList, StringBuilder builder, int size) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String promptSystem = getConfiguration(applicationInfo.getApplicationId(), LLM_PROMPT_SYSTEM_FINAL_COLLECT_STRATEGY);
        if (StringUtils.isNotBlank(promptSystem)) {
            String dateForPrompt = DateUtil.getDateForPrompt();
            String notAnswer = applicationInfo.getNotAnswer();
            if (StringUtils.isBlank(notAnswer)) {
                notAnswer = getConfiguration(LLM_NO_ANSWER_DEFAULT_TEXT);
            }
            String according = StringConstant.BLANK;
            if (promptSystem.contains("article_list")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("article_list", dataList);
                according = JSON.toJSONString(jsonObject);
            } else {
                according = builder.toString();
            }

            according = according.replace("</br>", "\n").replace("<br>", "\n");
            promptSystem = promptSystem.replace(SIZE_PLACEHOLDER, size + "")
                    .replace(CONTENT_PLACEHOLDER, according)
                    .replace(DATE_PLACEHOLDER, dateForPrompt)
                    .replace(NO_ANSWER_PLACEHOLDER, notAnswer);
            return promptSystem;
        }
        return StringConstant.BLANK;
    }


    /**
     * 获取知识库数据，以供大模型回答，这里是最原始的数据，没有经过重排过滤
     *
     * @param dto
     * @param contextList
     * @return
     */
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        KnowledgeContent content = new KnowledgeContent();
        List<String> topDocumentLibrary = getnCntents(dto, contextList, new StepEntity(), System.currentTimeMillis());
        content.setContentList(topDocumentLibrary);
        return content;
    }

    /**
     * 获取知识库数据
     *
     * @param dto
     * @param contextList
     * @return
     */
    private List<String> getnCntents(DialogueByStreamParam dto, Vector<StepEntity> contextList, StepEntity step, long start) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 判断是否开启问答库
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            return Lists.newArrayList();
        }
        // 原本是汇总全部策略 改为汇总指定策略
        Vector<KnowledgeContent> contentList = answerUtils.getContentList(dto, contextList, Lists.newArrayList());

        // 判断是否为空
        if (CollectionUtils.isEmpty(contentList)) {
            // 空数据则无法回答
            step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            step.setCostTime(System.currentTimeMillis() - start);
            return Lists.newArrayList();
        }
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return getTopDocumentLibraryData(dto, contentList, contextList);
    }

    /**
     * 根据步骤和内容获取数据
     * @param dto
     * @param contentList
     * @param contextList
     * @return
     */
    private List<String> getTopDocumentLibraryData(DialogueByStreamParam dto, Vector<KnowledgeContent> contentList, Vector<StepEntity> contextList) {
        // List<KnowledgeContent> mcpContentList = contentList.stream()
        //         .filter(p -> MCP_STRATEGY.equals(p.getModule()))
        //         .collect(Collectors.toList());
        List<String> collect = contentList.stream().flatMap(p -> {
            String module = p.getModule();
            List<String> contents = p.getContentList();
            if (CollectionUtils.isEmpty(contents)) {
                return null;
            }
            int size = contents.size();
            List<String> contentLists = Lists.newArrayList();
            for (int i = 0; i < size; i++) {
                contentLists.add(module + REARRANGE_SPLIT_CHAR + i + REARRANGE_SPLIT_CHAR + contents.get(i));
            }
            return contentLists.stream();
        }).distinct().collect(Collectors.toList());

        collect = collect.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            // 无数据则无法回答
            return Lists.newArrayList();
        }

        // 获取mcp数据,mcp不参与重排
        List<String> mcpContentList = collect.stream().filter(p -> p.startsWith(MCP_STRATEGY))
                .collect(Collectors.toList());

        // 获取其他数据
        collect = collect.stream().filter(p -> !p.startsWith(MCP_STRATEGY))
                .collect(Collectors.toList());

        // 获取评分top数据
        Map<String, List<String>> mapResult = getTopDocumentLibrary(dto.getContentTemp(), collect, contextList);
        List<String> topDocumentLibrary = mapResult.get(COLLECCT_REARRANGE_RESULT);
        if (CollectionUtils.isNotEmpty(mcpContentList)) {
            topDocumentLibrary.addAll(mcpContentList);
        }
        return topDocumentLibrary;
    }

    /**
     * 初始化引用文章
     * @param vectorLibrary
     * @param contextList
     * @param answerStrategyMap
     * @return
     */
    private List<SourceAnswerResult> initSourceAnswerResult(List<String> vectorLibrary, List<StepEntity> contextList, Map<String, AnswerStrategy> answerStrategyMap) {
        Map<String, List<String>> refMap = vectorLibrary.stream().collect(Collectors.groupingBy(p -> {
            String[] split = p.split(REARRANGE_SPLIT_CHAR);
            return split[0];
        }));

        List<DialogueStep> stepList = AnswerUtils.initDialogue(contextList);
        HashMap<String, DialogueStep> stepMap = stepList.stream().collect(Collectors.toMap(
                DialogueStep::getStep,
                p -> p,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
        List<SourceAnswerResult> datas = Lists.newArrayList();
        refMap.forEach((k, list) -> {
            AnswerStrategy answerStrategy = answerStrategyMap.get(k);
            DialogueStep step = new DialogueStep();
            step.setStep(k);
            step.setResult(list);
            List<SourceAnswerResult> sourceAnswerResults = answerStrategy.sourceAnswer(step, stepMap);
            datas.addAll(sourceAnswerResults);
        });
        return datas;
    }
}
