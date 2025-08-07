package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.DialogueConstant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.*;
import com.wenge.model.entity.table.ApplicationInfoTableDef;
import com.wenge.model.enums.*;
import com.wenge.model.mapper.FileMapper;
import com.wenge.model.mapper.es.DialogueStepMapper;
import com.wenge.model.mapper.es.KnowledgeDataMapper;
import com.wenge.model.service.*;
import com.wenge.model.strategy.answer.AnswerStrategy;
import com.wenge.model.strategy.recommend.RecommendStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.oauth.constants.AppConfigContant.*;
import static com.wenge.oauth.constants.AppConfigContant.ASSOCIATION_LLM_FLAG;

@Service
@Slf4j
public class RecommendQuestionServiceImpl implements RecommendQuestionService {

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private Map<String, RecommendStrategy> recommendStrategyMap;

    @Autowired
    private DialogueStepMapper dialogueStepMapper;

    @Autowired
    private Map<String, AnswerStrategy> answerStrategyMap;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ApplicationKnowledgeService applicationKnowledgeService;

    @Autowired
    private KnowledgeDataMapper knowledgeDataMapper;

    @Autowired
    private RedisService redisService;

    @Value("${university.api.userInfo:}")
    private String userInfoApi;

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    private ApplicationPluginDataService applicationPluginDataService;

    // 缓存推荐问题
    public static final ThreadLocal<List<RecommendQuestionData>> RECOMMEND_QUESTION_LIST_LOCAL_CACHE = new TransmittableThreadLocal<>();

    @Override
    public Result<List<RecommendQuestionResult>> recommendQuestion(RecommendQuestionParam param) {
        try {
            // 获取应用信息
            Wrappers wrappers = Wrappers.init()
                    .where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(param.getApplicationId()))
                    .limit(1);
            ApplicationInfo applicationInfo = applicationInfoService.getOne(wrappers);
            if (null == applicationInfo) {
                log.warn("应用不存在");
                return Result.success(Lists.newArrayList());
            }

            // 查询插件
            AppConfigListParam applicationPluginData = new AppConfigListParam();
            applicationPluginData.setApplicationId(param.getApplicationId());
            Result<List<ApplicationPluginData>> result = applicationPluginDataService.getApplicationPluginDataList(applicationPluginData);
            List<ApplicationPluginData> pluginDataList = result.getData();
            if (CollectionUtils.isEmpty(pluginDataList)) {
                return Result.success(Lists.newArrayList());
            }

            // 获取插件，如果没有打开推荐，那么就不再推荐
            Optional<ApplicationPluginData> any = pluginDataList.stream().filter(p -> AppPluginEnum.RECOMMENDATION.getCode().equals(p.getPluginCode())).findAny();
            if (!any.isPresent()) {
                return Result.success(Lists.newArrayList());
            }

            // 获取本应用全部配置
            List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(param.getApplicationId());
            if (CollectionUtils.isEmpty(configurationList)) {
                return Result.success(Lists.newArrayList());
            }

            // 封装 k-v键对
            Map<String, String> configMap = configurationList.stream()
                    .filter(p -> StringUtils.isNotBlank(p.getKeyInfo()) && StringUtils.isNotBlank(p.getValueInfo()))
                    .collect(Collectors.toMap(
                            ApplicationConfiguration::getKeyInfo,
                            ApplicationConfiguration::getValueInfo,
                            (v1, v2) -> v1,
                            Maps::newHashMap
                    ));
            // 获取推荐开关
            String recommendedEnable = configMap.get(RECOMMENDED_ENABLE);
            if (!YesNoEnum.YES.getName().equals(recommendedEnable)) {
                return Result.success(Lists.newArrayList());
            }

            // 获取应用绑定的知识库
            List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(param.getApplicationId());
            List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
            // 保存知识库id
            DialogueServiceImpl.KNOWLEDGE_ID_LIST.set(knowledgeIdList);
            DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);

            // 优先使用模糊引导的推荐
            RecommendStrategy recommendStrategy = recommendStrategyMap.get(QA_QUESTION_STRATEGY);
            List<RecommendQuestionResult> recommendQuestionResults = recommendStrategy.recommendQuestionByVague(param, dialogueStepMapper);
            if (CollectionUtils.isNotEmpty(recommendQuestionResults)) {
                return Result.success(recommendQuestionResults);
            }

            // 收集所有的推荐问题
            List<RecommendQuestionResult> allRecommend = Lists.newArrayList();

            // 先匹配问题
            String recommendedQFlag = configMap.get(RECOMMENDED_Q_FLAG);
            String recommendedNum = configMap.get(RECOMMENDED_NUM);
            int num = Integer.parseInt(recommendedNum);
            if (YesNoEnum.YES.getName().equals(recommendedQFlag)) {
                List<RecommendQuestionResult> recommendQuestion = getRecommendQuestion(param, QA_QUESTION_STRATEGY);
                allRecommend.addAll(recommendQuestion);
            }

            // 再匹配答案
            String recommendedAFlag = configMap.get(RECOMMENDED_A_FLAG);
            if (allRecommend.size() < num && YesNoEnum.YES.getName().equals(recommendedAFlag)) {
                List<RecommendQuestionResult> recommendQuestion = getRecommendQuestion(param, QA_CONTENT_STRATEGY);
                allRecommend.addAll(recommendQuestion);
            }

