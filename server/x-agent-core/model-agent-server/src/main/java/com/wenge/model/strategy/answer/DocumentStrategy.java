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
import com.wenge.model.enums.DocLinkTypeEnum;
import com.wenge.model.factory.UserThreadFactory;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FileService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.RearrangeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.FileTableDef.FILE;

@Service(AnswerStrategyContant.DOCUMENT_LIBRARY)
@Slf4j
public class DocumentStrategy implements AnswerStrategy {

    @Autowired
    private FileDataMapper fileDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private FileService fileService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        String answer = StringConstant.BLANK;

        answer = fromDocumentLibrary(dto, contextList);
        log.info("===>documentStrategy:{}", answer);
        answerData.setAnswer(answer);
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        long start = System.currentTimeMillis();
        StepEntity step = new StepEntity();
        step.setCreateTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        KnowledgeContent content = new KnowledgeContent();
        content.setModule(AnswerStrategyContant.DOCUMENT_LIBRARY);
        // 获取应用
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        // 判断是否开启问答库
        if (!YesNoEnum.YES.getName().equals(applicationInfo.getKnowledgeFlag())) {
            return content;
        }
        step.setStep(AnswerStrategyContant.DOCUMENT_STEP);
        contextList.add(step);
        List<FileData> fileDataList = getFileDataList(dto, step);
        // 内容[知识库id|文件id|评分]
        List<String> collect = fileDataList.stream().map(p -> p.getContent() + AnswerStrategyContant.PREFIX_SYMBOL +
                p.getKnowledgeId() + AnswerStrategyContant.MID_ONE_SYMBOL + p.getFileId() + AnswerStrategyContant.MID_ONE_SYMBOL +
                p.getScore() + AnswerStrategyContant.SUFFIX_SYMBOL).distinct().collect(Collectors.toList());
        content.setContentList(collect);
        step.setResult(collect);
        step.setEndTime(LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DatePattern.NORM_DATETIME_PATTERN));
        step.setCostTime(System.currentTimeMillis() - start);
        return content;
    }

    /**
     * 从文档库获取答案
     *
     * @param dto
     * @param contextList
     * @return
     */
    private String fromDocumentLibrary(DialogueByStreamParam dto, List<StepEntity> contextList) {
        StepEntity step = new StepEntity();
        contextList.add(step);
        List<FileData> vectorLibrary = getFileDataList(dto, step);

        if (CollectionUtils.isNotEmpty(vectorLibrary)) {
            // 获取评分top数据
            if (CollectionUtils.isNotEmpty(vectorLibrary)) {
                vectorLibrary = getTopDocumentLibrary(dto.getContentTemp(), vectorLibrary);
            }

            step.setResult(vectorLibrary);

            StepEntity modelStep = new StepEntity();
            contextList.add(modelStep);
            modelStep.setStep("文档库-大模型回答");

            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isEmpty(vectorLibrary)) {
                return StringConstant.BLANK;
            }

            // 获取评分top数据，封装知识库
            for (int i = 0; i < vectorLibrary.size(); i++) {
                builder.append("\n<<< 【知识库").append(i + 1).append("】是： ").append(vectorLibrary.get(i).getContent()).append(" >>> ");
            }

            // 构建prompt
            String according = AnswerUtils.buildPrompt(builder, dto.getContentTemp(), vectorLibrary.size());

            String conversationName = answerUtils.getGenerateCommon(according, StringConstant.BLANK, modelStep, dto.getListMsg(), StringConstant.BLANK);
            log.info("[文档库调用大模型] 生成会话名称完成 conversationName = {}", conversationName);
            conversationName = answerUtils.analyticAnswer(conversationName);
            if (StringUtils.isBlank(conversationName)) {
                return StringConstant.BLANK;
            }
            return conversationName;
        }
        return StringConstant.BLANK;
    }

    /**
     * 从文档库获取原始数据
     *
     * @param dto
     * @param step
     * @return
     */
    private List<FileData> getFileDataList(DialogueByStreamParam dto, StepEntity step) {
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
            List<CompletableFuture<List<FileData>>> futures = Lists.newArrayList();
            List<String> querys = new ArrayList<>(queryList);
            knowledgeVectorMap.forEach((vector, knowledgeInfos) -> {
                for (String query : querys) {
                    DialogueByStreamParam bean = BeanUtil.toBean(dto, DialogueByStreamParam.class);
                    bean.setContentTemp(query);
                    CompletableFuture<List<FileData>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
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

    /**
     * 获取评分top数据
     *
     * @param content
     * @param vectorLibrary
     * @return
     */
    private List<FileData> getTopDocumentLibrary(String content, List<FileData> vectorLibrary) {
        List<FileData> toYayiList = Lists.newArrayList();
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        RearrangeParam rearrangeParam = new RearrangeParam();
        // 调用雅意重排能力
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(content);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (FileData fileData : vectorLibrary) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            articles.setPara(fileData.getContent());
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

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非文档库的结果
        if (null == step || !step.getStep().equals(DOCUMENT_LIBRARY)) {
            return dataList;
        }
        if (null == step.getResult()) {
            return dataList;
        }
        List<String> list = (List<String>) step.getResult();
        return documentRef(list);
    }

    /**
     * 文档库 格式：内容[知识库id|文件id|评分]
     *
     * @param list
     * @return
     */
    private List<SourceAnswerResult> documentRef(List<String> list) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        List<SourceAnswerResult> refResultList = list.stream().map(p -> {
            String[] split1 = p.split(REARRANGE_SPLIT_CHAR);
            String text = split1[split1.length - 1];
            SourceAnswerResult sourceAnswerResult = new SourceAnswerResult();
            sourceAnswerResult.setKnowledgeName(StringConstant.BLANK);
            // 解析：内容[知识库id|文件id|评分]
            int indexOf = p.indexOf(PREFIX_SYMBOL);
            String substringed = p.substring(indexOf + PREFIX_SYMBOL.length(), p.length() - PREFIX_SYMBOL.length());
            String[] split = substringed.split(MID_ONE_SYMBOL);
            String knowledgeId = split[0];
            String fileId = split[1];
            String scoreStr = split[2];
            String knowledgeName = StringConstant.BLANK;
            // 如果有文件id
            if (StringUtils.isNotBlank(fileId)) {
                Wrappers wrappers = Wrappers.init()
                        .where(FILE.FILE_ID.eq(fileId))
                        .limit(1);
                File file = fileService.getOne(wrappers);
                // 文件有可能被删除
                if (null == file) {
                    file = new File();
                }
                if (StringUtils.isNotBlank(knowledgeId)) {
                    KnowledgeInfo knowledgeInfo = knowledgeInfoService.getById(knowledgeId);
                    if (null != knowledgeInfo) {
                        knowledgeName = knowledgeInfo.getKnowledgeName();
                    }
                }
                sourceAnswerResult.setRoute(ListUtil.toList(knowledgeName, DOCUMENT_ROUTE, file.getFileName()));

                if (StringUtils.isNotBlank(scoreStr)) {
                    float score = Float.parseFloat(scoreStr);
                    sourceAnswerResult.setScore(score);
                }

                sourceAnswerResult.setFileLink(file.getFileUrl());
                String fileName = file.getFileName();
                if (StringUtils.isNotBlank(fileName)) {
                    sourceAnswerResult.setFileName(file.getFileName().substring(0, fileName.lastIndexOf(".")));
                }
                sourceAnswerResult.setSourceType(1);
                sourceAnswerResult.setTransPdfUrl(file.getTransPdfUrl());
                sourceAnswerResult.setDocLinkType(DocLinkTypeEnum.SOURCE_LINK.getCode());
                // 获取该文件各个语言版本的链接
                List<FileLanguage> languageFile = fileService.getLanguageFile(fileId);
                sourceAnswerResult.setTranslationUrlMap(languageFile.stream().collect(Collectors.toMap(FileLanguage::getLanguage, FileLanguage::getFileUrl)));
//                List<FileLanguage> languageFile = languageFile.stream().filter(fileLanguage -> StringUtils.equals(fileLanguage.getLanguage(), LanguageEnum.ZH.getName())).collect(Collectors.toList());
                sourceAnswerResult.setFileLanguageList(languageFile);
            } else {
                sourceAnswerResult.setRoute(ListUtil.toList(knowledgeName, DOCUMENT_ROUTE));
            }
            sourceAnswerResult.setKnowledgeName(knowledgeName);
            if (StringUtils.isNotBlank(text)) {
                sourceAnswerResult.setText(text.split(PREFIX_SYMBOL)[0]);
            }
            return sourceAnswerResult;
        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(refResultList)) {
            Map<String, SourceAnswerResult> collect = refResultList.stream()
                    .filter(p -> null != p && StringUtils.isNotBlank(p.getFileName()))
                    .collect(Collectors.groupingBy(SourceAnswerResult::getFileName, Collectors.collectingAndThen(Collectors.toList(), lists -> {
                        SourceAnswerResult sourceAnswerResult = lists.get(0);
                        String text = lists.stream().map(SourceAnswerResult::getText).collect(Collectors.joining("\n"));
                        sourceAnswerResult.setText(text);
                        return sourceAnswerResult;
                    })));
            refResultList = new ArrayList<>(collect.values());
        }
        return refResultList;
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
                .should(QueryBuilders.termsQuery("status.keyword", YesNoEnum.YES.getName()))
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
        // List<String> knowldegeIdList = DialogueServiceImpl.KNOWLEDGE_ID_LIST.get();

        JSONObject jsonObject = new JSONObject();
        List<Double> doubles = denseVectorService.modelEncode(dto.getContentTemp(), knowldegeIdList.get(0));

        String contentDense = CONTENT_DENSE_FILED;
        if (doubles.size() == 1024) {
            contentDense = CONTENT_DENSE_FILED_1024;
        }
        // List<Double> doubles = knowledgeDataService.modelEncode(dto.getContentTemp());
        jsonObject.put("queryVector", doubles);
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
        List<FileData> fileDataList = getFileDataList(dto, step);
        return fileDataList.stream().map(p -> {
            ScoreDataResult result = new ScoreDataResult();
            result.setContent(p.getKnowledgeId() + " | " + p.getContent());
            result.setEsScore(p.getScore());
            return result;
        }).collect(Collectors.toList());
    }

    private List<FileData> getDataByKnowledgeId(DialogueByStreamParam dto, StepEntity step, List<String> knowldegeIdList) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        if (null == applicationInfo) {
            log.info("DocumentStrategy applicationInfo is null");
            return Lists.newArrayList();
        }

        LambdaEsQueryWrapper<FileData> esQueryWrapper = EsWrappers.lambdaQuery(FileData.class);
        SearchSourceBuilder searchSourceBuilder = fileDataMapper.getSearchSourceBuilder(esQueryWrapper);
        // 获取搜索方式
        QueryBuilder queryByWay = getQueryByWay(dto, applicationInfo.getContentScore(), knowldegeIdList);

        SearchSourceBuilder query = searchSourceBuilder.query(queryByWay);
        query.fetchSource(null, new String[]{AnswerStrategyContant.TITLE_DENSE_FILED, AnswerStrategyContant.CONTENT_DENSE_FILED});
        esQueryWrapper.setSearchSourceBuilder(query);
        int prepareNum = answerUtils.buildPrepareNum(applicationInfo);
        query.size(prepareNum);
        query.from(0);
        List<FileData> vectorLibrary = fileDataMapper.selectList(esQueryWrapper);
        String source = fileDataMapper.getSource(esQueryWrapper);
        step.setPrompt(source);
//        step.setResult(vectorLibrary.size() + "条");
//         step.setResult(vectorLibrary);
        return vectorLibrary;
    }
}
