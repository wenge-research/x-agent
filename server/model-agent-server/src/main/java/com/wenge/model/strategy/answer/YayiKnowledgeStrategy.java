package com.wenge.model.strategy.answer;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.constants.KnowledgeConstant;
import com.wenge.model.dto.param.DialogueByStreamParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.result.AnswerStepData;
import com.wenge.model.dto.result.KnowledgeContent;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.dto.result.SourceAnswerResult;
import com.wenge.model.entity.*;
import com.wenge.model.enums.StepStatusEnum;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.model.utils.DateUtil;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.param.RearrangeParam;
import com.wg.appframe.yayi.result.KnowledgeResult;
import com.wg.appframe.yayi.result.RearrangeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.*;
import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;
import static com.wenge.oauth.constants.AppConfigContant.*;

@Service(AnswerStrategyContant.YAYI_KNOWLEGE)
@Slf4j
public class YayiKnowledgeStrategy implements AnswerStrategy {

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private AnswerUtils answerUtils;

    @Autowired
    private FileService fileService;

    @Autowired
    private KnowledgeInfoService knowledgeInfoService;

    @Autowired
    private FoldersService foldersService;


    @Override
    public AnswerStepData getAnswer(DialogueByStreamParam dto, Vector<StepEntity> contextList, AnswerStepData answerData) {
        String answer = StringConstant.BLANK;

        // 从雅意知识库获取答案
        answer = fromDocumentLibrary(dto, contextList, answerData);
        log.info("===>yayiKnowldegeStrategy:{}", answer);
        answerData.setAnswer(answer);
        if (StringUtils.isBlank(answer)) {
            answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
        }
        //List<String> resInfoList = getResInfoList(dto.getContentTemp());
        return answerData;
    }

    @Override
    public KnowledgeContent getContent(DialogueByStreamParam dto, Vector<StepEntity> contextList) {
        return new KnowledgeContent();
    }