            // 匹配知识库
            String recommendedKnnFlag = configMap.get(RECOMMENDED_KNN_FLAG);
            if (allRecommend.size() < num && YesNoEnum.YES.getName().equals(recommendedKnnFlag)) {
                List<RecommendQuestionResult> recommendQuestion = getRecommendQuestion(param, QA_KNOWLEDGE_STRATEGY);
                allRecommend.addAll(recommendQuestion);
            }

            // 大模型发散
            String recommendedLLMFlag = configMap.get(RECOMMENDED_LLM_FLAG);
            if (allRecommend.size() < num && YesNoEnum.YES.getName().equals(recommendedLLMFlag)) {
                List<RecommendQuestionResult> recommendQuestion = getRecommendQuestion(param, QA_LARGEMODEL_STRATEGY);
                allRecommend.addAll(recommendQuestion);
            }

            // 防止最终结果超过设置的数量
            List<RecommendQuestionResult> recommendQuestionResultList = allRecommend.stream()
                    .limit(num)
                    .collect(Collectors.toList());
            return Result.success(recommendQuestionResultList);

            // 根据推荐策略获取推荐问题
            // List<RecommendQuestionResult> recommendQuestion = recommendStrategy.getRecommendQuestion(param);
            // List<RecommendQuestionResult> results = recommendQuestion.stream().filter(p -> !param.getQuestion().equals(p.getQuestion())).collect(Collectors.toList());
/*            ThreadPoolExecutor recommendPool = ExecutorBuilder.create().setCorePoolSize(10).setMaxPoolSize(20).setWorkQueue(new LinkedBlockingDeque<>()).build();
            // 异步获取推荐问题
            dealRecommendAsync(param, recommendPool);
            // 监听线程池返回的数据
            List<RecommendQuestionResult> results = listenRecommend(recommendPool);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RECOMMEND_QUESTION_LIST_LOCAL_CACHE.remove();
        }
        return Result.success(Lists.newArrayList());
    }

    /**
     * 根据推荐策略异步检索推荐问题
     * @param param
     * @param pool
     */
    private void dealRecommendAsync(RecommendQuestionParam param, ThreadPoolExecutor pool) {
        if (null == RECOMMEND_QUESTION_LIST_LOCAL_CACHE.get()) {
            RECOMMEND_QUESTION_LIST_LOCAL_CACHE.set(Lists.newArrayList());
        }
        RecommendQuestionData recommendQuestionData = new RecommendQuestionData();
        for (Map.Entry<String, RecommendStrategy> entry : recommendStrategyMap.entrySet()) {
            RecommendStrategy recommendStrategy = entry.getValue();
            String recommendStrategyName = entry.getKey();
            if (null != recommendStrategy) {
                pool.execute(() -> {
                    // 优先从配置的模糊引导问题里面找推荐问题
                    List<RecommendQuestionResult> recommendQuestionResults = recommendStrategy.recommendQuestionByVague(param, dialogueStepMapper);
                    if (CollectionUtils.isNotEmpty(recommendQuestionResults)) {
                        return;
                    }
                    // 根据推荐策略获取推荐问题
                    List<RecommendQuestionResult> recommendQuestion = recommendStrategy.getRecommendQuestion(param);
                    List<RecommendQuestionResult> results = recommendQuestion.stream().filter(p -> !param.getQuestion().equals(p.getQuestion())).collect(Collectors.toList());
                    recommendQuestionData.setRecommendStrategyName(recommendStrategyName);
                    recommendQuestionData.setRecommendQuestionResultList(results);
                    RECOMMEND_QUESTION_LIST_LOCAL_CACHE.get().add(recommendQuestionData);
                });
            }
        }
    }

    /**
     * 监听推荐问题的线程池结果
     * @param recommendPool 推荐策略线程池
     * @return 推荐问题结果列表
     */
    private List<RecommendQuestionResult> listenRecommend(ThreadPoolExecutor recommendPool) {
        // 最大推荐问题数量
        final int maxRecommendations = 3;
        // 超时时间60秒
        final long timeoutMillis = 60_000;
        List<RecommendQuestionResult> results = new ArrayList<>(maxRecommendations);
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline && results.size() < maxRecommendations) {
            // 检查线程池是否已完成任务
            boolean poolFinished = recommendPool.getActiveCount() == 0 || recommendPool.isShutdown() || recommendPool.isTerminated();
            if (!poolFinished) {
                ThreadUtil.safeSleep(20);
                continue;
            }
            // 获取推荐结果数据
            List<RecommendQuestionData> dataList = Optional.ofNullable(RECOMMEND_QUESTION_LIST_LOCAL_CACHE.get())
                    .orElse(Collections.emptyList());
            Map<String, List<RecommendQuestionResult>> strategyResults = dataList.stream()
                    .collect(Collectors.toMap(
                            RecommendQuestionData::getRecommendStrategyName,
                            RecommendQuestionData::getRecommendQuestionResultList,
                            (existing, replacement) -> existing,
                            LinkedHashMap::new
                    ));
            // 按优先级处理推荐策略
            processRecommendationStrategy(strategyResults, results, maxRecommendations);
            // 如果已收集足够的推荐结果，提前退出
            if (results.size() >= maxRecommendations) {
                break;
            }
        }

