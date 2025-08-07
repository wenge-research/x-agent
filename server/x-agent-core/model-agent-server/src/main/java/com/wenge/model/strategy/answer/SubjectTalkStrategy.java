package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.dto.result.SubjectWayMatterResult;
import com.wenge.model.dto.result.XinzhiWeather;
import com.wenge.model.entity.*;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.enums.UniversityActivityPracticeEnum;
import com.wenge.model.mapper.InterceptWordMapper;
import com.wenge.model.service.LifeServeGuideInfoService;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.MatterGuideService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.LlmGenerateUtil;
import com.wenge.model.vo.RecommendedListByKeywordVo;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.entity.YayiMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.oauth.constants.AppConfigContant.*;

@Service(AnswerStrategyContant.SUBJECT_TALK)
@Slf4j
public class SubjectTalkStrategy implements AnswerStrategy {

    @Autowired
    private LlmGenerateUtil llmGenerateUtil;

    @Autowired
    private Map<String, AnswerStrategy> answerStrategyMap;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MatterGuideService matterGuideService;

    @Autowired
    private LifeServeGuideInfoService lifeServeGuideInfoService;

    @Autowired
    private InterceptWordMapper interceptWordMapper;

    @Value("${weather.xinzhi.host}")
    private String xinzhiWeatherHost;

    @Value("${weather.xinzhi.v3DailyUrl}")
    private String xinzhiWeatherV3DailyUrl;

    @Value("${weather.xinzhi.v3NowUrl}")
    private String xinzhiWeatherV3NowUrl;

    @Value("${weather.xinzhi.key}")
    private String xinzhiWeatherKey;

    @Value("${carLimit.apispace.host}")
    private String apispaceHost;
    @Value("${carLimit.apispace.xianxingApi}")
    private String xianxingApi;
    @Value("${carLimit.apispace.token}")
    private String apispaceToken;

    @Value("${university.api.recommend}")
    private String universityApiRecommend;

    @Autowired
    private LlmInfoService llmInfoService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setStep(SUBJECT_STEP);
        step.setPrompt(dto.getContentTemp());
        try {
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

            // 没有开启主题讨论，则跳过
            if (!YesNoEnum.YES.getName().equals(applicationInfo.getSubjectFlag())) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return answerData;
            }
            contextList.add(step);

            // 获取讨论的话题
            //List<String> subjectTalkList = answerUtils.getInterceptWord(applicationInfo.getApplicationId(), SUBJECT_STEP);
            List<SceneManagement> sceneManagements = getSceneByAppId(applicationInfo.getApplicationId());
            if (CollectionUtils.isEmpty(sceneManagements)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(SUBJECT_NO_DATA);
                return answerData;
            }

            List<String> subjectTalkList = sceneManagements.stream().map(SceneManagement::getSystemPrompt).collect(Collectors.toList());
            String subjectPrompt = applicationInfo.getSubjectPrompt();

            // 没有配置主题策略，则跳过
            if (StringUtils.isBlank(subjectPrompt) || CollectionUtils.isEmpty(subjectTalkList)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(SUBJECT_NO_DATA);
                return answerData;
            }

            // 调用大模型，找到哪个场景
            String generateResult = generateResult(applicationInfo, dto, step, subjectTalkList, true);

