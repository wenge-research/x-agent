package com.wenge.model.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.ShowContant;
import com.wenge.model.dto.param.AnswerRefParam;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.QaFilter;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.DialogueAnswerOutline;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.*;
import com.wenge.model.enums.ApplicationRearrangeModelEnum;
import com.wenge.model.enums.DialogueSearchFileTypeEnum;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.factory.UserThreadFactory;
import com.wenge.model.mapper.BusinessScenarioListMapper;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.answer.AnswerStrategy;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.yayi.api.VolcengineServer;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.Generate30BParam;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.RearrangeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
import static com.wenge.oauth.constants.AppConfigContant.*;

@Component
@Slf4j
public class AnswerUtils {

    private static final double COS_THRESHOLD = 0.95;

    @Autowired
    private KnowledgeDataMapper knowledgeDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private LlmInfoService llmInfoService;

    @Autowired
    private Map<String, AnswerStrategy> answerStrategyMap;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private BusinessScenarioListMapper businessScenarioListMapper;

    @Autowired
    private VolcengineServer volcengineServer;

    /**
     * 根据向量查询知识库数据
     *
     * @param dto
     * @param dense
     * @param score
     * @param step
     * @param rerankStepModel
     * @param rangeScore
     * @return
     */
    public List<KnowledgeData> byDense(DialogueByStreamParam dto, String dense, float score, StepEntity step, StepEntity rerankStepModel, double rangeScore, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (null == applicationInfo) {
            log.info("AnswerUtils applicationInfo is null");
            return Lists.newArrayList();
        }

        // 获取知识库集合
        Map<String, List<KnowledgeInfo>> knowledgeVectorMap = DialogueServiceImpl.KNOWLEDGE_VECTOR_MAP.get();
        if (null == knowledgeVectorMap || knowledgeVectorMap.isEmpty()) {
            return Lists.newArrayList();
        }

        // 获取问题集合
        List<String> queryList = dto.getQueryList();
        if (null == queryList) {
            queryList = ListUtil.toList(dto.getContentTemp());
        }
        List<KnowledgeData> dataList = Lists.newArrayList();
        // 只有一种向量化，只有一个问题理解，则无需创建线程池
        if (knowledgeVectorMap.size() == 1 && queryList.size() == 1) {
            List<KnowledgeInfo> knowledgeInfos = knowledgeVectorMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            List<String> knowldegeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
            dataList = getDataByKnowledgeId(dto, dense, score, step, knowldegeIdList);
        } else {
            // 创建线程池
            List<CompletableFuture<List<KnowledgeData>>> futures = Lists.newArrayList();
            String traceId = DialogueServiceImpl.TRACE_ID.get();
            // 1. 创建自定义线程池（动态命名）
            UserThreadFactory userThreadFactory = new UserThreadFactory(traceId);

            // 3. 创建线程池
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    3,
                    8,
                    0,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(100),
                    userThreadFactory, // 使用自定义工厂
                    new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
            );
            List<String> querys = new ArrayList<>(queryList);
            // 按向量化维护遍历知识库
            knowledgeVectorMap.forEach((vector, knowledgeInfos) -> {
                if (CollectionUtils.isNotEmpty(knowledgeInfos)) {
                    // 按问题维度遍历知识库
                    querys.forEach(query -> {
                        CompletableFuture<List<KnowledgeData>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
                            List<String> knowldegeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
                            DialogueByStreamParam bean = BeanUtil.toBean(dto, DialogueByStreamParam.class);
                            bean.setContentTemp(query);
                            return getDataByKnowledgeId(bean, dense, score, step, knowldegeIdList);
                        }, executor);
                        futures.add(listCompletableFuture);
                    });
                }
            });

            // 等待所有  任务完成
            CompletableFuture.allOf(futures.stream().toArray(CompletableFuture[]::new));

            // 关闭线程池
            executor.shutdown();

            // 获取结果
            dataList = futures.stream().flatMap(p -> {
                try {
                    return p.join().stream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Stream.empty();
            }).collect(Collectors.toList());
        }

        // 记录耗时
        step.setResult(dataList);
        step.setEndTime(DateUtil.getCurrentTime());
        step.setCostTime(System.currentTimeMillis() - start);

        if (CollectionUtil.isEmpty(dataList)) {
            return dataList;
        }
        // 获取评分前几的场景（重排）
        List<KnowledgeData> sceneData = getSceneData(dto.getContentTemp(), dataList, rerankStepModel, rangeScore, dense);
        step.setResult(sceneData);

        // QA要通过大模型筛选
        KnowledgeData knowledgeData = null;
        if (CollectionUtils.isNotEmpty(sceneData) && !KNOWLEDGE_CONTENT_STEP.equals(step.getStep())) {
            knowledgeData = sceneData.get(0);
            // 标记命中的数据
            String qaScenesFilterEnable = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), QA_SCENES_FILTER_ENABLE);
            String qaScenesFilter = AppConfigContant.getConfiguration(LLM_PROMPT_SYSTEM_QA_SCENES_FILTER);
            if (YesNoEnum.YES.getName().equals(qaScenesFilterEnable) && StringUtils.isNotBlank(qaScenesFilter)) {
                if (StringUtils.isBlank(dto.getContent())) {
                    dto.setContent(dto.getContentTemp());
                }
                knowledgeData = filterQaByLlm(dto.getContent(), sceneData, contextList);
            }
        }

        if (null != knowledgeData) {
            // 将对象移动到第一个
            dataList.remove(knowledgeData);
            dataList.add(0, knowledgeData);
            // 标记第一个命中的
            markData(knowledgeData, dataList);
        } else {
            if (StringUtils.isNotBlank(dto.getClientId())) {
                return Lists.newArrayList();
            } else {
                // 标记第一个命中的
                markData(knowledgeData, dataList);
                return dataList;
            }
        }
        return sceneData;
    }

    /**
     * 根据向量直接查询知识库数据
     */
    public List<KnowledgeData> queryQAKnowledgeByDense(DialogueByStreamParam dto, String dense, float score, Integer recommendQuestionNumber) {
        // 根据输入内容检索QA对-根据评分查询QA对
        DialogueByStreamParam bean = BeanUtil.toBean(dto, DialogueByStreamParam.class);
        bean.setContentTemp(bean.getContentTemp());
        LambdaEsQueryWrapper<KnowledgeData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeData.class);
        SearchSourceBuilder searchSourceBuilder = knowledgeDataMapper.getSearchSourceBuilder(esQueryWrapper);

        // 获取查询方式
        QueryBuilder queryByWay = getQueryByVectorWay(dense, dto, score);
        SearchSourceBuilder query = searchSourceBuilder.query(queryByWay);
        query.fetchSource(null, new String[]{AnswerStrategyContant.TITLE_DENSE_FILED, CONTENT_DENSE_FILED});
        esQueryWrapper.setSearchSourceBuilder(query);
        query.size(recommendQuestionNumber);
        query.from(0);

        return knowledgeDataMapper.selectList(esQueryWrapper);
    }

    /**
     * 获取场景
     *
     * @param content
     * @param dataList
     * @param stepModel
     * @param rangeScore
     * @return
     */
    private List<KnowledgeData> getSceneData(String content, List<KnowledgeData> dataList, StepEntity stepModel, double rangeScore, String dense) {
        stepModel.setCreateTime(DateUtil.getCurrentTime());
        long start = System.currentTimeMillis();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        List<KnowledgeData> resultDataList = Lists.newArrayList();
        stepModel.setResult(resultDataList);
        int size = dataList.size();
        //StringBuffer sb = new StringBuffer("# 以json格式输出{ \"场景\", \"问题\",\"理由\"}，所有场景如下：\n");
        Vector<String> vector = new Vector<>();
        // 构建场景
        for (int i = 0; i < size; i++) {
            KnowledgeData jsonObject = dataList.get(i);
            // 构建场景
            buildDetail(jsonObject, i, vector, dense);
        }

        RearrangeParam rearrangeParam = new RearrangeParam();
        // 调用雅意重排能力
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(content);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (String text : vector) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            // 获取内容，从格式：【索引 ==> 内容】 中提取内容
            articles.setPara(text.replaceAll("\\d*==>", StringConstant.BLANK));
            articlesList.add(articles);
        }
        rangeContent.setArticles(articlesList);
        rearrangeParam.setContent(rangeContent);
        // 调用重排能力
        stepModel.setPrompt(JSON.toJSONString(rearrangeParam));
        RearrangeResult rearrange = buildRearrangeModel(rearrangeParam, applicationInfo);
        stepModel.setPrompt(JSON.toJSONString(rearrangeParam));
        stepModel.setResult(rearrange);
        RearrangeResult.RearrangeData data = rearrange.getData();
        int index = -1;
        if (null != data) {
            // 获取重排后的索引
            List<Integer> indexList = data.getIndex_list();
            if (CollectionUtils.isNotEmpty(indexList)) {
                size = Math.min(indexList.size(), applicationInfo.getFilterNum());
                List<BigDecimal> resScoresList = data.getRes_scores_list();

                // 获取重排后分数达标的数据
                for (int i = 0; i < size; i++) {
                    if (resScoresList.get(i).compareTo(BigDecimal.valueOf(rangeScore)) >= 0) {
                        String contentStr = vector.get(indexList.get(i));
                        if (StringUtils.isNotBlank(contentStr)) {
                            // 获取索引，从格式【索引 ==> 内容】中提取索引
                            String[] split = contentStr.split("==>");
                            if (split.length == 2) {
                                index = Integer.parseInt(split[0]);
                                KnowledgeData knowledgeData = dataList.get(index);
                                knowledgeData.setRangeScore(resScoresList.get(i));
                                resultDataList.add(knowledgeData);
                            }
                        }
                    }
                }
            }
        }
        // 设置重排结果，标记结果索引值
        //stepModel.setResult(JSON.toJSONString(data) + "====>" + index);

        stepModel.setEndTime(DateUtil.getCurrentTime());
        stepModel.setCostTime(System.currentTimeMillis() - start);
        return resultDataList;
    }

    /**
     * @param knowledgeData
     * @param index
     * @param vector
     */
    private void buildDetail(KnowledgeData knowledgeData, Integer index, Vector<String> vector, String dense) {
        // 设置格式： 【索引 ==> 内容】
        if (dense.contains(TITLE_FIELD)) {
            if (vector.stream().noneMatch(p -> p.contains(SPLIT_CHAR + knowledgeData.getTitle()))) {
                vector.add(index + SPLIT_CHAR + knowledgeData.getTitle());
            }
        } else {
            if (vector.stream().noneMatch(p -> p.contains(SPLIT_CHAR + knowledgeData.getContent()))) {
                vector.add(index + SPLIT_CHAR + knowledgeData.getTitle() + "\n" + knowledgeData.obtainAnswer());
            }
        }
        //vector.add(index + "==>" + jsonObject.getContent());
    }

    /**
     * 构建prompt
     * @param builder
     * @param contentTemp
     * @param size
     * @return
     */
    public static String buildPrompt(StringBuilder builder, String contentTemp, int size) {
        //String date = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy年MM月dd日");
        String dateTemp = DateUtil.getDateForPrompt();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String notAnswer = applicationInfo.getNotAnswer();
        if (StringUtils.isBlank(notAnswer)) {
            notAnswer = getConfiguration(LLM_NO_ANSWER_DEFAULT_TEXT);
        }
        if (null == notAnswer) {
            notAnswer = StringConstant.BLANK;
        }
        String promptPlaceholder = applicationInfo.getPromptTemplate();
        String according = builder.toString();
        according = according.replace("</br>", "\n").replace("<br>", "\n");
        promptPlaceholder = promptPlaceholder.replace(SIZE_PLACEHOLDER, size + StringConstant.BLANK)
                .replace(CONTENT_PLACEHOLDER, according)
                .replace(DATE_PLACEHOLDER, dateTemp)
                .replace(NO_ANSWER_PLACEHOLDER, notAnswer)
                .replace(QUESTION_PLACEHOLDER, Q_START_SYMBOL + contentTemp + Q_END_SYMBOL);
                //.replace("{question}", "『如何办理身份证』");

        return promptPlaceholder;
    }

    /**
     * 大模型接口
     *
     * @param text
     * @param question
     * @param step
     * @param list
     * @return
     */
    public String getGenerateCommon(String text, String question, StepEntity step, List<DialogueMessage> list, String systemPrompt) {
        return getGenerateCommon(text, question, step, list, null, systemPrompt);
    }

    /**
     * 大模型接口
     *
     * @param text
     * @param question
     * @param step
     * @param list
     * @param answerConsumer
     * @return
     */
    public String  getGenerateCommon(String text, String question, StepEntity step, List<DialogueMessage> list, Consumer<AnswerStepData> answerConsumer, String systemPrompt) {
        question = questionMark(question);
        Generate30BParam generate30BParam = new Generate30BParam();
        List<YayiMessage> messagesList = Lists.newArrayList();

        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (StringUtils.isBlank(systemPrompt)) {
            systemPrompt = applicationInfo.getSystemPromptResult();
        }

        YayiMessage messages = new YayiMessage();
        messages.setContent(systemPrompt);
        messages.setRole(SYSTEM_PROMPT_FIELD);
        messagesList.add(messages);
        generate30BParam.setMessages(messagesList);


        if (CollectionUtils.isNotEmpty(list)) {
            DialogueMessage dialogueMessage = list.get(list.size() - 1);
            if (dialogueMessage.getRole().equals(USER_PROMPT_FIELD)) {
                list.remove(list.size() - 1);
            }
            list.forEach(item -> {
                YayiMessage msg = new YayiMessage();
                msg.setRole(item.getRole());
                msg.setContent(item.getContent());
                messagesList.add(msg);
            });
        }

        String contsts = text;
        if (StringUtils.isNotBlank(question)) {
            contsts = contsts + QUESTION_FIELD + question;
        }

        // 获取大模型参数
        cn.hutool.json.JSONObject stepParam = step.getParam();
        String llmParamModelId = StringConstant.BLANK;
        if (null != stepParam && stepParam.containsKey(AnswerStrategyContant.LLM_PARAM_MODEL_ID)) {
            llmParamModelId = (String) stepParam.getOrDefault(AnswerStrategyContant.LLM_PARAM_MODEL_ID, StringConstant.BLANK);
        }
        if (StringUtils.isBlank(llmParamModelId)) {
            llmParamModelId = applicationInfo.getModelId();
        }

        // 获取模型信息
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(llmParamModelId);
        if (null == llmStrategy) {
            return AnswerStrategyContant.NO_ANSWER_TEXT;
        }
        // 生成答案
        if (null == answerConsumer) {
            // 模型生成答案
            return llmStrategy.generate(contsts, messagesList, step, false);
        } else {
            // 模型生成答案，流式回答
            return llmStrategy.generate(contsts, messagesList, step, false, answerConsumer);
        }
    }

    /**
     * 补充问号
     *
     * @return
     */
    public static String questionMark(String question) {
        if (StringUtils.isBlank(question)) {
            return question;
        }
        if (!question.trim().endsWith(QUESTION_MARK) && !question.trim().endsWith(QUESTION_MARK_CN)) {
            return question + QUESTION_MARK_CN;
        }
        return question;
    }

    /**
     * 解析答案
     * @param conversationName
     * @return
     */
    private static String analyticContent(String conversationName, String answerKey) {
        if (StringUtils.isBlank(conversationName)) {
            return conversationName;
        }
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String notAnswer = applicationInfo.getNotAnswer();
        String conversationNameTemp = conversationName;
        // 替换特殊字符，避免json解析失败
        conversationName = replaceText(conversationName);

        // 解析json
        conversationName = dealJson(conversationName);

        // 处理json后其他符号
        conversationName = dealJsonOther(conversationName);
        if (StringUtils.isNotBlank(conversationName) && JSONUtil.isTypeJSONObject(conversationName)) {
            try {
                // 如果答案中包含无法回答的文本，则返回无法回答
                if (conversationName.contains(notAnswer)) {
                    return notAnswer;
                }

                // 替换特殊字符
                JSONObject jsonObject = JSON.parseObject(conversationName);
                conversationName = (String) jsonObject.getOrDefault(answerKey, StringConstant.BLANK);
                conversationName = conversationName.replace(QUOTE_REPLACE_SYMBOL, JSON_QUOTE);
                if (StringUtils.isBlank(conversationName)) {
                    return StringConstant.BLANK;
                }

                // 长度小于5，则返回空
                if (conversationName.length() < notAnswer.length() + 1) {
                    return StringConstant.BLANK;
                }

                // 引用知识库为空，则返回无法回答
                String string = jsonObject.getString(REFERENCE_FIELD);
                if (StringUtils.isNotBlank(string) && NO_REFERENCE_FIELD.startsWith(string)) {
                    return notAnswer;
                }

                // 如果答案与无法回答相同，则返回无法回答
                if (notAnswer.equalsIgnoreCase(conversationName)) {
                    return notAnswer;
                }

                // 如果答案是数组，则转换为字符串
                if (StringUtils.isNotBlank(conversationName) && JSONUtil.isTypeJSONArray(conversationName)) {
                    JSONArray jsonArray = JSON.parseArray(conversationName);
                    conversationName = jsonArray.stream().map(Object::toString).collect(Collectors.joining("\n"));
                }
            } catch (Exception e) {
                conversationName = conversationNameTemp;
                // 解析失败，则截取字符串
                if (conversationName.contains(ANSWER_FIELD)) {
                    int indexOf = conversationName.indexOf(ANSWER_FIELD);
                    conversationName = conversationName.substring(indexOf + 2);
                }
                if (conversationName.contains("引用知识库")) {
                    int indexOf = conversationName.indexOf("引用知识库");
                    conversationName = conversationName.substring(0, indexOf);
                }
                if (conversationName.contains("问题")) {
                    int indexOf = conversationName.indexOf("问题");
                    conversationName = conversationName.substring(0, indexOf);
                }
                if (conversationName.contains("大纲")) {
                    int indexOf = conversationName.indexOf("大纲");
                    conversationName = conversationName.substring(0, indexOf);
                }
                conversationName = conversationName.replaceAll("\" ?: ?\"", "")
                        .replaceAll("\" ?, ?\"", "");
                // 解析失败，返回原始数据
                conversationName = dealJsonException(conversationNameTemp, ANSWER_FIELD);
            }
        }
        // 替换特殊字符
        //conversationName = replaceText(conversationName);
        //log.info("conversationName" + conversationName);
        return conversationName.replace(QUOTE_REPLACE_SYMBOL, JSON_QUOTE);
    }



    /**
     * 解析大纲
     * @param conversationName 原始答案
     * @return outline 大纲结果
     */
    public List<DialogueAnswerOutline> analyticOutlineStr(String conversationName) {
        List<DialogueAnswerOutline> outlineList = new ArrayList<>();
        try {
            String outlineStr = analyticContent(conversationName, "大纲");
            if (StringUtils.isBlank(outlineStr)) {
                return new ArrayList<>();
            }

            String[] firstSplitArray = outlineStr.split(ANSWER_OUTLINE_SPLIT_CHAR);
            for (String str : firstSplitArray) {
                String[] secondSplitArray = str.split(ANSWER_OUTLINE_SPLIT_CHAR_ONE);
                if (secondSplitArray.length < 2) {
                    continue;
                }
                String strValue = secondSplitArray[0];
                String arrayStr = secondSplitArray[1];
                String[] thirdSplitArray = arrayStr.split(ANSWER_OUTLINE_SPLIT_CHAR_TWO);
                DialogueAnswerOutline outline = new DialogueAnswerOutline();
                outline.setTitle(strValue);
                outline.setContent(Arrays.asList(thirdSplitArray));
                outlineList.add(outline);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outlineList;
    }

    /**
     * 解析大纲
     * @param conversationName 原始答案
     * @return outline 大纲结果
     */
    public static String analyticOutline(String conversationName) {
        return analyticContent(conversationName, "大纲");
    }

    /**
     * 解析答案中的json数据
     * @param conversationName 答案
     * @param key 键
     * @return
     */
    public String analyticAnswerByJsonKey(String conversationName, String key) {
        if (StringUtils.isBlank(conversationName)) {
            return conversationName;
        }
        String conversationNameTemp = conversationName;
        // 替换特殊字符，避免json解析失败
        conversationName = replaceText(conversationName);

        // 解析json
        conversationName = dealJson(conversationName);

        conversationName = dealJsonOther(conversationName);
        if (StringUtils.isNotBlank(conversationName) && JSONUtil.isTypeJSONObject(conversationName)) {
            try {
                conversationName = conversationName.replace(QUOTE_REPLACE_SYMBOL, JSON_QUOTE);
                JSONObject jsonObject = JSON.parseObject(conversationName);

                conversationName = (String) jsonObject.getOrDefault(key, StringConstant.BLANK);
                if (StringUtils.isBlank(conversationName)) {
                    return StringConstant.BLANK;
                }

                if (StringUtils.isNotBlank(conversationName) && JSONUtil.isTypeJSONArray(conversationName)) {
                    JSONArray jsonArray = JSON.parseArray(conversationName);
                    conversationName = jsonArray.stream().map(Object::toString).collect(Collectors.joining("\n"));
                }
            } catch (Exception e) {
                conversationName = dealJsonException(conversationNameTemp, key);
            }
        }
        // 替换特殊字符
        return conversationName.replace(QUOTE_REPLACE_SYMBOL, JSON_QUOTE);
    }

    /**
     * 替换特殊字符
     * @param text
     * @return
     */
    //private String replaceText(String text) {
    //    if (StringUtils.isNotBlank(text)) {
    //        List<String> subjectTalkList = answerUtils.getInterceptWord("替换词");
    //
    //        if (CollectionUtils.isNotEmpty(subjectTalkList)) {
    //            for (Object key : subjectTalkList) {
    //                text = text.replaceAll(key.toString(), "");
    //            }
    //        }
    //        if (text.startsWith("，")) {
    //            text = text.substring(1);
    //        }
    //        // 去掉连续多个中文符号在一起的
    //        text = text.replaceAll("[，。！？]{2}", "，");
    //        if (text.contains("引用知识库")) {
    //            int indexOf = text.indexOf("引用知识库");
    //            // 截取字符串
    //            text = text.substring(indexOf);
    //        }
    //    }
    //    return text;
    //}

    /**
     * 从向量库中获取答案
     * @param dto
     * @param contextList
     * @return
     */
    public String fromVectorLibrary(DialogueByStreamParam dto, List<StepEntity> contextList) {
        StepEntity step = new StepEntity();
        contextList.add(step);
        step.setStep(KNOWLEDGE_CONTENT_STEP);
        // 获取知识库数据
        List<KnowledgeData> vectorLibrary = getKnowledgeData(dto, step);

        if (CollectionUtils.isNotEmpty(vectorLibrary)) {
            // 获取评分top数据
            if (CollectionUtils.isNotEmpty(vectorLibrary)) {
                vectorLibrary = getTopkVectorLibrary(dto.getContentTemp(), vectorLibrary);
            }

            step.setResult(vectorLibrary);
            StepEntity modelStep = new StepEntity();
            contextList.add(modelStep);
            modelStep.setStep("政策向量库-大模型回答");

            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isEmpty(vectorLibrary)) {
                return StringConstant.BLANK;
            }

            // 获取评分top数据，封装知识库
            for (int i = 0; i < vectorLibrary.size(); i++) {
                builder.append("\n「 【知识库").append(i + 1).append("】是： ").append(vectorLibrary.get(i).getContent()).append(" 」 ");
            }

            // 构建prompt
            String according = AnswerUtils.buildPrompt(builder, dto.getContentTemp(), vectorLibrary.size());

            //boolean longFlag;
            //if (CollectionUtils.isNotEmpty(dto.getListMsg())) {
            //    String promptStr = dto.getListMsg().stream().map(DialogueMessage::getContent).collect(Collectors.joining("\n"));
            //    int length = promptStr.length();
            //    longFlag = length > 5000;
            //} else {
            //    longFlag = according.length() > 4000;
            //}
            String conversationName = getGenerateCommon(according, StringConstant.BLANK, modelStep, dto.getListMsg(), StringConstant.BLANK);
            log.info("[向量库调用大模型] 生成会话名称完成 conversationName = {}", conversationName);
            conversationName = analyticAnswer(conversationName);
            if (StringUtils.isBlank(conversationName)) {
                return StringConstant.BLANK;
            }

            for (KnowledgeData item : vectorLibrary) {
                if (StringUtils.isBlank(item.getLink())) {
                    item.setLink(StringConstant.BLANK);
                }
            }
            List<String> linkList = vectorLibrary.stream().map(KnowledgeData::getLink).collect(Collectors.toList());

            String linkLists = vectorLibrary.stream()
                    .distinct()
                    .filter(p -> StringUtils.isNotBlank(p.getLink()) && p.getLink().contains("http"))
                    .map(p -> KnowledgeData.getHttpAddress(p.getLink()))
                    .distinct()
                    .map(p -> {
                        for (String string : linkList) {
                            if (string.contains(p)) {
                                return string;
                            }
                        }
                        return StringConstant.BLANK;
                    })
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("\n *** \n"));

            if (StringUtils.isNotBlank(linkLists)) {
                linkLists = " <br>  **找到了相关信息如下** <br> " + linkLists;
                return conversationName + " <br> " + linkLists;
            }
            return conversationName;
        }
        return StringConstant.BLANK;
    }

    /**
     * 获取评分top数据
     * @param content
     * @param vectorLibrary
     * @return
     */
    private List<KnowledgeData> getTopkVectorLibrary(String content, List<KnowledgeData> vectorLibrary) {
        List<KnowledgeData> toYayiList = Lists.newArrayList();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        RearrangeParam rearrangeParam = new RearrangeParam();
        // 调用雅意重排能力
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(content);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (KnowledgeData vectorLibraryResult : vectorLibrary) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            articles.setPara(vectorLibraryResult.getContent());
            articlesList.add(articles);
        }
        rangeContent.setArticles(articlesList);
        rearrangeParam.setContent(rangeContent);
        // 调用重排能力
        RearrangeResult rearrange = buildRearrangeModel(rearrangeParam, applicationInfo);
        RearrangeResult.RearrangeData data = rearrange.getData();
        if (null != data) {
            // 获取重排后的索引
            List<Integer> indexList = data.getIndex_list();
            List<BigDecimal> resScoresList = data.getRes_scores_list();
            if (CollectionUtils.isNotEmpty(indexList)) {
                int size = Math.min(vectorLibrary.size(), applicationInfo.getFilterNum());
                Float rangeContentScore = applicationInfo.getRangeContentScore();
                if (null == rangeContentScore) {
                    rangeContentScore = 0F;
                }
                for (int i = 0; i < size; i++) {
                    BigDecimal score = resScoresList.get(i);
                    if (score.compareTo(BigDecimal.valueOf(rangeContentScore)) >= 0) {
                        toYayiList.add(vectorLibrary.get(indexList.get(i)));
                    }
                }
            }
        } else {
            // 默认取前n个
            for (int i = 0; i < applicationInfo.getFilterNum(); i++) {
                toYayiList.add(vectorLibrary.get(i));
            }
        }
        vectorLibrary.clear();
        return toYayiList;
    }

    /**
     * 获取知识库
     *
     * @param dto
     * @return
     */
    public List<KnowledgeData> getKnowledgeData(DialogueByStreamParam dto, StepEntity step) {
        Map<String, List<KnowledgeInfo>> knowledgeVectorMap = DialogueServiceImpl.KNOWLEDGE_VECTOR_MAP.get();
        if (null == knowledgeVectorMap || knowledgeVectorMap.isEmpty()) {
            return Lists.newArrayList();
        }

        List<String> queryList = dto.getQueryList();
        if (null == queryList) {
            queryList = ListUtil.toList(dto.getContentTemp());
        }
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (knowledgeVectorMap.size() == 1 && queryList.size() == 1) {
            List<KnowledgeInfo> knowledgeInfos = knowledgeVectorMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            List<String> knowldegeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
            return getByQuery(queryList.get(0), applicationInfo, knowldegeIdList);
        } else {
            List<CompletableFuture<List<KnowledgeData>>> listCompletableFuture = Lists.newArrayList();
            String traceId = DialogueServiceImpl.TRACE_ID.get();
            // 1. 创建自定义线程池（动态命名）
            UserThreadFactory userThreadFactory = new UserThreadFactory(traceId);

            // 3. 创建线程池
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    3,
                    8,
                    0,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(100),
                    userThreadFactory, // 使用自定义工厂
                    new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
            );
            List<String> querys = new ArrayList<>(queryList);
            knowledgeVectorMap.forEach((vector, knowledgeInfos) -> {
                if (CollectionUtils.isNotEmpty(knowledgeInfos)) {
                    List<String> knowldegeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
                    querys.forEach(query -> {
                        DialogueByStreamParam bean = BeanUtil.toBean(dto, DialogueByStreamParam.class);
                        bean.setContentTemp(query);
                        listCompletableFuture.add(CompletableFuture.supplyAsync(
                                () -> getByQuery(query, applicationInfo, knowldegeIdList), executor));
                    });
                }
            });

            CompletableFuture.allOf(listCompletableFuture.stream().toArray(CompletableFuture[]::new));
            executor.shutdown();
            return listCompletableFuture.stream().flatMap(p -> {
                List<KnowledgeData> join = p.join();
                return join.stream();
            }).collect(Collectors.toList());
        }
    }

    /**
     * 获取知识库内容
     *
     * @return
     */
    public Vector<KnowledgeContent> getContentList(DialogueByStreamParam dto, Vector<StepEntity> contextList, List<String> strategy) {
        Vector<KnowledgeContent> contentList = new Vector<>();
        // 获取所有策略，没有指定策略，则返回
        if (answerStrategyMap.isEmpty()) {
            return contentList;
        }
        // 初始化步骤
        List<String> stepIdList = initStepIdList(strategy, dto);
        if (CollectionUtils.isEmpty(stepIdList)) {
            return contentList;
        }

        // 定义异步执行线程池
        ThreadPoolExecutor pool = ExecutorBuilder.create().setCorePoolSize(8).setMaxPoolSize(8).setWorkQueue(new LinkedBlockingDeque<>()).build();
        if (!answerStrategyMap.isEmpty()) {
            stepIdList.forEach(stepId -> {
                if (answerStrategyMap.containsKey(stepId)) {
                    pool.execute(() -> {
                        try {
                            MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
                            contentList.add(answerStrategyMap.get(stepId).getContent(dto, contextList));
                        } catch (Exception e) {
                            log.error("获取知识库内容异常,stepId:{}", stepId, e);
                        } finally {
                            MDC.clear();
                        }
                    });
                }
            });
        }
        while (pool.getActiveCount() != 0 && !pool.isTerminated() && !pool.isShutdown()) {
            // 判断当前对话是否已完成，如果已完成，则关闭线程池
            if (StringUtils.isNotBlank(dto.getClientId()) && !DialogueServiceImpl.RUN_FLAG_MAP.getOrDefault(dto.getClientId(), false)) {
                break;
            }
            ThreadUtil.safeSleep(500);
        }
        pool.shutdown();
        return contentList;
    }

    /**
     * 初始化步骤
     * @return
     */
    private List<String> initStepIdList(List<String> strategy, DialogueByStreamParam dto) {
        List<String> stepIdList = ListUtil.toList();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            // 如果有指定的策略，就直接用指定策略
            if (CollectionUtils.isNotEmpty(strategy)) {
                stepIdList.addAll(strategy);
            } else {
                // 判断搜索类型
                Integer searchType = dto.getSearchType();
                if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_FILE.getCode())) {
                    // 文档库
                    stepIdList.add(AnswerStrategyContant.DOCUMENT_LIBRARY);
                } else if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_AUDIO.getCode())
                        || Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_IMAGE.getCode())
                        || Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_VIDEO.getCode())) {
                    // 多媒体
                    stepIdList.add(AnswerStrategyContant.MULTI_MEDIA_LIBRARY);
                } else if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_All.getCode())) {
                    // 文档库
                    stepIdList.add(AnswerStrategyContant.DOCUMENT_LIBRARY);
                    // 多媒体
                    stepIdList.add(AnswerStrategyContant.MULTI_MEDIA_LIBRARY);
                    // 知识库
                    stepIdList.add(AnswerStrategyContant.KNOWLEDGE_CONTENT);
                    // 如果开启了兜底，排除互联网步骤（没有开启兜底，要联网检索）
                    if (!StringUtils.equals(applicationInfo.getFinalNetworkFlag(), YesNoEnum.YES.getName())) {
                        // 联网检索
                        stepIdList.add(AnswerStrategyContant.NETWORK_STRATEGY);
                    }
                    // 结构化
                    stepIdList.add(AnswerStrategyContant.STRUCTURED_STRATEGY);
                    // 网站网页数据
                    stepIdList.add(AnswerStrategyContant.WEBSITE_STRATEGY);
                    // 问数搜索数据
                    stepIdList.add(AnswerStrategyContant.WENSHU_CONTENT);
                    // 工作流
                    stepIdList.add(AnswerStrategyContant.WORKFLOW_STRATEGY);
                    // 附件
                    stepIdList.add(AnswerStrategyContant.ATTACHMENT_STRATEGY);
                    // mcp
                    stepIdList.add(AnswerStrategyContant.MCP_STRATEGY);
                    // api
                    stepIdList.add(AnswerStrategyContant.API_STRATEGY);
                }
            }
        }
        return stepIdList;
    }

    /**
     * 检索问答库【问题】场景
     *
     * @param primaryData
     * @return
     */
    public List<SourceAnswerResult> qaTitleRef(DialogueStep primaryData) {
        Object refResult = primaryData.getResult();
        if (null == refResult) {
            return com.google.common.collect.Lists.newArrayList();
        }
        return getSourceAnswer(refResult, knowledgeData -> qaRef(knowledgeData, ListUtil.toList(AnswerStrategyContant.FIND_QA_TITLE_ROUTE)));
    }

    /**
     * 获取源数据
     *
     * @param refResult
     * @return
     */
    public List<SourceAnswerResult> getSourceAnswer(Object refResult, Function<KnowledgeData, SourceAnswerResult> function) {
        List<KnowledgeData> knowledgeData = JSON.parseArray(refResult.toString(), KnowledgeData.class);
        List<String> knowledgeIdList = knowledgeData.stream().map(KnowledgeData::getKnowledgeId).distinct().collect(Collectors.toList());
        Wrappers<Object> wrappers = Wrappers.init()
                .where(KNOWLEDGE_INFO.KNOWLEDGE_ID.in(knowledgeIdList));
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.list(wrappers);
        HashMap<String, KnowledgeInfo> knowledgeMap = knowledgeInfoList.stream().collect(Collectors.toMap(
                KnowledgeInfo::getKnowledgeId,
                p -> p,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
        return knowledgeData.stream().map(p -> {
            String knowledgeName = knowledgeMap.get(p.getKnowledgeId()).getKnowledgeName();
            p.setKnowledgeName(knowledgeName);
            return function.apply(p);
        }).collect(Collectors.toList());
    }

    /**
     * 检索问答库【回答】场景
     *
     * @param primaryData
     * @return
     */
    public List<SourceAnswerResult> qaContentRef(DialogueStep primaryData) {
        Object refResult = primaryData.getResult();
        if (null == refResult) {
            return Lists.newArrayList();
        }
        return getSourceAnswer(refResult, knowledgeData -> qaRef(knowledgeData, ListUtil.toList(AnswerStrategyContant.FIND_QA_CONTENT_ROUTE)));
    }

    /**
     * 问答库的溯源来源
     * @param knowledgeData
     * @param route
     * @return
     */
    private SourceAnswerResult qaRef(KnowledgeData knowledgeData, List<String> route) {
        SourceAnswerResult source = new SourceAnswerResult();
        source.setKnowledgeName(knowledgeData.getKnowledgeName());
        source.setRoute(route);
        String text = knowledgeData.getTitle();
        if (StringUtils.isBlank(text)) {
            text = "【回答】：" + knowledgeData.getContent().substring(0, Math.min(100, knowledgeData.getContent().length()));
        } else {
            text = "【问题】" + text;
        }
        source.setText(text);

        source.setSourceType(2);
        source.setFileLink(knowledgeData.getLink());
        return source;
    }

    /**
     * 润色答案
     *
     * @param answerData
     */
    public void polishAnswer(AnswerStepData answerData, DialogueByStreamParam dto, StepEntity modelStep, long start) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 判断是否需要润色答案
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getPolishFlag()) || StringUtils.isBlank(answerData.getAnswer())) {
            // 不需要润色
            answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
            // 这里直接推流
            dto.getAnswerConsumer().accept(answerData);
            return;
        }

        String according = applicationInfo.getPolishPrompt();
        if (StringUtils.isBlank(according)) {
            according = "请根据相关资料回答用户问题，请总结相关资料，可以使用相关资料中的数据、内容、观点、结论等，你的答案中不要编造、不要添加自己的观点";
        }

        if (!according.contains(ANSWER_PLACEHOLDER)) {
            according = according + "\n###############\n以下是相关资料\n" + answerData.getAnswer();
        }

        if (!according.contains(QUESTION_PLACEHOLDER)) {
            according = according + "\n###############\n请回答问题：\n" + dto.getContentTemp();
        }

        // String according = "你是一个热情的中关村政务客服助手，我提供一个问题和一个答案，请用热情的语气润色答案内容，答案需要添加敬语、问候语、感谢语等，然后重新整理数据排版和格式，如果有网页地址则用「」符号包裹，例如参考仿照示例「https://fw.ybj.beijing.gov.cn/hallEnter/」。我的问题是：{question}，我的答案是：{answer}";
        String dateForPrompt = com.wenge.model.utils.DateUtil.getDateForPrompt();
        according = according.replace(QUESTION_PLACEHOLDER, dto.getContentTemp())
                .replace(AnswerStrategyContant.ANSWER_PLACEHOLDER, answerData.getAnswer())
                .replace(AnswerStrategyContant.DATE_PLACEHOLDER, dateForPrompt)
                .replace(AnswerStrategyContant.ORIGIN_QUESTION_PLACEHOLDER, dto.getQuestion());

        String polishAnswer = getGenerateCommon(according, StringConstant.BLANK, modelStep, null, result -> {
            try {
                if (null == modelStep.getFirstLlmTime()) {
                    modelStep.setFirstLlmTime(System.currentTimeMillis() - start);
                }
                String reasoningContent = result.getReasoningContent();
                String answer = result.getAnswer();
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                answerData.setStatus(result.getStatus());
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        }, StringConstant.BLANK);
        log.info("[润色调用大模型] 生成完成 polishAnswer = {}", polishAnswer);
        if (polishAnswer.equals(AnswerStrategyContant.NO_ANSWER_TEXT)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
        }
        answerData.setAnswer(polishAnswer);
        modelStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        modelStep.setCostTime(System.currentTimeMillis() - start);
    }

    /**
     * 补全json
     */
    public static String completeJson(String conversationName) {
        if (StringUtils.isBlank(conversationName) || (!conversationName.contains("{") && !conversationName.contains("}") && !conversationName.contains("[") && !conversationName.contains("]"))) {
            return conversationName;
        }
        conversationName = conversationName.substring(1);
        if (StringUtils.isBlank(conversationName)) {
            return conversationName;
        }
        conversationName = conversationName.replace("\",\"", "\"----\"")
                .replace("\",", "\"----")
                .replace(",\"", "----\"");
        conversationName = conversationName.replace("{", StringConstant.BLANK)
                .replace("}", ",")
                .replace("\",\"", "\"----\"")
                .replace("\",", "\"----")
                .replace(",\"", "----\"");
        String[] split = conversationName.split("----");
        if (split.length < 1) {
            return StringConstant.BLANK;
        }

        StringBuilder sb = new StringBuilder("{");
        for (String str : split) {
            // 双引号为单数
            int count = StrUtil.count(str, "\"");
            if (count % 2 == 1) {
                str = str + "\"";
                if (!str.contains(":")) {
                    str = str + ":\"\"";
                }
            } else {
                if (!str.contains(":")) {
                    str = str + ":\"\"";
                } else if (!str.endsWith("\"")) {
                    str = str + "\"\"";
                }
            }
            sb.append(str).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("}");
        conversationName = sb.toString();
        if (conversationName.contains("{\"\":\"\"}")) {
            return StringConstant.BLANK;
        }
        return conversationName;
    }

    /**
     * 通知
     * @param stepData
     * @param answerConsumer
     */
    public static void notify(AnswerStepData stepData, Consumer<AnswerStepData> answerConsumer) {
        stepData.setStatus(StepStatusEnum.ANSWER_FAIL);
        answerConsumer.accept(stepData);
    }

    /**
     * 获取最终回答的依据来源
     * @param dialogueId
     * @param contextList
     * @param stepName
     * @param stepRef
     * @return
     */
    public static StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList, String strategy, String stepName, String stepRef) {
        // 获取最终回答
        List<AnswerStepData> answerStepData = DialogueServiceImpl.STEP_BY_LIST.get();
        if (CollectionUtils.isEmpty(answerStepData)) {
            return null;
        }
        // 判断当前步骤是否为最终回答
        Optional<AnswerStepData> any = answerStepData.stream()
                .filter(p -> p.getStatus().equals(StepStatusEnum.ANSWER_FINAL) && p.getStep().equals(strategy)).findAny();
        if (!any.isPresent()) {
            return null;
        }
        Optional<StepEntity> optionalStep = contextList.stream().filter(p -> stepName.equals(p.getStep())).findAny();
        if (!optionalStep.isPresent()) {
            return null;
        }
        StepEntity step = optionalStep.get();

        // 构建引用来源
        StepEntity reference = new StepEntity();
        reference.setDialogueId(dialogueId);
        reference.setStep(stepRef);
        long start = System.currentTimeMillis();
        reference.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        Object result = AnswerUtils.getResult(step.getResult());
        reference.setResult(result);
        reference.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        reference.setCostTime(System.currentTimeMillis() - start);
        return reference;
    }

    /**
     * 获取结果
     * @param result
     * @return
     */
    private static Object getResult(Object result) {
        String jsonStr = JSON.toJSONString(result);
        if (JSONUtil.isTypeJSONArray(jsonStr)) {
            return JSON.parseArray(jsonStr);
        } else if (JSONUtil.isTypeJSONObject(jsonStr)) {
            return JSON.parseObject(jsonStr);
        } else {
            return jsonStr;
        }
    }

    /**
     * 获取词库
     *
     * @param type
     * @return
     */
    public List<String> getInterceptWord(String appId, String type) {
        return getWordFromRedis(type, appId);
    }


    /**
     * 根据应用ID获取业务场景
     *
     * @param type
     * @return
     */
    public List<String> getBusinessScenarioList(String appId, String type) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(" application_id = '" + appId + "'");
        queryWrapper.and(" delete_flag = 0 ");
        queryWrapper.and(" show_flag = '是' ");
        final List<BusinessScenarioList> businessScenarioLists = businessScenarioListMapper.selectListByQuery(queryWrapper);
        List<String> listKeys = new ArrayList<>();
        businessScenarioLists.stream().forEach(p -> listKeys.add(p.getHitKeywords()));
        return listKeys;
    }

    /**
     * 从redis获取词库
     * @param type
     * @param applicationId
     * @return
     */
    private List<String> getWordFromRedis(String type, String applicationId) {
        Map<String, List<String>> appInterceptWordMap = DialogueServiceImpl.APP_INTERCEPT_WORD_MAP.getOrDefault(applicationId, Maps.newHashMap());
        if (appInterceptWordMap.isEmpty()) {
            return Lists.newArrayList();
        }
        return appInterceptWordMap.get(type);
    }

    /**
     * 获取查询方式，支持全文检索，语义检索，混合检索
     *
     * @param dense
     * @param dto
     * @return
     */
    private QueryBuilder getQueryByWay(String dense, DialogueByStreamParam dto, float score, List<String> knowldegeIdList) {
        // 必要条件
        BoolQueryBuilder mustCondition = prerequisite(dto, knowldegeIdList);
        // 默认混合检索
        if (null == dto.getSearchWay()) {
            fullTextSearch(dense, dto, mustCondition);
            return semanticSearch(mustCondition, dense, dto, score, knowldegeIdList);
        }

        switch (dto.getSearchWay()) {
            // 全文检索
            case FULL_TEXT:
                fullTextSearch(dense, dto, mustCondition);
                return mustCondition;
            // 语义检索
            case SEMANTIC:
                return semanticSearch(mustCondition, dense, dto, score, knowldegeIdList);
            default:
                fullTextSearch(dense, dto, mustCondition);
                return semanticSearch(mustCondition, dense, dto, score, knowldegeIdList);
        }
    }

    /**
     * 根据向量查询（写这个方法主要是为了解决demo和企商代码不一致的问题）
     * @param dense
     * @param dto
     * @param score
     * @return
     */
    private QueryBuilder getQueryByVectorWay(String dense, DialogueByStreamParam dto, float score) {
        // 必要条件
        BoolQueryBuilder mustCondition = prerequisiteByVectorWay(dto);
        // 默认混合检索
        if (null == dto.getSearchWay()) {
            fullTextSearch(dense, dto, mustCondition);
            return semanticSearchByVectorWay(mustCondition, dense, dto, score);
        }

        switch (dto.getSearchWay()) {
            // 全文检索
            case FULL_TEXT:
                fullTextSearch(dense, dto, mustCondition);
                return mustCondition;
            // 语义检索
            case SEMANTIC:
                return semanticSearchByVectorWay(mustCondition, dense, dto, score);
            default:
                fullTextSearch(dense, dto, mustCondition);
                return semanticSearchByVectorWay(mustCondition, dense, dto, score);
        }
    }

    /**
     * 必要条件
     */
    public BoolQueryBuilder prerequisite(DialogueByStreamParam dto, List<String> knowldegeIdList) {
        // 获取知识库 id
        // List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        List<QueryBuilder> must = bool.must();
        // 精确匹配知识库id
        must.add(QueryBuilders.termsQuery("knowledgeId.keyword", knowldegeIdList));
        must.add(QueryBuilders.termsQuery("delStatus.keyword", NumberConstants.ZERO + StringConstant.BLANK));
        // 检索已启用数据
        BoolQueryBuilder statusQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termsQuery("status", YesNoEnum.YES.getName()))
                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("status")));
        must.add(statusQuery);
        // 检索有效期内的数据
        String currentTime = DateUtil.getCurrentTime();
        // 构建 effectiveStartTime 的查询条件
        BoolQueryBuilder startTimeQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery("effectiveStartTime").lte(currentTime))
                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("effectiveStartTime")));
        // 构建 effectiveEndTime 的查询条件
        BoolQueryBuilder endTimeQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery("effectiveEndTime").gte(currentTime))
                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("effectiveEndTime")));
        // 将两个条件组合在一起
        // boolQuery.must(startTimeQuery);
        // boolQuery.must(endTimeQuery);
        must.add(startTimeQuery);
        must.add(endTimeQuery);
        // 检索精确回答的数据
        must.add(QueryBuilders.termsQuery("accurate.keyword", YesNoEnum.YES.getName()));
        if (StringUtils.isNotBlank(dto.getSuggest())) {
            must.add(QueryBuilders.termsQuery("suggest.keyword", dto.getSuggest()));
        }
        return bool;
    }


    /**
     * （写这个方法主要是为了解决demo和企商代码不一致的问题）
     * @param dto
     * @return
     */
    public BoolQueryBuilder prerequisiteByVectorWay(DialogueByStreamParam dto) {
        // 获取知识库 id
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        List<QueryBuilder> must = bool.must();
        // 精确匹配知识库id
        must.add(QueryBuilders.termsQuery("knowledgeId.keyword", dto.getKnowledgeIdList()));
        must.add(QueryBuilders.termsQuery("delStatus.keyword", NumberConstants.ZERO + StringConstant.BLANK));
        // 检索精确回答的数据
        must.add(QueryBuilders.termsQuery("accurate.keyword", YesNoEnum.YES.getName()));
        if (StringUtils.isNotBlank(dto.getSuggest())) {
            must.add(QueryBuilders.termsQuery("suggest.keyword", dto.getSuggest()));
        }
        return bool;
    }

    /**
     * 全文检索
     */
    public void fullTextSearch(String dense, DialogueByStreamParam dto, BoolQueryBuilder mustCondition) {
        List<QueryBuilder> must = mustCondition.must();
        // 精确匹配知识库id
        if (dense.contains("title")) {
            must.add(QueryBuilders.matchQuery("title", dto.getContentTemp()));
        } else if (dense.contains("content")) {
            must.add(QueryBuilders.matchQuery("content", dto.getContentTemp()));
        }
    }

    /**
     * 语义检索
     */
    public ScriptScoreQueryBuilder semanticSearch(BoolQueryBuilder mustCondition, String dense, DialogueByStreamParam dto, float score, List<String> knowldegeIdList) {
        List<QueryBuilder> must = mustCondition.must();

        // 检索相似度
        JSONObject jsonObject = new JSONObject();
        // List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();
        List<Double> doubles = denseVectorService.modelEncode(dto.getContentTemp(), knowldegeIdList.get(0));
        if (doubles.size() == 1024) {
            dense = dense + "1024";
        }
        must.add(QueryBuilders.existsQuery(dense));
        // List<Double> doubles = knowledgeDataService.modelEncode(dto.getContentTemp());
        jsonObject.put("queryVector", doubles);
        Script script = new Script(ScriptType.INLINE, "painless", "cosineSimilarity(params.queryVector,'" + dense + "')+1.0", jsonObject);

        ScriptScoreQueryBuilder scriptScoreQueryBuilder = QueryBuilders.scriptScoreQuery(mustCondition, script);
        // 设置分数阈值
        scriptScoreQueryBuilder.setMinScore(score);
        return scriptScoreQueryBuilder;
    }

    public ScriptScoreQueryBuilder semanticSearchByVectorWay(BoolQueryBuilder mustCondition, String dense, DialogueByStreamParam dto, float score) {
        List<QueryBuilder> must = mustCondition.must();

        // 检索相似度
        JSONObject jsonObject = new JSONObject();
        List<String> knowldegeIdList = dto.getKnowledgeIdList();
        List<Double> doubles = denseVectorService.modelEncode(dto.getContentTemp(), knowldegeIdList.get(0));
        if (doubles.size() == 1024) {
            dense = dense + "1024";
        }
        must.add(QueryBuilders.existsQuery(dense));
        //List<Double> doubles = knowledgeDataService.modelEncode(dto.getContentTemp());
        jsonObject.put("queryVector", doubles);
        Script script = new Script(ScriptType.INLINE, "painless", "cosineSimilarity(params.queryVector,'" + dense + "')+1.0", jsonObject);

        ScriptScoreQueryBuilder scriptScoreQueryBuilder = QueryBuilders.scriptScoreQuery(mustCondition, script);
        // 设置分数阈值
        scriptScoreQueryBuilder.setMinScore(score);
        return scriptScoreQueryBuilder;
    }

    /**
     * 替换特殊字符，避免json解析失败
     */
    public static String replaceText(String text) {
        return AnswerUtils.dealLLmJsonNewLine(text)
                .replace("\\\\t", StringConstant.BLANK)
                .replaceAll("\t*", StringConstant.BLANK)
                .replaceAll("“?问题”?:", "\"问题\":")
                .trim();
    }

    /**
     * 补全json
     */
    public static String dealJson(String conversationName) {
        if (StringUtils.isNotBlank(conversationName) && !JSONUtil.isTypeJSONObject(conversationName)) {
            // 补全json
            conversationName = completeJson(conversationName);
        } else {
            try {
                JSON.parseObject(conversationName);
            } catch (Exception e) {
                conversationName = completeJson(conversationName);
            }
        }
        return conversationName;
    }

    /**
     * 处理json后其他符号
     */
    public static String dealJsonOther(String conversationName) {
        if (conversationName.startsWith("\"")) {
            conversationName = conversationName.replaceFirst("\"", "");
        }
        if (conversationName.endsWith("\"")) {
            conversationName = conversationName.substring(0, conversationName.length() - 1);
        }

        conversationName = conversationName
                .replace("\"", QUOTE_REPLACE_SYMBOL)
                .replaceAll("-\\|-: *-\\|-", "\":\"")
                .replaceAll("\\{[\n ]*-\\|-", "{\"")
                .replaceAll("-\\|-[\n ]*}", "\"}")
                .replaceAll("-\\|-[\n ]*,[\n ]*-\\|-", "\",\"");
        return conversationName;
    }

    /**
     * 处理异常json格式
     */
    public static String dealJsonException(String conversationName, String key) {
        // 解析失败，则截取字符串
        if (conversationName.contains(key)) {
            int indexOf = conversationName.indexOf(key);
            conversationName = conversationName.substring(indexOf + 2);
            // 直接截取到",即可，这之前都是都答案，这之后不算答案
            if (conversationName.contains("\",")) {
                indexOf = conversationName.indexOf("\",");
                conversationName = conversationName.substring(0, indexOf);
            }
        }
        return conversationName.replaceAll("\" ?: ?\"", "")
                .replaceAll("\" ?, ?\"", "");
    }


    /**
     * 通过大模型重新筛选QA对
     */
    private KnowledgeData filterQaByLlm(String question, List<KnowledgeData> knowledgeDataList, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setStep(FIND_QA_FILTER);
        step.setCreateTime(DateUtil.getCurrentTime());
        contextList.add(step);
        QaFilter qaFilter = new QaFilter();
        qaFilter.setOriginalQuestion(question);
        List<QaFilter.Scene> scenes = Lists.newArrayList();
        int size = knowledgeDataList.size();
        for (int i = 0; i < size; i++) {
            scenes.add(new QaFilter.Scene(knowledgeDataList.get(i).getTitle(), knowledgeDataList.get(0).getContent(), i + 1));
        }
        qaFilter.setScenes(scenes);
        LocalDateTime now = LocalDateTimeUtil.now();
        // 获取当前星期几，比如“星期六”
        String dayOfWeek = now.format(DateTimeFormatter.ofPattern("EEEE"));
        String time = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy年MM月dd日 HH:mm:ss");
        String qaScenesFilter = AppConfigContant.getConfiguration(LLM_PROMPT_SYSTEM_QA_SCENES_FILTER);
        if (StringUtils.isBlank(qaScenesFilter)) {
            return null;
        }
        qaScenesFilter = qaScenesFilter.replace(DATE_PLACEHOLDER, time + "，" + dayOfWeek)
                .replace(SIZE_PLACEHOLDER, size + StringConstant.BLANK);
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        LlmStrategy activeLLm = llmInfoService.getActiveLLm(applicationInfo.getModelId());
        List<YayiMessage> messagesList = Lists.newArrayList();
        messagesList.add(new YayiMessage("system", qaScenesFilter));
        String generate = activeLLm.generate(JSON.toJSONString(qaFilter), messagesList, step, false);

        // 如果是高危，则切换大模型
        if (StringUtils.isNotBlank(generate) && generate.toLowerCase().contains(LLM_ERROR_RISK)) {
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
                    step.setParam(jsonObject);
                    generate = activeLLm.generate(JSON.toJSONString(qaFilter), messagesList, step, false);
                    if (!generate.toLowerCase().contains(LLM_ERROR_RISK)) {
                        break;
                    }
                }
            }
        }
        step.setResult(generate);
        // 解析json
        KnowledgeData knowledgeData = parseLlmJson(generate, knowledgeDataList);
        step.setEndTime(DateUtil.getCurrentTime());
        step.setCostTime(System.currentTimeMillis() - start);
        return knowledgeData;
    }

    /**
     * 处理大模型返回的json，去掉换行
     * @param llmJsonStr
     * @return
     */
    public static String dealLLmJson(String llmJsonStr) {
        if (StringUtils.isBlank(llmJsonStr)) {
            return StringConstant.BLANK;
        }
        if (llmJsonStr.contains(INFERENCE_START_TAG)) {
            if (llmJsonStr.contains(INFERENCE_END_TAG)) {
                int indexOf = llmJsonStr.indexOf(INFERENCE_END_TAG);
                llmJsonStr = llmJsonStr.substring(indexOf + INFERENCE_END_TAG.length());
            } else {
                return StringConstant.BLANK;
            }
        }
        if (StringUtils.isBlank(llmJsonStr)) {
            return StringConstant.BLANK;
        }
        return dealLLmJsonNewLine(llmJsonStr)
                .replaceAll("\\\\+[ntr]+", StringConstans.BLANK)
                .replaceAll("[\n\t\r ]+", StringConstans.BLANK)
                .replaceAll("\\\\+$", "");
    }

    /**
     * 处理大模型返回的json，保留换行
     * @param llmJsonStr
     * @return
     */
    public static String dealLLmJsonNewLine(String llmJsonStr) {
        if (StringUtils.isBlank(llmJsonStr)) {
            return StringConstant.BLANK;
        }
        return llmJsonStr
                .replace(BR, "\n")
                .replace(JSON_START, StringConstant.BLANK)
                .replace(JSON_END, StringConstant.BLANK)
                .replace(JSON_ESCAPE, JSON_QUOTE)
                .replaceAll("\\\\{2,}[ntr]+", StringConstans.BLANK);
    }

    /**
     * 解析大模型返回的json
     * @param generate
     * @param knowledgeDataList
     * @return
     */
    private KnowledgeData parseLlmJson(String generate, List<KnowledgeData> knowledgeDataList) {
        if (StringUtils.isNotBlank(generate)) {

            generate = dealLLmJson(generate);
            //JSONObject filterQaResult = JSON.parseObject(generate);
            if (generate.contains("没有符合的对话")) {
                return null;
            }
            if (JSONUtil.isTypeJSONArray(generate)) {
                List<JSONObject> parseArray = JSON.parseArray(generate, JSONObject.class);
                if (CollectionUtils.isNotEmpty(parseArray)) {
                    // 筛选出筛选结果为“完美”的
                    List<Integer> integerList = parseArray.stream().filter(p -> p.getString("筛选结果").equals("完美"))
                            .map(p -> p.getInteger("对话序号"))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(integerList)) {
                        knowledgeDataList = knowledgeDataList.stream().sorted((p1, p2) -> p2.getRangeScore().compareTo(p1.getRangeScore())).collect(Collectors.toList());
                    } else {
                        integerList = parseArray.stream().filter(p -> p.getString("筛选结果").equals("符合"))
                                .map(p -> p.getInteger("对话序号"))
                                .collect(Collectors.toList());
                    }
                    for (int i = 0; i < knowledgeDataList.size(); i++) {
                        if (integerList.contains(i + 1)) {
                            return knowledgeDataList.get(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 标记命中的数据
     */
    public void markData(KnowledgeData knowledgeData, List<KnowledgeData> dataList) {
        dataList.forEach(item->{
            if (null != knowledgeData && item.getId().equals(knowledgeData.getId())) {
                if (StringUtils.isNotBlank(item.getTitle())) {
                    item.setTitle(item.getTitle());
                    //item.setTitle("【命中】" + item.getTitle());
                } else {
                    item.setContent(item.getContent());
                    //item.setContent("【命中】" + item.getContent());
                }
            } else {
                if (StringUtils.isNotBlank(item.getTitle())) {
                    item.setTitle(item.getTitle());
                    //item.setTitle("【丢弃】" + item.getTitle());
                } else {
                    item.setContent(item.getContent());
                    //item.setContent("【丢弃】" + item.getContent());
                }
            }
        });
    }


    /**
     * 解析答案
     * @param conversationName
     * @return
     */
    public static String analyticAnswer(String conversationName) {
        if (StringUtils.isBlank(conversationName)) {
            return StringConstant.BLANK;
        }

        String backslashes = extractBackslashes(conversationName);
        if (StringUtils.isNotBlank(backslashes)) {
            conversationName = conversationName.substring(0, conversationName.lastIndexOf(backslashes));
        }
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String notAnswer = applicationInfo.getNotAnswer();
        String conversationNameTemp = conversationName;
        // 替换特殊字符，避免json解析失败
        conversationName = replaceText(conversationName);

        // 解析json
        conversationName = dealJson(conversationName);

        // 处理json后其他符号
        conversationName = dealJsonOther(conversationName);
        if (StringUtils.isNotBlank(conversationName) && JSONUtil.isTypeJSONObject(conversationName)) {
            try {
                // 如果答案中包含无法回答的文本，则返回无法回答
                if (conversationName.contains(notAnswer)) {
                    return notAnswer;
                }

                // 替换特殊字符
                cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(conversationName);
                conversationName = (String) jsonObject.getOrDefault(ANSWER_FIELD, StringConstant.BLANK);
                conversationName = conversationName.replace(QUOTE_REPLACE_SYMBOL, JSON_QUOTE);
                if (StringUtils.isBlank(conversationName)) {
                    return StringConstant.BLANK;
                }

                // 长度小于5，则返回空
                if (conversationName.length() < notAnswer.length() + 1) {
                    return StringConstant.BLANK;
                }

                // 引用知识库为空，则返回无法回答
                // String string = jsonObject.getString(REFERENCE_FIELD);
                // if (StringUtils.isNotBlank(string) && NO_REFERENCE_FIELD.startsWith(string)) {
                //     return notAnswer;
                // }

                // 如果答案与无法回答相同，则返回无法回答
                if (notAnswer.equalsIgnoreCase(conversationName)) {
                    return notAnswer;
                }

                // 如果答案是数组，则转换为字符串
                if (StringUtils.isNotBlank(conversationName) && JSONUtil.isTypeJSONArray(conversationName)) {
                    JSONArray jsonArray = JSON.parseArray(conversationName);
                    conversationName = jsonArray.stream().map(Object::toString).collect(Collectors.joining("\n"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 解析失败，返回原始数据
                conversationName = dealJsonException(conversationNameTemp, ANSWER_FIELD);
            }
        }
        // 替换特殊字符
        //conversationName = replaceText(conversationName);
        //log.info("conversationName" + conversationName);
        // 补上/
        String replace = conversationName.replace(QUOTE_REPLACE_SYMBOL, JSON_QUOTE);
        return replace + backslashes;
    }

    /**
     * 风险处理
     * @param dto
     * @param contextList
     */
    public void dealRisk(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(step);
        step.setStep(RISK_STEP);

        // 记录日子好
        StepEntity stepRef = new StepEntity();
        stepRef.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(stepRef);
        stepRef.setStep(FIND_ANSWER_BY_MODEL_REF);

        // 设置模型答案标识
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        applicationInfo.setModelAnswerFlag(YesNoEnum.YES.getName());

        // 模型答案记录
        AnswerStepData answerStep = new AnswerStepData();
        answerStep.setIndex(0);
        answerStep.setStep(FIND_ANSWER_BY_MODEL);
        answerStep.setAnswer(applicationInfo.getFailureTalk());
        answerStep.setStatus(StepStatusEnum.ANSWER_COMPLETE);
        answerStep.setStreamStatus(NumberConstants.ZERO);

        if (StringUtils.isBlank(dto.getContentTemp())) {
            dto.setContentTemp(dto.getContent());
        }
        //answerStrategy.getAnswer(dto, contextList, answerStep);

        // 获取风险系统提示
        String doorsillRiskLlmSystemPrompt = getConfiguration(applicationInfo.getApplicationId(), DOORSILL_RISK_LLM_SYSTEM_PROMPT);
        if (StringUtils.isNotBlank(doorsillRiskLlmSystemPrompt)) {
            String dateForPrompt = com.wenge.model.utils.DateUtil.getDateForPrompt();
            doorsillRiskLlmSystemPrompt = doorsillRiskLlmSystemPrompt.replace(DATE_PLACEHOLDER, dateForPrompt);
        }

        stepRef.setPrompt(dto.getContent());

        // 生成风险答案
        String generateCommon = getGenerateCommon(dto.getContent(), StringConstant.BLANK, step, ListUtil.toList(), result -> {
            try {
                if (null == step.getFirstLlmTime()) {
                    step.setFirstLlmTime(System.currentTimeMillis() - start);
                    stepRef.setFirstLlmTime(System.currentTimeMillis() - start);
                }
                String answer = result.getAnswer();
                answerStep.setStatus(result.getStatus());
                answerStep.setAnswer(answer);
                dto.getAnswerConsumer().accept(answerStep);
            } catch (Exception e) {
                e.printStackTrace();
                answerStep.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        }, doorsillRiskLlmSystemPrompt);
        if (generateCommon.equals(AnswerStrategyContant.NO_ANSWER_TEXT)) {
            answerStep.setStatus(StepStatusEnum.ANSWER_FAIL);
        }
        answerStep.setAnswer(generateCommon);
        stepRef.setResult(generateCommon);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        stepRef.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        stepRef.setCostTime(System.currentTimeMillis() - start);
        log.info("风控大模型回答:{}", generateCommon);

    }

    /**
     * 根据知识库ID获取知识库数据
     *
     * @param dto
     * @param dense
     * @param score
     * @param step
     * @param knowldegeIdList
     * @return
     */
    public List<KnowledgeData> getDataByKnowledgeId(DialogueByStreamParam dto, String dense, float score, StepEntity step, List<String> knowldegeIdList) {
        // 使用原问题检索QA对-根据评分查询QA对
        if (StringUtils.isBlank(dto.getContentTemp())) {
            dto.setContentTemp(dto.getContent());
        }
        LambdaEsQueryWrapper<KnowledgeData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeData.class);
        SearchSourceBuilder searchSourceBuilder = knowledgeDataMapper.getSearchSourceBuilder(esQueryWrapper);
        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (null == applicationInfo) {
            log.info("AnswerUtils applicationInfo is null");
            return Lists.newArrayList();
        }
        // 获取查询方式
        QueryBuilder queryByWay = getQueryByWay(dense, dto, score, knowldegeIdList);
        SearchSourceBuilder query = searchSourceBuilder.query(queryByWay);
        query.fetchSource(null, new String[]{AnswerStrategyContant.TITLE_DENSE_FILED, CONTENT_DENSE_FILED});
        esQueryWrapper.setSearchSourceBuilder(query);
        // 设置返回数量
        int prepareNum = buildPrepareNum(applicationInfo);
        query.size(prepareNum);
        query.from(0);
        esQueryWrapper.size(prepareNum);
        List<KnowledgeData> dataList = knowledgeDataMapper.selectList(esQueryWrapper);
        String source = knowledgeDataMapper.getSource(esQueryWrapper);
        step.setPrompt(source);
        return dataList;

        // 获取评分前几的场景
        // List<KnowledgeData> sceneData = getSceneData(dto.getContentTemp(), dataList, stepModel, rangeScore, dense);
        // step.setResult(sceneData);
        // KnowledgeData knowledgeData = null;
        //
        // // QA要通过大模型筛选
        // if (CollectionUtils.isNotEmpty(sceneData) && !KNOWLEDGE_CONTENT_STEP.equals(step.getStep())) {
        //     knowledgeData = sceneData.get(0);
        //     // 标记命中的数据
        //     String qaScenesFilterEnable = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), QA_SCENES_FILTER_ENABLE);
        //     String qaScenesFilter = AppConfigContant.getConfiguration(LLM_PROMPT_SYSTEM_QA_SCENES_FILTER);
        //     if (YesNoEnum.YES.getName().equals(qaScenesFilterEnable) && StringUtils.isNotBlank(qaScenesFilter)) {
        //         if (StringUtils.isBlank(dto.getContent())) {
        //             dto.setContent(dto.getContentTemp());
        //         }
        //         knowledgeData = filterQaByLlm(dto.getContent(), sceneData, contextList);
        //     }
        // }
        //
        // if (null != knowledgeData) {
        //     // 将对象移动到第一个
        //     dataList.remove(knowledgeData);
        //     dataList.add(0, knowledgeData);
        //     // 标记第一个命中的
        //     markData(knowledgeData, dataList);
        // } else {
        //     if (StringUtils.isNotBlank(dto.getClientId())) {
        //         return Lists.newArrayList();
        //     } else {
        //         // 标记第一个命中的
        //         markData(knowledgeData, dataList);
        //         return dataList;
        //     }
        // }
        // return sceneData;

    }

    /**
     * 提取以\结尾的字符
     * @param input
     * @return
     */
    public static String extractBackslashes(String input) {
        // 正则表达式匹配字符串结尾的连续反斜杠
        Pattern pattern = Pattern.compile("\\\\+$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            // 返回匹配到的反斜杠
            return matcher.group();
        } else {
            // 如果没有匹配到，返回空字符串
            return StringConstant.BLANK;
        }
    }

    private List<KnowledgeData> getByQuery(String content, ApplicationInfo applicationInfo, List<String> knowldegeIdList) {
        if (StringUtils.isBlank(content)) {
            return ListUtil.toList();
        }

        LambdaEsQueryWrapper<KnowledgeData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeData.class);
        SearchSourceBuilder searchSourceBuilder = knowledgeDataMapper.getSearchSourceBuilder(esQueryWrapper);
        // List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();

        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        List<QueryBuilder> must = bool.must();
        // 未删除的数据
        must.add(QueryBuilders.termQuery(DEL_STATUS_FIELD, "0"));
        // 精确匹配知识库id
        must.add(QueryBuilders.termsQuery(KNOWLEDGE_ID_FIELD, knowldegeIdList));
        // 检索非精确回答的知识库数据
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<QueryBuilder> should = boolQueryBuilder.should();
        should.add(QueryBuilders.termQuery(ACCURATE_FIELD, YesNoEnum.NO.getName()));
        should.add(QueryBuilders.termQuery(ACCURATE_FIELD, StringConstant.BLANK));
        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
        List<QueryBuilder> mustNot = boolQueryBuilder1.mustNot();
        mustNot.add(QueryBuilders.existsQuery("accurate"));
        should.add(boolQueryBuilder1);
        must.add(boolQueryBuilder);
        must.add(QueryBuilders.multiMatchQuery(content, CONTENT_FIELD, TITLE_FIELD));

        JSONObject jsonObject = new JSONObject();
        List<Double> doubles = denseVectorService.modelEncode(content, knowldegeIdList.get(0));
        jsonObject.put(QUERY_VECTOR_FIELD, doubles);
        String fiedlNmae = CONTENT_DENSE_FILED;
        if (doubles.size() == 1024) {
            fiedlNmae = CONTENT_DENSE_FILED_1024;
        }

        must.add(QueryBuilders.existsQuery(fiedlNmae));
        Script script = new Script(ScriptType.INLINE, SCRIPT_TYPE, "cosineSimilarity(params.queryVector,'" + fiedlNmae + "')+1.0", jsonObject);

        ScriptScoreQueryBuilder scriptScoreQueryBuilder = QueryBuilders.scriptScoreQuery(bool, script);
        scriptScoreQueryBuilder.setMinScore(applicationInfo.getContentScore());
        SearchSourceBuilder query = searchSourceBuilder.query(scriptScoreQueryBuilder);
        query.fetchSource(null, new String[]{TITLE_DENSE_FILED, CONTENT_DENSE_FILED});
        esQueryWrapper.setSearchSourceBuilder(query);
        int prepareNum = buildPrepareNum(applicationInfo);
        query.size(prepareNum);
        query.from(0);
        // String source = knowledgeDataMapper.getSource(esQueryWrapper);
        // step.setPrompt(source);
        // step.setResult(knowledgeData.size() + "条");
        return knowledgeDataMapper.selectList(esQueryWrapper);
    }

    /**
     * 初始化知识数据
     *
     * @param knowledgeData
     * @return
     */
    public static AnswerRefParam initAnswerRefByKnow(KnowledgeData knowledgeData) {
        AnswerRefParam answerRefParam = new AnswerRefParam();
        if (null == knowledgeData) {
            return answerRefParam;
        }
        answerRefParam.setTitle(knowledgeData.getTitle());
        answerRefParam.setContent(knowledgeData.getContent());
        answerRefParam.setUrl(knowledgeData.getLink());
        answerRefParam.setSourceType(2);
        return answerRefParam;
    }


    /**
     * 初始化对话
     * @param dialogueStepList
     * @return
     */
    public static List<DialogueStep> initDialogue(List<StepEntity> dialogueStepList) {
        if (CollectionUtils.isNotEmpty(dialogueStepList)) {
            try {
                List<DialogueStep> stepList = dialogueStepList.stream().map(p -> {
                    DialogueStep dialogueStep = new DialogueStep();
                    if (p.getPrompt() != null && p.getResult() != null ) {
                        dialogueStep.setIsShow(ShowContant.SHOW);
                        if (p.getResult() instanceof String && (StringUtils.isBlank((String)p.getResult())||p.getResult().equals("是否开启问题拆解：null")||p.getResult().equals("0条"))) {
                            dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                        }else if (p.getResult() instanceof Object[]&& ((Object[]) p.getResult()).length==0) {
                            dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                        }
                    }else {
                        dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                    }
                    if (null != p.getPrompt()) {
                        if (p.getPrompt() instanceof String) {
                            dialogueStep.setPrompt(p.getPrompt());
                        } else {
                            dialogueStep.setPrompt(com.alibaba.fastjson2.JSON.toJSONString(p.getPrompt()));
                        }
                    }
                    if (null != p.getResult()) {
                        if (p.getResult() instanceof String) {
                            dialogueStep.setResult(p.getResult());
                        } else {
                            dialogueStep.setResult(JSONUtil.toJsonStr(p.getResult()));
                        }
                    }

                    dialogueStep.setStep(p.getStep());
                    if (StringUtils.isBlank(p.getStep())) {
                        dialogueStep.setStep("未指定步骤");
                    }
                    dialogueStep.setDialogueId(p.getDialogueId());
                    dialogueStep.setCreateTime(p.getCreateTime());
                    dialogueStep.setCostTime(p.getCostTime());
                    dialogueStep.setFirstLlmTime(p.getFirstLlmTime());
                    dialogueStep.setEndTime(p.getEndTime());
                    dialogueStep.setTokenTotal(p.getTokenTotal());
                    return dialogueStep;
                }).collect(Collectors.toList());
                return stepList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 构建重排模型
     * @param rearrangeParam 重排参数
     * @param applicationInfo 应用参数
     * @return
     */
    public RearrangeResult buildRearrangeModel(RearrangeParam rearrangeParam, ApplicationInfo applicationInfo) {
        RearrangeResult rearrange = new RearrangeResult();
        String rearrangeModel = StringUtils.EMPTY;
        if (ObjectUtils.isNotEmpty(applicationInfo)) {
            rearrangeModel = applicationInfo.getRearrangeModel();
        }
        if (StringUtils.isBlank(rearrangeModel)
                || StringUtils.equals(rearrangeModel, ApplicationRearrangeModelEnum.YAYI.getCode())) {
            // 雅意重排
            rearrange = yayiServer.rearrange(rearrangeParam);
        } else if (StringUtils.equals(rearrangeModel, ApplicationRearrangeModelEnum.VOLCENGINE.getCode())) {
            // 火山引擎重排
            rearrange = volcengineServer.rearrange(rearrangeParam);
        }

        return rearrange;
    }

    /**
     * 构建知识库文段预备数量(重排前的数量)
     * @param applicationInfo 应用参数
     * @return
     */
    public int buildPrepareNum(ApplicationInfo applicationInfo) {
        if (null == applicationInfo) {
            return 0;
        }

        Integer prepareNumResult = 0;
        Integer prepareNum = applicationInfo.getPrepareNum();
        Integer volcenginePrepareNum = applicationInfo.getVolcenginePrepareNum();
        String rearrangeModel = applicationInfo.getRearrangeModel();
        if (StringUtils.isBlank(rearrangeModel)
                || StringUtils.equals(rearrangeModel, ApplicationRearrangeModelEnum.YAYI.getCode())) {
            // 雅意重排知识库文段数量
            prepareNumResult = prepareNum;
        } else if (StringUtils.equals(rearrangeModel, ApplicationRearrangeModelEnum.VOLCENGINE.getCode())) {
            // 火山引擎重排知识库文段数量
            prepareNumResult = volcenginePrepareNum;
        }

        return null == prepareNumResult ? 0 : prepareNumResult;
    }

    /**
     * 基于余弦相似度去掉重复片段
     *
     * @param contents
     * @return
     */
    public List<String> reduceRepetitionBasedOnCos(List<String> contents) {
        return deduplicate(contents, COS_THRESHOLD);
    }

    /**
     * 根据余弦相似度去重
     * @param texts 待去重文本列表
     * @param threshold 阈值
     * @return 去重后的文本片段
     */

    public static List<String> deduplicate(List<String> texts, double threshold) {
        List<String> result = new ArrayList<>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        for (int i = 0; i < texts.size(); i++) {
            String current = texts.get(i);
            boolean isDuplicate = false;

            // 检查是否与已保留的文本相似
            for (String kept : result) {
                // 两段文本差值超过小文本的长度，判定不相似
                if (Math.abs(current.length() - kept.length()) <= Math.min(current.length(), kept.length())) {
                    Map<CharSequence, Integer> vector1 = createVector(current);
                    Map<CharSequence, Integer> vector2 = createVector(kept);
                    double similarity = cosineSimilarity.cosineSimilarity(vector1, vector2);
                    // 检验相似度阈值
                    if (similarity > threshold) {
                        isDuplicate = true;
                        break;
                    }
                }

            }
            if (!isDuplicate) {
                result.add(current);
            }
        }
        return result;
    }

    private static Map<CharSequence, Integer> createVector(String text) {
        Map<CharSequence, Integer> vector = new HashMap<>();
        String[] words = text.toLowerCase().split("\\W+");

        for (String word : words) {
            vector.put(word, vector.getOrDefault(word, 0) + 1);
        }

        return vector;
    }
}