        // 确保返回结果不超过最大数量
        return results.size() > maxRecommendations ?
                results.subList(0, maxRecommendations) :
                results;
    }


    /**
     * 按策略优先级处理推荐结果
     * @param strategyResults
     * @param results
     * @param maxResults
     */
    private void processRecommendationStrategy(Map<String, List<RecommendQuestionResult>> strategyResults, List<RecommendQuestionResult> results, int maxResults) {
        // 定义策略优先级
        RecommendStrategyNameEnum[] priorityStrategies = {
                RecommendStrategyNameEnum.QA_QUESTION,
                RecommendStrategyNameEnum.QA_CONTENT,
                RecommendStrategyNameEnum.KNOWLEDGE,
                RecommendStrategyNameEnum.QA_LARGEMODEL
        };

        for (RecommendStrategyNameEnum strategy : priorityStrategies) {
            List<RecommendQuestionResult> strategyResultsList = strategyResults.get(strategy.getCode());
            if (CollectionUtils.isNotEmpty(strategyResultsList)) {
                // 计算还能添加多少结果
                int remainingSlots = maxResults - results.size();
                if (remainingSlots <= 0) break;
                // 添加推荐结果
                results.addAll(strategyResultsList.stream()
                        .limit(remainingSlots)
                        .collect(Collectors.toList()));
            }
        }
    }

    @Override
    public List<RecommendQuestionResult> recommendKnowledgeQAQuestion(RecommendQuestionParam param) {
        // 获取应用信息
        Wrappers wrappers = Wrappers.init()
                .where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(param.getApplicationId()))
                .limit(1);
        ApplicationInfo applicationInfo = applicationInfoService.getOne(wrappers);
        if (null == applicationInfo) {
            log.warn("应用不存在");
            return Lists.newArrayList();
        }

        // 获取应用绑定的知识库
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(param.getApplicationId());
        List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(knowledgeIdList)) {
            log.warn("未绑定知识库");
            return Lists.newArrayList();
        }
        // 保存知识库id
        DialogueServiceImpl.KNOWLEDGE_ID_LIST.set(knowledgeIdList);
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        // 获取QA策略
        RecommendStrategy recommendStrategy = recommendStrategyMap.get(AnswerStrategyContant.QA_STRATEGY_NAME);
        if (null != recommendStrategy) {
            List<RecommendQuestionResult> recommendQuestion = getQARecommendQuestion(param);
            return recommendQuestion.stream().filter(p -> !param.getQuestion().equals(p.getQuestion())).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     *
     * @param param 参数
     * @return 推荐问题列表
     */
    private List<RecommendQuestionResult> getQARecommendQuestion(RecommendQuestionParam param) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        StepEntity step = new StepEntity();
        step.setStep("推荐问答的问题");
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setSuggest("推荐");
        dto.setContentTemp(param.getQuestion());
        String guideQuestionNumberConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.GUIDE_QUESTION_NUMBER_CONFIG);
        if (StringUtils.isBlank(guideQuestionNumberConfig)) {
            log.info("guideQuestionNumberConfig配置丢失，要配置对应的值");
            return new ArrayList<>();
        }
        int guideQuestionNumber = 3;
        try {
            guideQuestionNumber = Integer.parseInt(guideQuestionNumberConfig);
        } catch (NumberFormatException e) {
            log.error("guideQuestionNumberConfig配置错误，无法将字符串转换为浮点数: " + e.getMessage());
        }
        applicationInfo.setFilterNum(guideQuestionNumber);
        String qaTitleScoreConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.QA_TITLE_SCORE_CONFIG);
        if (StringUtils.isBlank(qaTitleScoreConfig)) {
            log.info("qaTitleScoreConfig配置丢失，要配置对应的值");
            return new ArrayList<>();
        }
        String qaRangeTitleScoreConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.QA_RANGE_TITLE_SCORE);
        if (StringUtils.isBlank(qaRangeTitleScoreConfig)) {
            log.info("qaRangeTitleScoreConfig配置丢失，要配置对应的值");
            return new ArrayList<>();
        }
        float qaTitleScore = 0;
        try {
            qaTitleScore = Float.parseFloat(qaTitleScoreConfig);
        } catch (NumberFormatException e) {
            log.error("qaTitleScoreConfig配置错误，无法将字符串转换为浮点数: " + e.getMessage());
        }
        float qaRangeTitleScore = 0f;
        try {
            qaRangeTitleScore = Float.parseFloat(qaRangeTitleScoreConfig);
        } catch (NumberFormatException e) {
            log.error("qaRangeTitleScoreConfig配置错误，无法将字符串转换为浮点数: " + e.getMessage());
        }

        Vector<StepEntity> contextList = new Vector<>();
        List<KnowledgeData> knowledgeData = answerUtils.byDense(dto, AnswerStrategyContant.TITLE_DENSE_FILED, qaTitleScore, step, step, qaRangeTitleScore, contextList);
        if (CollectionUtil.isNotEmpty(knowledgeData)) {
            // 根据title去重后取前三个
            Set<String> seenTitles = new LinkedHashSet<>();
            knowledgeData = knowledgeData.stream()
                    .filter(v -> StringUtils.isNotEmpty(v.getTitle()) && seenTitles.add(v.getTitle()))
                    .limit(guideQuestionNumber)
                    .collect(Collectors.toList());
            List<RecommendQuestionResult> recommendQuestionResults = knowledgeData.stream().map(p -> {
                RecommendQuestionResult recommend = new RecommendQuestionResult();
                recommend.setQuestion(p.getTitle());
                recommend.setAnswer(p.getContent());
                recommend.setLink(p.getLink());
                return recommend;
            }).collect(Collectors.toList());

            return recommendQuestionResults;
        }

        return Lists.newArrayList();
    }

    @Override
    public Result<SourceAnswerFinalResult> sourceAnswer(SourceAnswerParam sourceAnswerParam) {
        SourceAnswerFinalResult result = new SourceAnswerFinalResult();
        try {
            ApplicationInfo applicationInfo = null;
            if (StringUtils.isNotBlank(sourceAnswerParam.getApplicationId())) {
                Wrappers wrappers = Wrappers.init()
                        .where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(sourceAnswerParam.getApplicationId()))
                        .limit(1);
                applicationInfo = applicationInfoService.getOne(wrappers);
                if (null != applicationInfo) {
                    applicationInfo.setFromManage(sourceAnswerParam.getFromManage());
                    DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
                }
            }
            String dialogueId = sourceAnswerParam.getDialogueId();
            if (StringUtils.isBlank(dialogueId)) {
                return Result.success(result);
            }

            // 获取步骤记录
            LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                    .eq(DialogueStep::getDialogueId, dialogueId)
                    .size(999);
            List<DialogueStep> dialogueStepList = dialogueStepMapper.selectList(wrapper);

            // 获取步骤记录，如果没找到，这里监听5秒
            if (CollectionUtils.isEmpty(dialogueStepList)) {
                int timeout = 5000;
                long start = System.currentTimeMillis();
                while (true) {
                    if (CollectionUtils.isNotEmpty(dialogueStepList)) {
                        break;
                    }
                    if (System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                    dialogueStepList = dialogueStepMapper.selectList(wrapper);
                    ThreadUtil.sleep(300);
                }
            }
            List<DialogueStep> dialogueSteps = Lists.newArrayList(dialogueStepList);
            if (CollectionUtils.isNotEmpty(dialogueSteps)) {
                List<SourceAnswerResult> sourceAnswerResultList = new ArrayList<>();

                Optional<DialogueStep> any = dialogueSteps.stream()
                        .filter(p -> p.getStep().contains(AnswerStrategyContant.REF_PREFIX)).findAny();
                HashMap<String, DialogueStep> stepMap = dialogueSteps.stream().collect(Collectors.toMap(
                        DialogueStep::getStep,
                        p -> p,
                        (k1, k2) -> k1,
                        Maps::newHashMap
                ));
                if (any.isPresent()) {
                    // 正常回答时，找到引用的回答
                    DialogueStep dialogueStep = any.get();

                    // 找到源数据
                    answerStrategyMap.forEach((step, strategy) -> {
                        sourceAnswerResultList.addAll(strategy.sourceAnswer(dialogueStep, stepMap));
                    });

                    // 溯源部分不展示知识库文件id
                    sourceAnswerResultList.forEach(item -> {
                        String text = item.getText();
                        if (text.contains(PREFIX_SYMBOL)) {
                            String substring = text.substring(0, text.indexOf(PREFIX_SYMBOL));
                            item.setText(substring);
                        }
                    });
                } else {
                    // 是否有拦截
                    Optional<DialogueStep> optional = dialogueSteps.stream()
                            .filter(p -> StringUtils.isNotBlank(p.getStep()) && p.getStep().contains(INTERCEPT)
                                    && null != p.getResult() && p.getResult().toString().contains(INTERCEPT_NOT_PASS))
                            .findAny();
                    answerStrategyMap.forEach((k, strategy) -> {
                        sourceAnswerResultList.addAll(strategy.notAnswer(dialogueSteps, optional.isPresent()));
                    });
                }

                // 按溯源文件内容相关度降序排序
                sortSourceAnswerResult(sourceAnswerParam.getRelevance(), sourceAnswerResultList);
                // 过滤没有文件名称的数据(仅过滤来自互联网的溯源)
                List<SourceAnswerResult> filterSourceAnswerResult = sourceAnswerResultList.stream()
                        .filter(v -> !Objects.equals(v.getSourceType(), 4) || StringUtils.isNotBlank(v.getFileName()))
                        .collect(Collectors.toList());

                result.setSourceAnswerResultList(filterSourceAnswerResult);
                // 对话的时候，溯源时去掉重复数据
                if (!"1".equals(sourceAnswerParam.getFromManage()) && CollectionUtils.isNotEmpty(filterSourceAnswerResult)) {
                    List<SourceAnswerResult> distinctList = filterSourceAnswerResult.stream().distinct().collect(Collectors.toList());
                    result.setSourceAnswerResultList(distinctList);
                }
            }
            // 获取关联知识库
            List<ApplicationKnowledge> applicationKnowledges = applicationKnowledgeService.list();
            Map<String, List<ApplicationKnowledge>> knowledgeMap = applicationKnowledges.stream().filter(p -> p.getType().equals(ApplicationKnowledgeTypeEnum.KNOWLEDGE.getType()))
                    .collect(Collectors.groupingBy(ApplicationKnowledge::getApplicationId, Collectors.collectingAndThen(Collectors.toList(), listData -> listData)));
            if (null != applicationInfo) {
                List<ApplicationKnowledge> knowledgeIds = knowledgeMap.getOrDefault(applicationInfo.getApplicationId(), org.apache.commons.compress.utils.Lists.newArrayList());
                applicationInfo.setKnowledgeIds(knowledgeIds);
                // 统计文档来源数量
                generateSourceFileCount(result, applicationInfo, sourceAnswerParam);
                // 设置是否开启联网判断标识
                String networkCheckFlag = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.NETWORK_CHECK_FLAG);
                if (StringUtils.isBlank(networkCheckFlag)) {
                    result.setNetworkCheckFlag(0);
                } else {
                    result.setNetworkCheckFlag(Integer.valueOf(networkCheckFlag));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DialogueServiceImpl.APPLICATION_INFO.remove();
        }
        return Result.success(result);
    }

    @Override
    public Result<List<ScoreDataResult>> getScoreData(ScoreDataParam scoreDataParam) {
        try {
            // 获取应用信息
            ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(scoreDataParam.getApplicationId(), null, YesNoEnum.YES.getName());
            if (null == applicationInfo) {
                log.warn("应用不存在");
                return Result.success(Lists.newArrayList());
            }
            applicationInfo.setContentScore(0F);
            applicationInfo.setQaTitleScore(0F);
            applicationInfo.setQaContentScore(0f);
            applicationInfo.setRangeContentScore(0f);
            applicationInfo.setQaRangeContentScore(0f);
            applicationInfo.setQaRangeTitleScore(0f);
            applicationInfo.setPrepareNum(20);
            applicationInfo.setFilterNum(20);
            DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);

            // 获取知识库
            List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(scoreDataParam.getApplicationId());
            List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
            DialogueServiceImpl.KNOWLEDGE_ID_LIST.set(knowledgeIdList);
            // 按向量化模型分组
            Map<String, List<KnowledgeInfo>> vectorGroup = knowledgeInfoList.stream().collect(Collectors.groupingBy(KnowledgeInfo::getDenseVectorId));
            DialogueServiceImpl.KNOWLEDGE_VECTOR_MAP.set(vectorGroup);
            // 获取回答策略
            AnswerStrategy answerStrategy = answerStrategyMap.get(scoreDataParam.getStrategy());
            if (null != answerStrategy) {
                List<ScoreDataResult> scoreData = answerStrategy.getScoreData(scoreDataParam);
                return Result.success(scoreData);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            DialogueServiceImpl.KNOWLEDGE_ID_LIST.remove();
            DialogueServiceImpl.APPLICATION_INFO.remove();
        }
        return Result.success(Lists.newArrayList());
    }

    @Override
    public Result<List<DialogueStep>> getStepByDialogId(StepByDialogIdParam stepParam) {
        if (StringUtils.isBlank(stepParam.getDialogId())) {
            return Result.success(Lists.newArrayList());
        }

        LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                .eq(DialogueStep::getDialogueId, stepParam.getDialogId())
                .size(100);
        List<DialogueStep> dialogueSteps = dialogueStepMapper.selectList(wrapper);
        return Result.success(dialogueSteps);
    }

    @Override
    public List<DialogueStep> getRerrangeStepByDialogIdList(List<String> dialogueIds) {
        if (CollectionUtils.isEmpty(dialogueIds)) {
            return new ArrayList<>();
        }
        LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                .in(DialogueStep::getDialogueId, dialogueIds)
                .eq(DialogueStep::getStep, FINAL_COLLECT_RANGE)
                .size(9999);
        List<DialogueStep> dialogueSteps = dialogueStepMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(dialogueSteps)) {
            return null;
        }

        return dialogueSteps;
    }

    @Override
    public Result<DialogueStep> getReviseQuestion(SourceAnswerParam sourceAnswerParam) {
        // 获取步骤记录
        LambdaEsQueryWrapper<DialogueStep> wrapper = EsWrappers.lambdaQuery(DialogueStep.class)
                .eq(DialogueStep::getDialogueId, sourceAnswerParam.getDialogueId())
                .size(999);

        List<DialogueStep> dialogueSteps = dialogueStepMapper.selectList(wrapper);
        Optional<DialogueStep> any = dialogueSteps.stream().filter(p -> p.getStep().equals(AnswerStrategyContant.CHANGE_CONTENT_STEP)).findAny();
        DialogueStep dialogueStep = any.orElseGet(DialogueStep::new);
        return Result.success(dialogueStep);
    }

    @Override
    public Result<PolicyRecommendResult> getRecommendByUserInfo(UserInfoParam param) {
        String userId = param.getUserId();
        PolicyRecommendResult result = new PolicyRecommendResult();

        List<String> recommendQuestionList = new ArrayList<>();

        // 先从缓存中获取
        String key = RedisKey.RECOMMENT_QUESTION + DialogueConstant.VISITOR;
        if (StringUtils.isNotBlank(userId)) {
            // 当前用户的key
            key = RedisKey.RECOMMENT_QUESTION + userId;
        }
        if (redisService.hasKey(key)) {
            recommendQuestionList = redisService.get(key, List.class);
            if (CollectionUtils.isNotEmpty(recommendQuestionList)) {
                result.setFiledList(recommendQuestionList);
                return Result.success(result);
            }
        }

        // 获取应用信息
        Wrappers wrappers = Wrappers.init()
                .where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(param.getApplicationId()))
                .limit(1);
        ApplicationInfo applicationInfo = applicationInfoService.getOne(wrappers);
        if (null == applicationInfo) {
            log.warn("应用不存在");
            result.setFiledList(new ArrayList<>());
            return Result.success(result);
        }
        // 获取应用绑定的知识库
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(param.getApplicationId());
        List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(knowledgeIdList)) {
            log.warn("未绑定知识库");
            result.setFiledList(new ArrayList<>());
            return Result.success(result);
        }
        applicationInfo.setKnowledgeIdList(knowledgeIdList);
        // 推荐个数默认值
        int recommendQuestionNumber = 5;
        String recommendQuestionNumberConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.RECOMMEDN_QUESTION_NUMBER_CONFIG);
        if (StringUtils.isNotBlank(recommendQuestionNumberConfig)) {
            try {
                recommendQuestionNumber = Integer.parseInt(recommendQuestionNumberConfig);
            } catch (NumberFormatException e) {
                log.error("推荐问题个数配置错误，无法将字符串转换为整数: " + e.getMessage());
            }
        }

        List<KnowledgeData> knowledgeDataList;
        // 没有用户id时，任意查即可
        if (StringUtils.isBlank(userId)) {
            knowledgeDataList = getAnyQARecommendQuestion(knowledgeIdList, recommendQuestionNumber, true);
        } else {
            String userInfo = getApiUserInfoData(userId);
            knowledgeDataList = getQARecommendQuestionByUser(userInfo, applicationInfo, recommendQuestionNumber);
            // 如果没有匹配到推荐问题，随机推荐即可
            if (CollectionUtils.isEmpty(knowledgeDataList)) {
                knowledgeDataList = getAnyQARecommendQuestion(knowledgeIdList, recommendQuestionNumber, false);
            }
        }

        // 标题去重
        Set<String> titles = new LinkedHashSet<>();
        recommendQuestionList = knowledgeDataList.stream()
                .filter(v -> StringUtils.isNotEmpty(v.getTitle()) && titles.add(v.getTitle()))
                .map(KnowledgeData::getTitle)
                .collect(Collectors.toList());

        result.setFiledList(recommendQuestionList);

        // 更新缓存
        redisService.set(key, recommendQuestionList, DialogueConstant.RECOMMENT_QUESTION_EXPIRE_TIME);

        return Result.success(result);
    }


    private String getApiUserInfoData(String userId) {
        // 获取用户信息
        JSONObject apiParam = new JSONObject();
        apiParam.put("id", userId);
        String response = "";
        try {
            response = HttpUtil.post(userInfoApi, JSON.toJSONString(apiParam));
        } catch (Exception e) {
            log.info("大学城获取活动或实践的接口异常");
            e.printStackTrace();
            throw new RuntimeException("大学城获取活动或实践的接口异常");
        }
        log.info("调用大学城用户信息接口的结果为 **************{}", response);
        RecommendedListByUserInfoResult recommendedListByUserInfoResult = JSONObject.parseObject(response, RecommendedListByUserInfoResult.class);
        if (recommendedListByUserInfoResult==null || !StringUtils.equals("000000", recommendedListByUserInfoResult.getCode())) {
            throw new RuntimeException("大学城获取活动或实践的接口异常");
        }
        RecommendedListByUserInfoResult.Detail detail = recommendedListByUserInfoResult.getData();
        if (detail == null) {
            throw new RuntimeException("大学城获取活动或实践的接口异常");
        }

        return "性别：" + detail.getSex() + "====>" + "年龄：" + detail.getAge() + "====>" + "学历："
                + detail.getEducationalBackground() + "====>" + "专业技能：" + detail.getProfessionalSkill()
                + "====>" + "已注册岗位：" + detail.getSignUpPositionName() + "====>"
                + "已注册岗位标签：" + detail.getSignUpPositionTag() + "====>" + "期望行业：" +  detail.getDesiredIndustry() +
                "====>" + "可就业地点：" + detail.getEmployablePlace();
    }

    /**
     * 获取推荐问题
     * @param knowledgeIdList
     * @param recommendQuestionNumber
     * @param randomFlag 随机查询标识
     * @return
     */
    private List<KnowledgeData> getAnyQARecommendQuestion(List<String> knowledgeIdList, Integer recommendQuestionNumber, Boolean randomFlag) {
        LambdaEsQueryWrapper<KnowledgeData> queryWrapper = EsWrappers.lambdaQuery(KnowledgeData.class);
        queryWrapper.eq(KnowledgeData::getDelStatus, "0")
                .eq(KnowledgeData::getAccurate, YesNoEnum.YES.getName())
                .in(KnowledgeData::getKnowledgeId, knowledgeIdList)
                .size(recommendQuestionNumber);
        if (randomFlag) {
            queryWrapper.orderByDesc(KnowledgeData::getUpdateTime);
        }
        return knowledgeDataMapper.selectList(queryWrapper);
    }

    private List<KnowledgeData> getQARecommendQuestionByUser(String userInfo, ApplicationInfo applicationInfo, Integer recommendQuestionNumber) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setSuggest("推荐");
        // 这里是向量化的内容
        dto.setContentTemp(userInfo);
        dto.setContent(userInfo);
        // 设置知识库id
        dto.setKnowledgeIdList(applicationInfo.getKnowledgeIdList());
        // 分值默认值
        float qaTitleScoreDense = 1.75f;
        String qaTitleScoreDenseConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.QA_TITLE_SCORE_DENSE_CONFIG);
        if (StringUtils.isNotBlank(qaTitleScoreDenseConfig)) {
            try {
                qaTitleScoreDense = Float.parseFloat(qaTitleScoreDenseConfig);
            } catch (NumberFormatException e) {
                log.error("分值配置错误，无法将字符串转换为浮点数: " + e.getMessage());
            }
        }

        return answerUtils.queryQAKnowledgeByDense(dto, AnswerStrategyContant.TITLE_DENSE_FILED, qaTitleScoreDense, recommendQuestionNumber);



    }

    /**
     * 溯源结果相关度排序
     * @param relevanceSortFlag 排序标识
     * @param sourceAnswerResultList 结果
     */
    private void sortSourceAnswerResult(Integer relevanceSortFlag, List<SourceAnswerResult> sourceAnswerResultList) {
        if (Objects.isNull(relevanceSortFlag)) {
            throw new IllegalArgumentException("相关度排序标识为空");
        }

        // 根据评分排序
        Comparator<SourceAnswerResult> comparator = Comparator.nullsLast(
                Comparator.comparing(SourceAnswerResult::getScore, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        sourceAnswerResultList.sort(relevanceSortFlag == AnswerRelevanceSortFlagEnum.SORT.getCode() ? comparator : comparator.reversed());

    }

    /**
     * 统计溯源文件数量
     * @param result 参数
     * @param applicationInfo 参数
     */
    private void generateSourceFileCount(SourceAnswerFinalResult result, ApplicationInfo applicationInfo, SourceAnswerParam sourceAnswerParam) {
        List<SourceAnswerResult> sourceAnswerResultList = result.getSourceAnswerResultList();
        if (CollectionUtils.isEmpty(sourceAnswerResultList)) {
            result.setSelectedFileCount(0L);
            result.setFileTotalCount(0L);
            return;
        }
        List<ApplicationKnowledge> knowledgeBases = applicationInfo.getKnowledgeIds();
        if (CollectionUtil.isEmpty(knowledgeBases)) {
            result.setSelectedFileCount(0L);
            result.setFileTotalCount(0L);
            return;
        }
        List<String> knowledgeIds = knowledgeBases.stream().map(ApplicationKnowledge::getKnowledgeId).collect(Collectors.toList());

        // 命中的url、文件、视频、音频、图片数量
        long selectedFileCount = sourceAnswerResultList.stream().filter(p -> StringUtils.isNotEmpty(p.getFileName()) && p.getSourceType()!=null && p.getSourceType()!=2)
                .count();

        List<String> foldersIds = Lists.newArrayList();
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        foldersIds.add("-1");
        // 获取文件夹id
        List<Folders> list = Folders.creat()
                .where(FOLDERS.KNOWLEDGE_ID.in(knowledgeIds))
                .lists();
        List<String> foldersIdList = list.stream().map(Folders::getFoldersId).collect(Collectors.toList());
        foldersIds.addAll(foldersIdList);

        // 从关联的知识库中查找的文件、视频、音频、图片数量
        Integer searchType = sourceAnswerParam.getSearchType();
        // 初始化为构建搜索全部条件，即searchType为-1时的搜索条件
        Wrappers<Object> fileWrappers = Wrappers.init()
                .and((FILE.FOLDERS_ID.in(foldersIds)))
                .and(FILE.STATUS.eq(FileStatusEnum.SUCCESS.getCode()))
                .and(FILE.DELETE_FLAG.eq(0));
        if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_FILE.getCode())) {
            // 文档（含雅意知识库文件）
            fileWrappers.and(FILE.TYPE.in(FileTypeEnum.FILE.getType(), FileTypeEnum.YAYI_DOC.getType()));
        } else if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_AUDIO.getCode())) {
            // 音频
            fileWrappers.and(FILE.TYPE.eq(FileTypeEnum.AUDIO.getType()));
        } else if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_IMAGE.getCode())) {
            // 图片
            fileWrappers.and(FILE.TYPE.eq(FileTypeEnum.IMAGE.getType()));
        } else if (Objects.equals(searchType, DialogueSearchFileTypeEnum.SEARCH_VIDEO.getCode())) {
            // 视频
            fileWrappers.and(FILE.TYPE.eq(FileTypeEnum.VIDEO.getType()));
        }
        long fileTotalCount = fileMapper.selectCountByQuery(fileWrappers);


        result.setSelectedFileCount(selectedFileCount);
        result.setFileTotalCount(fileTotalCount);
    }

    @Override
    public Result<List<AssociateQuestionResult>> associationQuestion(AssociateQuestionParam param) {
        try {
            // 获取应用信息
            Wrappers wrappers = Wrappers.init()
                    .where(ApplicationInfoTableDef.APPLICATION_INFO.APPLICATION_ID.eq(param.getApplicationId()))
                    .limit(1);
            ApplicationInfo applicationInfo = applicationInfoService.getOne(wrappers);
            if (null == applicationInfo) {
                log.warn("应用不存在");
                return Result.success(Lists.newArrayList());
            }

            // 查询插件
            AppConfigListParam applicationPluginData = new AppConfigListParam();
            applicationPluginData.setApplicationId(param.getApplicationId());
            Result<List<ApplicationPluginData>> result = applicationPluginDataService.getApplicationPluginDataList(applicationPluginData);
            List<ApplicationPluginData> pluginDataList = result.getData();
            if (CollectionUtils.isEmpty(pluginDataList)) {
                return Result.success(Lists.newArrayList());
            }

            // 获取插件，如果没有打开联想，那么就不再推荐联想
            Optional<ApplicationPluginData> any = pluginDataList.stream().filter(p -> AppPluginEnum.ASSOCIATION.getCode().equals(p.getPluginCode())).findAny();
            if (!any.isPresent()) {
                return Result.success(Lists.newArrayList());
            }

            // 获取本应用全部配置
            List<ApplicationConfiguration> configurationList = applicationConfigurationService.getConfigByAppId(param.getApplicationId());
            if (CollectionUtils.isEmpty(configurationList)) {
                return Result.success(Lists.newArrayList());
            }

            // 封装 k-v键对
            Map<String, String> configMap = configurationList.stream()
                    .filter(p -> StringUtils.isNotBlank(p.getKeyInfo()) && StringUtils.isNotBlank(p.getValueInfo()))
                    .collect(Collectors.toMap(
                            ApplicationConfiguration::getKeyInfo,
                            ApplicationConfiguration::getValueInfo,
                            (v1, v2) -> v1,
                            Maps::newHashMap
                    ));
            // 获取联想问开关
            String associationEnable = configMap.get(ASSOCIATION_ENABLE);
            if (!YesNoEnum.YES.getName().equals(associationEnable)) {
                return Result.success(Lists.newArrayList());
            }

            // 获取应用绑定的知识库
            List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(param.getApplicationId());
            List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
            // 保存知识库id
            DialogueServiceImpl.KNOWLEDGE_ID_LIST.set(knowledgeIdList);
            DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);

            // 收集所有的联想问题
            List<AssociateQuestionResult> allAssociate = Lists.newArrayList();

            // 先匹配问题
            String associateQFlag = configMap.get(ASSOCIATION_Q_FLAG);
            String associateNum = configMap.get(ASSOCIATION_NUM);
            int num = Integer.parseInt(associateNum);
            if (YesNoEnum.YES.getName().equals(associateQFlag)) {
                List<AssociateQuestionResult> associationQuestion = getAssociateQuestion(param, QA_QUESTION_STRATEGY);
                distinct(associationQuestion, allAssociate);
            }

            // 再匹配答案
            String associateAFlag = configMap.get(ASSOCIATION_A_FLAG);
            if (allAssociate.size() < num && YesNoEnum.YES.getName().equals(associateAFlag)) {
                List<AssociateQuestionResult> associateQuestion = getAssociateQuestion(param, QA_CONTENT_STRATEGY);
                distinct(associateQuestion, allAssociate);
            }

            // 匹配知识库
            String associationKnnFlag = configMap.get(ASSOCIATION_KNN_FLAG);
            if (allAssociate.size() < num && YesNoEnum.YES.getName().equals(associationKnnFlag)) {
                List<AssociateQuestionResult> associateQuestion = getAssociateQuestion(param, QA_KNOWLEDGE_STRATEGY);
                distinct(associateQuestion, allAssociate);
            }

            // 大模型发散
            String recommendedLLMFlag = configMap.get(ASSOCIATION_LLM_FLAG);
            if (allAssociate.size() < num && YesNoEnum.YES.getName().equals(recommendedLLMFlag)) {
                List<AssociateQuestionResult> associateQuestion = getAssociateQuestion(param, QA_LARGEMODEL_STRATEGY);
                distinct(associateQuestion, allAssociate);
            }
            // 防止最终结果超过设置的数量
            List<AssociateQuestionResult> associateQuestionResults = allAssociate.stream()
                    .limit(num)
                    .collect(Collectors.toList());
            return Result.success(associateQuestionResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(Lists.newArrayList());
    }

    /**
     * 问题去重
     */
    private void distinct(List<AssociateQuestionResult> associationQuestionResults, List<AssociateQuestionResult> allAssociations) {
        for (AssociateQuestionResult item : associationQuestionResults) {
            if (CollectionUtils.isNotEmpty(allAssociations)) {
                boolean exists = allAssociations.stream().anyMatch(
                        existing -> Objects.equals(existing.getQuestion(),
                                item.getQuestion()));
                if (!exists) {
                    allAssociations.add(item);
                }
            } else {
                allAssociations.add(item);
            }
        }
    }

    /**
     * 获取推荐问题
     * @param param
     * @param strategy
     * @return
     */
    private List<RecommendQuestionResult> getRecommendQuestion(RecommendQuestionParam param, String strategy) {
        RecommendStrategy recommendStrategy = recommendStrategyMap.get(strategy);
        return recommendStrategy.getRecommendQuestion(param);
    }

    /**
     * 获取推荐问题
     * @param param
     * @param strategy
     * @return
     */
    private List<AssociateQuestionResult> getAssociateQuestion(AssociateQuestionParam param, String strategy) {
        RecommendStrategy recommendStrategy = recommendStrategyMap.get(strategy);
        return recommendStrategy.getAssociationQuestion(param);
    }

}