    /**
     * 从文档库获取答案
     *
     * @param dto
     * @param contextList
     * @return
     */
    private String fromDocumentLibrary(DialogueByStreamParam dto, List<StepEntity> contextList, AnswerStepData answerData) {
        // 保存雅意知识库的原始数据
        StepEntity step = new StepEntity();
        step.setStep(YAYI_KNOWLEGE_STEP);
        step.setPrompt(dto.getContentTemp());
        contextList.add(step);
        // 从雅意知识库获取数据
        List<String> resInfoList = getResInfoList(dto.getContentTemp());
        List<String> list = ListUtil.toList(resInfoList);
        step.setResult(list);

        if (CollectionUtils.isNotEmpty(resInfoList)) {
            StepEntity rangeStep = new StepEntity();
            rangeStep.setStep(YAYI_KNOWLEGE_REARANGE);
            contextList.add(rangeStep);

            // 获取评分top数据
            if (CollectionUtils.isNotEmpty(resInfoList)) {
                Map<String, List<String>> topDocumentLibrary = getTopDocumentLibrary(dto.getContentTemp(), resInfoList, rangeStep);
                resInfoList = topDocumentLibrary.get(COLLECCT_REARRANGE_RESULT);
            }

            StepEntity modelStep = new StepEntity();
            contextList.add(modelStep);
            modelStep.setStep(YAYI_KNOWLEGE_LLM);

            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isEmpty(resInfoList)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                return StringConstant.BLANK;
            }

            // 获取评分top数据，封装知识库
            for (int i = 0; i < resInfoList.size(); i++) {
                String[] split = resInfoList.get(i).split(PREFIX_SYMBOL);
                builder.append("\n「【知识库").append(i + 1).append("】是： ").append(split[0]).append("」");
                list.add(RERANGE_TAG + resInfoList.get(i));
            }

            ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
            // 构建prompt
            String according = StringConstant.BLANK;
            String systemPrompt = buildSystemPrompt(builder, resInfoList.size());
            if (StringUtils.isNotBlank(systemPrompt)) {
                String userSystem = getConfiguration(applicationInfo.getApplicationId(), LLM_PROMPT_USER_FINAL_COLLECT_STRATEGY);
                String question = StringUtils.isNotBlank(dto.getNewQuestion()) ? dto.getNewQuestion() : dto.getContentTemp();
                according = userSystem.replace(QUESTION_PLACEHOLDER, question);
            } else {
                according = AnswerUtils.buildPrompt(builder, dto.getContentTemp(), resInfoList.size());
            }
            cn.hutool.json.JSONObject stepParam = new cn.hutool.json.JSONObject();
            // 大模型是否需要json格式化
            stepParam.set(LLM_JSON_FLAG, StringConstant.ONE);
            modelStep.setParam(stepParam);
            String conversationName = answerUtils.getGenerateCommon(according, StringConstant.BLANK, modelStep, dto.getListMsg(), result -> {
                try {
                    String answer = result.getAnswer();
                    answerData.setStatus(result.getStatus());
                    answer = answerUtils.analyticAnswer(answer);
                    if (StringUtils.isBlank(answer)) {
                        return;
                    }
                    if (StringUtils.isNotBlank(applicationInfo.getNotAnswer()) && applicationInfo.getNotAnswer().equals(answer)) {
                        answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                        return;
                    }
                    answerData.setAnswer(answer);
                    dto.getAnswerConsumer().accept(answerData);
                } catch (Exception e) {
                    e.printStackTrace();
                    answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
                }
            }, systemPrompt);
            log.info("[雅意知识库调用大模型] 生成会话名称完成 conversationName = {}", conversationName);
            if (conversationName.equals(AnswerStrategyContant.NO_ANSWER_TEXT)) {
                answerData.setStatus(StepStatusEnum.ANSWER_FAIL);
            }
            return conversationName;
        }
        return StringConstant.BLANK;
    }

    /**
     * 获取评分top数据
     *
     * @param content
     * @param vectorLibrary
     * @return
     */
    private Map<String, List<String>> getTopDocumentLibrary(String content, List<String> vectorLibrary, StepEntity rangeStep) {

        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        Map<String, List<String>> map = Maps.newHashMap();
        // 保存原数据
        List<String> toYayiList = Lists.newArrayList();
        map.put(COLLECCT_REARRANGE_RESULT, toYayiList);

        // 保存分数
        List<String> rearrangeScore = Lists.newArrayList();
        map.put(COLLECCT_REARRANGE_SCORE, rearrangeScore);
        RearrangeParam rearrangeParam = new RearrangeParam();
        // 调用雅意重排能力
        RearrangeParam.Content rangeContent = new RearrangeParam.Content();
        rangeContent.setQuery(content);
        List<RearrangeParam.Articles> articlesList = Lists.newArrayList();
        for (String contents : vectorLibrary) {
            RearrangeParam.Articles articles = new RearrangeParam.Articles();
            // 向量库没有title，这里固定给一个值
            articles.setTitle("11111");
            String[] split = contents.split(PREFIX_SYMBOL);
            articles.setPara(split[0]);
            articlesList.add(articles);
        }
        rangeContent.setArticles(articlesList);
        rearrangeParam.setContent(rangeContent);
        // 调用重排能力
        RearrangeResult rearrange = answerUtils.buildRearrangeModel(rearrangeParam, applicationInfo);
        rangeStep.setPrompt(rearrangeParam);
        rangeStep.setResult(rearrange);
        RearrangeResult.RearrangeData data = rearrange.getData();
        if (null != data) {
            // 获取重排后的索引
            List<Integer> indexList = data.getIndex_list();
            List<BigDecimal> resScoresList = data.getRes_scores_list();
            if (CollectionUtils.isNotEmpty(indexList)) {
                int size = Math.min(vectorLibrary.size(), applicationInfo.getFilterNum());
                size = Math.min(size, resScoresList.size());
                Float rangeContentScore = applicationInfo.getRangeContentScore();
                if (null == rangeContentScore) {
                    rangeContentScore = 0F;
                }
                for (int i = 0; i < size; i++) {
                    BigDecimal score = resScoresList.get(i);
                    if (score.compareTo(BigDecimal.valueOf(rangeContentScore)) >= 0) {
                        toYayiList.add(vectorLibrary.get(indexList.get(i)));
                        rearrangeScore.add(score.toPlainString());
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
        return map;
    }

    @Override
    public List<SourceAnswerResult> sourceAnswer(DialogueStep step, HashMap<String, DialogueStep> stepMap) {
        List<SourceAnswerResult> dataList = Lists.newArrayList();
        // 非雅意知识库的结果
        if (null == step || !step.getStep().equals(YAYI_KNOWLEGE_REF)) {
            return dataList;
        }
        // 获取原始数据
        DialogueStep primaryData = stepMap.get(YAYI_KNOWLEGE_STEP);
        if (null != primaryData) {
            Object refResult = primaryData.getResult();
            if (null == refResult) {
                return Lists.newArrayList();
            }
            // 解析原始数据
            List<String> knowledgeData = JSON.parseArray(refResult.toString(), String.class);
            knowledgeData = knowledgeData.stream().filter(p -> p.startsWith(RERANGE_TAG)).collect(Collectors.toList());
            // 提取文件id
            List<String> fileIdList = knowledgeData.stream().map(p -> {
                        // 内容@[文件id]@
                        String[] split = p.split(PREFIX_SYMBOL);
                        return split[1].replace(SUFFIX_SYMBOL, StringConstant.BLANK);
                    })
                    .filter(StringUtils::isNotBlank)
                    .distinct().collect(Collectors.toList());

            // 查询文件
            List<File> foldersByFiles = fileService.getFoldersByFileid(fileIdList);

            // 文件id映射为文件夹id
            Map<String, String> fileIdToFolderIdMap = foldersByFiles.stream().collect(Collectors.toMap(
                    File::getFileId,
                    File::getFoldersId,
                    (k1, k2) -> k1
            ));

            List<String> folderIdList = foldersByFiles.stream().map(File::getFoldersId).distinct().collect(Collectors.toList());

            // 查询文件夹
            List<Folders> folderList = foldersService.getKnowledgeIdsByFoldersId(folderIdList);
            // 获取知识库id
            List<String> knowledgeIdList = folderList.stream().map(Folders::getKnowledgeId).distinct().collect(Collectors.toList());

            // 文件夹id映射为知识库id
            Map<String, String> folderIdToKnowldegeIdMap = folderList.stream().collect(Collectors.toMap(
                    Folders::getFoldersId,
                    Folders::getKnowledgeId,
                    (k1, k2) -> k1
            ));

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
                SourceAnswerResult result = new SourceAnswerResult();
                String[] split = p.split(PREFIX_SYMBOL);
                //String knowledgeId = StringConstant.BLANK;
                String fileId = StringConstant.BLANK;
                if (split.length > 1) {
                    fileId = split[1].replace(SUFFIX_SYMBOL, StringConstant.BLANK);
                }
                String fileName = StringConstant.BLANK;
                String transPdfUrl = StringConstant.BLANK;
                String fileLink = StringConstant.BLANK;
                String knowledgeName = StringConstant.BLANK;
                if (StringUtils.isNotBlank(fileId)) {
                    Wrappers fileWrappers = Wrappers.init()
                            .where(FILE.FILE_ID.eq(fileId))
                            .limit(1);
                    File file = fileService.getOne(fileWrappers);
                    // 文件有可能被删除
                    if (null == file) {
                        file = new File();
                    }
                    fileName = file.getFileName();
                    transPdfUrl = file.getTransPdfUrl();
                    fileLink = file.getFileUrl();
                }
                //String knowledgeName = knowledgeMap.get(knowledgeId).getKnowledgeName();
                //result.setKnowledgeName(knowledgeName);
                String foldersId = fileIdToFolderIdMap.get(fileId);
                if (StringUtils.isNotBlank(foldersId)) {
                    String knowledgeId = folderIdToKnowldegeIdMap.get(foldersId);
                    if (StringUtils.isNotBlank(knowledgeId)) {
                        KnowledgeInfo knowledgeInfo = knowledgeMap.get(knowledgeId);
                        if (null != knowledgeInfo) {
                            knowledgeName = knowledgeInfo.getKnowledgeName();
                        }
                    }
                }

                result.setFileLink(fileLink);
                result.setSourceType(1);
                result.setFileName(fileName);
                result.setRoute(ListUtil.toList(YAYI_KNOWLEGE_ROUTE, knowledgeName, fileName));
                result.setKnowledgeName(knowledgeName);
                result.setText(split[0].replace(RERANGE_TAG, StringConstant.BLANK));
                result.setTransPdfUrl(transPdfUrl);
                return result;
            }).collect(Collectors.toList());
            //dataList.addAll(collectRef);
        }
        return dataList;
    }

    /**
     * 从雅意知识库获取数据
     * @param question
     * @return
     */
    private List<String> getResInfoList(String question) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();

        // 获取应用绑定的知识库
        List<KnowledgeInfo> knowledgeInfoList = knowledgeInfoService.effectiveKnowledge(applicationInfo.getApplicationId());
        if (CollectionUtils.isEmpty(knowledgeInfoList)) {
            return Lists.newArrayList();
        }

        // 获取知识库id
        List<String> knowledgeIdList = knowledgeInfoList.stream().map(KnowledgeInfo::getKnowledgeId).distinct().collect(Collectors.toList());

        // 创建线程池
        ThreadPoolExecutor pool = ExecutorBuilder.create().setCorePoolSize(8).setMaxPoolSize(8).setWorkQueue(new LinkedBlockingDeque<>()).build();

        Vector<String> knowledgeResultList = new Vector<>();
        knowledgeIdList.forEach(knowledgeId -> {
            pool.execute(() -> {
                // 封装参数
                KnowledgeParam param = new KnowledgeParam();
                KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
                param.setContent(knowledgeContent);
                knowledgeContent.setFunction(KnowledgeConstant.FUNCTION_DOC_CHAT);
                knowledgeContent.setRobot_id(knowledgeId);
                knowledgeContent.setUser_message(question);
                knowledgeContent.setPrompt_max_tokens(3200);
                param.setModel(KnowledgeParam.KNOWLEDGE_CONFIG);
                param.setReadTimeout(30);
                param.setConnectTimeout(30);
                // 调用算法接口
                KnowledgeResult knowledge = yayiServer.knowledge(param);
                KnowledgeResult.DocumentData data = knowledge.getData();
                if (null != data) {
                    List<KnowledgeResult.ResInfo> resInfo = data.getRes_info();
                    if (CollectionUtils.isNotEmpty(resInfo)) {
                        List<String> contentList = resInfo.stream().map(res -> {
                            KnowledgeResult.FilePagePara filePagePara = res.getFile_page_para();
                            String fileId = filePagePara.getFile_id();
                            String context = res.getContext();
                            // 内容@[文件id]@
                            return context + PREFIX_SYMBOL + fileId + SUFFIX_SYMBOL;
                        }).collect(Collectors.toList());
                        knowledgeResultList.addAll(contentList);
                    }

                }
            });
        });

        while (pool.getActiveCount() != 0 && !pool.isShutdown() && !pool.isTerminated()) {
            ThreadUtil.sleep(500);
        }
        pool.shutdownNow();
        if (CollectionUtils.isEmpty(knowledgeResultList)) {
            return Lists.newArrayList();
        }
        return knowledgeResultList;
    }

    /**
     * 提供检索原文依据
     * @param dialogueId
     * @param contextList
     * @return
     */
    public StepEntity answerRef(String dialogueId, Vector<StepEntity> contextList) {
        return AnswerUtils.answerRef(dialogueId, contextList, YAYI_KNOWLEGE, YAYI_KNOWLEGE_STEP, YAYI_KNOWLEGE_REF);
    };


    @Override
    public List<ScoreDataResult> getScoreData(ScoreDataParam scoreDataParam) {
        DialogueByStreamParam dto = new DialogueByStreamParam();
        dto.setContentTemp(scoreDataParam.getQuestion());
        dto.setClientId(scoreDataParam.getClientId());
        // 从雅意知识库获取数据
        List<String> resInfoList = getResInfoList(dto.getContentTemp());
        StepEntity rangeStep = new StepEntity();
        // 重排评分
        Map<String, List<String>> topDocumentLibrary = getTopDocumentLibrary(dto.getContentTemp(), resInfoList, rangeStep);
        resInfoList = topDocumentLibrary.get(COLLECCT_REARRANGE_RESULT);
        List<String> rearrangeScoreList = topDocumentLibrary.get(COLLECCT_REARRANGE_SCORE);

        int index = 0;
        List<ScoreDataResult> results = new ArrayList<>();
        for (String content : resInfoList) {
            ScoreDataResult result = new ScoreDataResult();
            result.setContent(content);
            result.setRearrangeScore(new BigDecimal(rearrangeScoreList.get(index++)));
            results.add(result);
        }

        return results;
    }

    /**
     * 构建system prompt
     *
     * @param builder
     * @param size
     * @return
     */
    public String buildSystemPrompt(StringBuilder builder, int size) {
        ApplicationInfo applicationInfo = DialogueServiceImpl.APPLICATION_INFO.get();
        String promptSystem = getConfiguration(applicationInfo.getApplicationId(), LLM_PROMPT_SYSTEM_FINAL_COLLECT_STRATEGY);
        if (StringUtils.isNotBlank(promptSystem)) {
            String dateForPrompt = DateUtil.getDateForPrompt();
            String notAnswer = applicationInfo.getNotAnswer();
            if (StringUtils.isBlank(notAnswer)) {
                notAnswer = getConfiguration(LLM_NO_ANSWER_DEFAULT_TEXT);
            }
            String according = builder.toString();
            according = according.replace("</br>", "\n").replace("<br>", "\n");
            promptSystem = promptSystem.replace(SIZE_PLACEHOLDER, size + "")
                    .replace(CONTENT_PLACEHOLDER, according)
                    .replace(DATE_PLACEHOLDER, dateForPrompt)
                    .replace(NO_ANSWER_PLACEHOLDER, notAnswer);
            return promptSystem;
        }
        return StringConstant.BLANK;
    }
}
