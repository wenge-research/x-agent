package com.wenge.model.strategy.answer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wenge.model.config.NetworkConfig;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.NetworkApiParam;
import com.wenge.model.dto.param.ScMeteSearch;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.*;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.DialogueStep;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.entity.WebsiteConfig;
import com.wenge.model.enums.ProcessPushEnum;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.enums.WebsiteTypeEnum;
import com.wenge.model.factory.UserThreadFactory;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.CsSignUtil;
import com.wenge.oauth.constants.AppConfigContant;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.graylog.trace.MDCTraceUtils;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.PromptWebParam;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.PromptWebResult;
import com.wg.appframe.yayi.result.RearrangeResult;
import com.wg.appframe.yayi.result.UrlTextResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.WebsiteConfigTableDef.WEBSITE_CONFIG;
import static com.wenge.oauth.constants.AppConfigContant.*;

/**
 * 互联网检索
 */
@Service(AnswerStrategyContant.NETWORK_STRATEGY)
@Slf4j
public class NetworkStrategy implements AnswerStrategy{

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private NetworkConfig networkConfig;

    @Autowired
    private RedisService redisService;

    @Value("${network.scMate.api:}")
    private String scApi;
    @Value("${network.scMate.appkey:}")
    private String scAppkey;
    @Value("${network.scMate.appSecret:}")
    private String scAppSecret;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setStep(AnswerStrategyContant.NETWORK_STEP);

