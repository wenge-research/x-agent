package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.constants.ShowContant;
import com.wenge.model.constants.SystemVerifyContant;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.*;
import com.wenge.model.enums.*;
import com.wenge.model.mapper.IpBlacklistMapper;
import com.wenge.model.mapper.es.DialogueMapper;
import com.wenge.model.mapper.es.DialogueStepMapper;
import com.wenge.model.mapper.es.LlmConversationMapper;
import com.wenge.model.service.*;
import com.wenge.model.strategy.answer.AnswerStrategy;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.SseEmitterUtils;
import com.wenge.model.utils.translate.TranslateUtils;
import com.wenge.oauth.constants.AppConfigContant;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.holder.ContextHolders;
import com.wenge.oauth.service.UserService;
import com.wenge.oauth.util.RequestUtil;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.NumberConstants;
import com.wg.appframe.core.constant.ResultCodeBase;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.ResultCodeEnum;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.constants.StringConstans;
import com.wg.appframe.yayi.entity.YayiMessage;
import com.wg.appframe.yayi.param.ContentParsingParam;
import com.wg.appframe.yayi.param.DialogueFilterParam;
import com.wg.appframe.yayi.param.YayiContentIndexParam;
import com.wg.appframe.yayi.param.YayiPluginSelectionModelParam;
import com.wg.appframe.yayi.result.*;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.InterceptWordHouseApplicationRelTableDef.INTERCEPT_WORD_HOUSE_APPLICATION_REL;
import static com.wenge.model.entity.table.InterceptWordHouseTableDef.INTERCEPT_WORD_HOUSE;
import static com.wenge.model.entity.table.InterceptWordTableDef.INTERCEPT_WORD;
import static com.wenge.model.entity.table.IpBlacklistTableDef.IP_BLACKLIST;
import static com.wenge.model.entity.table.WebsiteConfigTableDef.WEBSITE_CONFIG;
import static com.wenge.model.enums.StepStatusEnum.ANSWER_FAIL;
import static com.wenge.model.enums.StepStatusEnum.ANSWER_FINAL;
import static com.wenge.oauth.constants.AppConfigContant.*;
import static com.wenge.oauth.entity.table.OauthUserTableDef.OAUTH_USER;

@Service
@Slf4j
public class DialogueServiceImpl implements DialogueService {
    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private IpBlacklistMapper ipBlacklistMapper;

    @Autowired
    private DialogueMapper dialogueMapper;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private LlmConversationMapper conversationMapper;

    @Autowired
    private SmartPromptConfigService smartPromptConfigService;

    @Autowired
    private PromptConfigService promptConfigService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private InterceptWordService wordService;

    @Autowired
    private Map<String, AnswerStrategy> answerStrategyMap;

    @Autowired
    private LlmInfoService llmInfoService;

    @Resource(name = "dialogueByStreamPool")
    private ThreadPoolExecutor poolExecutor;

    @Autowired
    private DialogueRecordService dialogueRecordService;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private BusinessScenarioListService businessScenarioListService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationInfoService appInfoService;

    // 缓存应用信息
    public static final ThreadLocal<ApplicationInfo> APPLICATION_INFO = new TransmittableThreadLocal<>();
    // 缓存知识库id
    public static final ThreadLocal<List<String>> KNOWLEDGE_ID_LIST = new TransmittableThreadLocal<>();
    public static final ThreadLocal<Map<String, List<KnowledgeInfo>>> KNOWLEDGE_VECTOR_MAP = new TransmittableThreadLocal<>();
    // 缓存traceId
    public static final ThreadLocal<String> TRACE_ID = new TransmittableThreadLocal<>();

    // 缓存步骤
    public static final ThreadLocal<List<AnswerStepData>> STEP_BY_LIST = new TransmittableThreadLocal<>();

    //public static final List<InterceptWord> INTERCEPT_WORD_LIST = Lists.newArrayList();

    // 缓存词库
    public static final Map<Long, List<InterceptWord>> INTERCEPT_WORD_HOUSE_MAP = Maps.newHashMap();

    // 缓存应用id映射词库id
    public static final Map<String, List<Long>> APP_INTERCEPT_WORD_HOUSE_ID_MAP = Maps.newHashMap();

    // 缓存应用id映射词库的分组
    public static final Map<String, Map<String, List<String>>> APP_INTERCEPT_WORD_MAP = Maps.newHashMap();

    // 当前客户端问答状态，key是客户端clientId,value是true-在运行中，false-未运行，clientId为空也是未运行，这个状态用来停止与当前对话有关的线程池的任务
    public static Map<String, Boolean> RUN_FLAG_MAP = Maps.newConcurrentMap();

    private static final Pattern TONE_PATTERN = Pattern.compile("[0-5]");

    @Autowired
    private DialogueStepMapper dialogueStepMapper;

    @Autowired
    private SceneApplicationRefService sceneApplicationRefService;

    @Autowired
    private RecommendQuestionService recommendQuestionService;

    @Value("${agentX.streamApi}")
    private String streamApi;

    public static Map<String, List<InterceptWord>> APP_WORD_MAP = Maps.newConcurrentMap();

    /**
     * 重启需要清空缓存
     */
    @PostConstruct
    public void init() {
        redisService.batchDel(RedisKey.MATTER_DATA);
        redisService.batchDel(RedisKey.SCENE);
    }
    @Override
    public JSONObject dialogueByStreamEvaluation(DialogueByStreamParam param) {
        long start = System.currentTimeMillis();
        //return new Thread(() -> dialogueRun(param, start), MDC.get("traceId")).start();
        return dialogueRun(param, start, true);
    }
    @Override
    public SseEmitter dialogueByStream(DialogueByStreamParam param, HttpServletRequest request) {
        long start = System.currentTimeMillis();
        String clientId = param.getClientId();
        SseEmitter emitter = SseEmitterUtils.getConnection(clientId);

        // 鉴权
        boolean authPassFlag = auth(param, request);
        if (!authPassFlag) {
            return emitter;
        }
        new Thread(() -> dialogueRun(param, start, true), MDC.get("traceId")).start();
        return emitter;
    }

    /**
     * 对话入口
     *
     * @param param
     */
    @Override
    public JSONObject dialogueRun(DialogueByStreamParam param, long start, boolean stopFlag) {
        return requestStreamApi(param, streamApi, param.getClientId());
    }


    @Override
    public SseEmitter documentDialogue(DocumentDialogueParam param, HttpServletRequest request) throws IOException {
        String clientId = param.getClientId();
        SseEmitter connection = SseEmitterUtils.getConnection(clientId);
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getAppId(), null, YesNoEnum.YES.getName());
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        // 获取模型信息
        LlmStrategy llmStrategy = llmInfoService.getActiveLLm(applicationInfo.getModelId());
        if (null == llmStrategy) {
            log.info("未配置大模型");
            SseEmitterUtils.send(clientId, "未配置大模型");
            SseEmitterUtils.complete(clientId);
            return connection;
        }
        ContentParsingParam parsingParam = getContentParsingParam(param.getFileType());
        // 提取文档中的内容
        ContentParsingResult contentParsingResult = yayiServer.contentParsing(param.getFileUrl(), parsingParam);
        log.info("雅意提取的文档内容：{}", JSONObject.toJSONString(contentParsingResult));
        if (!"success".equals(contentParsingResult.getMsg())) {
            log.info("雅意提取文档失败：{}", contentParsingResult.getMsg());
            SseEmitterUtils.send(clientId, String.format("雅意提取文档失败：%s", contentParsingResult.getMsg()));
            SseEmitterUtils.complete(clientId);
            return connection;
        }
        String content = (String) contentParsingResult.getData().getFile_content().getContent();
        String prompt = param.getCustomizePrompt();
        if (StringUtils.isBlank(prompt)) {
            List<SmartPromptConfig> promptList = smartPromptConfigService.getPromptConfigByAppId(param.getAppId(), param.getModule());
            if (CollectionUtils.isEmpty(promptList)) {
                log.info("应用:{} module:{} 未配置提示词", param.getAppId(), param.getModule());
                SseEmitterUtils.send(clientId, String.format("应用:%s module:%s 未配置提示词", param.getAppId(), param.getModule()));
                SseEmitterUtils.complete(clientId);
                return connection;
            }
            prompt = promptList.get(0).getPromptContent();
        }
        DialogueByStreamParam dialogue = new DialogueByStreamParam();
        dialogue.setClientId(clientId);
        dialogue.setApplicationId(param.getAppId());
        dialogue.setStream("true");
        String finalPrompt = prompt;
        CompletableFuture.runAsync(() -> {
            llmStrategy.generate(finalPrompt, getGenerate128KParam(finalPrompt, content), new StepEntity(), Boolean.TRUE, result -> {
                try {
                    String answer = result.getAnswer();
                    dialogue.setAnswerFlag("true");
                    dialogue.setAnswer(answer.replace("\n", "<br/>"));
                    boolean finishFlag = SseEmitterUtils.sendMsg(clientId, JSONObject.toJSONString(dialogue));
                    if (finishFlag) {
                        throw new RuntimeException("stop....");
                    }
                } catch (Exception e) {
                    log.error("error:{}", e.getMessage(), e);
                }
            });
            SseEmitterUtils.complete(clientId);
        }, poolExecutor);