            SceneManagement sceneManagement = getProcessing(generateResult, answerData, step, subjectTalkList);
            // 没有找到对应场景，则跳过
            if (null == sceneManagement) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(generateResult + FIELD_SEPARATOR + SUBJECT_NO_SCENE);
                return answerData;
            }

            String processing = sceneManagement.getProcessing();
            if (StringUtils.isBlank(processing)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(sceneManagement.getSceneName() + FIELD_SEPARATOR + SUBJECT_NO_WAY);
                return answerData;
            }
            JSONObject processingObj = JSON.parseObject(processing);
            // 处理答案，如果有则直接回答固定答案
            String answer = processingObj.getString(SUBJECT_ANSWER_FIELD);
            if (StringUtils.isNotBlank(answer)) {
                answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                answerData.setAnswer(answer);
                step.setResult(SUBJECT_ANSWER + FIELD_SEPARATOR + sceneManagement.getSceneName() + FIELD_SEPARATOR + answer);
                dto.getAnswerConsumer().accept(answerData);
                return answerData;
            }
            // 处理方式
            String way = processingObj.getString(SUBJECT_WAY_FIELD);
            // 处理方式为空则跳过
            if (StringUtils.isBlank(way) || SUBJECT_WAY_NONE.equals(way)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult(SUBJECT_STEP + FIELD_SEPARATOR + sceneManagement.getSceneName() + FIELD_SEPARATOR + SUBJECT_NO_WAY);
                return answerData;
            }

            // 问题添加前后缀
            String question = polishQuestion(dto, processingObj);

            // 处理方式
            dealWay(way, step, applicationInfo, answerData, question, dto, contextList, sceneManagement);
        } catch (Exception e) {
            log.error("SubjectTalkStrategy error:{}", e.getMessage(), e);
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            step.setResult(ERROR_STEP + ":" + e.getMessage());
        }
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return answerData;
    }

    /**
     * 提取多个场景号，解析器
     *
     * @param result
     * @return
     */
    private List<Integer> indexListParser(String result, ApplicationInfo applicationInfo) {
        List<Integer> indexList = new ArrayList<>();
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

                    int sceneLimitCount = 10;
                    String sceneLimitCountConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.SCENE_LIMIT_COUNT);
                    try {
                        if (StringUtils.isNotBlank(sceneLimitCountConfig)) {
                            sceneLimitCount = Integer.parseInt(sceneLimitCountConfig);
                        }
                    } catch (Exception e) {
                        log.error("sceneLimitCount字符串转换为整数异常");
                        e.printStackTrace();
                    }

                    int count = 0;
                    while (matcher.find()) {
                        index = Integer.parseInt(matcher.group(1));
                        index = index - 1;
                        indexList.add(index);
                        count++;
                        if (count == sceneLimitCount) {
                            break;
                        }
                    }
                }
            }
        }
        return indexList;
    }

    /**
     * 提取单个场景号，解析器
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
     * 寻找其他策略的答案
     */
    private void subjectWay(String question, ApplicationInfo applicationInfo, DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData, String step) {
        if (question.equals(dto.getContentTemp())) {
            log.info("问题和临时一致时，直接调用答案");
            List<AnswerStepData> answerStepData = DialogueServiceImpl.STEP_BY_LIST.get();
            Optional<AnswerStepData> any = answerStepData.stream().filter(p -> p.getStep().equals(step)).findAny();
            // 策略不存在时，直接无法回答
            if (!any.isPresent()) {
                answerData.setAnswer(applicationInfo.getFailureTalk());
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            AnswerStepData stepData = any.get();
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start <= 10000) {
                // 策略完成，获取答案
                if (stepData.getStatus().equals(StepStatusEnum.ANSWER_COMPLETE) || stepData.getStatus().equals(StepStatusEnum.ANSWER_FINAL)) {
                    answerData.setAnswer(stepData.getAnswer());
                    answerData.setStatus(stepData.getStatus());
                    return;
                }
                // 策略失败，直接无法回答
                if (stepData.getStatus().equals(StepStatusEnum.ANSWER_FAIL)) {
                    answerData.setAnswer(applicationInfo.getFailureTalk());
                    answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                    return;
                }
            }
            answerData.setAnswer(applicationInfo.getFailureTalk());
        } else {
            log.info("问题和临时不一致时，重新检索答案");
            dto.setContentTemp(question);
            // 讨论话题转到打磨新回答时，这里应用配置成支持大模型发散
            if (FIND_ANSWER_BY_MODEL.equals(step)) {
                applicationInfo.setModelAnswerFlag(YesNoEnum.YES.getName());
            } else if (!applicationInfo.getProcessStep().contains(step)) {
                answerData.setAnswer(applicationInfo.getFailureTalk());
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return;
            }
            AnswerStrategy answerStrategy = answerStrategyMap.get(step);
            // 重新获取答案,从其他检索策略获取答案
            answerStrategy.getAnswer(dto, contextList, answerData);
            if (answerData.getStatus().equals(StepStatusEnum.ANSWER_FAIL)) {
                answerData.setAnswer(applicationInfo.getFailureTalk());
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        }
    }

    /**
     * 讨论话题，使用大模型判断话题
     *
     * @param applicationInfo
     * @param dto
     * @param step
     * @param customizeFlag   自定义指令标识  true-使用应用设置的指令  false-使用配置里面的指令
     * @return
     */
    private String generateResult(ApplicationInfo applicationInfo, DialogueByStreamParam dto, StepEntity step, List<String> subjectTalkList, Boolean customizeFlag) {
        String subjectPrompt = applicationInfo.getSubjectPrompt();
        // 如果逻辑要走的是配置里面的指令，要是没配置指令，就默认使用应用设置的指令
        if (!customizeFlag) {
            // 多场景指令
            String configSubjectPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.SUBJECT_TALK_PROMPT);
            // 话题讨论场景数
            int subjectTalkSceneNum = 5;
            String subjectTalkSceneNumConfig = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.SUBJECT_TALK_SCENE_NUM);
            if (StringUtils.isNotBlank(subjectTalkSceneNumConfig)) {
                subjectTalkSceneNum = Integer.parseInt(subjectTalkSceneNumConfig);
            }
            if (StringUtils.isNotBlank(configSubjectPrompt)) {
                subjectPrompt = configSubjectPrompt.replace(AnswerStrategyContant.SCENE_NUM_PLACEHOLDER, String.valueOf(subjectTalkSceneNum));
            }
        }

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

        String question = dto.getContentTemp();
        if (!customizeFlag) {
            // 匹配大学城边问边办的场景时候，用原问题匹配
            question = dto.getContent();
        }
        question = AnswerUtils.questionMark(question);
        subjectPrompt = subjectPrompt.replace(QUESTION_PLACEHOLDER, question);
        // 调用大模型，找到场景（根据指令判断是找一个指令还是多个指令）
        cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
        stepParam.set(LLM_JSON_FLAG, StringConstant.ONE);
        // 从配置中获取模型名称
        String modelId = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), SUBJECT_TALK_LLM_NAME);
        if (StringUtils.isNotBlank(modelId)) {
            stepParam.set(LLM_PARAM_MODEL_ID, modelId);
        }
        step.setParam(stepParam);
        return llmGenerateUtil.generate(subjectPrompt, null, step, false);
    }

    /**
     * 获取命中的活动推荐场景
     *
     * @param details
     * @param generateResult
     * @return
     */
    private List<RecommendedListByKeywordVo.Detail> getRecommendScene(List<RecommendedListByKeywordVo.Detail> details, String generateResult, ApplicationInfo applicationInfo) {
        if (generateResult.contains("其他场景") || CollectionUtils.isEmpty(details)) {
            return new ArrayList<>();
        }

        // 获取场景的索引列表
        List<Integer> indexs = indexListParser(generateResult, applicationInfo);
        if (CollectionUtils.isEmpty(indexs)) {
            return new ArrayList<>();
        }

        // 筛选出命中的场景
        List<RecommendedListByKeywordVo.Detail> hitDetails = indexs.stream()
                .map(details::get)
                .collect(Collectors.toList());

        return hitDetails;
    }

    /**
     * 获取处理方式
     */
    private SceneManagement getProcessing(String generateResult, AnswerStepData answerData, StepEntity step, List<String> subjectTalkList) {
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

        String subject = subjectTalkList.get(indexs);

        // 没有找到处理方式，则跳过
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        SceneManagement sceneManagement = null;
        // 获取场景列表
        List<SceneManagement> sceneManagements = getSceneByAppId(applicationInfo.getApplicationId());
        if (CollectionUtils.isNotEmpty(sceneManagements)) {
            // 筛选出命中的场景
            Optional<SceneManagement> any = sceneManagements.stream()
                    .filter(p -> StringUtils.isNotBlank(p.getSystemPrompt()) && p.getSystemPrompt().equals(subject))
                    .findAny();

            if (any.isPresent()) {
                sceneManagement = any.get();
            }
        }
        if (null == sceneManagement) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return null;
        }

        // 处理方式为空则跳过
        String processing = sceneManagement.getProcessing();
        if (StringUtils.isBlank(processing)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return null;
        }

        // 判断不是json格式, 则跳过
        if (!JSONUtil.isTypeJSONObject(processing)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return null;
        }
        return sceneManagement;
    }

    /**
     * 处理问题，形成临时的新的问题
     *
     * @param dto
     * @param processingObj
     * @return
     */
    public static String polishQuestion(DialogueByStreamParam dto, JSONObject processingObj) {
        String dateForPrompt = DateUtil.getDateForPrompt();
        // 前缀问题
        String question = dto.getContentTemp();
        String preQuestion = processingObj.getString(SUBJECT_PRE_QUE_FIELD);
        if (StringUtils.isNotBlank(preQuestion)) {
            preQuestion = preQuestion.replace(DATES_PLACEHOLDER, dateForPrompt);
            question = preQuestion + question;
        }

        // 问题后缀
        String extendQuestion = processingObj.getString(SUBJECT_EXT_QUE_FIELD);
        if (StringUtils.isNotBlank(extendQuestion)) {
            extendQuestion = extendQuestion.replace(DATES_PLACEHOLDER, dateForPrompt);
            question = question + extendQuestion;
        }

        // 临时问题替换
        String replaceQuestion = processingObj.getString(SUBJECT_REP_QUE_FIELD);
        if (StringUtils.isNotBlank(replaceQuestion)) {
            replaceQuestion = replaceQuestion.replace(DATES_PLACEHOLDER, dateForPrompt);
            question = replaceQuestion;
        }
        return question;
    }

    /**
     * 处理事项方式
     *
     * @param dto
     * @param sceneManagement
     * @param applicationInfo
     * @param question
     * @param answerData
     */
    private void subjectWayMatter(DialogueByStreamParam dto, SceneManagement sceneManagement, ApplicationInfo applicationInfo, String question, AnswerStepData answerData, StepEntity step) {
        // interceptWord.getContent()
        SubjectWayMatterResult matterResult = new SubjectWayMatterResult();
        matterResult.setSceneManagement(sceneManagement);
        List<MatterGuide> matterNameList = matterGuideService.getMatterName(applicationInfo.getApplicationId(), sceneManagement.getSceneId());
        if (CollectionUtils.isEmpty(matterNameList)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return;
        }
        matterResult.setMatterNameList(matterNameList);
        llmMatter(matterResult, question, answerData, dto, step);
    }

    /**
     * 处理”生活服务“方式
     *
     * @param dto             参数
     * @param applicationInfo 应用信息
     * @param question        问题
     * @param answerData      回答步骤信息
     */
    private void subjectWayLifeServe(DialogueByStreamParam dto, SceneManagement sceneManagement, ApplicationInfo applicationInfo, String question, AnswerStepData answerData, StepEntity step) {
        List<MatterGuide> matterGuides = matterGuideService.getMatterName(applicationInfo.getApplicationId(), sceneManagement.getSceneId());
        List<LifeServeGuideInfo> lifeServeGuideInfos = lifeServeGuideInfoService.getListByHandleMethodIdAndMatterGuideId(matterGuides.get(0).getMatterId());
        if (CollectionUtils.isEmpty(lifeServeGuideInfos)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return;
        }

        llmLifeServe(applicationInfo, lifeServeGuideInfos, question, answerData, dto, step);
    }

    /**
     * 活动/实践的方式处理
     *
     * @param dto             参数
     * @param applicationInfo 应用信息
     * @param question        问题
     * @param answerData      回答步骤信息
     * @param type            类型  1-实践 2-活动
     */
    private void subjectWayActivityOrPractice(DialogueByStreamParam dto, ApplicationInfo applicationInfo, String question, AnswerStepData answerData,
                                              StepEntity step, Integer type) {
        String result = "";
        try {
            String key = StringConstant.BLANK;
            if (Objects.equals(type, UniversityActivityPracticeEnum.PRACTIVE.getCode())) {
                key = RedisKey.UNIVERSITY_PRACTICE;
            } else if (Objects.equals(type, UniversityActivityPracticeEnum.ACTIVITY.getCode())) {
                key = RedisKey.UNIVERSITY_ACTIVITY;
            } else {
                key = RedisKey.UNIVERSITY_OTHER;
            }
            if (redisService.hasKey(key)) {
                result = (String) redisService.get(key);
            } else {
                JSONObject param = new JSONObject();
                param.put("type", type);
                param.put("page", 1);
                param.put("size", 100);
                result = HttpUtil.post(universityApiRecommend, JSON.toJSONString(param));
                // 更新缓存
                redisService.set(key, result, 60 * 60 * 24);
                log.info("调用大学城api获取活动或实践的的结果为 **************{}", result);
            }
        } catch (Exception e) {
            log.info("获取大学城活动数据异常");
            e.printStackTrace();
        }

        RecommendedListByKeywordVo recommendedListByKeywordVo = JSONObject.parseObject(result, RecommendedListByKeywordVo.class);
        List<RecommendedListByKeywordVo.Detail> detailList = recommendedListByKeywordVo.getData();
        if (CollectionUtils.isEmpty(detailList)) {
            detailList = new ArrayList<>();
        }
        // 职位名称====>详情描述
        String classification = StringConstant.BLANK;
        if (Objects.equals(type, UniversityActivityPracticeEnum.ACTIVITY.getCode())) {
            classification = UniversityActivityPracticeEnum.ACTIVITY.getDesc();
        } else if (Objects.equals(type, UniversityActivityPracticeEnum.PRACTIVE.getCode())) {
            classification = UniversityActivityPracticeEnum.PRACTIVE.getDesc();
        } else {
            classification = UniversityActivityPracticeEnum.OTHER.getDesc();
        }
        AtomicInteger counter = new AtomicInteger(1);
        String classificationName = classification;
        List<String> detailDescriptions = detailList.stream().filter(v->StringUtils.isNotBlank(v.getSurfaceDrawingFile())
                        && StringUtils.isNotBlank(v.getUrl()) && StringUtils.isNotBlank(v.getPositionName()))
                .map(detail -> classificationName + counter.getAndIncrement() + "\n " +
                        "职位名称：" + detail.getPositionName() +
                        "====>工作时间描述：" + detail.getWorkingHoursDescription() +
                        "====>薪资待遇：" + detail.getSalaryAndTreatment() +
                        "====>薪资结算周期：" + detail.getPayrollCycle() +
                        "====>薪资：" + detail.getPay() +
                        "====>是否面议：" + detail.getIsTreatmentNegotiable() +
                        "====>详情描述：" + detail.getDetailDescription())
                .collect(Collectors.toList());
        // 调用大模型，根据问题匹配推荐的场景
        String generateResult = generateResult(applicationInfo, dto, step, detailDescriptions, false);
        // 根据活动场景提取匹配到的数据
        List<RecommendedListByKeywordVo.Detail> recommendDetailList = getRecommendScene(detailList, generateResult, applicationInfo);

        if (CollectionUtils.isEmpty(recommendDetailList)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return;
        }

        // 将跳转链接和封面图做下域名映射
        recommendDetailList.forEach(v -> {
            v.setUrl(v.getUrl() + "?id=" + v.getId());
            v.setSurfaceDrawingFile(v.getSurfaceDrawingFile().replace(UNIVERSITY_ORIGIN_ADDRESS, PLATFORM_DOMAIN));
        });

        llmActivityOrPractice(applicationInfo, recommendDetailList, question, answerData, dto, step);
    }


    /**
     * 大模型提示事项
     *
     * @param matterResult
     * @param question
     * @param answerData
     * @param dto
     */
    private void llmMatter(SubjectWayMatterResult matterResult, String question, AnswerStepData answerData, DialogueByStreamParam dto, StepEntity step) {
        List<MatterGuide> matterNameList = matterResult.getMatterNameList();
        // 按照执行顺序排序
        List<MatterGuide> sortedMatterNameList = matterNameList.stream().sorted(Comparator.comparingInt(MatterGuide::getSorted)).collect(Collectors.toList());
        Optional<MatterGuide> any = sortedMatterNameList.stream().filter(p -> SUBJECT_SCENE_ENTRANCE.equals(p.getMatterType())).findAny();

        // 这里优先寻找 “事项-场景入口”，如果找不到，则取列表中第一个事项
        MatterGuide matterGuide;
        if (any.isPresent()) {
            matterGuide = any.get();
        } else {
            if (sortedMatterNameList.size() == 1) {
                matterGuide = sortedMatterNameList.get(0);
            } else {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                step.setResult("缺少场景的入口事项");
                return;
            }
        }

        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        messages.setRole(SYSTEM_PROMPT_FIELD);
        if (StringUtils.isBlank(matterGuide.getExtraSystemPrompt()) || StringUtils.isBlank(matterGuide.getPrompt())) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            step.setResult("缺少场景入口的系统提示词或开场白");
            return;
        }

        // 构建system prompt 和大模型的开场白
        String extraSystemPrompt = matterGuide.getExtraSystemPrompt();
        String dateForPrompt = DateUtil.getDateForPrompt();
        extraSystemPrompt = extraSystemPrompt.replace(DATES_PLACEHOLDER, dateForPrompt);
        messages.setContent(extraSystemPrompt);
        messagesList.add(messages);

        messages = new YayiMessage();
        messages.setRole(YAYI_ROLE);
        messages.setContent(matterGuide.getPrompt());
        messagesList.add(messages);

        // 关芯客服的应用id
        String guanxinAppId = getConfiguration(FUN_DIVISION_APPID);

        // 根据事项的类型，分别处理不同的逻辑
        String matterType = matterGuide.getMatterType();
        switch (matterType) {
            case "事项-调用API": // 调用API，这里将来会使用插件，插件是一个API请求，这里先要通过大模型获取参数，然后封装API的参数
                String generate = llmGenerateUtil.generate(question, messagesList, step, false);
                // 解析参数
                String jsonStr = AnswerUtils.dealLLmJson(generate);
                if (JSONUtil.isTypeJSONObject(jsonStr)) {
                    JSONObject jsonObject = JSON.parseObject(jsonStr);
                    // 请求API，这里使用API插件，等工作流合并过来之后，再开发
                }
                break;
            default:
                llmGenerateUtil.generate(question, messagesList, step, false, result -> {
                    try {
                        String answer = result.getAnswer();
                        String reasoningContent = result.getReasoningContent();
                        answerData.setStatus(result.getStatus());
                        answerData.setAnswer(answer);
                        answerData.setReasoningContent(reasoningContent);
                        answerData.setCustomize(matterResult);
                        // 如果当前应用为关系客服，那么这里给出办事事项即可--为了兼容已有的办事表单
                        if (dto.getApplicationId().equals(guanxinAppId)) {
                            answerData.setCustomize(sortedMatterNameList.get(0));
                        }
                        dto.getAnswerConsumer().accept(answerData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                    }
                });
                break;
        }

    }

    /**
     * 大模型提示-服务
     *
     * @param applicationInfo
     * @param lifeServeGuideInfos
     * @param question
     * @param answerData
     * @param dto
     */
    private void llmLifeServe(ApplicationInfo applicationInfo, List<LifeServeGuideInfo> lifeServeGuideInfos, String question, AnswerStepData answerData, DialogueByStreamParam dto, StepEntity step) {
        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("system");
        String systemPrompt = applicationInfo.getSystemPromptResult();
        messages.setContent(systemPrompt);
        messagesList.add(messages);

        llmGenerateUtil.generate(question, messagesList, step, false, result -> {
            try {
                String answer = result.getAnswer();
                String reasoningContent = result.getReasoningContent();
                answerData.setStatus(result.getStatus());
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                answerData.setCustomize(lifeServeGuideInfos);
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        });
    }

    /**
     * 大模型提示-活动/实践
     *
     * @param applicationInfo
     * @param recommendDetailList
     * @param question
     * @param answerData
     * @param dto
     */
    private void llmActivityOrPractice(ApplicationInfo applicationInfo, List<RecommendedListByKeywordVo.Detail> recommendDetailList, String question,
                             AnswerStepData answerData, DialogueByStreamParam dto, StepEntity step) {
        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("system");
        String systemPrompt = applicationInfo.getSystemPromptResult();
        messages.setContent(systemPrompt);
        messagesList.add(messages);

        llmGenerateUtil.generate(question, messagesList, step, false, result -> {
            try {
                String answer = result.getAnswer();
                String reasoningContent = result.getReasoningContent();
                answerData.setStatus(result.getStatus());
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                answerData.setCustomize(recommendDetailList);
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        });
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

    /**
     * 处理方式
     *
     * @param way
     * @param step
     * @param applicationInfo
     * @param answerData
     * @param question
     * @param dto
     * @param contextList
     */
    private void dealWay(String way, StepEntity step, ApplicationInfo applicationInfo, AnswerStepData answerData, String question, DialogueByStreamParam dto, Vector<StepEntity> contextList, SceneManagement sceneManagement) {
        String answer = StringConstant.BLANK;

        // 处理方式
        switch (way) {
            // 拦截
            case SUBJECT_WAY_INTERCEPT:
                answer = applicationInfo.getFailureTalk();
                answerData.setStatus(StepStatusEnum.ANSWER_COMPLETE);
                answerData.setAnswer(answer);
                step.setResult(SUBJECT_WAY_INTERCEPT + FIELD_SEPARATOR + sceneManagement.getSceneName());
                break;
            // 内置问题
            case SUBJECT_WAY_IN:
                step.setResult(SUBJECT_WAY_IN + FIELD_SEPARATOR + sceneManagement.getSceneName());
                subjectWay(question, applicationInfo, dto, contextList, answerData, BUILT_IN);
                break;
            // 问答【问题】
            case SUBJECT_WAY_QUE:
                step.setResult(SUBJECT_WAY_QUE + FIELD_SEPARATOR + sceneManagement.getSceneName());
                subjectWay(question, applicationInfo, dto, contextList, answerData, FIND_QA_TITLE);
                break;
            // 问答【回答】
            case SUBJECT_WAY_ANS:
                step.setResult(SUBJECT_WAY_ANS + FIELD_SEPARATOR + sceneManagement.getSceneName());
                subjectWay(question, applicationInfo, dto, contextList, answerData, FIND_QA_CONTENT);
                break;
            // 知识库
            case SUBJECT_WAY_KNOWLEDGE:
                step.setResult(SUBJECT_WAY_KNOWLEDGE + FIELD_SEPARATOR + sceneManagement.getSceneName());
                subjectWay(question, applicationInfo, dto, contextList, answerData, FINAL_COLLECT);
                break;
            // 大模型
            case SUBJECT_WAY_LLM:
                step.setResult(SUBJECT_WAY_LLM + FIELD_SEPARATOR + sceneManagement.getSceneName());
                subjectWay(question, applicationInfo, dto, contextList, answerData, FIND_ANSWER_BY_MODEL);
                break;
            // 事项
            case SUBJECT_WAY_MATTER:
                step.setResult(SUBJECT_WAY_MATTER + FIELD_SEPARATOR + sceneManagement.getSceneName());
                StepEntity matterLlmStep = new StepEntity();
                matterLlmStep.setStep(MATTER_LLM_STEP);
                contextList.add(matterLlmStep);
                subjectWayMatter(dto, sceneManagement, applicationInfo, question, answerData, matterLlmStep);
                break;
            // 生活服务
            case SUBJECT_WAY_LIFE_SERVE:
                step.setResult(SUBJECT_WAY_LIFE_SERVE + FIELD_SEPARATOR + sceneManagement.getSceneName());
                StepEntity lifeServeLlmStep = new StepEntity();
                lifeServeLlmStep.setStep(LIFE_SERVE_LLM_STEP);
                contextList.add(lifeServeLlmStep);
                subjectWayLifeServe(dto, sceneManagement, applicationInfo, question, answerData, lifeServeLlmStep);
                break;
            // 活动
            case SUBJECT_WAY_ACTIVITY:
                step.setResult(SUBJECT_WAY_ACTIVITY + FIELD_SEPARATOR + sceneManagement.getSceneName());
                StepEntity activityLlmStep = new StepEntity();
                activityLlmStep.setStep(ACTIVITY_LLM_STEP);
                contextList.add(activityLlmStep);
                subjectWayActivityOrPractice(dto, applicationInfo, question, answerData, activityLlmStep, UniversityActivityPracticeEnum.ACTIVITY.getCode());
                break;
            // 实践
            case SUBJECT_WAY_PRACTICE:
                step.setResult(SUBJECT_WAY_PRACTICE + FIELD_SEPARATOR + sceneManagement.getSceneName());
                StepEntity practiceLlmStep = new StepEntity();
                practiceLlmStep.setStep(PRACTICE_LLM_STEP);
                contextList.add(practiceLlmStep);
                subjectWayActivityOrPractice(dto, applicationInfo, question, answerData, practiceLlmStep, UniversityActivityPracticeEnum.PRACTIVE.getCode());
                break;
            // 天气
            case SUBJECT_WAY_WEATHER:
                step.setResult(SUBJECT_WAY_WEATHER + FIELD_SEPARATOR + sceneManagement.getSceneName());
                StepEntity weatherLlmStepAddr = new StepEntity();
                weatherLlmStepAddr.setStep(WEATHER_LLM_ADDR);
                contextList.add(weatherLlmStepAddr);
                StepEntity weatherLlmStep = new StepEntity();
                weatherLlmStep.setStep(WEATHER_LLM_STEP);
                contextList.add(weatherLlmStep);
                weather(answerData, dto, weatherLlmStep, weatherLlmStepAddr);
                break;
            // 车辆限行限号
            case SUBJECT_WAY_CAR_LIMIT:
                step.setResult(SUBJECT_WAY_CAR_LIMIT + FIELD_SEPARATOR + sceneManagement.getSceneName());
                StepEntity carLimitLlmAddr = new StepEntity();
                carLimitLlmAddr.setStep(CAR_LIMIT_LLM_ADDR);
                contextList.add(carLimitLlmAddr);
                StepEntity carLimitLlmStep = new StepEntity();
                carLimitLlmStep.setStep(CAR_LIMIT_LLM_STEP);
                contextList.add(carLimitLlmStep);
                dto.setContentTemp(question);
                carLimit(answerData, dto, carLimitLlmStep, carLimitLlmAddr);
                break;
            default:
                break;
        }
    }

    /**
     * 天气情况，支持昨天-未来15天
     * 心知天气 API :获取指定城市未来最多 15 天每天的白天和夜间预报，以及昨日的历史天气。付费用户可获取全部数据，免费用户只返回 3 天天气预报。降水预报目前只支持国内城市。
     */
    private void weather(AnswerStepData answerData, DialogueByStreamParam dto, StepEntity weatherLlmStep, StepEntity weatherLlmStepAddr) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        // 提取用户提问的地址
        JSONObject weatherAddress = getWeatherAddress(dto.getContentTemp(), weatherLlmStepAddr);
        // 获取当前应用的天气默认城市拼音
        String cityName = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), WEATHER_ADDRESS_CITY_NAME);
        String cityPinYin = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), WEATHER_ADDRESS_CITY_PINYIN);
        if (null != weatherAddress) {
            if (!weatherAddress.getString("拼音").contains("无")) {
                cityPinYin = weatherAddress.getString("拼音");
                if (cityPinYin.toUpperCase().endsWith("SHI")) {
                    cityPinYin = cityPinYin.substring(0, cityPinYin.length() - 3);
                }
            }
            if (!weatherAddress.getString("城市").contains("无")) {
                cityName = weatherAddress.getString("城市");
            }
        }
        // 实时天气
        String realTimeWeather = realTimeWeather(cityPinYin);
        // 未来天气
        String futureWeather = futureWeather(cityPinYin);
        String weather = StringConstant.BLANK;
        if (StringUtils.isNotBlank(realTimeWeather)) {
            weather = realTimeWeather;
        }
        if (StringUtils.isNotBlank(futureWeather)) {
            weather += ("\n" + futureWeather);
        }
        if (StringUtils.isNotBlank(weather)) {
            weatherLlm(dto.getContent(), weather, answerData, dto, cityName, weatherLlmStep);
        } else {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
        }
    }

    /**
     * 天气情况大模型总结
     */
    private void weatherLlm(String question, String weather, AnswerStepData answerData, DialogueByStreamParam dto, String city, StepEntity weatherLlmStep) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("system");
        String systemPrompt = applicationInfo.getSystemPromptResult();
        messages.setContent(systemPrompt);
        messagesList.add(messages);
        //StepEntity step = new StepEntity();

        String usrePrompt = getConfiguration(LLM_PROMPT_SYSTEM_WEATHER_SUMMARIES);
        if (StringUtils.isBlank(usrePrompt)) {
            return;
        }
        usrePrompt = usrePrompt.replace(WEATHER_PLACEHOLDER, weather)
                .replace(CITY_PLACEHOLDER, city)
                .replace(QUESTIONS_PLACEHOLDER, question);
        //JSONObject stepPrompt = (JSONObject) weatherLlmStep.getPrompt();

        String generate = llmGenerateUtil.generate(usrePrompt, messagesList, weatherLlmStep, false, result -> {
            try {
                String answer = result.getAnswer();
                String reasoningContent = result.getReasoningContent();
                //stepPrompt.put(WEATHERLLM_PROMPT, step.getPrompt());
                answerData.setStatus(result.getStatus());
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        });
        weatherLlmStep.setResult(generate);
    }

    /**
     * 提取天气的地址
     */
    private JSONObject getWeatherAddress(String question, StepEntity weatherLlmStepAddr) {
        List<YayiMessage> messagesList = Lists.newArrayList();
        String systemPromt = AppConfigContant.getConfiguration(LLM_PROMPT_SYSTEM_WEATHER_ADDRESS);

        // 获取当前星期几，比如“星期六”
        String dayOfWeek = DateUtil.getCurrentWeekCn();
        String time = LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy年MM月dd日 HH:mm:ss");

        systemPromt = systemPromt.replace(DATE_PLACEHOLDER, time + "，" + dayOfWeek);

        YayiMessage messages = new YayiMessage();
        messages.setRole("system");
        messages.setContent(systemPromt);
        messagesList.add(messages);

        JSONObject weather = new JSONObject();
        weather.put("question", question);
        String generate = llmGenerateUtil.generate(JSON.toJSONString(weather), messagesList, weatherLlmStepAddr, false);
        weatherLlmStepAddr.setResult(generate);
        generate = AnswerUtils.dealLLmJson(generate);
        if (JSONUtil.isTypeJSONObject(generate)) {
            return JSON.parseObject(generate);
        }

        return null;
    }

    /**
     * 实时天气
     */
    private String realTimeWeather(String cityPinYin) {
        JSONObject param = new JSONObject();
        param.put("id", IdUtil.simpleUUID());
        param.put("key", xinzhiWeatherKey);
        param.put("location", cityPinYin);
        param.put("language", "zh-Hans");
        param.put("unit", "c");
        log.info("realTimeWeather.id:{}, param:{}", param.getString("id"), JSON.toJSONString(param));
        String response = HttpUtil.get(xinzhiWeatherHost + xinzhiWeatherV3NowUrl, param);
        log.info("realTimeWeather.id:{},response:{}", param.getString("id"), response);

        if (StringUtils.isNotBlank(response) && JSONUtil.isTypeJSONObject(response)) {
            JSONObject responseData = JSON.parseObject(response);
            if (responseData.containsKey("results")) {
                JSONArray results = responseData.getJSONArray("results");
                List<XinzhiWeather> resultsList = results.toList(XinzhiWeather.class);
                if (CollectionUtils.isEmpty(resultsList)) {
                    return StringConstant.BLANK;
                }
                XinzhiWeather xinzhiWeather = resultsList.get(0);
                XinzhiWeather.Now now = xinzhiWeather.getNow();
                if (null != now) {
                    // 2015-09-20天气：白天多云，晚间晴，最高温度26度，最低温度17度，风向未东北风，降水概率为0%，风向角度255°，风速是9.66km/h，风力等级为，降水量为，相对湿度为
                    StringBuilder weatherDetail = new StringBuilder("现在当前实时天气：");
                    JSONObject builder = new JSONObject();
                    if (StringUtils.isNotBlank(now.getText())) {
                        builder.put("天气现象：", now.getText());
                    }
                    if (StringUtils.isNotBlank(now.getTemperature())) {
                        builder.put("温度：", now.getTemperature() + "度");
                    }
                    if (StringUtils.isNotBlank(now.getFeels_like())) {
                        builder.put("体感温度：", now.getFeels_like() + "度");
                    }
                    if (StringUtils.isNotBlank(now.getPressure())) {
                        builder.put("大气压强：", now.getPressure() + "hPa");
                    }

                    if (StringUtils.isNotBlank(now.getHumidity())) {
                        builder.put("相对湿度：", now.getHumidity() + "%");
                    }

                    if (StringUtils.isNotBlank(now.getVisibility())) {
                        builder.put("能见度：", now.getVisibility() + "km");
                    }
                    if (StringUtils.isNotBlank(now.getWind_direction_degree())) {
                        builder.put("风向角度：", now.getWind_direction_degree());
                    }
                    if (StringUtils.isNotBlank(now.getWind_speed())) {
                        builder.put("风速：", now.getWind_speed() + "km/h");
                    }

                    if (StringUtils.isNotBlank(now.getWind_scale())) {
                        builder.put("风力等级：", now.getWind_scale() + "级");
                    }
                    builder.forEach((k, v) -> {
                        weatherDetail.append(k).append(v).append("，");
                    });
                    return weatherDetail.toString();
                }
            }
        }
        return StringConstant.BLANK;
    }

    /**
     * 未来天气，最大15天
     */
    private String futureWeather(String cityPinYin) {

        // https://api.seniverse.com/v3/weather/daily.json?
        // key=S78k_qg8fl3Cyw12P&location=beijing&language=zh-Hans&unit=c&start=-1&days=5
        JSONObject param = new JSONObject();
        param.put("id", IdUtil.simpleUUID());
        param.put("key", xinzhiWeatherKey);
        param.put("location", cityPinYin);
        param.put("language", "zh-Hans");
        param.put("unit", "c");
        param.put("start", -1);
        param.put("days", 15);
        log.info("futureWeather.id:{}, param:{}", param.getString("id"), JSON.toJSONString(param));
        String response = HttpUtil.get(xinzhiWeatherHost + xinzhiWeatherV3DailyUrl, param);
        log.info("futureWeather.id:{}, result:{}", param.getString("id"), response);

        if (StringUtils.isNotBlank(response) && JSONUtil.isTypeJSONObject(response)) {
            JSONObject responseData = JSON.parseObject(response);
            if (responseData.containsKey("results")) {
                JSONArray results = responseData.getJSONArray("results");
                List<XinzhiWeather> resultsList = results.toList(XinzhiWeather.class);
                if (CollectionUtils.isEmpty(resultsList)) {
                    return StringConstant.BLANK;
                }
                XinzhiWeather xinzhiWeather = resultsList.get(0);
                List<XinzhiWeather.Day> daily = xinzhiWeather.getDaily();
                String weather = daily.stream().map(day -> {
                    // 2015-09-20天气：白天多云，晚间晴，最高温度26度，最低温度17度，风向未东北风，降水概率为0%，风向角度255°，风速是9.66km/h，风力等级为，降水量为，相对湿度为
                    StringBuilder weatherDetail = new StringBuilder(day.getDate() + "天气：");
                    JSONObject builder = new JSONObject();
                    if (StringUtils.isNotBlank(day.getText_day())) {
                        builder.put("白天天气：", day.getText_day());
                    }
                    if (StringUtils.isNotBlank(day.getText_night())) {
                        builder.put("夜晚天气：", day.getText_night());
                    }
                    if (StringUtils.isNotBlank(day.getHigh())) {
                        builder.put("最高温度：", day.getHigh() + "度");
                    }
                    if (StringUtils.isNotBlank(day.getLow())) {
                        builder.put("最低温度：", day.getLow() + "度");
                    }

                    if (StringUtils.isNotBlank(day.getWind_direction())) {
                        builder.put("风向：", day.getWind_direction());
                    }
                    if (StringUtils.isNotBlank(day.getWind_direction_degree())) {
                        builder.put("风向角度：", day.getWind_direction_degree());
                    }
                    if (StringUtils.isNotBlank(day.getWind_speed())) {
                        builder.put("风速：", day.getWind_speed() + "km/h");
                    }

                    if (StringUtils.isNotBlank(day.getWind_scale())) {
                        builder.put("风力等级：", day.getWind_scale() + "级");
                    }
                    if (StringUtils.isNotBlank(day.getRainfall())) {
                        builder.put("降水量：", day.getRainfall() + "mm");
                    }

                    if (StringUtils.isNotBlank(day.getHumidity())) {
                        builder.put("相对湿度：", day.getHumidity() + "%");
                    }
                    builder.forEach((k, v) -> {
                        weatherDetail.append(k).append(v).append("，");
                    });
                    return weatherDetail.toString();
                }).collect(Collectors.joining("\n"));
                return weather;
            }
        }
        return StringConstant.BLANK;
    }

    /**
     * 获取车辆限行信息
     *
     * @param answerData
     * @param dto
     * @param carLimitLlmStep
     */
    private void carLimit(AnswerStepData answerData, DialogueByStreamParam dto, StepEntity carLimitLlmStep, StepEntity carLimitLlmAddr) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        JSONObject weatherAddress = getWeatherAddress(dto.getContentTemp(), carLimitLlmAddr);
        // 获取当前应用的限行默认城市拼音
        String cityName = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), CAR_LIMIT_ADDRESS_CITY_NAME);
        String cityPinYin = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), CAR_LIMIT_ADDRESS_CITY_PINYIN);
        if (null != weatherAddress) {
            if (!weatherAddress.getString("拼音").contains("无")) {
                cityPinYin = weatherAddress.getString("拼音");
                if (cityPinYin.toUpperCase().endsWith("SHI")) {
                    cityPinYin = cityPinYin.substring(0, cityPinYin.length() - 3);
                }
            }
            if (!weatherAddress.getString("城市").contains("无")) {
                cityName = weatherAddress.getString("城市");
            }
        }

        Resource resource = resourceLoader.getResource("classpath:runtime-data/apispace_area.json");
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
        List<JSONObject> parseArray = JSON.parseArray(contentBuilder.toString(), JSONObject.class);
        String districtPy = cityPinYin.toLowerCase();
        Optional<JSONObject> district = parseArray.stream().filter(p -> p.getString("district_py").equals(districtPy)).findAny();
        if (!district.isPresent()) {
            String configuration = getConfiguration(applicationInfo.getApplicationId(), WEATHER_ADDRESS_CITY_PINYIN);
            district = parseArray.stream().filter(p -> p.getString("district_py").equals(configuration)).findAny();
        }
        String areacode = district.get().getString("areacode");
        JSONObject carTraffic = getCarTraffic(areacode);
        if (null == carTraffic) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            return;
        }
        if (carTraffic.containsKey("limits")) {
            List<JSONObject> limits = carTraffic.getJSONArray("limits").toList(JSONObject.class);
            limits.forEach(item -> {
                if ("w".equalsIgnoreCase(item.getString("number"))) {
                    item.put("number", "不限号");
                }
            });
        }
        carTrafficLLm(cityName, carTraffic, answerData, dto, carLimitLlmStep);
    }


    /**
     * 获取限行信息
     *
     * @param areacode
     * @return
     */
    private JSONObject getCarTraffic(String areacode) {
        JSONObject param = new JSONObject();
        param.put("areacode", areacode);
        param.put("days", 15);
        // 使用apispace服务：https://www.apispace.com/
        HttpRequest get = HttpUtil.createGet(apispaceHost + xianxingApi);
        get.form(param);
        get.header("X-APISpace-Token", apispaceToken);
        log.info("限行信息url:{},请求参数：{}", get.getUrl(), JSON.toJSONString(param));
        String body = get.execute().body();
        log.info("限行信息请求结果：{}", body);

        if (StringUtils.isNotBlank(body) && JSONUtil.isTypeJSONObject(body)) {
            JSONObject responseData = JSON.parseObject(body);
            if (NumberConstants.ZERO == responseData.getInteger("status")) {
                JSONObject result = responseData.getJSONObject("result");
                return result.getJSONObject("traffic");
            }
        }
        return null;
    }

    /**
     * 限行信息LLM
     *
     * @param cityName
     * @param carTraffic
     * @param answerData
     * @param dto
     * @param carLimitLlmStep
     */
    private void carTrafficLLm(String cityName, JSONObject carTraffic, AnswerStepData answerData, DialogueByStreamParam dto, StepEntity carLimitLlmStep) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String currentTime = DateUtil.getDateForPrompt();
        String systemPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), LLM_PROMPT_USER_CAR_LIMIT);

        systemPrompt = systemPrompt.replace("${date}", currentTime)
                .replace("${cityName}", cityName)
                .replace("${carLimitRule}", JSON.toJSONString(carTraffic));

        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        messages.setRole("system");
        //String systemPrompt = applicationInfo.getSystemPromptResult();
        messages.setContent(systemPrompt);
        messagesList.add(messages);

        String generate = llmGenerateUtil.generate(dto.getContentTemp(), messagesList, carLimitLlmStep, false, result1 -> {
            try {
                String answer = result1.getAnswer();
                String reasoningContent = result1.getReasoningContent();
                answerData.setStatus(result1.getStatus());
                answerData.setAnswer(answer);
                answerData.setReasoningContent(reasoningContent);
                dto.getAnswerConsumer().accept(answerData);
            } catch (Exception e) {
                e.printStackTrace();
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
        });
        carLimitLlmStep.setResult(generate);
    }

    /**
     * 根据应用id获取场景
     *
     * @param applicationId
     * @return
     */
    private List<SceneManagement> getSceneByAppId(String applicationId) {
        String sceneKey = DialogueServiceImpl.getSubWordKey(applicationId);
        if (redisService.hasKey(sceneKey)) {
            String sceneFromRedis = redisService.get(sceneKey, String.class);
            return JSON.parseArray(sceneFromRedis, SceneManagement.class);
        }
        return Lists.newArrayList();
    }
}