        try {
            MDC.put(MDCTraceUtils.KEY_TRACE_ID, DialogueServiceImpl.TRACE_ID.get());
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            // 联网搜索
            if (!YesNoEnum.YES.getName().equals(applicationInfo.getNetworkFlag())) {
                return answerData;
            }
            contextList.add(step);
            String answer = fromNetwork(dto, contextList, step, answerData, applicationInfo, start);
            if (StringUtils.isBlank(answer)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
            log.info("===>fromNetwork:{}", answer);
            answerData.setAnswer(answer);
        } catch (Exception e) {
            log.error("NetworkStrategy error:{}", e.getMessage(), e);
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            step.setResult(ERROR_STEP + ":" + e.getMessage());
        }
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return answerData;

    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.NETWORK_STRATEGY);
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        // 联网搜索
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getNetworkFlag())) {
            return content;
        }
        Vector<String> allText = new Vector<>();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setStep(AnswerStrategyContant.NETWORK_STEP);
        contextList.add(step);

        // 获取网络数据
        List<NetworkApiResult.Datum> datumList = distinctList(dto, step, applicationInfo);

        if (CollectionUtils.isNotEmpty(datumList)) {
            for (NetworkApiResult.Datum datum : datumList) {
                allText.add(datum.getTitle() + "\n" + datum.getContent());
            }
        }
        log.info("获取联网检详情数据完成");

        // 推送状态
        DialogueServiceImpl.pushProcess(ProcessPushEnum.NETWORK, new ArrayList<>(datumList), dto);

        step.setResult(datumList);
        content.setContentList(allText);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return content;
    }

    /**
     * 获取网络数据
     * @param dto
     * @param step
     * @param applicationInfo
     * @return
     */
    private List<NetworkApiResult.Datum> distinctList(DialogueByStreamParam dto, StepEntity step, ApplicationInfo applicationInfo) {
        String md5 = SecureUtil.md5(dto.getContent());
        String key = RedisKey.NETWORK_CONTENT + md5;
        //Vector<String> allText = new Vector<>();
        Vector<NetworkApiResult.Datum> datumListFromRedis = new Vector<>();
        String internetRedisEnable = getConfiguration(applicationInfo.getApplicationId(), INTERNET_REDIS_ENABLE);
        boolean cacheFlag = YesNoEnum.YES.getName().equals(internetRedisEnable);
        if (cacheFlag && redisService.hasKey(key)) {
            step.setPrompt("从redis中获取，key: " + key);
            datumListFromRedis = redisService.getObject(key, Vector.class);
        }
        // 是否需要再次互联网检索，默认不需要
        boolean needNetFlag = false;
        if (CollectionUtils.isEmpty(datumListFromRedis)) {
            // 缓存没有数据，则需要从网站获取
            needNetFlag = true;
            datumListFromRedis = new Vector<>();
        } else {
            // 如果缓存中分数大于1的数据数量小于3，则添加新的数据
            String internetRewritingScore = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.INTERNET_REWRITING_SCORE);
            Float rewritingScore = Float.parseFloat(StringUtils.isBlank(internetRewritingScore) ? "0.75" : internetRewritingScore);
            long count = datumListFromRedis.stream().filter(p -> p.getScore() >= rewritingScore).count();
            String internetRedisCount = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.INTERNET_REDIS_COUNT);
            int redisCount = Integer.parseInt(StringUtils.isBlank(internetRedisCount) ? "3" : internetRedisCount);
            if (count < redisCount) {
                needNetFlag = true;
            }
        }

        // 判断是否需要再次检索
        if (needNetFlag) {
            Vector<NetworkApiResult.Datum> datumList = getContentFromWeb(dto, step, applicationInfo, md5);
            datumListFromRedis.addAll(datumList);
        }

        // 去重
        List<NetworkApiResult.Datum> distinctList = datumListFromRedis.stream().distinct().collect(Collectors.toList());

        // 筛选出符合正则的网页
        String networkSourceRegex = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), NETWORK_SOURCE_REGEX);
        if (StringUtils.isNotBlank(networkSourceRegex)) {
            distinctList = distinctList.stream()
                    .filter(p -> {
                        Pattern pattern = Pattern.compile(networkSourceRegex);
                        Matcher matcher = pattern.matcher(p.getUrl());
                        return matcher.find();
                    })
                    .collect(Collectors.toList());
        }

        datumListFromRedis.clear();
        datumListFromRedis.addAll(distinctList);

        // 重排，只有开通 缓存时才进行重排，这样缓存起的数据质量会好一点
        if (cacheFlag) {
            rearrangeNetworkContent(dto.getContentTemp(), applicationInfo, datumListFromRedis);
        }

        // 设置内容
        if (CollectionUtils.isNotEmpty(datumListFromRedis)) {
            datumListFromRedis.forEach(item->{
                if (StringUtils.isBlank(item.getContext())) {
                    item.setContext(item.getContent());
                }
                if (StringUtils.isBlank(item.getContent())) {
                    item.setContent(item.getContext());
                }
            });

             // 缓存数据
            if (cacheFlag) {
                redisService.set(key, datumListFromRedis, 60 * 60 * 24 * 7);
            }
        }
        return datumListFromRedis;
    }

    /**
     * 联网搜索
     *
     * @param dto
     * @param contextList
     * @return
     */
    private String fromNetwork(DialogueByStreamParam dto, Vector<StepEntity> contextList, StepEntity step, AnswerStepData answerData, ApplicationInfo applicationInfo, long start) {
        // 获取网络数据
        List<NetworkApiResult.Datum> datumList = distinctList(dto, step, applicationInfo);
        DialogueServiceImpl.pushProcess(ProcessPushEnum.NETWORK, new ArrayList(datumList), dto);

        if (CollectionUtils.isNotEmpty(datumList)) {
            // 获取评分top数据
            if (CollectionUtils.isNotEmpty(datumList)) {
                datumList = getTopContent(dto.getContentTemp(), datumList);
            }

            step.setResult(datumList);

            StepEntity modelStep = new StepEntity();
            contextList.add(modelStep);
            modelStep.setStep(AnswerStrategyContant.NETWORK_ANSWER);

            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isEmpty(datumList)) {
                return StringConstant.BLANK;
            }

            // 获取评分top数据，封装知识库
            for (int i = 0; i < datumList.size(); i++) {
                builder.append("\n<<< 【知识库").append(i + 1).append("】是： ").append(datumList.get(i).getContent()).append(" >>> ");
            }

            // 构建prompt
            String according = AnswerUtils.buildPrompt(builder, dto.getContentTemp(), datumList.size());

            String fromNetworkContent = getConfiguration(applicationInfo.getApplicationId(), FROM_NETWORK_CONTENT);
            boolean simpleFlag = StringUtils.isNotBlank(according) && according.contains("========================以下是相关资料");
            com.alibaba.fastjson2.JSONObject resultObj = new com.alibaba.fastjson2.JSONObject();
            if (simpleFlag) {
                resultObj.put(ANSWER_FIELD, StringConstant.BLANK);
            }
            String conversationName = answerUtils.getGenerateCommon(according, StringConstant.BLANK, modelStep, dto.getListMsg(), result -> {
                try {
                    if (null == step.getFirstLlmTime()) {
                        step.setFirstLlmTime(System.currentTimeMillis() - start);
                    }
                    if (StringUtils.isNotBlank(result.getErrorMessage())) {
                        if (result.getErrorMessage().toLowerCase().contains(LLM_ERROR_RISK)) {
                            return;
                        }
                    }
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

                    String answer = answerUtils.analyticAnswer(originalAnswer);
                    String outline = answerUtils.analyticOutline(originalAnswer);
                    if (StringUtils.isBlank(answer) && StringUtils.isBlank(reasoningContent)) {
                        return;
                    }
                    if (StringUtils.isNotBlank(applicationInfo.getNotAnswer()) && applicationInfo.getNotAnswer().equals(answer)) {
                        answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                        return;
                    }
                    if (StepStatusEnum.ANSWER_COMPLETE.equals(answerData.getStatus()) && StringUtils.isNotBlank(fromNetworkContent)) {
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
            }, StringConstant.BLANK);
            log.info("[联网检索] 生成会话名称完成 conversationName = {}", conversationName);
            conversationName = AnswerUtils.analyticAnswer(answerData.getAnswer());
            if (StringUtils.isBlank(conversationName) || AnswerStrategyContant.NO_ANSWER_TEXT.equals(conversationName)) {
                return StringConstant.BLANK;
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
    private List<NetworkApiResult.Datum> getTopContent(String content, List<NetworkApiResult.Datum> vectorLibrary) {
        List<NetworkApiResult.Datum> toYayiList = Lists.newArrayList();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        RearrangeParam rearrangeParam = new RearrangeParam();
        // 调用雅意重排能力
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(content);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (NetworkApiResult.Datum fileData : vectorLibrary) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            articles.setPara(fileData.getTitle() + "\n" + fileData.getContent());
            articlesList.add(articles);
        }
        rangeContent.setArticles(articlesList);
        rearrangeParam.setContent(rangeContent);
        // 调用重排能力
        RearrangeResult rearrange = answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
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
     * 获取联网检索数据，朱超 元搜索
     *
     * @return
     */
    private List<NetworkApiResult.Datum> getNetworkTextList(String question, WebsiteConfig config, StringBuffer builder, String md5) {
        NetworkApiParam param = new NetworkApiParam();
        param.setRequestid(md5);
        param.setEngine(config.getEngine());
        // 联网检索时：返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容
        String dataType = config.getDataType();

        if (StringUtils.isBlank(dataType)) {
            dataType = networkConfig.getDataType();
        }

        // 联网检索时：返回top N条数据，大于1，可为空或不传
        Integer top = config.getTop();
        if (null == config.getTop()) {
            top = networkConfig.getTop();
        }
        Integer isRender = config.getIsRender();
        if (null == isRender) {
            isRender = networkConfig.getIsRender();
        }

        // 联网检索时：检索的关键词
        String word = StringConstant.BLANK;
        if (StringUtils.isNotBlank(question)) {
            word = question;
        }

        String prefix = config.getPrefix();
        if (StringUtils.isBlank(prefix)) {
            prefix = networkConfig.getPrefix();
        }

        // 联网检索时：前缀词语
        String prefixResult = StringConstant.BLANK;
        if (StringUtils.isNotBlank(prefix)) {
            String[] split = prefix.split("\\|");
            if (split.length > 1) {
                switch (split[1]) {
                    // "双引号"包含
                    case "\"":
                        prefixResult = "\"" + split[0] + "\"";
                        break;
                    // "-"不包含
                    case "-":
                        prefixResult = " -" + split[0];
                        break;
                    // "intitle"标题
                    case "intitle":
                        prefixResult = "intitle:" + split[0];
                        break;
                    // 限定搜索结果中网页的URL包含特定关键词
                    case "inurl":
                        prefixResult = "inurl:" + split[0];
                        break;
                    // 限定搜索结果中网页的文本包含特定关键词
                    case "intext":
                        prefixResult = "intext:" + split[0];
                        break;
                    // 限定搜索结果中网页的标题范围
                    case "allintitle":
                        prefixResult = "allintitle:" + split[0];
                        break;
                    // 限定搜索结果中网页的文本范围
                    case "allinurl":
                        prefixResult = "allinurl:" + split[0];
                        break;
                    // 限定搜索结果中网页的数字范围
                    case "numrange":
                        prefixResult = "numrange:" + split[0];
                        break;
                    default:
                        break;
                }
            }
        }

        if (StringUtils.isNotBlank(prefixResult)) {
            word = prefixResult + " " + word;
        } else if (StringUtils.isNotBlank(prefix)) {
            word = prefix + " " + word;
        }

        // 联网检索时：后缀词语
        String suffix = config.getSuffix();
        if (StringUtils.isBlank(suffix)) {
            suffix = networkConfig.getSuffix();
        }
        if (StringUtils.isNotBlank(suffix)) {
            word = word + " " + suffix;
        }

        // 初始化问题时，使用指定的网站检索，格式为：site:www.baidu.com
        String site = config.getWebUrl();
        if (StringUtils.isNotBlank(site)) {
            site = " site:" + site;
            word = word + site;
        }

        // 记录搜索条件
        param.setWord(word);
        param.setData_type(dataType);
        param.setTop(top);
        param.setIs_render(isRender);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(param));
        builder.append("\n").append(JSON.toJSONString(param));
        log.info("===>联网检索请求id：{},URL:{},参数:{}", param.getRequestid(), networkConfig.getApi(), JSON.toJSONString(param));
        HttpResponse response = HttpUtil.createPost(networkConfig.getApi())
                .form(jsonObject)
                .execute();
        int statusCode = response.getStatus();
        if (statusCode != 200) {
            log.info("===>联网检索请求id:{},异常", param.getRequestid());
            return Lists.newArrayList();
        }

        String post = response.body();
        log.info("===>联网检索请求id:{},结果:{}", param.getRequestid(), post);
        if (StringUtils.isBlank(post)) {
            return Lists.newArrayList();
        }

        if (!JSONUtil.isTypeJSONObject(post)) {
            return Lists.newArrayList();
        }

        // 只有success才是成功
        NetworkApiResult networkApiResult = JSON.parseObject(post, NetworkApiResult.class);
        if (!"success".equals(networkApiResult.getMessage())) {
            return Lists.newArrayList();
        }

        List<NetworkApiResult.Datum> datumList = networkApiResult.getData();
        if (CollectionUtils.isEmpty(datumList)) {
            return Lists.newArrayList();
        }
        return datumList;
    }

    /**
     * 获取联网检索数据,YAYI的服务
     *
     * @param question
     * @param config
     * @return
     */
    private List<NetworkApiResult.Datum> getYayiNetwork(String question, WebsiteConfig config, StringBuffer builder, String md5, String clientId) {
        NetworkApiParam param = new NetworkApiParam();
        param.setRequestid(IdUtil.simpleUUID());
        param.setEngine(config.getEngine());
        // 联网检索时：返回数据类型，snapshot 快照(不采详情页)、snapshot_and_context 快照和内容
        String dataType = config.getDataType();

        if (StringUtils.isBlank(dataType)) {
            dataType = networkConfig.getDataType();
        }

        // 联网检索时：返回top N条数据，大于1，可为空或不传
        Integer top = config.getTop();
        if (null == config.getTop()) {
            top = networkConfig.getTop();
        }

        // 联网检索时：检索的关键词
        String word = StringConstant.BLANK;
        if (StringUtils.isNotBlank(question)) {
            word = question;
        }

        String prefix = config.getPrefix();
        if (StringUtils.isBlank(prefix)) {
            prefix = networkConfig.getPrefix();
        }
        if (StringUtils.isNotBlank(prefix)) {
            word = prefix + " " + word;
        }
        // 联网检索时：后缀词语
        String suffix = config.getSuffix();
        if (StringUtils.isBlank(suffix)) {
            suffix = networkConfig.getSuffix();
        }
        if (StringUtils.isNotBlank(suffix)) {
            word = word + " " + suffix;
        }

        // 初始化问题时，使用指定的网站检索，格式为：site:www.baidu.com
        String site = config.getWebUrl();
        if (StringUtils.isNotBlank(site)) {
            site = " site:" + site;
            word = word + site;
        }

        PromptWebParam promptWebParam = new PromptWebParam();
        PromptWebParam.Content content = new PromptWebParam.Content();
        promptWebParam.setContent(content);
        promptWebParam.setId(md5);
        // content.setWeb_source_list(ListUtil.toList("quark"));
        content.setWeb_source_list(ListUtil.toList(config.getEngine()));
        content.setGet_news_num(top);
        content.setTop_k(top);
        content.setData_type(dataType);
        PromptWebResult promptWebResult = yayiServer.promptWeb(word, promptWebParam);
        builder.append("\n").append(JSON.toJSONString(promptWebParam));
        PromptWebResult.Data data = promptWebResult.getData();
        List<NetworkApiResult.Datum> datumList = Lists.newArrayList();
        if (null != data) {
            List<PromptWebResult.ResInfoDetail> resInfo = data.getResInfo();
            return resInfo.parallelStream().map(p -> {
                // 判断是否被取消
                if (StringUtils.isNotBlank(clientId) && !DialogueServiceImpl.RUN_FLAG_MAP.getOrDefault(clientId, false)) {
                    return null;
                }
                NetworkApiResult.Datum datum = BeanUtil.toBean(p, NetworkApiResult.Datum.class);
                datum.setContent(p.getContext());
                // 如果正文和摘要一致，则需要检索url获取正文数据
                if (StringUtils.isBlank(p.getContext()) || p.getContext().equals(p.getAbstracts())) {
                    try {
                        // 获取正文数据
                        UrlTextResult urlTextResult = urlToTextByHttp(p.getUrl());
                        // 容错
                        if (null == urlTextResult) {
                            urlTextResult = yayiServer.url2Text(p.getUrl());
                        }
                        UrlTextResult.TextData textData = urlTextResult.getData();
                        if (null != textData) {
                            datum.setContent(textData.getContent());
                            datum.setTitle(textData.getTitle());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return datum;
            }).collect(Collectors.toList());
        }
        return datumList;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非检索问答库【回答】的结果
        if (null == step || !step.getStep().equals(NETWORK_REF) && !step.getStep().equals(NETWORK_STRATEGY)) {
            return dataList;
        }
        DialogueStep primaryData = stepMap.get(NETWORK_STEP);
        if (null != primaryData) {
            Object refResult = primaryData.getResult();
            if (null == refResult) {
                return dataList;
            }
            List<PromptWebResult.ResInfoDetail> infoDetails = JSON.parseArray(refResult.toString(), PromptWebResult.ResInfoDetail.class);

            // 过滤符合条件的网页链接
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            // 前台聊天时，只展示指定的网页
            if (null != applicationInfo && !"1".equals(applicationInfo.getFromManage())) {
                String networkSourceRegex = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), NETWORK_SOURCE_REGEX);
                if (StringUtils.isNotBlank(networkSourceRegex)) {
                    infoDetails = infoDetails.stream()
                            .filter(p -> {
                                Pattern pattern = Pattern.compile(networkSourceRegex);
                                Matcher matcher = pattern.matcher(p.getUrl());
                                return matcher.find();
                            })
                            .collect(Collectors.toList());
                }
            }
            List<SourceAnswerResult> sourceAnswer = infoDetails.stream()
                    .map(p -> {
                        SourceAnswerResult source = new SourceAnswerResult();
                        source.setKnowledgeName(NO_KNOWLEDGE);
                        source.setRoute(ListUtil.toList(NETWORK_ROUTE));
                        source.setText(p.getContext());
                        String title = p.getTitle();
                        source.setSourceType(4);
                        source.setFileName(title);
                        source.setFileLink(p.getUrl());
                        return source;
                    }).collect(Collectors.toList());
            dataList.addAll(sourceAnswer);
        }
        return dataList;
    }

    @Override
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, NETWORK_STRATEGY, NETWORK_STEP, NETWORK_REF);
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setClientId(scoreDataParam.getClientId());
        dto.setContentTemp(scoreDataParam.getQuestion());
        dto.setContent(scoreDataParam.getQuestion());
        dto.setClientId(IdUtil.simpleUUID());
        List<ScoreDataResult> results = new ArrayList<>();
        try {
            DialogueServiceImpl.RUN_FLAG_MAP.put(dto.getClientId(), true);
            StepEntity step = new StepEntity();
            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            //Vector<NetworkApiResult.Datum> datumList = distinctList(dto, step, applicationInfo, IdUtil.simpleUUID());
            List<NetworkApiResult.Datum> datumList = distinctList(dto, step, applicationInfo);
            for (NetworkApiResult.Datum datum : datumList) {
                ScoreDataResult result = new ScoreDataResult();
                result.setTitle(datum.getTitle() + "【" + datum.getUrl() + "】");
                result.setContent(datum.getContent());
                result.setRearrangeScore(BigDecimal.valueOf(datum.getScore()));
                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DialogueServiceImpl.RUN_FLAG_MAP.remove(dto.getClientId());
        }
        return results;
    }

    /**
     * 联网检索
     *
     * @param dto
     * @param step
     * @param applicationInfo
     * @return
     */
    private Vector<NetworkApiResult.Datum> getContentFromWeb(DialogueByStreamParam dto, StepEntity step, ApplicationInfo applicationInfo, String md5) {
        // 获取网站配置
        List<WebsiteConfig> websiteConfigList = WebsiteConfig.create()
                .where(WEBSITE_CONFIG.APPLICATION_ID.eq(applicationInfo.getApplicationId()))
                .and(WEBSITE_CONFIG.TYPE.eq(WebsiteTypeEnum.WEB_SEARCH.getCode()))
                .and(WEBSITE_CONFIG.STATUS.eq(YesNoEnum.YES.getName()))
                .lists();


        // 添加默认配置，没有指定网站
        if (CollectionUtils.isEmpty(websiteConfigList)) {
            WebsiteConfig defaultConfig = WebsiteConfig.create()
                    .setEngine(networkConfig.getEngine())
                    .setDataType(networkConfig.getDataType())
                    .setTop(networkConfig.getTop())
                    .setPrefix(networkConfig.getPrefix())
                    .setSuffix(networkConfig.getSuffix())
                    .setIsRender(networkConfig.getIsRender());
            websiteConfigList.add(defaultConfig);
        }

        // 设置引擎
        if (CollectionUtils.isNotEmpty(websiteConfigList)) {
            websiteConfigList.forEach(item -> item.setEngine(applicationInfo.getNetworkChannel()));
        }

        Vector<NetworkApiResult.Datum> datumList = new Vector<>();
        StringBuffer builder = new StringBuffer("检索条件:");
        // 3. 创建线程池
        String traceId = MDC.get(MDCTraceUtils.KEY_TRACE_ID);
        UserThreadFactory userThreadFactory = new UserThreadFactory(traceId);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                3,
                8,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                userThreadFactory, // 使用自定义工厂
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );
        // 检索指定网站
        for (WebsiteConfig websiteConfig : websiteConfigList) {
            // 百度检索会出现多次查询不一致的情况，这里尝试重试3次，获取不同的数据
            String internetSearchCount = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.INTERNET_SEARCH_COUNT);
            // 获取搜索平台
            String searchPlatformTemp = netPlatform(applicationInfo);
            int retryCount = StringUtils.isBlank(internetSearchCount) ? 1 : Integer.parseInt(internetSearchCount);
            for (int i = 0; i < retryCount; i++) {
                // 检索理解后的问题
                List<String> queryList = dto.getQueryList();
                for (String query : queryList) {
                    String contentQuery = query;
                    poolExecutor.execute(() -> {
                        try {
                            MDC.put(MDCTraceUtils.KEY_TRACE_ID, traceId);
                            List<NetworkApiResult.Datum> datum = Lists.newArrayList();
                            if ("YAYI".equals(searchPlatformTemp)) {
                                // 使用李军峰的bing检索(雅意的检索)
                                datum = getYayiNetwork(contentQuery, websiteConfig, builder, md5, dto.getClientId());
                            } else if ("metasearch".equals(searchPlatformTemp)) {
                                // 原数据检索，朱超
                                datum = getNetworkTextList(contentQuery, websiteConfig, builder, md5);
                            } else if ("sc_meta".equals(searchPlatformTemp)) {
                                // 元搜索 https://api-sc.wengegroup.com/openapi/v1/interactionVolume/yayiMetaSearch
                                datum = getScMateNetwork(contentQuery, builder, applicationInfo);
                            }
                            datumList.addAll(datum);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
        while (!poolExecutor.isShutdown() && !poolExecutor.isTerminated() && 0 != poolExecutor.getActiveCount()) {
            // 判断当前对话是否已完成，如果已完成，则关闭线程池
            if (StringUtils.isNotBlank(dto.getClientId()) && !DialogueServiceImpl.RUN_FLAG_MAP.getOrDefault(dto.getClientId(), false)) {
                break;
            }
            ThreadUtil.sleep(500);
        }
        poolExecutor.shutdown();
        step.setPrompt(builder.toString());

        List<NetworkApiResult.Datum> datumFilterList = datumList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        // 默认分数为0
        datumFilterList.forEach(item -> {
            item.setScore(0F);
        });
        log.info("联网检索到{}条结果", datumFilterList.size());
        step.setResult(datumList);
        return datumList;
    }

    /**
     * 重排
     */
    private void rearrangeNetworkContent(String question, ApplicationInfo applicationInfo, Vector<NetworkApiResult.Datum> datumListFromRedis) {
        if (CollectionUtils.isEmpty(datumListFromRedis)) {
            return;
        }
        RearrangeParam rearrangeParam = new RearrangeParam();
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(question);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (NetworkApiResult.Datum datum : datumListFromRedis) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            articles.setPara(datum.getTitle() + "\n" + datum.getContent());
            articlesList.add(articles);
        }
        rearrangeParam.setContent(rangeContent);
        rangeContent.setArticles(articlesList);
        RearrangeResult rearrange = answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
        RearrangeResult.RearrangeData data = rearrange.getData();
        List<Integer> indexList = data.getIndex_list();
        List<BigDecimal> resScoresList = data.getRes_scores_list();
        int scoreIndex = 0;
        for (Integer index : indexList) {
            NetworkApiResult.Datum datum = datumListFromRedis.get(index);
            datum.setScore(resScoresList.get(scoreIndex++).floatValue());
        }
        String internetRedisCount = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.INTERNET_REDIS_COUNT);
        String internetRewritingScore = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.INTERNET_REWRITING_SCORE);
        int redisCount = Integer.parseInt(StringUtils.isBlank(internetRedisCount) ? "3" : internetRedisCount);
        Float rewritingScore = Float.parseFloat(StringUtils.isBlank(internetRewritingScore) ? "0.75" : internetRewritingScore);
        List<NetworkApiResult.Datum> topN = datumListFromRedis.stream()
                .filter(p -> p.getScore() >= rewritingScore)
                .sorted((p1, p2) -> (int) ((p2.getScore() * 100000) - (p1.getScore() * 100000)))
                .limit(redisCount)
                .collect(Collectors.toList());
        datumListFromRedis.clear();
        datumListFromRedis.addAll(topN);
    }

    /**
     * 网页转文本
     * @param url
     */
    private UrlTextResult urlToTextByHttp(String url) {
        try {
            log.info("urlToTextByHttp.开始请求url:{}", url);
            HttpRequest get = HttpUtil.createGet(url);
            get.setReadTimeout(3000);
            get.setConnectionTimeout(3000);
            String body = get.execute().body();
            Document parse = Jsoup.parse(body);
            String text = parse.text();
            if (StringUtils.isNotBlank(text) && !"redirect".equals(text)) {
                UrlTextResult urlTextResult = new UrlTextResult();
                UrlTextResult.TextData data = new UrlTextResult.TextData();
                urlTextResult.setData(data);
                data.setContent(text);
                data.setTitle(parse.title());
                return urlTextResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取网络渠道
     * @param applicationInfo
     * @return
     */
    private String netPlatform(ApplicationInfo applicationInfo) {
        // 通过应用配置获取互联网检索平台
        String searchPlatform = AppConfigContant.getConfiguration(applicationInfo.getApplicationId(), AppConfigContant.INTERNET_SEARCH_PLATFORM);
        // 默认使用雅意平台
        if (StringUtils.isBlank(searchPlatform)) {
            searchPlatform = "YAYI";
        }

        // 如果是谷歌检索，那么就直接使用元搜索平台，因为其他检索平台不支持谷歌检索
        if ("google".equals(applicationInfo.getNetworkChannel())
                || "google_news".equals(applicationInfo.getNetworkChannel())) {
            searchPlatform = "sc_meta";
        }

        return searchPlatform;
    }

    /**
     * 获取联网检索数据,长沙元检索
     *
     * @param question
     * @return
     */
    private List<NetworkApiResult.Datum> getScMateNetwork(String question, StringBuffer builder, ApplicationInfo applicationInfo) {
        try {
            ScMeteSearch scMeteSearch = new ScMeteSearch();
            scMeteSearch.setWord(question);
            scMeteSearch.setEngine(applicationInfo.getNetworkChannel());
            scMeteSearch.setRequestId(IdUtil.simpleUUID());
            scMeteSearch.setDataType("snapshot_and_context");
            scMeteSearch.setTop(10);
            scMeteSearch.setIsRender(0);
            scMeteSearch.setIsForeignMedia(1);

            cn.hutool.json.JSONObject headers = CsSignUtil.buildHeader(scAppkey, scAppSecret, JSONUtil.parseObj(scMeteSearch));
            HttpRequest post = HttpUtil.createPost(scApi);
            headers.forEach((k, v) -> {
                post.header(k, v.toString());
            });
            post.timeout(1000 * 60 * 3);
            post.body(JSONUtil.toJsonStr(scMeteSearch));
            builder.append("\n").append(scApi).append("\n").append(JSONUtil.toJsonStr(scMeteSearch));
            log.info("请求地址,{},请求参数:{}", scApi, JSONUtil.toJsonStr(scMeteSearch));
            String body = post.execute().body();
            log.info("请求结果:{}", body);
            cn.hutool.json.JSONObject entries = JSONUtil.parseObj(body);
            ScMeteSearchResult bean = entries.toBean(ScMeteSearchResult.class);
            List<ScMeteSearchResult.YayiMetaSearchVO> dataList = bean.getData();
            if (CollectionUtils.isNotEmpty(dataList)) {
                return dataList.stream().map(p -> {
                    NetworkApiResult.Datum datum = BeanUtil.toBean(p, NetworkApiResult.Datum.class);
                    datum.setContext(p.getContent());
                    return datum;
                }).collect(Collectors.toList());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Lists.newArrayList();
    }
}