        return connection;
    }

    @Override
    public Map<String, String> matching(BiddingMatchingParam param) {
        List<SmartPromptConfig> prompt = smartPromptConfigService.getPromptConfigByAppId(param.getAppId(), "matching");
        if (CollectionUtils.isEmpty(prompt)) {
            log.info("应用:{} 未配置提示词", param.getAppId());
            throw new RuntimeException("当前应用未配置提示词");
        }
        Generate30BResult result = yayiServer.generate30B(String.format(prompt.get(0).getPromptContent(), param.getBidding(), param.getBid()));
        if (!"success".equals(result.getMsg())) {
            throw new RuntimeException("匹配失败" + result.getMsg());
        }
        String matchingResult = result.getData().getChoices().get(0).getMessage().getContent().replace('`', ' ').replace("json", StringConstant.BLANK);

        return JSON.parseObject(matchingResult, new TypeReference<HashMap<String, String>>() {
        });
    }

    @Override
    public Result<String> translateText(TranslateParam stepParam) {
        if (StringUtils.isBlank(stepParam.getText()) || StringUtils.isBlank(stepParam.getTgtLang()) || StringUtils.isBlank(stepParam.getSrcLang())) {
            return Result.success(StringConstant.BLANK);
        }
        try {
            YayiTranslationResult yayiTranslationResult = yayiServer.yayiTranslate(stepParam.getText(), stepParam.getSrcLang(), stepParam.getTgtLang());
            if (StringConstans.CODE.equals(yayiTranslationResult.getCode())) {
                YayiTranslationResult.TranslationData data = yayiTranslationResult.getData();
                if (null != data) {
                    String tgt = data.getTgt();
                    return Result.success(tgt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(StringConstant.BLANK);
    }

    private static List<YayiMessage> getGenerate128KParam(String prompt, String content) {
        List<YayiMessage> messagesList = Lists.newArrayList();
        YayiMessage messages = new YayiMessage();
        // 取第一个作为系统提示词
        messages.setContent(prompt);
        messages.setRole("system");
        messagesList.add(messages);

        YayiMessage userMessage = new YayiMessage();
        // 取第一个作为系统提示词
        userMessage.setContent(content);
        userMessage.setRole("user");
        messagesList.add(userMessage);
        return messagesList;
    }

    private static ContentParsingParam getContentParsingParam(String fileType) {
        ContentParsingParam parsingParam = new ContentParsingParam();
        ContentParsingParam.Content content = new ContentParsingParam.Content();
        Integer watermark = null, mode = null, get_ocr = null;
        switch (fileType) {
            case "pdf":
                watermark = 0;
                get_ocr = 1;
                mode = 1;
                break;
            case "doc":
            case "docx":
                mode = 1;
                break;
            default:
        }
        content.setMode(mode);
        content.setGet_ocr(get_ocr);
        content.setWatermark(watermark);
        parsingParam.setContent(content);
        return parsingParam;
    }

    /**
     * 政策问答入口
     *
     * @param dto
     */
    private JSONObject policy(DialogueByStreamParam dto, Vector<StepEntity> contextList, long start) {
        if (StringUtils.isBlank(dto.getContent())) {
            errorStream(dto, contextList, EMPTY_QUESTION);
            return null;
        }
        // 消费大模型答案
        consumerLlmAnswer(dto);

        // 启动模糊引导的条件：开启模糊引导，并且不是多轮对话或者为第一次对话
        boolean doorsillFlag = doorsillCheck(dto, contextList);
        if (!doorsillFlag) {
            return null;
        }

        // 问题进行改写
        changeContent(dto, contextList);

        // 到这里，如果rewriteContent为空，那么没有经过改写，这里就直接使用原来的问题
        if (StringUtils.isBlank(dto.getRewriteContent())) {
            dto.setContentTemp(dto.getContent());
        }

        // 问题拆解
        List<String> queryList = problemUnderstanding(contextList, dto);
        dto.setQueryList(queryList);

        // 在向量化之前要先处理逻辑,如果处理成功就直接给出回答
        boolean doneFlag = beforeVector(dto, contextList);
        if (doneFlag) {
            errorStream(dto, contextList, INTERCEPT_IP_STEP);
            return JSON.parseObject(JSON.toJSONString(dto));
        }

        // 初始化拦截词
        initWord();

        // 保存会话id
        StepEntity top = new StepEntity();
        top.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(top);
        top.setStep(DIALOGUE_ID);
        top.setPrompt(dto.getContentTemp());
        top.setResult(TRACE_ID.get());

        // 初始化步骤
        List<String> stepIdList = initStepIdList(dto);
        if (CollectionUtils.isEmpty(stepIdList)) {
            errorStream(dto, contextList, "没有步骤流程异常");
            return null;
        }

        // 定义异步执行线程池
        ThreadPoolExecutor pool = ExecutorBuilder.create().setCorePoolSize(10).setMaxPoolSize(20).setWorkQueue(new LinkedBlockingDeque<>()).build();
        // 异步执行检索
        dealAsync(pool, dto, contextList, stepIdList);

        // 监听线程池返回的数据
        listenAnswer(contextList, pool, dto);

        // 计算每一个步骤消耗的token合计
        dto.setConsumeTokensTotal(contextList.stream().mapToInt(StepEntity::getTokenTotal).sum());
        log.info("问题：{}，本次消耗token为：{}", dto.getQuestion(), dto.getConsumeTokensTotal());
        pool.shutdownNow();
        MDC.clear();
        top.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        top.setCostTime(System.currentTimeMillis() - start);
        return JSON.parseObject(JSONUtil.toJsonStr(dto));
    }

    /**
     * 改写问题
     *
     * @param dto
     * @return
     */
    private void changeContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        String rewritingStrategy = getConfiguration(applicationInfo.getApplicationId(), FILTERING_REWRITING_STRATEGY);
        if (StringUtils.isBlank(rewritingStrategy)) {
            rewritingStrategy = "YAYI";
        }

        switch (rewritingStrategy) {
            // 雅意改写
            case "YAYI":
                yayiChange(dto, contextList, start);
                break;
            // 本地改写
            case "LOCAL":
                customizeChange(dto, contextList, start);
                break;
            default:
                break;
        }
    }

    /**
     * 模糊问题引导
     *
     * @param dto         参数
     * @param contextList 执行步骤列表
     */
    private boolean doorsillCheck(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        boolean passFlag = true;
        try {
            ApplicationInfo applicationInfo = APPLICATION_INFO.get();
            String vagueGuideEnable = getConfiguration(applicationInfo.getApplicationId(), VAGUE_RISK_ENABLE);
            if (!YesNoEnum.YES.getName().equals(vagueGuideEnable)) {
                return true;
            }

            // 判断是否内置问题，如果是内置问题就直接返回
            boolean builtFlag = isBuiltInQuestion(dto);
            if (builtFlag) {
                return passFlag;
            }

            // 判断用户问题是否存在异常
            DoorsillEnum doorsill = doorsill(dto, contextList);

            // 用户问题正常标识，默认通过
            switch (doorsill) {
                // 风险
                case RISK:
                    answerUtils.dealRisk(dto, contextList);
                    passFlag = false;
                    break;
                // 模糊
                case BLUR:
                    passFlag = !vagueFlag(dto, contextList);
                    break;
                // 正常
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passFlag;
    }

    /**
     * 意图不明确的问题推荐
     *
     * @param dto
     * @return
     */
    @Override
    public boolean recommendedContent(DialogueByStreamParam dto, Vector<StepEntity> contextList, Boolean bool) {
        String contentTemp = dto.getContent();
        dto.setContentTemp(contentTemp);
        StepEntity changeStep = new StepEntity();
        contextList.add(changeStep);
        changeStep.setStep(VAGUE_LLM_STEP);
        changeStep.setPrompt(dto.getContent());
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        // 获取问题意图推荐指令
        String recommendedPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.RECOMMENDED_PROMPT);
        if (StringUtils.isBlank(recommendedPrompt)) {
            changeStep.setResult("没有问题意图推荐指令，要设置recommended_prompt配置");
            return false;
        }

        // 获取根据内容组织答案指令
        String organizateAnswerPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.ORGANIZATE_ANSWER_PROMPT);
        if (StringUtils.isBlank(organizateAnswerPrompt)) {
            changeStep.setResult("没有组织答案内容的指令，要配置对应的值");
            return false;
        }

        if (bool) {
            // 判断是否内置问题，如果是内置问题，就不进行改写
            boolean builtFlag = isBuiltInQuestion(dto);
            if (builtFlag) {
                return false;
            }
        }

        List<DialogueMessage> listMsg = Lists.newArrayList();
        recommendedPrompt = recommendedPrompt.replace("{question}", dto.getContent());
        listMsg.add(new DialogueMessage("user", recommendedPrompt));
        // 调用大模型 输入不同的指令返回不同的结果
        String changeResult = change(listMsg, changeStep);
        if (StringUtils.isNotBlank(changeResult)) {
            if (JSONUtil.isTypeJSONObject(changeResult)) {
                JSONObject jsonObject = JSON.parseObject(changeResult);
                jsonObject.getString(ORIGINAL_QUESTION_FIELD);
                String tuijianQuestions = jsonObject.getString(NEW_QUESTION_FIELD);
                try {
                    JSONArray jsonArray = JSON.parseArray(tuijianQuestions);
                    dto.setRecommendedQuestions(jsonArray);
                    if (jsonArray != null && jsonArray.size() > 1) {
                        dto.setShowRecommendedQuestions("true");
                        log.info("===============意图不明确 推荐问题===========");

                        // 找到推荐问题
                        String organizateTitleContent = IntStream.range(0, jsonArray.size())
                                .mapToObj(i -> (i + 1) + "." + jsonArray.get(i))
                                .filter(StringUtils::isNotEmpty)
                                .collect(Collectors.joining("\n"));
                        List<DialogueMessage> dialogueMessages = Lists.newArrayList();
                        organizateAnswerPrompt = organizateAnswerPrompt.replace(QUESTION_PLACEHOLDER, organizateTitleContent);
                        dialogueMessages.add(new DialogueMessage("user", organizateAnswerPrompt));
                        // 调用大模型，让大模型根据知识库QA给出的推荐问题，然后形成最终的答案

                        StepEntity quesionChangeStep = new StepEntity();
                        contextList.add(quesionChangeStep);
                        quesionChangeStep.setStep(LLM_CHANGE_CONTENT_RECOMMENDED);
                        //quesionChangeStep.setPrompt(dto.getContent());
                        String organizateAnswerResult = vagueStream(organizateAnswerPrompt, quesionChangeStep, dto, System.currentTimeMillis());
                        //String organizateAnswerResult = change(dialogueMessages, quesionChangeStep);
                        dto.setAnswer(organizateAnswerResult);
                        log.info("organizateAnswerResult:" + organizateAnswerResult);
                        return true;
                    }
                    //if (jsonArray != null && jsonArray.size() == 1) {
                    //    contentTemp = jsonArray.getString(0);
                    //    log.info("===============意图明确 推荐问题===========");
                    //}
                } catch (Exception e) {
                    //contentTemp = tuijianQuestions;
                    //jsonArray = new JSONArray();
                    //jsonArray.add(contentTemp);
                    log.error("===============意图明确 不推荐问题===========");
//                    log.error("意图明确问题异常：", e.getMessage());
//                    e.printStackTrace();
                }
            }
        }
        //listMsg.add(new DialogueMessage("user", contentTemp));
        //dto.setContentTemp(contentTemp);
        //dto.setRewriteContent(contentTemp);
        //changeStep.setResult(contentTemp);
        //dto.setRecommendedListMsg(listMsg);
        return false;
    }


    /**
     * 意图不明确的问题推荐
     *
     * @param dto
     * @return
     */
    @Override
    public boolean vagueContent(DialogueByStreamParam dto, Vector<StepEntity> contextList, Boolean bool) {
        String contentTemp = dto.getContent();
        dto.setContentTemp(contentTemp);
        StepEntity changeStep = new StepEntity();
        contextList.add(changeStep);
        changeStep.setStep(VAGUE_LLM_STEP);
        changeStep.setPrompt(dto.getContent());
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        // 获取问题意图推荐指令
        String recommendedPrompt = applicationInfo.getVagueConfig();
        if (StringUtils.isBlank(recommendedPrompt)) {
            changeStep.setResult("应用没有配置模糊问题推荐指令");
            return false;
        }

        List<DialogueMessage> listMsg = Lists.newArrayList();
        recommendedPrompt = recommendedPrompt.replace("{question}", dto.getContent());
        listMsg.add(new DialogueMessage("user", recommendedPrompt));
        // 调用大模型 输入不同的指令返回不同的结果
        String changeResult = change(listMsg, changeStep);
        if (StringUtils.isNotBlank(changeResult)) {
            if (JSONUtil.isTypeJSONObject(changeResult)) {
                JSONObject jsonObject = JSON.parseObject(changeResult);
                jsonObject.getString(ORIGINAL_QUESTION_FIELD);
                String tuijianQuestions = jsonObject.getString(NEW_QUESTION_FIELD);
                try {
                    JSONArray jsonArray = JSON.parseArray(tuijianQuestions);
                    dto.setRecommendedQuestions(jsonArray);
                    if (jsonArray != null && jsonArray.size() > 1) {
                        dto.setShowRecommendedQuestions("true");
                        log.info("===============意图不明确 推荐问题===========");
                        return true;
                    }
                } catch (Exception e) {
                    log.error("===============意图明确 不推荐问题===========");
                }
            }
        }
        return false;
    }



    /**
     * 调用雅意13B接口
     *
     * @param question
     * @return
     */
    private String vagueStream(String question, StepEntity changeStep, DialogueByStreamParam dto, long start) {
        try {
            cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
            ApplicationInfo applicationInfo = APPLICATION_INFO.get();

            // 改写参数，从配置中获取模型名称
            String vagueGuideModelId = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), VAGUE_GUIDE_MODEL_ID);
            if (StringUtils.isNotBlank(vagueGuideModelId)) {
                stepParam.set(LLM_PARAM_MODEL_ID, vagueGuideModelId);
            }
            // 大模型是否需要json格式化
            stepParam.set(LLM_JSON_FLAG, StringConstant.BLANK);
            changeStep.setParam(stepParam);
            // StringBuilder sb = new StringBuilder();
            List<DialogueMessage> list = Lists.newArrayList();
            String systemPromptResult = applicationInfo.getSystemPromptResult();
            list.add(new DialogueMessage(SYSTEM_PROMPT_FIELD, systemPromptResult));

            AnswerStepData answerStep = new AnswerStepData();
            answerStep.setIndex(0);
            answerStep.setStep(VAGUE_LLM_STRATEGY);
            answerStep.setStreamStatus(NumberConstants.ZERO);
            STEP_BY_LIST.get().add(answerStep);

            // 从配置中读取模糊查询是否不推送推荐问题开关
            String vaguePushFlag = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.VAGUE_NOT_ANSWER_FLAG);
            return answerUtils.getGenerateCommon(question, StringConstant.BLANK, changeStep, list, result -> {
                if (null == changeStep.getFirstLlmTime()) {
                    changeStep.setFirstLlmTime(System.currentTimeMillis() - start);
                }
                if (YesNoEnum.YES.getName().equals(vaguePushFlag)){
                    answerStep.setStatus(ANSWER_FINAL);
                    answerStep.setAnswer(applicationInfo.getFailureTalk());
                    changeStep.setResult(applicationInfo.getFailureTalk());
                } else {
                    answerStep.setStatus(result.getStatus());
                    answerStep.setAnswer(result.getAnswer());
                    changeStep.setResult(result.getAnswer());
                }
                dto.getAnswerConsumer().accept(answerStep);
            }, StringConstant.BLANK);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return StringConstant.BLANK;
    }

    /**
     * 调用雅意13B接口
     *
     * @param list
     * @return
     */
    private String change(List<DialogueMessage> list, StepEntity changeStep) {
        try {
            cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
            ApplicationInfo applicationInfo = APPLICATION_INFO.get();

            // 改写参数，从配置中获取模型名称
            String vagueGuideModelId = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), VAGUE_GUIDE_MODEL_ID);
            if (StringUtils.isNotBlank(vagueGuideModelId)) {
                stepParam.set(LLM_PARAM_MODEL_ID, vagueGuideModelId);
            }
            // 大模型是否需要json格式化
            stepParam.set(LLM_JSON_FLAG, StringConstant.BLANK);
            changeStep.setParam(stepParam);
            //StringBuilder sb = new StringBuilder();
            String generateCommon = answerUtils.getGenerateCommon(list.get(list.size() - 1).getContent(), StringConstant.BLANK, changeStep, list, StringConstant.BLANK);
            return AnswerUtils.dealLLmJson(generateCommon);
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return StringConstant.BLANK;
    }


    /**
     * 在向量化之前要先处理逻辑
     *
     * @param dto
     * @param contextList
     * @return
     */
    private boolean beforeVector(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        // 拦截ip
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getIpFlag())) {
            return false;
        }
        boolean exitFlag = interceptIp(dto.getIpAddress());
        StepEntity ipStep = new StepEntity();
        contextList.add(ipStep);
        ipStep.setStep(INTERCEPT_IP_STEP);
        ipStep.setPrompt(dto.getIpAddress());
        ipStep.setResult(INTERCEPT_PASS);
        if (exitFlag) {
            ipStep.setResult(INTERCEPT_IP_NOT + dto.getIpAddress());
            //finalAnswer = applicationInfo.getFailureTalk();
            //SseEmitterUtils.sendSpeed(dto, finalAnswer);
            //SseEmitterUtils.complete(dto.getClientId());
            //dto.setAnswer(finalAnswer);
            //insertDialogue(dto, contextList);
            return true;
        }

        return false;
    }

    /**
     * 处理多个ip
     *
     * @param ip
     * @return
     */
    private boolean interceptIp(String ip) {
        IpBlacklist byIp = null;
        if (StringUtils.isNotBlank(ip)) {
            String[] split = ip.split(",");
            String nowTime = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd HH:mm:ss");
            // todo:
            Wrappers wrappers = Wrappers.init()
                    .and(IP_BLACKLIST.IP_ADDRESS.in(split))
                    .limit(1);
            //byIp = ipBlacklistMapper.getByIp(split, nowTime);
            byIp = ipBlacklistMapper.selectOneByQuery(wrappers);
        }
        return null != byIp;
    }

    /**
     * 保存对话
     *
     * @param dto         对话日志
     * @param contentList 溯源文件
     */
    public void insertDialogue(DialogueByStreamParam dto, List<StepEntity> contentList) {
        String answer = dto.getAnswer();
        final Dialogue entity = new Dialogue();
        entity.setDialogueId(dto.getDialogueId());
        entity.setQuestion(dto.getContent() + StringConstant.BLANK.trim());
        entity.setConversationId(dto.getConversationId());
        if (null != answer) {
            ApplicationInfo applicationInfo = APPLICATION_INFO.get();

            String userMessagePre = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), USER_MESSAGE_PRE);
            if (StringUtils.isNotBlank(userMessagePre)) {
                answer = answer.replace(userMessagePre, StringConstant.BLANK);
            }
            answer = answer
                    .replace(INFERENCE_END_TAG, "\n")
                    .replace(INFERENCE_START_TAG, "\n");
        }

        entity.setAnswer(answer);
        entity.setApplicationId(APPLICATION_INFO.get().getApplicationId());
        // 从token中获取用户信息
        TokenUser tokenUserInfo = ContextHolders.getTokenUserInfo();
        if (null != tokenUserInfo && tokenUserInfo.getId() != null) {
            entity.setUserName(tokenUserInfo.getUserName());
            entity.setUserId(tokenUserInfo.getIdNo());
        } else {
            entity.setUserName(dto.getUserName());
            entity.setUserId(dto.getUserId());
        }
        entity.setClientType(dto.getClientType());
        String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        entity.setTraceId(traceId);
        entity.setCreateTime(LocalDateTimeUtil.now().format(DateTimeFormatter.ofPattern(DateUtil.DEFAULT_FORMAT)));
        entity.setVerifyStatus(DialogueVerifyStatusEnum.WAIT_VERIFY.getCode());
        entity.setDeleted(Integer.valueOf(YesNoEnum.NO.getCode()));
        entity.setAnswerFlag("true".equals(dto.getAnswerFlag()) ? "0" : "1");
        entity.setConsumeTokensTotal(dto.getConsumeTokensTotal()); //大模型消耗token值
        entity.setDeleted(Integer.valueOf(YesNoEnum.NO.getCode()));

        /*
         * 能正常回答时，直接审核通过
         * 应用中存在相同问题且已审核通过的数据 系统直接审核  审核人信息记录系统信息
         * 1-根据应用id和问题名称和审核通过状态查询是否存在相同问题
         * 2-不存在走原有逻辑 存在将对应的两个审核相关字段（verifyStatus auditStatus）设置为已审核通过
         **/
        if ("true".equals(dto.getAnswerFlag())) {
            final EsPageInfo<Dialogue> page = EsWrappers.lambdaChainQuery(dialogueMapper)
                    .eq(Dialogue::getAuditStatus, DialoguAuditStatusEnum.AUDIT_PASS.getCode())
                    .eq(Dialogue::getApplicationId, APPLICATION_INFO.get().getApplicationId())
                    .eq(Dialogue::getQuestion, dto.getContent() + StringConstant.BLANK.trim()).orderByDesc("createTime.keyword").page(1, 1);
            if (page != null && CollectionUtil.isNotEmpty(page.getList())) {
                log.info("存在相同问题已经审核通过，该问题不需要审核。");
                Dialogue dialogue = page.getList().get(0);
                entity.setVerityDialogueId(dialogue.getDialogueId());
                entity.setVerifyDeptId(SystemVerifyContant.SYSTEM_VERIFY_DEPT_ID);
                entity.setVerifyDeptName(SystemVerifyContant.SYSTEM_VERIFY_DEPT_NAME);
                entity.setVerifyUserName(SystemVerifyContant.SYSTEM_VERIFY_USER_NAME);
                entity.setVerifyUserId(SystemVerifyContant.SYSTEM_VERIFY_USER_ID);
                entity.setVerifyStatus(dialogue.getVerifyStatus());
                entity.setAuditStatus(dialogue.getAuditStatus());
                entity.setAuditTime(com.wenge.model.utils.DateUtil.getCurrentTime());
                entity.setVerifyAnswer(entity.getAnswer());

                String pushQaEnble = AppConfigContant.getConfiguration(dto.getApplicationId(), SYSTEM_CHECK_PUSH_QA_ENABLE);

                if (YesNoEnum.YES.getName().equals(pushQaEnble)) {
                    // 自动推送数据到知识库 并删除相同问题的数据
                    log.info("相同问题系统自动审核 推送到知识库覆盖原有相同问题数据...问题：{}, 应用ID：{}", entity.getQuestion(), dto.getApplicationId());
                    entity.setVerifyAnswer(entity.getAnswer());
                    TokenUser tokenUserSystem = new TokenUser();
                    tokenUserSystem.setDeptId(SystemVerifyContant.SYSTEM_VERIFY_DEPT_ID);
                    tokenUserSystem.setDeptName(SystemVerifyContant.SYSTEM_VERIFY_DEPT_NAME);
                    tokenUserSystem.setUserName(SystemVerifyContant.SYSTEM_VERIFY_USER_NAME);
                    try {
                        tokenUserSystem.setId(-1L); // 系统审核 -1
                    } catch (Exception e) {
                        e.printStackTrace();
                        entity.setVerifyAnswer(entity.getAnswer());
                    }
                    dialogueRecordService.pushingKnowledgeData(entity, dto.getApplicationId(), "4", tokenUserSystem);
                }
            }
        }

        // 长文写作回答过长，这里截取前 10000个文本
        String answer1 = entity.getAnswer();
        if (StringUtils.isNotBlank(answer1) && answer1.length() > 10000) {
            int length = answer1.length();
            int min = Math.min(length, 10000);
            entity.setAnswer(answer1.substring(0, min));
        }
        dialogueMapper.insert(entity);

        if (CollectionUtils.isNotEmpty(contentList)) {
            contentList.forEach(item -> {
                item.setDialogueId(entity.getDialogueId());
                item.setConversationId(entity.getConversationId());
            });
            // 保存上下文
            addContent(contentList);
        }

    }

    /**
     * 保存上下文
     *
     * @param dialogueStepList
     */
    private void addContent(List<StepEntity> dialogueStepList) {
        new Thread(() -> {
            List<DialogueStep> stepList = stepToDialogueStep(dialogueStepList);
            dialogueStepMapper.insertBatch(stepList);
        }).start();
    }

    /**
     * 初始化敏感词库
     */
    private void initWord() {
        // 获取词库
        initInterceptWordList();
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        List<InterceptWord> interceptWordList = Lists.newArrayList();
        if (!INTERCEPT_WORD_HOUSE_MAP.isEmpty()) {
            // 获取应用绑定的词库
            List<Long> wordHouseIdList = APP_INTERCEPT_WORD_HOUSE_ID_MAP.get(applicationInfo.getApplicationId());
            if (CollectionUtils.isNotEmpty(wordHouseIdList)) {
                // 获取词库
                interceptWordList = wordHouseIdList.stream().flatMap(p -> {
                    List<InterceptWord> interceptWords = INTERCEPT_WORD_HOUSE_MAP.get(p);
                    if (CollectionUtils.isNotEmpty(interceptWords)) {
                        return interceptWords.stream();
                    }
                    return Stream.empty();
                }).collect(Collectors.toList());
                // 获取词库分组
                initWordByApplicationId(interceptWordList, applicationInfo.getApplicationId());
            }
        }
        // 初始化场景
        initScene();
    }

    /**
     * 异步处理
     *
     * @param pool
     * @param dto
     * @param contextList
     * @param stepIdList
     * @return
     */
    private void dealAsync(ThreadPoolExecutor pool, DialogueByStreamParam dto, Vector<StepEntity> contextList, List<String> stepIdList) {
        if (!answerStrategyMap.isEmpty()) {
            // 索引从3开始，0-2是预留
            int index = 3;
            for (String stepId : stepIdList) {
                if (answerStrategyMap.containsKey(stepId)) {
                    AnswerStrategy answerStrategy = answerStrategyMap.get(stepId);
                    AnswerStepData stepData = new AnswerStepData();
                    stepData.setStep(stepId);
                    stepData.setIndex(index++);
                    stepData.setStatus(StepStatusEnum.ANSWER_SEARCH);
                    stepData.setStreamStatus(NumberConstants.ZERO);
                    STEP_BY_LIST.get().add(stepData);
                    pool.execute(() -> {
                        try {
                            MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
                            answerStrategy.getAnswer(dto, contextList, stepData);
                        } catch (Exception e) {
                            log.error("执行策略异常,stepId:{}", stepId, e);
                        } finally {
                            MDC.clear();
                        }
                    });
                }
            }
        }
    }

    /**
     * 监听回答
     *
     * @param contextList
     * @param pool
     * @return
     */
    private void listenAnswer(Vector<StepEntity> contextList, ThreadPoolExecutor pool, DialogueByStreamParam dto) {
        StepEntity timeout = new StepEntity();
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        contextList.add(timeout);
        timeout.setStep("超时拦截");
        long start = System.currentTimeMillis();
        timeout.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        //String finalAnswer = StringConstant.BLANK;
        Integer answerTimeout = applicationInfo.getAnswerTimeout();
        if (answerTimeout < 20) {
            answerTimeout = 60;
        }
        dto.setContextList(contextList);
        List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
        while (true) {
            // 回答超时，返回结果
            timeout.setResult("未拦截，耗时：" + (System.currentTimeMillis() - start));
            timeout.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            timeout.setCostTime(System.currentTimeMillis() - start);
            if (System.currentTimeMillis() - start > answerTimeout * 1000) {
                boolean anyMatch = answerStepData.stream().anyMatch(p -> p.getStreamStatus() == NumberConstants.ONE);
                // 如果没有任务在回答，则要停止
                if (!anyMatch) {
                    timeout.setResult("已拦截，耗时：" + (System.currentTimeMillis() - start));
                    timeout.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                    timeout.setCostTime(System.currentTimeMillis() - start);
                    break;
                }
            }

            // 判断是否有任务在回答
            boolean breakFlag = breakFlag(contextList, dto);
            if (breakFlag) {
                break;
            }

            // 如果没有任务
            if (pool.getActiveCount() == 0 || pool.isShutdown() || pool.isTerminated()) {
                // 理论上，一次问答不会超过 10分钟，如果超过10分钟，那么这里就直接停止
                if (System.currentTimeMillis() - start > 60 * 10 * 1000) {
                    break;
                }
                // 如果有任务给出答案，那么就继续监听
                boolean answerFinalFlag = answerStepData.stream().anyMatch(p -> p.getStatus().equals(StepStatusEnum.ANSWER_COMPLETE));
                if (answerFinalFlag) {
                    continue;
                }
                log.info("没有任务，则退出");
                break;
            }
            ThreadUtil.safeSleep(20);
        }
        //return finalAnswer;
    }

    /**
     * 保存大模型prompt会话
     *
     * @param dto
     */
    private void saveLlmPromptMessage(DialogueByStreamParam dto) {
        try {
            DialogueMessage lastUserMsg = null;
            // 获取最新一条user的消息
            List<DialogueMessage> messages = Lists.newArrayList();
            lastUserMsg = new DialogueMessage();
            lastUserMsg.setDialogueId(dto.getDialogueId());
            lastUserMsg.setQuestion(dto.getQuestion());
            lastUserMsg.setRole("user");
            lastUserMsg.setContent(dto.getContentTemp());

            messages.add(lastUserMsg);
            LlmConversation conversation = new LlmConversation();
            conversation.setId(IdUtil.simpleUUID());
            conversation.setConversationId(dto.getConversationId());
            conversation.setMessageList(messages);
            //conversation.setStep(stepName);
            String period = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), "yyyy-MM-dd HH:mm:ss");
            conversation.setCreateTime(period);

            LambdaEsQueryWrapper<LlmConversation> wrapper = EsWrappers.lambdaQuery(LlmConversation.class)
                    .eq(LlmConversation::getConversationId, dto.getConversationId())
                    .orderByDesc("createTime.keyword")
                    .limit(1);
            List<LlmConversation> smartConversations = Lists.newArrayList();
            for (int i = 0; i < 5; i++) {
                try {
                    smartConversations = conversationMapper.selectList(wrapper);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtil.sleep(150);
                }
            }
            //List<LlmConversation> smartConversations = conversationMapper.selectList(wrapper);

            // 添加问题
            if (CollectionUtils.isNotEmpty(smartConversations)) {
                LlmConversation smartConversation = smartConversations.get(0);
                messages.clear();
                List<DialogueMessage> messageList = smartConversation.getMessageList();
                messageList.add(lastUserMsg);
                messages.addAll(messageList);
            }

            // 添加答案
            DialogueMessage answer = new DialogueMessage(dto.getDialogueId(), "yayi", dto.getAnswer());
            messages.add(answer);
            for (int i = 0; i < 10; i++) {
                try {
                    conversationMapper.insert(conversation);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtil.sleep(150);
                }
            }

            // 清空历史消息
            wrapper = EsWrappers.lambdaQuery(LlmConversation.class)
                    .eq(LlmConversation::getConversationId, dto.getConversationId())
                    .lt(LlmConversation::getCreateTime, period);
            for (int i = 0; i < 10; i++) {
                try {
                    conversationMapper.delete(wrapper);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    ThreadUtil.sleep(150);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录引用来源
     *
     * @param dialogueId
     * @param contextList
     * @return
     */
    private void recordReference(String dialogueId, Vector<StepEntity> contextList) {
        try {
            List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
            if (CollectionUtils.isEmpty(answerStepData)) {
                return;
            }
            Optional<AnswerStepData> any = answerStepData.stream().filter(p -> p.getStatus().equals(ANSWER_FINAL)).findAny();
            if (!any.isPresent()) {
                return;
            }

            AnswerStepData stepData = any.get();
            String stepId = stepData.getStep();
            if (StringUtils.isNotBlank(stepId)) {
                AnswerStrategy answerStrategy = answerStrategyMap.get(stepId);
                if (null == answerStrategy) {
                    log.info("没有找到指定策略");
                    return;
                }
                StepEntity reference = answerStrategy.answerRef(dialogueId, contextList);
                if (null != reference) {
                    contextList.add(reference);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存对话
     *
     * @param dto
     * @param contextList
     */
    private void saveDialog(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        List<WebsiteConfig> websiteConfigList = WebsiteConfig.create()
                .where(WEBSITE_CONFIG.APPLICATION_ID.eq(dto.getApplicationId()))
                .and(WEBSITE_CONFIG.TYPE.eq(WebsiteTypeEnum.SHOW_BUT.getCode()))
                .and(WEBSITE_CONFIG.STATUS.eq(YesNoEnum.YES.getName()))
                .lists();
        dto.setWebsiteConfigs(websiteConfigList);
        insertDialogue(dto, contextList);
    }

    /**
     * 初始化步骤
     *
     * @return
     */
    private List<String> initStepIdList(DialogueByStreamParam dto) {
        List<String> stepIdList = ListUtil.toList();

        // 如果按搜索类型检索，这里需要指定策略为知识库策略(这块逻辑是用于智能搜索，非智能问答)
        if (!Objects.equals(dto.getSearchType(), DialogueSearchFileTypeEnum.SEARCH_All.getCode())) {
            stepIdList.add(AnswerStrategyContant.FINAL_COLLECT);
            return stepIdList;
        }

        // 开启知识库检索
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        String processStep = applicationInfo.getProcessStep();
        if (StringUtils.isNotBlank(processStep)) {
            String[] split = processStep.split(",");
            stepIdList = ListUtil.toList(split);
        }

        // 如果开启了互联网兜底，互联网兜底放在大模型发散前面
        if (StringUtils.equals(applicationInfo.getFinalNetworkFlag(), YesNoEnum.YES.getName())) {
            int finalCollectIndex = stepIdList.indexOf(AnswerStrategyContant.FIND_ANSWER_BY_MODEL);
            if (-1 == finalCollectIndex) {
                // 没有大模型发散步骤，把联网兜底放到步骤最后面
                stepIdList.add(AnswerStrategyContant.NETWORK_STRATEGY);
            } else {
                // 有大模型发散步骤，把联网兜底放到检索知识库的前面
                stepIdList.add(finalCollectIndex - 1, AnswerStrategyContant.NETWORK_STRATEGY);
            }
        }

        return stepIdList;
    }

    /**
     * 获取签名
     *
     * @param applicationInfo
     * @param timestamp
     * @return
     */
    private String getSignature(ApplicationInfo applicationInfo, long timestamp) {
        String signatureContent = applicationInfo.getApplicationId() + "|\n" + applicationInfo.getApiSecret() + "|\n" + timestamp;
        return SecureUtil.md5(signatureContent);
    }

    /**
     * 校验参数
     *
     * @param param
     * @param request
     * @param applicationInfo
     * @return
     */
    private Result check(DialogueApiParam param, HttpServletRequest request, ApplicationInfo applicationInfo) {
        if (StringUtils.isBlank(param.getApiKey())) {
            return Result.fail("appkey不能为空");
        }
        String signature = request.getHeader("signature");

        if (StringUtils.isBlank(signature)) {
            return Result.fail("signature不能为空");
        }
        String timestamp = request.getHeader("timestamp");
        if (StringUtils.isBlank(param.getApiKey())) {
            return Result.fail("timestamp不能为空");
        }

        //if (System.currentTimeMillis() - Long.parseLong(timestamp) > 1000) {
        //    return Result.fail("请求超时不能为空");
        //}
        if (null == applicationInfo) {
            return Result.fail("appkey不存在");
        }
        DialogueByStreamParam dialogueByStreamParam = param.getParam();
        if (null == dialogueByStreamParam || StringUtils.isBlank(dialogueByStreamParam.getContent())) {
            return Result.fail("问题内容不能为空");
        }

        String signatured = getSignature(applicationInfo, Long.parseLong(timestamp));
        if (!signature.equals(signatured)) {
            return Result.fail("签名错误");
        }
        return Result.success();
    }

    /**
     * 消费大模型答案
     *
     * @param dto
     */
    private void consumerLlmAnswer(DialogueByStreamParam dto) {
        List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
        if (CollectionUtils.isEmpty(answerStepData)) {
            answerStepData = Lists.newArrayList();
            STEP_BY_LIST.set(answerStepData);
        }
        dto.setListMsg(Lists.newArrayList());
        JSONObject currentData = new JSONObject();
        currentData.put("current", 0);
        currentData.put("beginFlag", false);
        dto.setAnswerConsumer(answers -> {
            JSONObject currentData1 = answers.getCurrentData();
            if (null == currentData1) {
                answers.setCurrentData(currentData);
            }
            findFinalAnswer(answers, dto);
        });
    }

    /**
     * 提取纯文本
     *
     * @param content
     * @return
     */
    private static String plainText(String content) {
        try {
            // 将Markdown转换为纯文本
            String textContent = markdownText(content);

            return textContent.replaceAll("<.*?>", StringConstant.BLANK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * 提取markdown中的纯文本
     *
     * @param content
     * @return
     */
    private static String markdownText(String content) {
        // 创建Markdown解析器
        Parser parser = Parser.builder().build();
        Node document = parser.parse(content);

        // 创建纯文本渲染器
        TextContentRenderer renderer = TextContentRenderer.builder().build();

        // 将Markdown转换为纯文本
        return renderer.render(document);
    }

    /**
     * 完成对话
     *
     * @param dto
     * @param contextList
     */
    @Override
    public void completeAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        // 记录最终答案的引用来源
        recordReference(dto.getDialogueId(), contextList);

        // 无法回答时，还需要推荐相关机构
        //suggestOrg(dto);

        // 记录对话
        saveDialog(dto, contextList);
        // 记录prompt对话内容
        saveLlmPromptMessage(dto);
    }

    @Override
    public Result<String> checkSensitiveWord(SensitiveWordCheck param) {
        try {
            if (StringUtils.isBlank(param.getContent())) {
                return Result.success(null);
            }

            if (StringUtils.isBlank(param.getApplicationId())) {
                return Result.fail("应用id不能为空");
            }
            List<InterceptWord> list = Lists.newArrayList();
            if (!APP_WORD_MAP.containsKey(param.getApplicationId())) {
                Wrappers<Object> init = Wrappers.init()
                        .select(INTERCEPT_WORD.ALL_COLUMNS)
                        .innerJoin(INTERCEPT_WORD_HOUSE_APPLICATION_REL).on(INTERCEPT_WORD_HOUSE_APPLICATION_REL.INTERCEPT_WORD_HOUSE_ID.eq(INTERCEPT_WORD.INTERCEPT_WORD_HOUSE_ID))
                        .innerJoin(INTERCEPT_WORD_HOUSE).on(INTERCEPT_WORD_HOUSE.ID.eq(INTERCEPT_WORD_HOUSE_APPLICATION_REL.INTERCEPT_WORD_HOUSE_ID))
                        .where(INTERCEPT_WORD_HOUSE.STATUS.eq(1))
                        .and(INTERCEPT_WORD.STATUS.eq(1))
                        .and(INTERCEPT_WORD_HOUSE_APPLICATION_REL.APPLICATION_ID.eq(param.getApplicationId()));

                list = wordService.list(init);
                if (CollectionUtils.isNotEmpty(list)) {
                    APP_WORD_MAP.put(param.getApplicationId(), list);
                }
            } else {
                list = APP_WORD_MAP.get(param.getApplicationId());
            }

            if (CollectionUtils.isEmpty(list)) {
                return Result.success(null);
            }

            Map<String, List<InterceptWord>> collect = list.stream().collect(Collectors.groupingBy(InterceptWord::getType));
            List<InterceptWord> interceptWords = collect.get(InterceptTypeEnum.INTERCEPT_ALL_QUESTION.getName());
            List<String> collect1 = interceptWords.stream().map(InterceptWord::getContent).distinct().collect(Collectors.toList());

            // APPLICATION_INFO.set(applicationInfo);
            // initWord();
            // 拦截问题全词
            String word = checkSensitive(collect1,
                    (not, pinyin) -> param.getContent().equalsIgnoreCase(not) || param.getContent().equalsIgnoreCase(pinyin));
            if (StringUtils.isNotBlank(word)) {
                return Result.success(word);
            }

            // 拦截提问
            interceptWords = collect.get(InterceptTypeEnum.INTERCEPT_QUESTION.getName());
            collect1 = interceptWords.stream().map(InterceptWord::getContent).distinct().collect(Collectors.toList());
            word = checkSensitive(collect1,
                    (not, pinyin) -> param.getContent().toLowerCase().contains(not.toLowerCase()) || param.getContent().toLowerCase().contains(pinyin.toLowerCase()));
            if (StringUtils.isNotBlank(word)) {
                return Result.success(word);
            }

            // // 拦截禁用词

            interceptWords = collect.get(InterceptTypeEnum.FORBIDDEN_WORD.getName());
            collect1 = interceptWords.stream().map(InterceptWord::getContent).distinct().collect(Collectors.toList());
            word = checkSensitive(collect1,
                    (not, pinyin) -> param.getContent().toLowerCase().contains(not.toLowerCase()) || param.getContent().toLowerCase().contains(pinyin.toLowerCase()));
            if (StringUtils.isNotBlank(word)) {
                return Result.success(word);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            APPLICATION_INFO.remove();
        }

        return Result.success(null);
    }

    @Override
    public Result genEChartsDataFromModel(GenEchartsParam param) {
        String applicationId = param.getApplicationId();
        ApplicationInfo applicationInfo = appInfoService.getByAppId(applicationId);
        DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        // 从应用配置中读取系统指令
        String echartsSystemPrompt = AppConfigContant.getConfiguration(applicationId, ECHARTS_SYSTEM_PROMPT);
        // 获取模型信息
        LlmStrategy activeLLm = llmInfoService.getActiveLLm((applicationInfo.getModelId()));
        if (null == activeLLm) {
            return Result.fail("没有配置大模型");
        }

        String resultJson = null;
        try {
            List<YayiMessage> yayiMessageList = Lists.newArrayList();
            YayiMessage yayiMessage = new YayiMessage();
            yayiMessage.setRole("system");
            yayiMessage.setContent(echartsSystemPrompt);
            yayiMessageList.add(yayiMessage);
            resultJson = activeLLm.generate(JSON.toJSONString(param.getContent()), yayiMessageList, new StepEntity(), false);
            String text = resultJson.replaceAll("```", "").replaceAll("\n", "").replaceAll("\n\n", "");
//            // 转换文本为 html 文件
//            MultipartFile file = MultipartFileUtils.convertTxtToHtml(text, "echarts" + "_" + System.currentTimeMillis());
//            // 将 html 上传 minio
//            MinioInfoResult infoResult = wosUtil.upload(bucket, "echarts", file, true);
            return Result.success(text);
        } catch (Exception e) {
            log.error("通过大模型生成 echarts 图出错", e);
            return Result.success(resultJson);
        }
    }

    @Override
    public Result conversationSummary(BiddingMatchingParam param) {
        PromptConfigListParam promptConfigListParam = new PromptConfigListParam();
        promptConfigListParam.setPromptTitle("对话总结提示词");
        List<PromptConfig> prompt = promptConfigService.getBuiltInPrompt(promptConfigListParam);
        if (CollectionUtils.isEmpty(prompt)) {
            log.info("应用:{} 未配置提示词", param.getAppId());
            throw new RuntimeException("当前应用未配置提示词");
        }
        String finalPrompt = String.format(prompt.get(0).getContent(), param.getConversation());
        String appId = param.getAppId();
        ApplicationInfo application = applicationInfoService.getByAppId(appId);
        String modelId ;
        if (null == application) {
            // 如果为空，用v3
            modelId = "87026c3464664ad49a8b622ec719fa70";
            ApplicationInfo applicationInfo = new ApplicationInfo();
            LlmInfo llmInfo = llmInfoService.getByModelId(modelId);
            applicationInfo.setLlmInfo(llmInfo);
            DialogueServiceImpl.APPLICATION_INFO.set(applicationInfo);
        } else {
            modelId = application.getModelId();
            DialogueServiceImpl.APPLICATION_INFO.set(application);
        }
        LlmStrategy activeLLm = llmInfoService.getActiveLLm(modelId);
        String result = activeLLm.generate(finalPrompt, null, new StepEntity(), true);
//        String matchingResult = result.getData().getChoices().get(0).getMessage().getContent().replace('`', ' ').replace("json", StringConstant.BLANK);
        return Result.success(result);
    }

    @Override
    public Result queryCollectRearangeStepInfo(DialogueCollectRerrangeStepParam param) {
        // 读取excel数据
        Map<String, String> dataMap = new HashMap<>();
        List<String> questionList = new ArrayList<>();
        List<String> applicationIdList = new ArrayList<>();
        MultipartFile file = param.getFile();
        if (file == null) {
            return Result.fail("文件不能为空");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            return Result.fail("不支持该文件类型，请上传xlsx类型的文件");
        }
        try {
            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            // 第一行为  序号 问题 应用名 应用id
            // 从第二行开始读取
            int row = 1;
            List<List<Object>> read = reader.read();
            read = read.subList(row, read.size());
            for (List<Object> dataDetailList : read) {
                List<Object> objects = JSON.parseArray(JSON.toJSONString(dataDetailList));
                // 序号
                String number = getCelleValue(objects, 0);
                // 问题
                String question = getCelleValue(objects, 1);
                // 应用名
                String applicationName = getCelleValue(objects, 2);
                // 应用名id
                String applicationId = getCelleValue(objects, 3);
                questionList.add(question);
                applicationIdList.add(applicationId);
                dataMap.put(applicationId + ":" + question, number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 查询对话记录
        List<Dialogue> dialogues = dialogueRecordService.getRecorByApplicationIdsAndQuestions(questionList, applicationIdList);
        if (CollectionUtils.isEmpty(dialogues)) {
            return Result.success();
        }
        Map<String, Dialogue> dialogueIdMap = Maps.uniqueIndex(
                dialogues,
                Dialogue::getDialogueId
        );

        // 查询重排步骤
        List<String> dialogueIds = dialogues.stream().map(Dialogue::getDialogueId).collect(Collectors.toList());
        List<DialogueStep> dialogueSteps = recommendQuestionService.getRerrangeStepByDialogIdList(dialogueIds);
        if (CollectionUtils.isEmpty(dialogueSteps)) {
            return Result.success();
        }
        dialogueSteps.forEach(v->{
            Dialogue dialogue = dialogueIdMap.get(v.getDialogueId());
            if (null != v.getPrompt() && null != dialogue) {
                String key = dialogue.getApplicationId() + ":" + dialogue.getQuestion();
                String number = dataMap.get(key);
                String articleParaStr = extractParaJson(v.getPrompt().toString());
                writeToTxt(number, articleParaStr);
            }
        });

        return Result.success();
    }

    /**
     * 将内容写到指定文件夹
     * @param fileName
     * @param content
     */
    private void writeToTxt(String fileName, String content) {
        if (StringUtils.isNotBlank(fileName)) {
            try {
                String dirPath = "output_folder/";
                Files.createDirectories(Paths.get(dirPath));
                Path filePath = Paths.get(dirPath + fileName + ".txt");
                // 写入文件内容
                Files.write(filePath,
                        content.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                log.info("成功写入文件: {}", filePath.toAbsolutePath());
            } catch (IOException e) {
                log.error("写入文件失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 从单元格获取数据
     *
     * @param dataList
     * @param index
     * @return
     */
    private String getCelleValue(List<Object> dataList, Integer index) {
        if (dataList.size() > index && null != dataList.get(index) && StringUtils.isNotBlank(dataList.get(index).toString())) {
            return dataList.get(index).toString();
        }
        return "";
    }

    private String extractParaJson(String originalJsonStr) {
        try {
            JSONObject original = JSONObject.parseObject(originalJsonStr);
            JSONObject content = original.getJSONObject("content");
            if (null == content) {
                return StringConstant.BLANK;
            }
            JSONArray articles = content.getJSONArray("articles");
            if (null == articles) {
                return StringConstant.BLANK;
            }

            JSONObject result = new JSONObject();
            JSONArray newArticles = new JSONArray();

            for (int i = 0; i < articles.size(); i++) {
                JSONObject article = articles.getJSONObject(i);
                String para = article.getString("para");
                JSONObject newArticle = new JSONObject();
                newArticle.put("para", para);
                newArticles.add(newArticle);
            }

            result.put("articles", newArticles);
            return result.toString();

        } catch (Exception e) {
            return "{\"articles\":[]}";
        }
    }

    /**
     * 获取最终答案
     *
     * @param dto
     */
    private void findFinalAnswer(AnswerStepData stepData, DialogueByStreamParam dto) {
        try {
            // 如果步骤为空，则直接返回
            if (null == stepData) {
                return;
            }
            // 获取步骤索引，如果为空，则直接返回
            Integer index = stepData.getIndex();
            if (null == index) {
                return;
            }

            // 获取步骤列表
            List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
            //if (CollectionUtils.isEmpty(answerStepData)) {
            //    return;
            //}

            // 如果前面步骤状态都为失败，则输出当前答案
            boolean allMatch = answerStepData.stream().filter(p -> null != p.getIndex() && p.getIndex() < index)
                    .allMatch(p -> (p.getStatus().equals(ANSWER_FAIL)));
            if (!allMatch) {
                return;
            }

            // 推送引用内容给前台
            dto.setRefList(stepData.getRefList());

            // 推送进度，检索知识库过程
            JSONObject currentData = stepData.getCurrentData();
            // 当前推流进度
            if (null == currentData) {
                currentData = new JSONObject();
                currentData.put("current", 0);
                currentData.put("beginFlag", false);
                stepData.setCurrentData(currentData);
            }
            // 推送知识库检索，推送一次即可
            sendRef(currentData, dto);

            // 标记当前步骤为最终输出档案的步骤
            dto.setFinalStepId(stepData.getStep());
            // 设置正在推流，如果推流已完成，这里就不需要设置为正在推流
            if (NumberConstants.TWO != stepData.getStreamStatus()) {
                stepData.setStreamStatus(NumberConstants.ONE);
            }

            // 设置推流自定义数据
            dto.setMatterGuide(stepData.getCustomize());

            // 推理
            String reasoningContent = stepData.getReasoningContent();

            // 获取最终答案
            String result = stepData.getAnswer();
            if (StringUtils.isBlank(result)) {
                result = StringConstant.BLANK;
            }

            // 判断推流是否完成
            boolean finnishFlag = stepData.getStatus().equals(StepStatusEnum.ANSWER_COMPLETE);
            ApplicationInfo applicationInfo = APPLICATION_INFO.get();

            // 如果最终答案为空，而且已完成回答，则输出默认无法回答提示语
            if (finnishFlag && StringUtils.isBlank(result)) {
                result = applicationInfo.getFailureTalk();
            }

            // 设置是否能回答标识
            if ((StringUtils.isNotBlank(result) || StringUtils.isNotBlank(reasoningContent)) && (StringUtils.isBlank(applicationInfo.getFailureTalk()) || !result.equals(applicationInfo.getFailureTalk()))) {
                dto.setAnswerFlag("true");
            }

            // 配置按钮样式
            JSONObject urlReplace = new JSONObject();
            if (StringUtils.isNotBlank(applicationInfo.getButtonStyle()) && StringUtils.isNotBlank(applicationInfo.getButtonText())) {
                // 配置按钮样式
                currentData.putIfAbsent("urlReplace", urlReplace);
                if (StringUtils.isNotBlank(result)) {
                    if (currentData.containsKey("urlReplace")) {
                        urlReplace = currentData.getJSONObject("urlReplace");
                    } else {
                        currentData.put("urlReplace", urlReplace);
                    }
                    SseEmitterUtils.getButton(result, dto.getWebsiteConfigs(), urlReplace);
                }
            }

            // 判断是否有网页标识」,这里是用来标记网页链接的开头
            if (currentData.getBoolean("beginFlag") && result.substring(result.length() - 1).equals("」")) {
                currentData.put("beginFlag", false);
                currentData.put("current", result.length());
            }

            // 清空历史消息，不往前台推送历史消息
            dto.setListMsg(Lists.newArrayList());

            // 如果大模型有推理过程，这里要重置推流的起点
            if (StringUtils.isNotBlank(reasoningContent) && StringUtils.isNotBlank(result)) {
                String string = currentData.getString("stream");
                if (StringUtils.isNotBlank(string) && "reasoningContent".equals(string)) {
                    currentData.put("current", 0);
                    currentData.put("stream", "result");
                }
            }

            // 判断是否有推理内容
            String stream = currentData.getString("stream");
            if ((StringUtils.isBlank(stream) || "reasoningContent".equals(stream)) && StringUtils.isNotBlank(reasoningContent) && (StringUtils.isBlank(currentData.getString("stream")) || StringUtils.isBlank(result))) {
                currentData.put("stream", "reasoningContent");
                result = reasoningContent;
            }

            // 是否开启内容索引
            String contentIndexFlag = getConfiguration(applicationInfo.getApplicationId(), CONTENT_INDEX_FLAG);
            currentData.put("contentIndexFlag", contentIndexFlag);

            // 不推送知识库 id 给前台
            dto.setKnowledgeIdList(null);
            if (YesNoEnum.YES.getName().equals(contentIndexFlag)) {
                // 如果只有一个引用，则设置索引为 0，无需再调用索引添加服务
                if (CollectionUtils.isNotEmpty(dto.getRefList()) && dto.getRefList().size() == 1) {
                    if (finnishFlag && !result.contains("◹[0]◸")) {
                        result = result + "◹[0]◸";
                    }
                }
            }

            // 如果有正常答案，则开始推流
            if ("true".equals(dto.getAnswerFlag())) {
                // 当前步骤已完成，并且有正常答案，则开始推流
                if (finnishFlag) {
                    if (currentData.getInteger("current") == result.length()) {
                        // 有些大模型结束前，会重复推送一样的内容，这里是防止推送重复内容导致索引越界
                        sendSse(stepData, dto, finnishFlag, result, currentData, urlReplace, result.length() - 1, reasoningContent);
                    } else {
                        // 逐字推送
                        for (int i = currentData.getInteger("current"); i < result.length(); i++) {
                            boolean flag = sendSse(stepData, dto, finnishFlag, result, currentData, urlReplace, i, reasoningContent);
                            if (flag) {
                                break;
                            }
                        }
                    }
                    // 如果对话已完成，不再触发findFinalAnswer，并且最后一次推送的是推理内容，那么这里就要重新触发一次，这时会推送正文内容
                    if ("reasoningContent".equals(currentData.getString("stream"))) {
                        Integer count = 1;
                        Integer retry = currentData.getInteger("retry");
                        if (null != retry) {
                            count = currentData.getInteger("retry");
                            count = count + 1;
                        }
                        if (count < 2) {
                            currentData.put("retry", count);
                            findFinalAnswer(stepData, dto);
                            currentData.remove("retry");
                        }
                    }
                } else {
                    // 当步骤进行中，逐字推送
                    for (int i = currentData.getInteger("current"); i < result.length(); i++) {
                        boolean flag = sendSse(stepData, dto, finnishFlag, result, currentData, urlReplace, i, reasoningContent);
                        if (flag) {
                            break;
                        }
                    }
                }
            } else {
                dto.setAnswer(result);
                // 一次性输出最终答案
                SseEmitterUtils.sendMsg(dto.getClientId(), JSON.toJSONString(dto));
            }

            // 如果retry不为空，说明此次是重试的，那么就直接返回无需再往下执行
            Integer retry = currentData.getInteger("retry");
            if (null != retry) {
                return;
            }

            // 当前推流进度
            currentData.put("current", result.length());
            //data.put("answer", result);
            // 推流结束
            if (finnishFlag) {
                // 标记当前步骤为最终输出答案
                stepData.setStatus(ANSWER_FINAL);
                dto.setFinishReason(StepStatusEnum.ANSWER_COMPLETE);
                if (StringUtils.isBlank(result)) {
                    dto.setAnswer(applicationInfo.getFailureTalk());
                    //data.put("answer", applicationInfo.getFailureTalk());
                }

                //urlReplace.forEach((url, replace) -> {
                //    String answer = dto.getAnswer();
                //    dto.setAnswer(answer.replace(url, replace.toString()));
                //});
                // 推流结束的时候处理"outline"
                if (StringUtils.isEmpty(dto.getAnswer())) {
                    dto.setOutline(new ArrayList<>());
                } else {
                    List<DialogueAnswerOutline> outline = answerUtils.analyticOutlineStr(stepData.getOutline());
                    dto.setOutline(outline);
                }
                SseEmitterUtils.sendMsg(dto.getClientId(), JSON.toJSONString(dto));
                // 完成对话后，记录日志
                //completeAnswer(dto, contextList);
                // SseEmitterUtils.complete(dto.getClientId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测是否需要退出监听
     *
     * @return
     */
    private boolean breakFlag(Vector<StepEntity> contextList, DialogueByStreamParam dto) {
        List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
        // 没有任何答案，则退出
        if (CollectionUtils.isEmpty(answerStepData)) {
            log.info("没有任何答案，则退出");
            return true;
        }

        // 全都是无法回答，则退出
        boolean allMatch = answerStepData.stream().allMatch(p -> p.getStatus().equals(ANSWER_FAIL));
        if (allMatch) {
            log.info("全都是无法回答，则退出");
            errorStream(dto, contextList, "全都是无法回答");
            return true;
        }

        // 完成回答则退出
        //boolean anyMatch = answerStepData.stream().anyMatch(p -> p.getStatus().equals(StepStatusEnum.ANSWER_FINAL));
        StepStatusEnum finishReason = dto.getFinishReason();
        if (StepStatusEnum.ANSWER_COMPLETE.equals(finishReason)) {
            log.info("完成回答则退出");
            return true;
        }

        // 判断是否已有最终输出答案
        boolean answerFinalFlag = answerStepData.stream().anyMatch(p -> p.getStatus().equals(ANSWER_FINAL));
        if (answerFinalFlag) {
            log.info("已有最终输出答案");
            return true;
        }
        // 监听答案
        for (AnswerStepData answerStep : answerStepData) {
            if (answerStep.getStatus().equals(StepStatusEnum.ANSWER_COMPLETE)) {
                ThreadUtil.sleep(1000);
                if (null == answerStep.getStreamStatus() || NumberConstants.ZERO == answerStep.getStreamStatus()) {
                    JSONObject currentData = answerStep.getCurrentData();
                    if (null == currentData) {
                        currentData = new JSONObject();
                        currentData.put("current", 0);
                        currentData.put("beginFlag", false);
                    }
                    findFinalAnswer(answerStep, dto);
                }

                if (ANSWER_FINAL.equals(answerStep.getStatus())) {
                    log.info("已有最终输出答案，最终执行步骤：{}", answerStep.getStep());
                    log.info("已有最终输出答案");
                    return true;
                }
                break;
            }
        }

        return false;
    }

    /**
     * 异常输出
     *
     * @param dto
     * @param contextList
     */
    private void errorStream(DialogueByStreamParam dto, Vector<StepEntity> contextList, String msg) {
        MDC.clear();
        log.warn(msg);
        JSONObject currentData = new JSONObject();
        currentData.put("current", 0);
        currentData.put("beginFlag", false);
        dto.setErrorMsg(msg);

        AnswerStepData answerStep = new AnswerStepData();
        answerStep.setIndex(0);
        answerStep.setStep(ERROR_STRATEGY);
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        answerStep.setAnswer(applicationInfo.getFailureTalk());
        answerStep.setStatus(StepStatusEnum.ANSWER_COMPLETE);
        answerStep.setStreamStatus(NumberConstants.ZERO);
        List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
        if (null == answerStepData) {
            List<AnswerStepData> stepDataList = Lists.newArrayList();
            STEP_BY_LIST.set(stepDataList);
        }
        STEP_BY_LIST.get().add(answerStep);
        StepEntity step = new StepEntity();
        step.setStep(ERROR_STEP);
        step.setResult(msg);
        step.setPrompt("原问题：" + dto.getContent() + "\n改写后：" + dto.getContentTemp());
        contextList.add(step);
        answerStep.setCurrentData(currentData);
        findFinalAnswer(answerStep, dto);
    }


    /**
     * 问题明确推荐返回
     *
     * @param dto
     * @param contextList
     */
    private void recommendedStream(DialogueByStreamParam dto, Vector<StepEntity> contextList, String msg) {
        MDC.clear();
        JSONObject currentData = new JSONObject();
        currentData.put("current", 0);
        currentData.put("beginFlag", false);

        AnswerStepData answerStep = new AnswerStepData();
        answerStep.setIndex(0);
        answerStep.setStep(RECOMMENDED_STRATEGY);
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        answerStep.setAnswer(applicationInfo.getRecommendedTalk());
        answerStep.setStatus(StepStatusEnum.ANSWER_SEARCH);
        answerStep.setStreamStatus(NumberConstants.ZERO);
        List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
        if (null == answerStepData) {
            List<AnswerStepData> stepDataList = Lists.newArrayList();
            STEP_BY_LIST.set(stepDataList);
        }
        STEP_BY_LIST.get().add(answerStep);
        StepEntity step = new StepEntity();
        step.setStep(RECOMMENDED_STRATEGY);
        step.setResult(msg);
        step.setPrompt("原问题：" + dto.getContent() + "\n推荐后：" + dto.getRecommendedQuestions().toJSONString());
        contextList.add(step);
        answerStep.setCurrentData(currentData);
        findFinalAnswer(answerStep, dto);
    }

    /**
     * 问题明确后根据知识库QA推荐返回
     *
     * @param dto
     * @param contextList
     */
    private void recommendedQAKnowledgeStream(DialogueByStreamParam dto, Vector<StepEntity> contextList, String msg) {
        MDC.clear();
        JSONObject currentData = new JSONObject();
        currentData.put("current", 0);
        currentData.put("beginFlag", false);

        AnswerStepData answerStep = new AnswerStepData();
        answerStep.setIndex(0);
        //answerStep.setStep(KNOWLEDGE_QA_RECOMMENDED_STRATEGY);
        answerStep.setAnswer(dto.getAnswer());
        answerStep.setStatus(StepStatusEnum.ANSWER_COMPLETE);
        answerStep.setStreamStatus(NumberConstants.ZERO);
        List<AnswerStepData> answerStepData = STEP_BY_LIST.get();
        if (null == answerStepData) {
            List<AnswerStepData> stepDataList = Lists.newArrayList();
            STEP_BY_LIST.set(stepDataList);
        }
        STEP_BY_LIST.get().add(answerStep);
        StepEntity step = new StepEntity();
        //step.setStep(KNOWLEDGE_QA_RECOMMENDED_STRATEGY);
        step.setResult(msg);
        step.setPrompt("原问题：" + dto.getContent() + "\n根据知识库QA推荐后：" + dto.getRecommendedQuestions().toJSONString());
        contextList.add(step);
        answerStep.setCurrentData(currentData);
        findFinalAnswer(answerStep, dto);
    }

    /**
     * 清除本地缓存
     */
    private void clearLocal() {
        APPLICATION_INFO.remove();
        KNOWLEDGE_ID_LIST.remove();
        STEP_BY_LIST.remove();
        TRACE_ID.remove();
    }

    /**
     * 获取词库
     *
     * @return
     */
    private void initInterceptWordList() {
        if (INTERCEPT_WORD_HOUSE_MAP.isEmpty()) {
            List<InterceptWord> allWord = wordService.getAllWord();
            Map<Long, List<InterceptWord>> collect = allWord.stream()
                    .filter(Objects::nonNull)
                    .filter(p -> null != p.getInterceptWordHouseId())
                    .collect(Collectors.groupingBy(InterceptWord::getInterceptWordHouseId));
            INTERCEPT_WORD_HOUSE_MAP.putAll(collect);
        }
    }

    /**
     * 获取事项
     *
     * @return
     */
    private void initScene() {
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        // 获取缓存
        String matterKey = getSubWordKey(applicationInfo.getApplicationId());
        if (!redisService.hasKey(matterKey)) {
            // 如果缓存中没有，则从数据库中获取
            List<SceneManagement> sceneManagements = sceneApplicationRefService.getSceneByAppId(applicationInfo.getApplicationId());
            redisService.set(matterKey, JSON.toJSONString(sceneManagements), 60 * 60 * 24 * 3);
        }
    }

    /**
     * 过滤有效话题
     *
     * @param matterGuideInfo 话题信息
     */
    private void filterSubject(List<MatterGuide> matterGuideInfo) {
        if (CollectionUtil.isEmpty(matterGuideInfo)) {
            return;
        }

        // 过滤要讨论的话题，即subjectFlag为1的事项
        List<MatterGuide> matterGuideInfoTemp = new ArrayList<>();
        for (MatterGuide matterGuide : matterGuideInfo) {
            Integer subjectFlag = matterGuide.getSubjectFlag();
            String processing = matterGuide.getProcessing();
            if (Objects.isNull(subjectFlag) || Objects.equals(subjectFlag, DialogueSubjectFlagEnum.NO.getCode())) {
                continue;
            }

            if (StringUtils.isBlank(processing) || !JSONUtil.isTypeJSONObject(processing)) {
                continue;
            }

            try {
                JSONObject processingObj = JSON.parseObject(processing);
                String way = processingObj.getString(SUBJECT_WAY_FIELD);
                if (StringUtils.isBlank(way) || SUBJECT_WAY_NONE.equals(way)) {
                    continue;
                }
            } catch (Exception e) {
                log.error("parser processingObj error:{}", e.getMessage(), e);
                continue;
            }

            matterGuideInfoTemp.add(matterGuide);
        }

        // 将过滤后的结果赋值给原始列表
        matterGuideInfo.clear();
        matterGuideInfo.addAll(matterGuideInfoTemp);
    }


    /**
     * 获取业务场景
     *
     * @return
     */
    private List<BusinessScenarioList> getBusinessScenario() {
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        // 获取缓存
        String businessScenarioKey = RedisKey.BUSINESS_SCENARIO + applicationInfo.getApplicationId() + ":";
        String businessScenarioStr = redisService.get(businessScenarioKey, String.class);
        List<BusinessScenarioList> businessScenarioLists = null;
        if (StringUtils.isNotBlank(businessScenarioStr)) {
            businessScenarioLists = JSON.parseArray(businessScenarioStr, BusinessScenarioList.class);
        }
        // 如果缓存中没有，则从数据库中获取
        if (CollectionUtils.isEmpty(businessScenarioLists)) {
            QueryWrapper queryWrapper = QueryWrapper.create();
            queryWrapper.and(" application_id = " + applicationInfo.getApplicationId() + " ");
            queryWrapper.and(" delete_flag = 0 ");
            queryWrapper.and(" show_flag = '是' ");
            businessScenarioLists = businessScenarioListService.getMapper().selectListByQuery(queryWrapper);
            redisService.set(businessScenarioKey, JSON.toJSONString(businessScenarioLists), 60 * 60 * 1 * 1);
        }
        return businessScenarioLists;
    }

    /**
     * 初始化参数
     *
     * @param param
     */
    private void initParam(DialogueByStreamParam param, ApplicationInfo applicationInfo) {
        // 自动识别问题的所属的语言，并将其转为简体中文
        String translateToZhFlag = getConfiguration(applicationInfo.getApplicationId(), TRANSLATE_TO_ZH_FLAG);
        if (YesNoEnum.YES.getName().equals(translateToZhFlag)) {
            String targetContent = transContentToZh(param.getContent());
            param.setContent(targetContent);
        }
        param.setDialogueId(IdUtil.simpleUUID());
        param.setStream("true");
        param.setFinishReason(StepStatusEnum.ANSWER_PROCESS);
        param.setQuestion(param.getContent());
        param.setAnswerFlag("false");
        param.setProgressList(Lists.newArrayList());


        // 临时开关互联网检索
        if (StringUtils.isNotBlank(param.getNetworkFlag())) {
            applicationInfo.setNetworkFlag(param.getNetworkFlag());
            if (StringUtils.isBlank(applicationInfo.getNetworkChannel())) {
                // 获取默认的搜索引擎
                String networkDefaultChannel = getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.NETWORK_DEFAULT_CHANNEL);
                // 如果没有配置检索引擎，默认使用夸克引擎
                if (StringUtils.isBlank(networkDefaultChannel)) {
                    networkDefaultChannel = "quark";
                }
                applicationInfo.setNetworkChannel(networkDefaultChannel);
            }
        }

    }

    /**
     * 将原始语言内容翻译中文内容
     *
     * @param content 原始内容
     * @return 结果
     */
    private String transContentToZh(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        try {
            byte[] utf8Bytes = content.getBytes(StandardCharsets.UTF_8);
            content = new String(utf8Bytes, StandardCharsets.UTF_8);

            JSONObject jsonObject = TranslateUtils.getAliyunTranslateContent(content, "auto", "zh", null);
            if (jsonObject != null && jsonObject.get("status").equals("success")) {
                final JSONObject data = jsonObject.getJSONObject("data");
                return data.getString("tgt");
            }
        } catch (Exception e) {
            log.info("问答内容转换中文简体异常");
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 获取认证器，并进行认证
     *
     * @param param
     * @param request
     */
    private boolean auth(DialogueByStreamParam param, HttpServletRequest request) {
        if (YesNoEnum.YES.getName().equals(param.getDebuggerFlag()) || null == request) {
            return true;
        }
        // 获取应用信息
        Vector<StepEntity> contextList = new Vector<>();
        ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, param.getDebuggerFlag());
        if (null == applicationInfo) {
            applicationInfo = new ApplicationInfo();
            applicationInfo.setFailureTalk(StringConstant.BLANK);
            APPLICATION_INFO.set(applicationInfo);
            errorStream(param, contextList, ERROR_APP_NOT_EXIST);
            return false;
        }
        // 获取客户端类型
        String clientType = request.getHeader("clientType");
        // H5判断是否需要登录
        boolean h5Login = ClientTypeEnum.H5.getCode().equals(clientType) && StringUtils.isNotBlank(applicationInfo.getClientAuthChannel());
        // PC判断是否需要登录
        boolean pcLogin = ClientTypeEnum.PC.getCode().equals(clientType) && StringUtils.isNotBlank(applicationInfo.getPcAuthChannel());
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (null != tokenOauthUserInfo) {
            // 获取用户
            param.setUserName(tokenOauthUserInfo.getUserName());
            param.setUserId(tokenOauthUserInfo.getId() + "");
        }
        if (h5Login || pcLogin) {
            try {
                // 获取用户，否则重新登录
                if (null == tokenOauthUserInfo || null == tokenOauthUserInfo.getId()) {
                    // 判断免登录
                    TokenUser user = checkFree(param, request);
                    if (null != user) {
                        return true;
                    }
                    authFail(param, ResultCodeEnum.TOKEN_INVALID);
                    return false;
                }
                // 获取用户
                param.setUserName(tokenOauthUserInfo.getUserName());
                param.setUserId(tokenOauthUserInfo.getId() + "");
            } catch (Exception e) {
                log.warn("没有用户");
                // 判断免登录
                TokenUser user = checkFree(param, request);
                if (null != user) {
                    return true;
                }
                authFail(param, ResultCodeEnum.TOKEN_INVALID);
                return false;
            }
        }
        return true;
    }


    /**
     * 认证失败
     *
     * @param param
     */
    private void authFail(DialogueByStreamParam param, ResultCodeBase resultCodeBase) {
        try {
            Result result = Result.fail(resultCodeBase);
            SseEmitterUtils.sendMsg(param.getClientId(), JSON.toJSONString(result));
            SseEmitterUtils.complete(param.getClientId());
        } catch (Exception e) {
            log.warn("没有用户");
        }
    }

    /**
     * 初始化词库
     *
     * @param interceptWordList
     * @param applicationId
     */
    private void initWordByApplicationId(List<InterceptWord> interceptWordList, String applicationId) {
        Map<String, List<String>> interceptWordMap = APP_INTERCEPT_WORD_MAP.computeIfAbsent(applicationId, k -> Maps.newHashMap());

        // 按照类型分组
        Map<String, List<InterceptWord>> groupType = interceptWordList.stream().filter(p -> StringUtils.isNotBlank(p.getContent())).collect(Collectors.groupingBy(InterceptWord::getType));

        // 获取词库枚举
        InterceptTypeEnum[] values = InterceptTypeEnum.values();

        // 遍历词库
        for (InterceptTypeEnum value : values) {
            String key = value.getName();

            if (interceptWordMap.containsKey(key)) {
                continue;
            }
            List<InterceptWord> interceptWords = groupType.getOrDefault(value.getName(), Lists.newArrayList());
            // 添加到redis
            List<String> intercepts = interceptWords.stream().map(InterceptWord::getContent).distinct().collect(Collectors.toList());
            interceptWordMap.put(key, intercepts);
        }
    }

    /**
     * 获取词库key
     *
     * @param applicationId
     * @return
     */
    public static String getSubWordKey(String applicationId) {
        if (StringUtils.isNotBlank(applicationId)) {
            //return RedisKey.SUBJECT + applicationId + ":";
            return RedisKey.SCENE + applicationId + ":";
        } else {
            //return RedisKey.SUBJECT;
            return RedisKey.SCENE;
        }
    }

    /**
     * 获取业务场景
     *
     * @param applicationId
     * @return
     */
    public static String getBusinessScenarioKey(String applicationId) {
        if (StringUtils.isNotBlank(applicationId)) {
            return RedisKey.BUSINESS_SCENARIO + applicationId + ":";
        } else {
            return RedisKey.BUSINESS_SCENARIO;
        }
    }

    /**
     * 处理符号
     *
     * @return
     */
    private static String processSymbols(String result) {
        result = result.replace("\n-", "\n- ");
        result = replaceSymbolsByStep(result, "&nbsp;");
        result = replaceSymbolsByStep(result, "&emsp;");
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }
        // 处理#
        result = dealHash(result);

        return result;
    }

    /**
     * 获取原始的http
     *
     * @param content
     * @return
     */
    private static String getOrgUrl(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        content = content
                .replace("<h|t|t|p", "http")
                .replace("h|t|t|p", "http")
                .replace("h|t|t|p", "http")
                .replace("h-t-t-p", "http");
        content = processSymbols(content);
        return content;
    }

    /**
     * 判断是否内置问题
     */
    private boolean isBuiltInQuestion(DialogueByStreamParam dto) {
        List<String> stepIdList = initStepIdList(dto);
        if (CollectionUtils.isNotEmpty(stepIdList) && stepIdList.contains(BUILT_IN)) {
            AnswerStrategy answerStrategy = answerStrategyMap.get(BUILT_IN);
            if (null != answerStrategy) {
                Vector<StepEntity> contextList = new Vector<>();
                KnowledgeContent content = answerStrategy.getContent(dto, contextList);
                if (null != content) {
                    List<String> contentList = content.getContentList();
                    return CollectionUtils.isNotEmpty(contentList);
                }
            }
        }
        return false;
    }

    /**
     * 判断是否内置问题
     */
    private List<ScoreDataResult> findQa(DialogueByStreamParam dto) {
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        List<String> stepIdList = initStepIdList(dto);
        if (CollectionUtils.isNotEmpty(stepIdList) && stepIdList.contains(FIND_QA_TITLE)) {
            AnswerStrategy answerStrategy = answerStrategyMap.get(FIND_QA_TITLE);
            if (null != answerStrategy) {
                ScoreDataParam scoreDataParam = new ScoreDataParam();
                scoreDataParam.setQuestion(dto.getContent());
                scoreDataParam.setApplicationId(applicationInfo.getApplicationId());
                scoreDataParam.setClientId(dto.getClientId());
                return answerStrategy.getScoreData(scoreDataParam);
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 替换符号，从完整到少
     *
     * @param content
     * @param symbols
     * @return
     */
    private static String replaceSymbolsByStep(String content, String symbols) {
        for (int i = symbols.length(); i >= 2; i--) {
            content = content.replaceAll(symbols.substring(0, i), " ");
        }
        return content;
    }

    /**
     * 处理#号
     */
    private static String dealHash(String result) {
        result = result.replace("#", "# ")
                .replace("# #", "##").replace("# #", "##");
        StringBuilder contentBuilder = new StringBuilder();
        if (result.contains("# ")) {
            String[] split = result.split("# ");
            int length = split.length;
            for (int i = 0; i < length; i++) {
                if (!endWithTab(split[i])) {
                    contentBuilder.append(split[i]);
                    if (i != length - 1) {
                        contentBuilder.append("#");
                    }
                } else {
                    contentBuilder.append(split[i]);
                    if (i != length - 1) {
                        contentBuilder.append("# ");
                    }
                }
            }
            if (result.endsWith("# ")) {
                contentBuilder.append("#");
            }
        } else {
            contentBuilder.append(result);
        }
        return contentBuilder.toString();
    }

    /**
     * 以控制符结尾
     */
    private static boolean endWithTab(String content) {
        return content.endsWith("\t")
                || content.endsWith("\n")
                || content.endsWith("\r")
                || content.endsWith("\f")
                || content.endsWith("#");
    }

    /**
     * 模糊处理，使用QA精确备选
     *
     * @param dto
     * @param contextList
     * @return
     */
    private String vagueQaDeal(DialogueByStreamParam dto, Vector<StepEntity> contextList, long start) {
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        StepEntity changeStep = new StepEntity();
        changeStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(changeStep);
        changeStep.setStep(CHANGE_CONTENT_QA_NO);
        changeStep.setPrompt(dto.getContent());

        // 先检索QA对，如果有QA对，则不进行模糊问题推荐，往下走会检索到 qa 模块
        List<ScoreDataResult> qaList = findQa(dto);
        if (CollectionUtils.isNotEmpty(qaList)) {
            changeStep.setResult("已有QA对答案，不需要引导");
            return StringConstant.ONE;
        }

        // 获取根据内容组织答案指令
        String organizateAnswerPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.ORGANIZATE_ANSWER_PROMPT);
        if (StringUtils.isBlank(organizateAnswerPrompt)) {
            changeStep.setResult("缺少配置信息：organizate_answer_prompt");
            return StringConstant.TWO;
        }

        try {
            // 重排获取推荐问题
            RecommendQuestionParam recommendQuestionParam = new RecommendQuestionParam();
            recommendQuestionParam.setQuestion(dto.getContent());
            recommendQuestionParam.setApplicationId(dto.getApplicationId());
            List<RecommendQuestionResult> recommendQuestionResults = recommendQuestionService.recommendKnowledgeQAQuestion(recommendQuestionParam);

            JSONArray recommendQuestionResultJson = JSON.parseArray(JSON.toJSONString(recommendQuestionResults));
            dto.setRecommendedQuestions(recommendQuestionResultJson);
            // 调用大模型，对推荐的问题重新组织答案
            if (CollectionUtils.isNotEmpty(recommendQuestionResults)) {
                log.info("===============意图不明确 找到知识库QA里面2个及以上的推荐问题===========");
                // 找到推荐问题
                String organizateTitleContent = IntStream.range(0, recommendQuestionResults.size())
                        .mapToObj(i -> (i + 1) + "." + recommendQuestionResults.get(i).getQuestion())
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.joining("\n"));
                List<DialogueMessage> dialogueMessages = Lists.newArrayList();
                organizateAnswerPrompt = organizateAnswerPrompt.replace(QUESTION_PLACEHOLDER, organizateTitleContent);
                dialogueMessages.add(new DialogueMessage("user", organizateAnswerPrompt));

                StepEntity quesionChangeStep = new StepEntity();
                quesionChangeStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                contextList.add(quesionChangeStep);
                quesionChangeStep.setStep(KNOWLEDGE_QA_CHANGE_CONTENT_RECOMMENDED);
                // quesionChangeStep.setPrompt(dto.getContent());
                String organizateAnswerResult = vagueStream(organizateAnswerPrompt, quesionChangeStep, dto, start);
                // String organizateAnswerResult = change(dialogueMessages, quesionChangeStep);
                dto.setAnswer(organizateAnswerResult);
                quesionChangeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                quesionChangeStep.setCostTime(System.currentTimeMillis() - start);
                changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                changeStep.setCostTime(System.currentTimeMillis() - start);
                changeStep.setFirstLlmTime(quesionChangeStep.getFirstLlmTime());
                return StringConstant.THREE;
            }
        } catch (Exception e) {
            log.error("===============意图明确 不推荐问题===========");
            // contentTemp = newQuestion;
        }
        return StringConstant.TWO;
    }

    /**
     * 判断是否为第一次对话
     *
     * @return
     */
    private boolean firstDialogFlag(String conversationId) {
        if (StringUtils.isBlank(conversationId)) {
            return false;
        }

        LambdaEsQueryWrapper<LlmConversation> wrapper = EsWrappers.lambdaQuery(LlmConversation.class)
                .eq(LlmConversation::getConversationId, conversationId)
                .orderByDesc(LlmConversation::getCreateTime)
                .limit(1);
        LlmConversation smartConversation = null;
        for (int i = 0; i < 5; i++) {
            try {
                smartConversation = conversationMapper.selectOne(wrapper);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                ThreadUtil.sleep(150);
            }
        }
        return null == smartConversation;
    }

    /**
     * 构建历史会话
     *
     * @param dto
     * @return
     */
    private List<DialogueMessage> buildHistory(DialogueByStreamParam dto) {
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        List<DialogueMessage> listMsgHistory = Lists.newArrayList();
        //changeStep.setResult(CHANGE_CONTENT_MUL);
        int multiDialogueNum = 3;
        if (null != applicationInfo.getMultiDialogueNum()) {
            multiDialogueNum = applicationInfo.getMultiDialogueNum();
        }
        LambdaEsQueryWrapper<LlmConversation> wrapper = EsWrappers.lambdaQuery(LlmConversation.class)
                .eq(LlmConversation::getConversationId, dto.getConversationId())
                .orderByDesc(LlmConversation::getCreateTime)
                .limit(1);
        LlmConversation smartConversation = null;
        for (int i = 0; i < 5; i++) {
            try {
                smartConversation = conversationMapper.selectOne(wrapper);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                ThreadUtil.sleep(150);
            }
        }
        if (null != smartConversation) {

            listMsgHistory = smartConversation.getMessageList();

            //List<DialogueMessage> listMsgTemp = Lists.newArrayList();
            if (CollectionUtils.isEmpty(listMsgHistory)) {
                return listMsgHistory;
            }
            // 获取对话数量，提取最新的对话记录
            List<DialogueMessage> listMsgTemp = CollectionUtil.reverse(listMsgHistory);
            int min = Math.min(listMsgTemp.size(), multiDialogueNum * 2);
            listMsgHistory = listMsgTemp.subList(0, min);
            listMsgHistory = CollectionUtil.reverse(listMsgHistory);

            // 获取对话id
            if (CollectionUtils.isNotEmpty(listMsgHistory)) {
                listMsgHistory.forEach(item -> {
                    if (StringUtils.isBlank(item.getContent())) {
                        item.setContent(StringConstant.BLANK);
                    }
                });
            }
            List<String> dialogueIdList = listMsgHistory.stream().map(DialogueMessage::getDialogueId).distinct().collect(Collectors.toList());
            dto.setDialogueIdList(dialogueIdList);
        }
        return listMsgHistory;
    }

    /**
     * 构建改写问题
     *
     * @param dto
     * @param listMsg
     * @param listMsgHistory
     * @param multiFilteringRewritingPromptFlag 多轮对话指令标识
     */
    private String buildRewritingPrompt(DialogueByStreamParam dto, List<DialogueMessage> listMsg, List<DialogueMessage> listMsgHistory,
                                        List<YayiMessage> yayiMessages, Boolean multiFilteringRewritingPromptFlag) {
        YayiMessage userMes = yayiMessages.remove(yayiMessages.size() - 1);
        List<DialogueMessage> builtInMessageList = yayiMessages.stream().map(p -> {
            return BeanUtil.toBean(p, DialogueMessage.class);
        }).collect(Collectors.toList());
        listMsg.addAll(builtInMessageList);

        int size = listMsgHistory.size();
        // 历史问题及回答
        JSONObject historyAnswer = new JSONObject();
        // 历史对话中，索引为偶数的是用户的问题，索引为奇数的是AI的回答
        String userQuestion = StringConstant.BLANK;
        String llmAnswer = StringConstant.BLANK;
        StringBuilder historyAnswerBuilder = new StringBuilder();
        for (int i = 0; i < size; i = i + 2) {
            userQuestion = listMsgHistory.get(i).getContent();
            llmAnswer = listMsgHistory.get(i + 1).getContent();
            historyAnswerBuilder.append(MULTI_QUESTION).append(userQuestion).append("\n").append(MULTI_ANSWER).append(llmAnswer).append("\n\n");
            historyAnswer.put(userQuestion, llmAnswer);
        }

        String historyAnswerStr = StringConstant.BLANK;
        if (multiFilteringRewritingPromptFlag) {
            historyAnswerStr = historyAnswerBuilder.toString();
        } else {
            historyAnswerStr = JSON.toJSONString(historyAnswer);
        }

        return userMes.getContent().replace(QUESTIONS_PLACEHOLDER, dto.getContent())
                .replace(HISTORY_QUESTION_PLACEHOLDER, historyAnswerStr);
    }

    /**
     * 自定义改写
     *
     * @param dto
     * @param contextList
     */
    private void customizeChange(DialogueByStreamParam dto, Vector<StepEntity> contextList, long start) {
        boolean changeFlag = changeFlag(dto, contextList);
        if (!changeFlag) {
            return;
        }

        StepEntity changeStep = contextList.stream().filter(p -> CHANGE_CONTENT_STEP.equals(p.getStep())).findAny().get();
        changeStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));

        // 获取改写指令
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        // 改写指令
        String filteringRewritingPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.FILTERING_REWRITING_PROMPT);
        // 多轮对话改写指令
        String multiFilteringRewritingPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.MULTI_FILTERING_REWRITING_PROMPT);
        if (StringUtils.isBlank(filteringRewritingPrompt) && StringUtils.isBlank(multiFilteringRewritingPrompt)) {
            changeStep.setResult("没有改写指令，要设置filtering_rewriting_prompt或multi_filtering_rewriting_prompt的配置");
            return;
        }
        // 为了不影响原有逻辑，如果配置了“多轮对话改写指令”，以多轮对话改写指令为主
        boolean multiFilteringRewritingPromptFlag = false;
        if (StringUtils.isNotBlank(multiFilteringRewritingPrompt)) {
            filteringRewritingPrompt = multiFilteringRewritingPrompt;
            multiFilteringRewritingPromptFlag = true;
        }

        List<DialogueMessage> listMsg = Lists.newArrayList();

        // 历史消息
        List<DialogueMessage> listMsgHistory = Lists.newArrayList();

        // 判断是否开启多轮对话
        if (YesNoEnum.YES.getName().equals(applicationInfo.getMultiDialogueFlag())) {
            listMsgHistory.addAll(buildHistory(dto));
        }
        String contentTemp = dto.getContent();
        // 2024-10-29 当前这个版本是多个对话的指令
        // 这是个多轮对话的指令
        if (JSONUtil.isTypeJSONArray(filteringRewritingPrompt)) {
            List<YayiMessage> yayiMessages = JSON.parseArray(filteringRewritingPrompt, YayiMessage.class);
            if (CollectionUtils.isEmpty(yayiMessages)) {
                return;
            }
            filteringRewritingPrompt = buildRewritingPrompt(dto, listMsg, listMsgHistory, yayiMessages, multiFilteringRewritingPromptFlag);
        } else {
            filteringRewritingPrompt = filteringRewritingPrompt.replace(QUESTION_PLACEHOLDER, dto.getContent());
        }

        listMsg.add(new DialogueMessage("user", filteringRewritingPrompt));
        // 改写问题
        StepEntity localChange = new StepEntity();
        localChange.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(localChange);
        localChange.setStep(CHANGE_CONTENT_LOCAL_RESULT);

        cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
        // 改写参数，从配置中获取模型名称
        String rewriteModelName = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), REWRITE_LLM_MODEL_NAME);
        if (StringUtils.isNotBlank(rewriteModelName)) {
            LlmInfo byModelName = llmInfoService.getByModelName(rewriteModelName);
            if (null != byModelName) {
                stepParam.set(LLM_PARAM_MODEL_ID, byModelName.getModelId());
            }
        }
        // 大模型是否需要json格式化
        stepParam.set(LLM_JSON_FLAG, StringConstant.BLANK);
        localChange.setParam(stepParam);
        StringBuilder results = new StringBuilder();
        // listMsg 里面是指令，包括system和user，filteringRewritingPrompt是user的content
        boolean dealJsonFlag = multiFilteringRewritingPromptFlag;
        answerUtils.getGenerateCommon(filteringRewritingPrompt, StringConstant.BLANK, localChange, listMsg, result -> {
            if (null == changeStep.getFirstLlmTime()) {
                localChange.setFirstLlmTime(System.currentTimeMillis() - start);
                changeStep.setFirstLlmTime(System.currentTimeMillis() - start);
            }
            String answer = result.getAnswer();
            if (StringUtils.isNotBlank(answer)) {
                answer = AnswerUtils.dealLLmJson(answer);
                if (StringUtils.isNotBlank(answer)) {
                    if (!dealJsonFlag) {
                        // 非多轮对话指令，才补全json
                        answer = answerUtils.dealJson(answer);
                    }
                    if (StringUtils.isNotBlank(answer) && JSONUtil.isTypeJSONObject(answer)) {
                        JSONObject jsonObject = JSON.parseObject(answer);
                        if (jsonObject.size() > 1) {
                            String newQuestionField = jsonObject.getString(NEW_QUESTION_FIELD);
                            results.append(newQuestionField);
                            throw new RuntimeException("已获取到新问题，停止推流");
                        }
                    }
                }
            }
        }, StringConstant.BLANK);

        // 获取到改写的新问题
        contentTemp = results.toString();
        dto.setNewQuestion(contentTemp);
        localChange.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        localChange.setCostTime(System.currentTimeMillis() - start);
        changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        changeStep.setCostTime(System.currentTimeMillis() - start);
        if (StringUtils.isBlank(contentTemp)) {
            return;
        }
        String defaultRegion = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.DEFAULT_REGION);
        if (StringUtils.isNotBlank(defaultRegion) && !contentTemp.contains(defaultRegion)) {
            contentTemp = "在" + defaultRegion + "，" + contentTemp;
        }

        listMsg.clear();
        listMsg.addAll(listMsgHistory);
        listMsg.add(new DialogueMessage("user", contentTemp));
        dto.setContentTemp(contentTemp);
        dto.setRewriteContent(contentTemp);
        changeStep.setResult(contentTemp);
        dto.setListMsg(listMsg);
        localChange.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        localChange.setCostTime(System.currentTimeMillis() - start);
        changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        changeStep.setCostTime(System.currentTimeMillis() - start);
    }

    /**
     * 使用雅意改写
     *
     * @param dto
     * @param contextList
     */
    private void yayiChange(DialogueByStreamParam dto, Vector<StepEntity> contextList, long start) {
        boolean changeFlag = changeFlag(dto, contextList);
        if (!changeFlag) {
            return;
        }
        StepEntity yayiChange = contextList.stream().filter(p -> CHANGE_CONTENT_STEP.equals(p.getStep())).findAny().get();
        yayiChange.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.of(start), DatePattern.NORM_DATETIME_PATTERN));

        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        List<DialogueMessage> listMsg = Lists.newArrayList();
        List<DialogueMessage> listMsgHistory = Lists.newArrayList();

        // 判断是否开启多轮对话
        if (YesNoEnum.YES.getName().equals(applicationInfo.getMultiDialogueFlag())) {
            listMsgHistory.addAll(buildHistory(dto));
        }

        // 改写问题
        DialogueFilterParam dialogueFilterParam = new DialogueFilterParam();
        dialogueFilterParam.setFunction("filtering_rewriting");
        List<YayiMessage> msgList = listMsgHistory.stream()
                .map(p -> new YayiMessage(p.getRole(), StringUtils.isNotBlank(p.getQuestion()) ? p.getQuestion() : p.getContent())).collect(Collectors.toList());
        msgList.add(new DialogueMessage("user", dto.getContent()));
        listMsg.add(new DialogueMessage("user", dto.getContent()));
        String contentTemp = StringConstant.BLANK;
        if (CollectionUtils.isNotEmpty(listMsgHistory)) {
            DialogueFilterResult dialogueFilterResult = yayiServer.dialogueFilter(msgList, dialogueFilterParam);
            StepEntity changeStep = new StepEntity();
            changeStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.of(start), DatePattern.NORM_DATETIME_PATTERN));
            contextList.add(changeStep);
            changeStep.setStep(YAYI_CHANGE_CONTENT_RESULT);
            changeStep.setPrompt(dialogueFilterParam);
            DialogueFilterResult.Data data = dialogueFilterResult.getData();
            changeStep.setResult(dialogueFilterResult);
            yayiChange.setCostTime(System.currentTimeMillis() - start);
            changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            changeStep.setCostTime(System.currentTimeMillis() - start);
            if (null == data) {
                return;
            }
            List<DialogueFilterResult.Result> changeList = data.getResult();
            if (CollectionUtils.isNotEmpty(changeList)) {
                DialogueFilterResult.Result result = changeList.get(0);
                contentTemp = result.getContent().replace("当前问题：", StringConstant.BLANK);
                dto.setNewQuestion(contentTemp);
                String defaultRegion = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.DEFAULT_REGION);
                if (StringUtils.isNotBlank(defaultRegion)) {
                    contentTemp = "在" + defaultRegion + "，" + contentTemp;
                }

                listMsg.remove(listMsg.size() - 1);
                listMsgHistory.add(new DialogueMessage("user", contentTemp));
            }
        }
        dto.setContentTemp(contentTemp);
        dto.setListMsg(listMsgHistory);
        dto.setRewriteContent(contentTemp);
        yayiChange.setResult(contentTemp);
        yayiChange.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        yayiChange.setCostTime(System.currentTimeMillis() - start);
        // changeStep.setResult(contentTemp);
    }

    /**
     * 判断是否需要改写
     *
     * @param dto
     * @param contextList
     * @return
     */
    private boolean changeFlag(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity changeStep = new StepEntity();
        contextList.add(changeStep);
        changeStep.setStep(CHANGE_CONTENT_STEP);
        changeStep.setPrompt(dto.getContent());
        changeStep.setCreateTime(DateUtil.getCurrentDateString());
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        // 如果打开多伦对话，那么默认打开改写
        if (YesNoEnum.YES.getName().equals(applicationInfo.getMultiDialogueFlag())) {
            applicationInfo.setRewritingFlag(YesNoEnum.YES.getName());
        }

        if (!YesNoEnum.YES.getName().equals(applicationInfo.getRewritingFlag())) {
            changeStep.setResult(CHANGE_CONTENT_NO);
            changeStep.setEndTime(DateUtil.getCurrentDateString());
            changeStep.setCostTime(System.currentTimeMillis() - start);
            return false;
        }

        // 判断是否内置问题，如果是内置问题，并且启用了内置问题的步骤，就不进行改写
        boolean builtFlag = isBuiltInQuestion(dto);
        if (builtFlag) {
            List<String> stepIdList = initStepIdList(dto);
            if (stepIdList.contains(BUILT_IN_STEP)) {
                changeStep.setResult(CHANGE_CONTENT_BUILT_IN);
                changeStep.setEndTime(DateUtil.getCurrentDateString());
                changeStep.setCostTime(System.currentTimeMillis() - start);
                return false;
            }
        }
        changeStep.setEndTime(DateUtil.getCurrentDateString());
        changeStep.setCostTime(System.currentTimeMillis() - start);
        return true;
    }

    /**
     * 对话前，对用户的问题进行检验
     *
     * @param dto
     * @param contextList
     * @return
     */
    private DoorsillEnum doorsill(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        // 记录步骤
        StepEntity changeStep = new StepEntity();
        contextList.add(changeStep);
        changeStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        changeStep.setStep(DOORSILL_CHECK_STEP);
        // 用户问题检查的大模型user指令
        String doorsillLlmUsrPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.DOORSILL_LLM_USR_PROMPT);
        if (StringUtils.isBlank(doorsillLlmUsrPrompt)) {
            log.info("没有配置用户问题检查的大模型user指令");
            changeStep.setResult("没有配置doorsill_llm_usr_prompt");
            changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            changeStep.setCostTime(System.currentTimeMillis() - start);
            return DoorsillEnum.NORMAL;
        }

        // 推送进度
        pushProcess(ProcessPushEnum.RISK, ListUtil.toList(), dto);

        List<DialogueMessage> listMsg = Lists.newArrayList();
        doorsillLlmUsrPrompt = doorsillLlmUsrPrompt.replace(QUESTION_PLACEHOLDER, dto.getContent());
        listMsg.add(new DialogueMessage("user", doorsillLlmUsrPrompt));

        // 执行大模型，判断用户问题
        String changeResult = change(listMsg, changeStep);
        if (StringUtils.isNotBlank(changeResult) && JSONUtil.isTypeJSONObject(changeResult)) {
            JSONObject jsonObject = JSON.parseObject(changeResult);

            // 如有有风险则优先返回风险
            String isRisk = jsonObject.getString(IS_RISK);
            if (YesNoEnum.YES.getName().equals(isRisk)) {
                pushProcess(ProcessPushEnum.RISK, ListUtil.toList("有风险"), dto);
                String doorsillRiskEnable = getConfiguration(applicationInfo.getApplicationId(), DOORSILL_RISK_ENABLE);
                if (YesNoEnum.YES.getName().equals(doorsillRiskEnable)) {
                    changeStep.setResult("此问题有风险");
                    changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                    changeStep.setCostTime(System.currentTimeMillis() - start);
                    return DoorsillEnum.RISK;
                }
            }
            pushProcess(ProcessPushEnum.RISK, ListUtil.toList("无风险"), dto);
            // 模糊问题标识
            String isBlurry = jsonObject.getString(IS_BLURRY);
            if (YesNoEnum.YES.getName().equals(isBlurry)) {
                // 模糊引导开关
                String vagueGuideEnable = getConfiguration(applicationInfo.getApplicationId(), VAGUE_GUIDE_ENABLE);
                if (YesNoEnum.YES.getName().equals(vagueGuideEnable)) {
                    changeStep.setResult("此问题过于模糊");
                    changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
                    changeStep.setCostTime(System.currentTimeMillis() - start);
                    return DoorsillEnum.BLUR;
                }
            }
        }
        changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        changeStep.setCostTime(System.currentTimeMillis() - start);
        return DoorsillEnum.NORMAL;
    }

    /**
     * 模糊问题处理
     *
     * @param dto
     * @param contextList
     * @return true:已引导，不需要执行问答流程，false:未引导，需要执行问答流程
     */
    private boolean vagueFlag(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        // 获取当前对话是否为第一次对话
        boolean firstDialogFlag = firstDialogFlag(dto.getConversationId());

        String vagueNeedEnable = getConfiguration(applicationInfo.getApplicationId(), VAGUE_NEED_ENABLE);

        // 判断是否为多轮对话
        boolean multiDialogueFlag = YesNoEnum.YES.getName().equals(applicationInfo.getMultiDialogueFlag());
        if (YesNoEnum.YES.getName().equals(vagueNeedEnable)) {
            if (multiDialogueFlag && !firstDialogFlag) {
                return false;
            }
        }

        // 模糊指令来源
        boolean vagueFlag = false;

        // 先匹配 qa
        String result = vagueQaDeal(dto, contextList, start);
        switch (result) {
            case StringConstant.TWO:
                // 未找到 qa 数据，还需要LLM 引导
                // 再匹配大模型
                vagueFlag = vagueLLm(dto, contextList, start);
                break;
            case StringConstant.THREE:
                // 已通过 qa 引导，不再需要执行问答流程
                vagueFlag = true;
            default:
                break;
        }

        return vagueFlag;
    }

    /**
     * 模糊问题LLM处理
     *
     * @param dto
     * @param contextList
     * @return
     */
    private boolean vagueLLm(DialogueByStreamParam dto, Vector<StepEntity> contextList, long start) {
        StepEntity changeStep = new StepEntity();
        changeStep.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(changeStep);
        changeStep.setStep(VAGUE_LLM_STEP);
        changeStep.setPrompt(dto.getContent());
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();

        // 获取问题意图推荐指令
        String vaguePromptLlmSystemPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.VAGUE_PROMPT_LLM_SYSTEM_PROMPT);
        if (StringUtils.isBlank(vaguePromptLlmSystemPrompt)) {
            changeStep.setResult("没有问题意图推荐指令，要设置vague_prompt_llm_system_prompt");
            return false;
        }

        String dateForPrompt = com.wenge.model.utils.DateUtil.getDateForPrompt();
        vaguePromptLlmSystemPrompt = vaguePromptLlmSystemPrompt.replace(DATE_PLACEHOLDER, dateForPrompt);

        try {
            StepEntity step = new StepEntity();
            step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            contextList.add(step);
            step.setStep(VAGUE_LLM_RESULT);

            cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
            // 改写参数，从配置中获取模型名称
            String vagueGuideModelId = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), VAGUE_GUIDE_MODEL_ID);
            if (StringUtils.isNotBlank(vagueGuideModelId)) {
                stepParam.set(LLM_PARAM_MODEL_ID, vagueGuideModelId);
            }
            // 大模型是否需要json格式化
            stepParam.set(LLM_JSON_FLAG, StringConstant.BLANK);
            step.setParam(stepParam);
            AnswerStepData answerStep = new AnswerStepData();
            answerStep.setIndex(0);
            answerStep.setStep(VAGUE_LLM_STRATEGY);
            answerStep.setStreamStatus(NumberConstants.ZERO);
            STEP_BY_LIST.get().add(answerStep);

            // 获取内容
            vaguePromptLlmSystemPrompt = getContent(dto, contextList, applicationInfo, vaguePromptLlmSystemPrompt);
            vaguePromptLlmSystemPrompt = vaguePromptLlmSystemPrompt.replace("${knowledge}", StringConstant.BLANK);

            // 大模型引导
            String generateCommon = answerUtils.getGenerateCommon(dto.getContent(), StringConstant.BLANK, step, ListUtil.toList(), result -> {
                if (null == changeStep.getFirstLlmTime()) {
                    changeStep.setFirstLlmTime(System.currentTimeMillis() - start);
                    step.setFirstLlmTime(System.currentTimeMillis() - start);
                }

                String vaguePushFlag  = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.VAGUE_NOT_ANSWER_FLAG);
                if (YesNoEnum.YES.getName().equals(vaguePushFlag)){
                    answerStep.setStatus(ANSWER_FINAL);
                    answerStep.setAnswer(applicationInfo.getFailureTalk());
                    changeStep.setResult(applicationInfo.getFailureTalk());
                } else {
                    answerStep.setStatus(result.getStatus());
                    answerStep.setAnswer(result.getAnswer());
                    changeStep.setResult(result.getAnswer());
                }
                dto.getAnswerConsumer().accept(answerStep);
            }, vaguePromptLlmSystemPrompt);
            // return AnswerUtils.dealLLmJson(generateCommon);
            changeStep.setResult(generateCommon);
            changeStep.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            changeStep.setCostTime(System.currentTimeMillis() - start);
            step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
            step.setCostTime(System.currentTimeMillis() - start);
            return true;
        } catch (Exception e) {
            log.error("error:{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 判断免登录
     *
     * @param param
     * @param request
     * @return
     */
    private TokenUser checkFree(DialogueByStreamParam param, HttpServletRequest request) {
        try {
            ApplicationInfo applicationInfo = applicationInfoService.getActiveApp(param.getApplicationId(), null, param.getDebuggerFlag());
            String freeLoginUserName = getConfiguration(applicationInfo.getApplicationId(), FREE_LOGIN_USER_NAME);
            if (StringUtils.isNotBlank(freeLoginUserName)) {
                String[] split = freeLoginUserName.split(",");
                String accountName = request.getHeader("Account-name");
                if (StringUtils.isBlank(accountName)) {
                    return null;
                }
                for (String name : split) {
                    if (name.equals(accountName)) {
                        log.info("用户名：" + name + "免登录");
                        String redisKey = RedisConstant.TOKEN_ACCOUNT + accountName;
                        if (redisService.hasKey(redisKey)) {
                            // 获取用户
                            TokenUser user = redisService.getObject(redisKey, TokenUser.class);
                            param.setUserName(user.getUserName());
                            param.setUserId(user.getId() + "");
                            return user;
                        } else {
                            Wrappers<Object> wrappers = Wrappers.init()
                                    .where(OAUTH_USER.ACCOUNT_NAME.eq(accountName))
                                    .limit(1);
                            OauthUser oauthUser = userService.getOne(wrappers);
                            TokenUser user = BeanUtil.toBean(oauthUser, TokenUser.class);
                            if (null != user) {
                                // 获取用户
                                param.setUserName(user.getUserName());
                                param.setUserId(user.getId() + "");
                                redisService.set(redisKey, user, 60 * 60 * 24);
                                return user;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取内容
     *
     * @param dto
     * @param contextList
     * @return
     */
    private String getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList, ApplicationInfo applicationInfo, String vaguePromptLlmSystemPrompt) {
        String vaguePromptFromKnowledgeEnable = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), VAGUE_PROMPT_FROM_KNOWLEDGE_ENABLE);
        if (!YesNoEnum.YES.getName().equals(vaguePromptFromKnowledgeEnable)) {
            return vaguePromptLlmSystemPrompt;
        }

        String esScore = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), VAGUE_PROMPT_FROM_KNOWLEDGE_ES_SCORE);
        if (StringUtils.isBlank(esScore)) {
            esScore = "1.3";
        }
        Float contentScoreOld = applicationInfo.getContentScore();
        Float rangeContentScoreOld = applicationInfo.getRangeContentScore();
        String networkFlag = applicationInfo.getNetworkFlag();
        Integer filterNum = applicationInfo.getFilterNum();
        applicationInfo.setFilterNum(10);
        if (NumberUtil.isNumber(esScore)) {
            applicationInfo.setContentScore(Float.parseFloat(esScore));
        }

        String rerankScore = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), VAGUE_PROMPT_FROM_KNOWLEDGE_RERANK_SCORE);
        if (StringUtils.isBlank(rerankScore)) {
            rerankScore = "0.5";
        }
        if (NumberUtil.isNumber(rerankScore)) {
            applicationInfo.setRangeContentScore(Float.parseFloat(rerankScore));
        }

        List<String> stepIdList = initStepIdList(dto);
        List<String> contentList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(stepIdList) && stepIdList.contains(FINAL_COLLECT)) {
            AnswerStrategy answerStrategy = answerStrategyMap.get(FINAL_COLLECT);
            if (null != answerStrategy) {
                KnowledgeContent content = answerStrategy.getContent(dto, contextList);
                List<String> vectorLibrary = content.getContentList();

                for (String s : vectorLibrary) {
                    String[] split = s.split(REARRANGE_SPLIT_CHAR);
                    // 获取内容，格式为 内容[额外信息]
                    String textDetail = split[2];
                    if (textDetail.contains(PREFIX_SYMBOL)) {
                        int indexOf = textDetail.lastIndexOf(PREFIX_SYMBOL);
                        textDetail = textDetail.substring(0, indexOf);
                    }
                    contentList.add(textDetail);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(contentList)) {
            // 临时关闭联网
            applicationInfo.setNetworkFlag(YesNoEnum.NO.getName());
            vaguePromptLlmSystemPrompt = vaguePromptLlmSystemPrompt.replace("${knowledge}", "\n##############你可以参考资料：\n" + JSONUtil.toJsonStr(contentList) + "\n##############\n");
        }

        // 恢复配置
        applicationInfo.setNetworkFlag(networkFlag);
        applicationInfo.setContentScore(contentScoreOld);
        applicationInfo.setRangeContentScore(rangeContentScoreOld);
        applicationInfo.setFilterNum(filterNum);
        return vaguePromptLlmSystemPrompt;
    }

    /**
     * 返回数据封装推理结果
     *
     * @param dto
     * @param reasoningContentFlag
     * @param result
     * @param i
     */
    private void dealReasoningContentFlag(DialogueByStreamParam dto, boolean reasoningContentFlag, String result, int i) {
        if (reasoningContentFlag) {
            String substring = result.substring(0, i + 1);
            if (substring.contains(INFERENCE_END_TAG)) {
                int i1 = result.indexOf(INFERENCE_END_TAG);
                dto.setAnswer(result.substring(i1 + INFERENCE_END_TAG.length(), i + 1));
            } else {
                dto.setReasoningContent(substring);
            }
        } else {
            dto.setAnswer(result.substring(0, i + 1));
        }
    }

    /**
     * 检测文本是否敏感
     *
     * @return
     */
    private String checkSensitive(List<String> notQuestion, BiFunction<String, String, Boolean> condition) {
        String pinyin;
        if (CollectionUtils.isNotEmpty(notQuestion)) {
            for (String not : notQuestion) {
                pinyin = toPinyin(not);
                if (condition.apply(not, pinyin)) {
                    return not;
                }
            }
        }
        return StringConstant.BLANK;
    }

    /**
     * 汉字转拼音
     *
     * @param chinese
     * @return
     */
    public String toPinyin(String chinese) {
        StringBuilder out = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null) {
                String pinyin = pinyinArray[0];
                // 移除声调
                pinyin = TONE_PATTERN.matcher(pinyin).replaceAll("");
                out.append(pinyin);
            } else {
                out.append(c);
            }
        }
        return out.toString().replace(" ", "");
    }


    /**
     * 问题理解
     *
     * @param contextList
     */
    private List<String> problemUnderstanding(Vector<StepEntity> contextList, DialogueByStreamParam dto) {
        long start = System.currentTimeMillis();
        StepEntity stepEntity = new StepEntity();
        stepEntity.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        contextList.add(stepEntity);
        stepEntity.setStep(UNDERSTANDING);
        stepEntity.setPrompt(dto.getContentTemp());

        if (StringUtils.isBlank(dto.getContentTemp())) {
            stepEntity.setResult("问题内容为空");
            return Lists.newArrayList();
        }

        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        String pluginModelFlag = getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.PLUGIN_MODEL_FLAG);
        stepEntity.setResult("是否开启问题拆解：" + pluginModelFlag);

        List<String> queryList = Lists.newArrayList();
        try {
            // 插件模型, 开启时进行问题理解
            if (YesNoEnum.YES.getName().equals(pluginModelFlag)) {
                pushProcess(ProcessPushEnum.UNDERSTANDING, ListUtil.toList("问题理解中"), dto);
                YayiPluginSelectionModelParam param = new YayiPluginSelectionModelParam();
                param.setAvaliable_plugins(ListUtil.toList("search"));
                param.setUse_only_custom_plugins(false);
                param.setMessages(ListUtil.toList(new YayiMessage(USER_PROMPT_FIELD, dto.getContentTemp())));
                YayiPluginSelectionModelResult modelResult = yayiServer.pluginModel(param);
                stepEntity.setPrompt(param);
                stepEntity.setResult(modelResult);
                if (null != modelResult) {
                    YayiPluginSelectionModelResult.PluginData data = modelResult.getData();
                    if (null != data) {
                        YayiPluginSelectionModelResult.Message message = data.getMessage();
                        if (null != message) {
                            String pluginArgs = message.getPluginArgs();
                            if (StringUtils.isNotBlank(pluginArgs) && JSONUtil.isTypeJSONObject(pluginArgs)) {
                                cn.hutool.json.JSONObject entries = JSONUtil.parseObj(pluginArgs);
                                if (entries.containsKey("query")) {
                                    cn.hutool.json.JSONArray query = entries.getJSONArray("query");
                                    if (CollectionUtils.isNotEmpty(query)) {
                                        queryList.addAll(query.toList(String.class));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        queryList.add(dto.getContentTemp());
        queryList = queryList.stream().distinct().collect(Collectors.toList());
        stepEntity.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        stepEntity.setCostTime(System.currentTimeMillis() - start);
        pushProcess(ProcessPushEnum.UNDERSTANDING, queryList, dto);
        return queryList;
    }

    /**
     * 针对答案的内容分别找到对应的引用原文
     *
     * @param answer
     * @param refList
     * @param currentData
     * @param lastStreamFlag
     * @return
     */
    private String findIndex(String answer, List<AnswerRefParam> refList, JSONObject currentData, boolean lastStreamFlag) {
        if (StringUtils.isBlank(answer)) {
            return answer;
        }
        if (!answer.contains("\n") && !lastStreamFlag) {
            return answer;
        }
        JSONObject contentIndexMap = new JSONObject();
        if (currentData.containsKey("contentIndexMap")) {
            contentIndexMap = currentData.getJSONObject("contentIndexMap");
        } else {
            currentData.put("contentIndexMap", contentIndexMap);
        }

        String[] split = answer.split("\n+");
        for (int i = 0; i < split.length; i++) {
            if (!contentIndexMap.containsKey(split[i])) {
                // 如果是最后一个，并且答案以换行结尾，则不进行索引
                if (!answer.endsWith("\n") && !lastStreamFlag) {
                    continue;
                }
                String indexByText = indexByYayi(split[i], refList);
                if (!"error".equals(StringUtils.trim(indexByText))) {
                    contentIndexMap.put(split[i], indexByText);
                }
            }
        }
        JSONObject temp = new JSONObject();
        temp.put("answer", answer);
        if (!contentIndexMap.isEmpty()) {
            contentIndexMap.forEach((k, v) -> {
                String tempStr = temp.getString("answer");
                tempStr = tempStr.replace(k, v.toString());
                temp.put("answer", tempStr);
            });
        }
        return temp.getString("answer");
    }

    private String indexByYayi(String text, List<AnswerRefParam> refList) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        // 去除开头的符号
        String temp = text.replaceFirst("[<|>*# -]+", StringConstant.BLANK);
        if (temp.length() < 8) {
            return text;
        }

        if (temp.contains("◹[")) {
            return text;
        }

        YayiContentIndexParam param = new YayiContentIndexParam();
        YayiContentIndexParam.Content content = new YayiContentIndexParam.Content();
        content.setMode("chat");
        param.setContent(content);
        content.setText(text);
        List<YayiContentIndexParam.Doc> docList = com.google.common.collect.Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(refList)) {
            docList = refList.stream().map(p -> {
                YayiContentIndexParam.Doc doc = new YayiContentIndexParam.Doc();
                doc.setTitle(p.getTitle());
                doc.setContext(p.getContent());
                doc.setUrl(p.getUrl());
                return doc;
            }).collect(Collectors.toList());
        }
        content.setDoc_list(docList);
        YayiContentIndexResult yayiContentIndexResult = yayiServer.indexContent(param);
        if (null == yayiContentIndexResult) {
            return text;
        }

        YayiContentIndexResult.IndexData data = yayiContentIndexResult.getData();
        if (null == data) {
            return text;
        }
        String res = data.getRes();
        content.setText(res);
        content.setMode("chat_with_quotes");
        YayiContentIndexResult yayiContentIndexChangeResult = yayiServer.indexContent(param);
        Map<String, Object> resInfo = yayiContentIndexChangeResult.getData().getRes_info();
        if (null != resInfo) {
            resInfo.forEach((k, v) -> {
                if (k != null && k.matches("\\d+")) {
                    int index = Integer.parseInt(k);
                    if (index > 0 && index <= refList.size()) {
                        if (v != null) {
                            List<?> list = (List<?>) v;
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < list.size(); i++) {
                                Object value = list.get(i);
                                sb.append(value);
                            }
                            refList.get(index-1).setContent(sb.toString());
                        }
                    }
                }
            });
        }
        if (StringUtils.isBlank(res)) {
            return text;
        }
        return res + "\n";
    }

    /**
     * 推送消息
     *
     * @param stepData
     * @param dto
     * @param finnishFlag
     * @param result
     * @param currentData
     * @param urlReplace
     * @param i
     * @param reasoningContent
     * @return
     * @throws IOException
     */
    private boolean sendSse(AnswerStepData stepData, DialogueByStreamParam dto, boolean finnishFlag, String result, JSONObject currentData, JSONObject urlReplace, int i, String reasoningContent) throws IOException {
//        if (i > 0) {
//            dto.setRefList(null);
//        }
        if (i > 0 && !(finnishFlag && i == result.length() - 1)) {
            dto.setRefList(null);
        }
        // 输出速度
        ThreadUtil.sleep(20);
        // 返回数据封装推理结果
        if ("reasoningContent".equals(currentData.getString("stream"))) {
            dto.setReasoningContent(reasoningContent.substring(0, i + 1));
        } else {
            String answerTemp = result.substring(0, i + 1);
            String contentIndexFlag = currentData.getString("contentIndexFlag");
            String indexContet = answerTemp;
            if (YesNoEnum.YES.getName().equals(contentIndexFlag)) {
                indexContet = findIndex(answerTemp, stepData.getRefList(), currentData, finnishFlag && i == result.length() - 1);
            }
            dto.setAnswer(indexContet);
        }
        // dealReasoningContentFlag(dto, reasoningContentFlag, result, i);
        if (!urlReplace.isEmpty()) {
            urlReplace.forEach((url, replace) -> {
                dto.setAnswer(dto.getAnswer().replace(url, replace.toString()));
                // data.put("answer", answer.replace(url, replace.toString()));
            });
        }
        dto.setAnswer(getOrgUrl(dto.getAnswer()));

        if (finnishFlag) {
            if (i >= result.length() - 1) {
                String plainText = plainText(result);
                // data.put("plainText", plainText);
                dto.setPlainText(plainText);
                stepData.setStreamStatus(NumberConstants.TWO);
            }
        }

        if (null == dto.getAnswer()) {
            dto.setAnswer(StringConstant.BLANK);
        }
        DialogueByStreamParam temp = tempClear(dto);
        return SseEmitterUtils.sendMsg(dto.getClientId(), JSON.toJSONString(temp));
    }

    /**
     * 推送进度
     *
     * @param pushEnum  保持唯一，并逐渐增加
     * @param resultList
     * @param dto
     */
    public static void pushProcess(ProcessPushEnum pushEnum, List resultList, DialogueByStreamParam dto) {
        List<DialogueProgress> progressList = dto.getProgressList();
        if (CollectionUtil.isEmpty(progressList)) {
            progressList = new ArrayList<>();
            dto.setProgressList(progressList);
        }

        Optional<DialogueProgress> any = progressList.stream().filter(p -> pushEnum.getProgress().equals(p.getProgress())).findAny();
        if (any.isPresent()) {
            any.get().setResultList(resultList);
        } else {
            DialogueProgress dialogueProgress = new DialogueProgress();
            dialogueProgress.setStepIndex(pushEnum.getStepIndex());
            dialogueProgress.setProgress(pushEnum.getProgress());
            dialogueProgress.setResultList(resultList);
            progressList.add(dialogueProgress);
        }
        try {
            if (CollectionUtil.isNotEmpty(progressList)) {
                DialogueByStreamParam temp = JSONUtil.toBean(JSONUtil.toJsonStr(dto), DialogueByStreamParam.class);
                temp.setContextList(null);
                temp.setListMsg(null);
                temp.setDialogueIdList(null);
                temp.setQueryList(null);
                temp.setBuiltInData(null);
                SseEmitterUtils.sendMsg(dto.getClientId(), JSON.toJSONString(temp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> associationContent(AssociateQuestionParam associateQuestionParam) {
        ApplicationInfo applicationInfo = APPLICATION_INFO.get();
        // 根据应用名和应用介绍，结合历史对话记录生成新的问题
        LastestRecordPageParam lastestRecordPageParam = new LastestRecordPageParam();
        lastestRecordPageParam.setLastestFlag("0");
        lastestRecordPageParam.setApplicationId(applicationInfo.getApplicationId());
        Result<EsPageInfo<Dialogue>> lastOneRecord = dialogueRecordService.getLastOneRecord(lastestRecordPageParam);
        List<Dialogue> dialogues = lastOneRecord.getData().getList();
        Set<String> titleSet = Sets.newHashSet();
        if (CollectionUtil.isNotEmpty(dialogues)) {
            titleSet = dialogues.stream()
                    .map(Dialogue::getQuestion)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        // 获取根据内容组织答案指令
        String associationPrompt = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.ASSOCIATION_PROMPT);
        if (StringUtils.isBlank(associationPrompt)) {
            log.error("没有问题联想指令，要设置association_prompt配置");
            return Lists.newArrayList();
        }
        associationPrompt = associationPrompt.replace("{keywords}", associateQuestionParam.getQuestion());
        associationPrompt = associationPrompt.replace("{history}", CollectionUtil.isNotEmpty(titleSet) ? JSONUtil.toJsonStr(titleSet) : "[]");
        associationPrompt = associationPrompt.replace("{appName}", applicationInfo.getApplicationName());
        associationPrompt = associationPrompt.replace("{appDesc}", applicationInfo.getIntroduce());
        associationPrompt = associationPrompt.replace("{count}", associateQuestionParam.getQuestion());
        String generateCommon = answerUtils.getGenerateCommon(null,
                associationPrompt,
                new StepEntity(), null, null);

        int startIndex1 = generateCommon.indexOf("[");
        int endIndex1 = generateCommon.lastIndexOf("]");
        if (startIndex1 != -1&&endIndex1!= -1) {
            return JSON.parseArray(generateCommon).toJavaList(String.class);
        } else {
            log.error("大模型调用失败");
            return Lists.newArrayList();
        }
    }

    /**
     * 发送引用溯源
     * @param currentData
     * @param dto
     */
    private void sendRef(JSONObject currentData, DialogueByStreamParam dto) {
        List<AnswerRefParam> refList = dto.getRefList();
        if (CollectionUtil.isNotEmpty(refList) && !YesNoEnum.YES.getName().equals(currentData.getString("pushProcessFlag"))) {
            currentData.put("pushProcessFlag", YesNoEnum.YES.getName());
            List<AnswerRefParam> refParamList = refList.stream().filter(p -> CollectionUtil.isNotEmpty(p.getModule()) && p.getModule().contains(DOCUMENT_ROUTE)).collect(Collectors.toList());
            DialogueServiceImpl.pushProcess(ProcessPushEnum.SEARCH_KNOWLEDGE, refParamList, dto);
        }
    }

    /**
     * 步骤转换成对话步骤
     * @param dialogueStepList
     * @return
     */
    private List<DialogueStep> stepToDialogueStep(List<StepEntity> dialogueStepList) {
        if (CollectionUtils.isNotEmpty(dialogueStepList)) {
            try {
                return dialogueStepList.stream().map(p -> {
                    DialogueStep dialogueStep = new DialogueStep();
                    if (p.getPrompt() != null && p.getResult() != null) {
                        dialogueStep.setIsShow(ShowContant.SHOW);
                        if (p.getResult() instanceof String && (StringUtils.isBlank((String) p.getResult()) || p.getResult().equals("是否开启问题拆解：null"))) {
                            dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                        } else if (JSONUtil.toJsonStr(p.getResult()).equals("[]") || JSONUtil.toJsonStr(p.getResult()).equals("{}")) {
                            dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                        } else if (p.getStep() != null && p.getStep().equals("会话id")) {
                            dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                        }
                    } else {
                        dialogueStep.setIsShow(ShowContant.NOT_SHOW);
                    }
                    if (null != p.getPrompt()) {
                        if (p.getPrompt() instanceof String) {
                            dialogueStep.setPrompt(p.getPrompt());
                        } else {
                            dialogueStep.setPrompt(JSON.toJSONString(p.getPrompt()));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 推流时，临时清空大字段
     * @param dto
     * @return
     */
    private DialogueByStreamParam tempClear(DialogueByStreamParam dto) {
        DialogueByStreamParam bean = JSONUtil.toBean(JSONUtil.toJsonStr(dto), DialogueByStreamParam.class);
        bean.setRefList(Lists.newArrayList());
        bean.setListMsg(Lists.newArrayList());
        bean.setProgressList(Lists.newArrayList());
        bean.setQueryList(Lists.newArrayList());
        bean.setDialogueIdList(Lists.newArrayList());
        bean.setContextList(new Vector<>());
        bean.setBuiltInData(null);
        return bean;
    }

    /**
     * 请求对话 api
     * @param param
     * @param api
     * @param clientId
     * @return
     */
    public static JSONObject requestStreamApi(Object param, String api, String clientId) {
        final String contentType = "application/json; charset=utf-8";

        final Request.Builder builder = new Request.Builder();
        final MediaType mediaType = MediaType.parse(contentType);
        final RequestBody body = RequestBody.create(mediaType, JSONUtil.toJsonStr(param));
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (null != requestAttributes) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            if (null != attributes) {
                HttpServletRequest httpServletRequest = attributes.getRequest();
                if (null == httpServletRequest) {
                    String token = RequestUtil.extractToken(httpServletRequest);
                    if (StringUtils.isNotBlank(token)) {
                        builder.addHeader("authorization", token);
                    }
                }
            }
        }

        Request request = builder
                .url(api)
                .post(body)
                .build();
        OkHttpClient okHttpClient = SpringUtil.getBean(OkHttpClient.class);

        try (final Response response = okHttpClient.newCall(request).execute();
             final InputStream it = response.body().byteStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(it, StandardCharsets.UTF_8));
            responseByStream(bufferedReader, res -> {
                try {
                    SseEmitterUtils.sendMsg(clientId, JSONUtil.toJsonStr(res));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SseEmitterUtils.completeDelay(clientId);
        }
        return new JSONObject();
    }

    /**
     * 通过流的方式返回数据
     * @param bufferedReader
     * @param consumer
     * @throws Exception
     */
    private static void responseByStream(BufferedReader bufferedReader, Consumer<DialogueByStreamParam> consumer) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.isNotBlank(line)) {
                    line = line.replace("data:", "");

                    if (JSONUtil.isTypeJSON(line) && !line.contains("[DONE]")) {
                        cn.hutool.json.JSONObject entries = JSONUtil.parseObj(line);
                        DialogueByStreamParam bean = entries.toBean(DialogueByStreamParam.class);
                        consumer.accept(bean);
                    }
                }
            }
        }
    }
}

