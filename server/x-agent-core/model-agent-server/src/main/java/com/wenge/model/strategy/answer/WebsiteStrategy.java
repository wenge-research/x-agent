package com.wenge.model.strategy.answer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.*;
import com.wenge.model.factory.UserThreadFactory;
import com.wenge.model.mapper.es.KnowledgeUrlDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FileService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.UrlParserInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.model.constants.AnswerStrategyContant.*;

@Service(WEBSITE_STRATEGY)
@Slf4j
public class WebsiteStrategy implements AnswerStrategy {

    @Autowired
    private KnowledgeUrlDataMapper knowledgeUrlDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private FileService fileService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private UrlParserInfoService urlParserInfoService;

    @Autowired
    private AnswerUtils answerUtils;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        answerData.setAnswer(StringConstant.BLANK);
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(WEBSITE_STRATEGY);
        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        // 判断是否开启问答库
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            return content;
        }
        step.setStep(AnswerStrategyContant.WEBSITE_STEP);
        contextList.add(step);
        List<KnowledgeUrlData> fileDataList = getWebsiteDataList(dto, step);
        // 内容 前缀符号 知识库id|urlId|评分 后最符号
        List<String> collect = fileDataList.stream().map(p -> p.getContent() + PREFIX_SYMBOL + p.getKnowledgeId() + MID_ONE_SYMBOL +
                p.getUrlId() + MID_ONE_SYMBOL + p.getScore() + SUFFIX_SYMBOL).distinct().collect(Collectors.toList());
        content.setContentList(collect);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return content;
    }

    /**
     * 从网址网页获取原始数据
     *
     * @param dto
     * @param step
     * @return
     */
    private List<KnowledgeUrlData> getWebsiteDataList(DialogueByStreamParam dto, StepEntity step) {
        Map<String, List<KnowledgeInfo>> knowledgeVectorMap = DialogueServiceImpl.KNOWLEDGE_VECTOR_MAP.get();
        if (null == knowledgeVectorMap || knowledgeVectorMap.isEmpty()) {
            return Lists.newArrayList();
        }

        List<String> queryList = dto.getQueryList();
        if (null == queryList) {
            queryList = ListUtil.toList(dto.getContentTemp());
        }
        if (knowledgeVectorMap.size() == 1 && queryList.size() == 1) {
            List<KnowledgeInfo> knowledgeInfos = knowledgeVectorMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            List<String> knowldegeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
            return getDataByKnowledgeId(dto, step, knowldegeIdList);
        } else {
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
            List<CompletableFuture<List<KnowledgeUrlData>>> futures = Lists.newArrayList();

            knowledgeVectorMap.forEach((vector, knowledgeInfos) -> {
                for (String query : querys) {
                    DialogueByStreamParam bean = BeanUtil.toBean(dto, DialogueByStreamParam.class);
                    bean.setContentTemp(query);
                    CompletableFuture<List<KnowledgeUrlData>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
                        List<String> knowldegeIdList = knowledgeInfos.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());
                        return getDataByKnowledgeId(bean, step, knowldegeIdList);
                    }, executor);
                    futures.add(listCompletableFuture);
                }
            });

            CompletableFuture.allOf(futures.stream().toArray(CompletableFuture[]::new));
            executor.shutdown();
            return futures.stream().flatMap(p -> {
                try {
                    return p.join().stream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Stream.empty();
            }).collect(Collectors.toList());
        }
    }


    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非网址网页的结果
        if (null == step || !step.getStep().equals(WEBSITE_STRATEGY)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        List<String> list = (List<String>) step.getResult();
        return websiteRef(list);
    }

    /**
     * 网址网页 格式：内容[知识库id|文件id|评分]
     *
     * @param list
     * @return
     */
    private List<SourceAnswerResult> websiteRef(List<String> list) {
        return list.stream().map(p -> {
            String[] split1 = p.split(AnswerStrategyContant.REARRANGE_SPLIT_CHAR);
            String text = split1[split1.length - 1];
            text = text.split(PREFIX_SYMBOL)[0];
            SourceAnswerResult sourceAnswerResult = new SourceAnswerResult();
            sourceAnswerResult.setKnowledgeName(StringConstant.BLANK);
            // 解析：内容[知识库id|urlId|评分]
            int indexOf = p.indexOf(PREFIX_SYMBOL);
            String substringed = p.substring(indexOf + PREFIX_SYMBOL.length(), p.length() - PREFIX_SYMBOL.length());
            String[] split = substringed.split(MID_ONE_SYMBOL);
            String knowledgeId = split[0];
            String urlId = split[1];
            String scoreStr = split[2];
            String knowledgeName = StringConstant.BLANK;
            // urlId
            String title = StringConstant.BLANK;
            if (StringUtils.isNotBlank(urlId)) {
                UrlParserInfo urlParserInfo = urlParserInfoService.getById(urlId);
                // 文件有可能被删除
                if (null == urlParserInfo) {
                    urlParserInfo = new UrlParserInfo();
                }
                if (StringUtils.isNotBlank(knowledgeId)) {
                    KnowledgeInfo knowledgeInfo = knowledgeInfoService.getById(knowledgeId);
                    if (null != knowledgeInfo) {
                        knowledgeName = knowledgeInfo.getKnowledgeName();
                    }
                }
                if (StringUtils.isNotBlank(scoreStr)) {
                    float score = Float.parseFloat(scoreStr);
                    sourceAnswerResult.setScore(score);
                }
                title = urlParserInfo.getTitle();

                sourceAnswerResult.setRoute(ListUtil.toList(WEBSITE_ROUTE, title));
                sourceAnswerResult.setFileLink(urlParserInfo.getPageUrl());
                sourceAnswerResult.setFileName(title);
                sourceAnswerResult.setText(urlParserInfo.getPageUrl());
                sourceAnswerResult.setSourceType(4);
            } else {
                sourceAnswerResult.setRoute(ListUtil.toList(WEBSITE_ROUTE, title));
            }
            sourceAnswerResult.setKnowledgeName(knowledgeName);
            sourceAnswerResult.setText(text);
            return sourceAnswerResult;
        }).collect(Collectors.toList());
    }

    /**
     * 获取查询方式，支持全文检索，语义检索，混合检索
     *
     * @param dto
     * @return
     */
    private QueryBuilder getQueryByWay(DialogueByStreamParam dto, float score, List<String> knowldegeIdList) {
        // 必要条件
        BoolQueryBuilder mustCondition = prerequisite(knowldegeIdList);
        // 默认混合检索
        if (null == dto.getSearchWay()) {
            fullTextSearch(dto, mustCondition);
            return semanticSearch(mustCondition, dto, score, knowldegeIdList);
        }

        switch (dto.getSearchWay()) {
            // 全文检索
            case FULL_TEXT:
                fullTextSearch(dto, mustCondition);
                return mustCondition;
            // 语义检索
            case SEMANTIC:
                return semanticSearch(mustCondition, dto, score, knowldegeIdList);
            default:
                fullTextSearch(dto, mustCondition);
                return semanticSearch(mustCondition, dto, score, knowldegeIdList);
        }
    }


    /**
     * 必要条件
     * @return
     */
    public BoolQueryBuilder prerequisite(List<String> knowldegeIdList) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        List<QueryBuilder> must = bool.must();
        must.add(QueryBuilders.termsQuery("knowledgeId.keyword", knowldegeIdList));
        // 检索已启用数据
        BoolQueryBuilder statusQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termsQuery("enable", YesNoEnum.YES.getCode()))
                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("status")));
        must.add(statusQuery);
        // 检索有效期内的数据
        String currentTime = DateUtil.getCurrentTime();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 构建 effectiveStartTime 的查询条件
        BoolQueryBuilder startTimeQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery("effectiveStartTime").lte(currentTime))
                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("effectiveStartTime")));
        // 构建 effectiveEndTime 的查询条件
        BoolQueryBuilder endTimeQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.rangeQuery("effectiveEndTime").gte(currentTime))
                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("effectiveEndTime")));
        // 将两个条件组合在一起
        boolQuery.must(startTimeQuery);
        boolQuery.must(endTimeQuery);
        must.add(boolQuery);
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

    /**
     * 语义搜索
     *
     * @param mustCondition
     * @param dto
     * @param score
     * @return
     */
    public ScriptScoreQueryBuilder semanticSearch(BoolQueryBuilder mustCondition, DialogueByStreamParam dto, float score, List<String> knowldegeIdList) {
        List<QueryBuilder> must = mustCondition.must();

        JSONObject jsonObject = new JSONObject();
        // List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();
        List<Double> doubles = denseVectorService.modelEncode(dto.getContentTemp(), knowldegeIdList.get(0));
        // List<Double> doubles = knowledgeDataService.modelEncode(dto.getContentTemp());
        jsonObject.put("queryVector", doubles);
        String contentDense = CONTENT_DENSE_FILED;
        if (doubles.size() == 1024) {
            contentDense = CONTENT_DENSE_FILED_1024;
        }
        Script script = new Script(ScriptType.INLINE, "painless", "cosineSimilarity(params.queryVector,'" + contentDense + "')+1.0", jsonObject);
        must.add(QueryBuilders.existsQuery(contentDense));

        ScriptScoreQueryBuilder scriptScoreQueryBuilder = QueryBuilders.scriptScoreQuery(mustCondition, script);
        scriptScoreQueryBuilder.setMinScore(score);
        return scriptScoreQueryBuilder;
    }

    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setContentTemp(scoreDataParam.getQuestion());
        dto.setClientId(scoreDataParam.getClientId());
        StepEntity step = new StepEntity();
        List<KnowledgeUrlData> fileDataList = getWebsiteDataList(dto, step);
        return fileDataList.stream().map(p -> {
            ScoreDataResult result = new ScoreDataResult();
            result.setContent(p.getKnowledgeId() + " | " + p.getContent());
            result.setEsScore(p.getScore());
            return result;
        }).collect(Collectors.toList());
    }

    private List<KnowledgeUrlData> getDataByKnowledgeId(DialogueByStreamParam dto, StepEntity step, List<String> knowldegeIdList) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (null == applicationInfo) {
            log.info("WebsiteStrategy applicationInfo is null");
            return Lists.newArrayList();
        }
        LambdaEsQueryWrapper<KnowledgeUrlData> esQueryWrapper = EsWrappers.lambdaQuery(KnowledgeUrlData.class);
        SearchSourceBuilder searchSourceBuilder = knowledgeUrlDataMapper.getSearchSourceBuilder(esQueryWrapper);
        // 获取搜索方式
        QueryBuilder queryByWay = getQueryByWay(dto, applicationInfo.getContentScore(), knowldegeIdList);

        SearchSourceBuilder query = searchSourceBuilder.query(queryByWay);
        query.fetchSource(null, new String[]{AnswerStrategyContant.CONTENT_DENSE_FILED});
        esQueryWrapper.setSearchSourceBuilder(query);
        int prepareNum = answerUtils.buildPrepareNum(applicationInfo);
        query.size(prepareNum);
        query.from(0);
        List<KnowledgeUrlData> vectorLibrary = knowledgeUrlDataMapper.selectList(esQueryWrapper);
        String source = knowledgeUrlDataMapper.getSource(esQueryWrapper);
        step.setPrompt(source);
//        step.setResult(vectorLibrary.size() + "条");
        step.setResult(vectorLibrary);
        return vectorLibrary;
    }
}
