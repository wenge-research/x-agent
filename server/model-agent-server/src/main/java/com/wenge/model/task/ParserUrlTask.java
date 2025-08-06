package com.wenge.model.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.constants.TaskConstant;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wenge.model.entity.UrlParserInfo;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.mapper.UrlParserInfoMapper;
import com.wenge.model.mapper.es.KnowledgeUrlDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.FileUtils;
import com.wenge.model.vo.ParserUrlVo;
import com.wenge.oauth.dto.param.ApplicationConfigurationParam;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.config.CoreContextProvider;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.exception.WosException;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.Generate128KParam;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.KnowledgeResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.entity.table.UrlParserInfoTableDef.URL_PARSER_INFO;

/**
 * 提交任务，执行解析URL并且入库ES向量结果的任务
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParserUrlTask implements Callable<Boolean> {

    private KnowledgeUrlDataMapper knowledgeUrlDataMapper;

    private UrlParserInfoMapper urlParserInfoMapper;

    private String urlId;

    private YayiServer yayiServer;

    private String knowledgeId;

    private String pageUrl;

    private String parserUrl;

    private Integer errorNum;

    private String appKey;

    private String appSecret;

    private String bucket;

    private WosUtil wosUtil;

    private DenseVectorService denseVectorService;



    @Override
    public Boolean call() {
        String result = "";
        try {
            JSONObject param = new JSONObject();
            param.put("start_url", pageUrl);
            try {
                result = HttpUtil.post(parserUrl, JSON.toJSONString(param));
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("yayi解析url结果为**************{}", result);
            ParserUrlVo parserUrlVoResult = JSONObject.parseObject(result, ParserUrlVo.class);

            // 提取文本的标题
            String sourceContent = parserUrlVoResult.getContent();
            String extractTitlePrompt = getApplicationConfigurationByKeyInfo(TaskConstant.EXTRACT_TITLE_PROMPT);
            String extractTitleResult = StringConstant.BLANK;
            if (StringUtils.isNotBlank(extractTitlePrompt)) {
                Generate128KParam generateExtractTitle128KParam = generate128KLLMParam(9999);
                extractTitlePrompt = extractTitlePrompt + "【" + sourceContent + "】";
                extractTitleResult = yayiServer.generate128KStr(extractTitlePrompt, generateExtractTitle128KParam);
            }

            // 使用大模型清洗数据，这里采取分段清洗，将原始内容分为前1000字、中间内容、后1000字，只清洗前1000字和后1000字
            if (StringUtils.isBlank(sourceContent) || sourceContent.length() <= 2000) {
                String cleanContentPrompt = getApplicationConfigurationByKeyInfo(TaskConstant.CLEAN_CONTENT_PROMPT);
                // 内容长度2000以内，直接清洗
                if (StringUtils.isNotBlank(cleanContentPrompt)) {
                    Generate128KParam generateCleanContent128KParam = generate128KLLMParam(9999);
                    cleanContentPrompt = cleanContentPrompt + "【" + sourceContent + "】";
                    result = yayiServer.generate128KStr(cleanContentPrompt, generateCleanContent128KParam);
                }
            } else {
                // 内容长度大于2000，分段清洗
                String prefixContent = sourceContent.substring(0, 1000);
                String suffixContent = sourceContent.substring(sourceContent.length() - 1000);
                String middleContent = sourceContent.substring(1000, sourceContent.length() - 1000);
                String cleanPrefixContentPrompt = getApplicationConfigurationByKeyInfo(TaskConstant.CLEAN_PREFIX_CONTENT_PROMPT);
                String cleanSuffixContentPrompt = getApplicationConfigurationByKeyInfo(TaskConstant.CLEAN_SUFFIX_CONTENT_PROMPT);
                if (StringUtils.isNotBlank(cleanPrefixContentPrompt) && StringUtils.isNotBlank(cleanSuffixContentPrompt)) {
                    prefixContent = cleanPrefixContentPrompt + prefixContent;
                    suffixContent = cleanSuffixContentPrompt + suffixContent;
                    Map<String, String> contentMap = new HashMap<>();
                    cleanPrefixAndSuffixContent(prefixContent, suffixContent, contentMap);
                    result = contentMap.get(TaskConstant.CLEAN_PREFIX_CONTENT_RESULT) + middleContent + contentMap.get(TaskConstant.CLEAN_SUFFIX_CONTENT_RESULT);
                }
            }
            parserUrlVoResult.setContent(result);

            if (parserUrlVoResult.getStatus_code() != HttpStatus.HTTP_OK) {
                // 如果初始url解析失败，则此次任务视为失败
                updateStatusFail(String.format("url解析失败，statusCode:%d; errorMsg:%s", parserUrlVoResult.getStatus_code(), parserUrlVoResult.getMessage()));
                return false;
            } else {
                Map<String, ParserUrlVo> urlIdToParserUrlVoMap = new HashMap<>();
                // 原始url解析出的数据
                ParserUrlVo originParserUrlVo = new ParserUrlVo();
                originParserUrlVo.setUrl(parserUrlVoResult.getUrl());
                originParserUrlVo.setContent(parserUrlVoResult.getContent());
                originParserUrlVo.setTitle(StringUtils.isNotBlank(extractTitleResult) ? extractTitleResult : parserUrlVoResult.getTitle());
                urlIdToParserUrlVoMap.put(urlId, originParserUrlVo);
                // 原始url页面内的url解析出的内容
                List<ParserUrlVo> parserInsideUrlVoList = parserUrlVoResult.getSubUrl();
                if (CollectionUtil.isNotEmpty(parserInsideUrlVoList)) {
                    parserInsideUrlVoList = parserInsideUrlVoList.stream()
                            .filter(v -> StringUtils.isNotEmpty(v.getTitle()) && StringUtils.isNotEmpty(v.getContent())
                                    && Objects.equals(v.getStatus_code(), HttpStatus.HTTP_OK))
                            .collect(Collectors.toList());
                    // 对url内部解析出的url,生成key-value（urlId-parserUrlVo）
                    parserInsideUrlVoList.forEach(v->{
                        String insideUrlId = IdUtil.randomUUID();
                        urlIdToParserUrlVoMap.put(insideUrlId, v);
                    });
                }

                //结果
                List<UrlParserInfo> urlParserInfoListResult = new ArrayList<>();
                List<KnowledgeUrlData> knowledgeUrlDataListResult = new ArrayList<>();
                // 对所有url解析出的内容做yayi切片处理
                for (Map.Entry<String, ParserUrlVo> parserUrlVoEntry : urlIdToParserUrlVoMap.entrySet()) {
                    String parserUrlId = parserUrlVoEntry.getKey();
                    ParserUrlVo parserUrlVo = parserUrlVoEntry.getValue();
                    analysisContent(parserUrlId, parserUrlVo, urlParserInfoListResult, knowledgeUrlDataListResult);
                }

                // 保存数据
                if (CollectionUtil.isNotEmpty(urlParserInfoListResult)) {
                    urlParserInfoMapper.insertBatch(urlParserInfoListResult);
                }
                if (CollectionUtil.isNotEmpty(knowledgeUrlDataListResult)) {
                    knowledgeUrlDataMapper.insertBatch(knowledgeUrlDataListResult);
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateStatusFail(String.format("url转文本失败，api:%s; result:%s", parserUrl, result));
        return false;
    }

    /**
     * 分析内容
     * @param parserUrlId urlId
     * @param parserUrlVo url解析出的数据
     */
    private void analysisContent(String parserUrlId, ParserUrlVo parserUrlVo,  List<UrlParserInfo> urlParserInfoListResult,
                                 List<KnowledgeUrlData> knowledgeUrlDataListResult) throws WosException {
        String title = parserUrlVo.getTitle();
        String content = parserUrlVo.getContent();
        String currentUrl = parserUrlVo.getUrl();
        // 写入文件
        MultipartFile contentFile;
        try {
            contentFile = FileUtils.str2MultipartFile(content);
        } catch (IOException e) {
            log.info("url解析内容写入文件异常");
            throw new RuntimeException(e.getMessage());
        }
        MinioInfoResult contentUploadResult = wosUtil.upload(bucket, "parserUrl", contentFile, true);

        // 发给雅意，进行切片
        String resultUrl = contentUploadResult.getUrlPath();
        log.info("解析文件地址{}", resultUrl);
        String encode = URLUtil.encode(resultUrl);
        // DocumentResult documentResult = yayiServer.documentAnalysis(encode);
        ContentParsingNewVersionResult documentResult = yayiServer.contentParsingNewVersion(encode);
        if (null == documentResult) {
            updateStatusFail(String.format("雅意切片失败，具体失败原因如下，code:%d; errorMsg:%s", documentResult.getCode(), documentResult.getMsg()));
            return;
        }

        ContentParsingNewVersionResult.ContentParsingNewVersionData data = documentResult.getData();
        if (null == data) {
            updateStatusFail(String.format("雅意切片失败，具体失败原因如下，code:%d; errorMsg:%s", documentResult.getCode(), documentResult.getMsg()));
            return;
        }
        // DocumentResult.DocumentData data = documentResult.getData();
        // List<DocumentResult.FileContent> fileContents = data.getFile_content();
        ContentParsingNewVersionResult.FileContent fileContents = data.getFile_content();
        if (null == fileContents) {
            updateStatusFail(String.format("雅意切片失败，具体失败原因如下，code:%d; errorMsg:%s", documentResult.getCode(), documentResult.getMsg()));
            return;
        }

        List<ContentParsingNewVersionResult.ContentValue> fileContentLists = fileContents.getContent();
        if (CollectionUtil.isEmpty(fileContentLists)) {
            updateStatusFail(String.format("雅意切片失败，具体失败原因如下，code:%d; errorMsg:%s", documentResult.getCode(), documentResult.getMsg()));
            return;
        }
        // 删除minio上传的临时文件
        wosUtil.delete(bucket, resultUrl.substring(resultUrl.lastIndexOf('/') + 1));

        if (documentResult.getCode() != HttpStatus.HTTP_OK) {
            log.info("雅意切片失败");
            // 如果当前解析的url是初始的url，就更新失败状态
            if (StringUtils.equals(parserUrlId, urlId)) {
                updateStatusFail(String.format("雅意切片失败，具体失败原因如下，code:%d; errorMsg:%s", documentResult.getCode(), documentResult.getMsg()));
            }
            return;
        }

        // DocumentResult.DocumentData data = documentResult.getData();
        // List<DocumentResult.FileContent> fileContents = data.getFile_content();
        KnowledgeParam knowledgeParam = new KnowledgeParam();
        KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
        knowledgeParam.setModel(KnowledgeParam.KNOWLEDGE_SPLIT_CONFIG);
        knowledgeContent.setRobot_id(IdUtil.simpleUUID());
        knowledgeContent.setFunction("updateDocs");
        //KnowledgeParam.FileContent fileContent = new KnowledgeParam.FileContent();
        List<KnowledgeParam.FileContent> fileContentList = BeanUtil.copyToList(fileContentLists, KnowledgeParam.FileContent.class);
        fileContentList.forEach(item -> {
            KnowledgeParam.FileInfo fileInfo = new KnowledgeParam.FileInfo();
            fileInfo.setFile_id(knowledgeContent.getRobot_id());
            fileInfo.setFile_name(knowledgeContent.getRobot_id());
            fileInfo.setFile_type("txt");
            fileInfo.setPage_num(item.getPage_num());
            item.setInfo(fileInfo);
        });
        knowledgeContent.setFile_content(fileContentList);
        knowledgeParam.setContent(knowledgeContent);
        KnowledgeResult knowledge = yayiServer.knowledgeSplit(knowledgeParam);
        if (knowledge.getCode() != HttpStatus.HTTP_OK) {
            log.info("知识库分段失败");
            // 如果当前解析的url是初始的url，就更新失败状态
            if (StringUtils.equals(parserUrlId, urlId)) {
                updateStatusFail(String.format("知识库分段失败，具体失败原因如下，code:%d; errorMsg:%s", knowledge.getCode(), knowledge.getMsg()));
            }
            return;
        }
        String accountName = AppContextHolder.getAccountName();
        KnowledgeResult.DocumentData knowledgeData = knowledge.getData();
        List<KnowledgeResult.ChunkResult> chunkResultList = knowledgeData.getChunk_result();
        if (CollectionUtil.isNotEmpty(chunkResultList)) {
            List<KnowledgeResult.TitleContent> contentList = chunkResultList.get(0).getTitle_content();
            // 进入ES批量容器
            List<KnowledgeUrlData> knowledgeUrlDataList = new ArrayList<>();
            String time = DateUtil.format(new Date(), DateUtil.PATTERN_1);
            for (KnowledgeResult.TitleContent titleContent : contentList) {
                String text = titleContent.getText();
                String id = IdUtil.randomUUID();
                KnowledgeUrlData knowledgeUrlData = KnowledgeUrlData.builder()
                        .knowledgeId(knowledgeId)
                        .urlId(parserUrlId)
                        .content(text)
                        .enable(1)
                        .url(currentUrl)
                        .encodeType(1)
                        .createTime(time)
                        .updateTime(time)
                        .createUser(accountName)
                        .updateUser(accountName)
                        .category("原文切片")
                        .id(id)
                        .build();
                denseVectorService.modelEncode(text, knowledgeId, knowledgeUrlData, CONTENT_DENSE_FILED);

                knowledgeUrlDataList.add(knowledgeUrlData);
            }

            // 只有成功处理完成的url，更新url元数据状态为成功
            if (StringUtils.equals(parserUrlId, urlId)) {
                updateStatusSuccess(title, fileContentLists, content);
            } else {
                // 原始url内部解析出的url
                UrlParserInfo urlParserInfo = UrlParserInfo.builder()
                        .urlId(parserUrlId)
                        .deleteFlag(0)
                        .errorNum(0)
                        .knowledgeId(knowledgeId)
                        .status(FileStatusEnum.SUCCESS.getCode())
                        .pageUrl(currentUrl)
                        .subsectionRule(1)
                        .title(title)
                        .paragraphsNum(fileContentLists.size())
                        .wordCount(content.length())
                        .avgParagraphsCount(content.length() / fileContentLists.size())
                        .createTime(time)
                        .updateTime(time)
                        .build();
                urlParserInfoListResult.add(urlParserInfo);
            }

            knowledgeUrlDataListResult.addAll(knowledgeUrlDataList);
        }
    }

    /**
     * 分片插入
     */
    private void saveUrlParserInfo(List<UrlParserInfo> urlParserInfoList) {
        if (CollectionUtil.isEmpty(urlParserInfoList)) {
            return;
        }

        List<UrlParserInfo> toSaveList;
        long pageNo = 1;
        int limit = 200;
        while (true) {
            toSaveList = urlParserInfoList.stream().skip((pageNo - 1) * limit).limit(limit).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(toSaveList)) {
                break;
            }
            urlParserInfoMapper.insertBatch(toSaveList);
            pageNo++;
        }
    }

    /**
     * 更新解析状态为失败
     *
     * @param msg 失败信息
     */
    private void updateStatusFail(String msg) {
        UpdateChain.of(UrlParserInfo.class)
                .set(URL_PARSER_INFO.STATUS, FileStatusEnum.FAIL.getCode())
                .set(URL_PARSER_INFO.ERROR_MSG, msg)
                .set(URL_PARSER_INFO.ERROR_NUM, errorNum + 1)
                .where(URL_PARSER_INFO.URL_ID.eq(urlId))
                .update();
    }

    /**
     * 修改url元数据为成功状态
     * @param title 标题
     * @param fileContents 文档段落集合
     * @param content 文档内容
     */
    private void updateStatusSuccess(String title, List<ContentParsingNewVersionResult.ContentValue> fileContents, String content) {
        UpdateChain.of(UrlParserInfo.class)
                .set(URL_PARSER_INFO.SUBSECTION_RULE, 1)
                .set(URL_PARSER_INFO.STATUS, FileStatusEnum.SUCCESS.getCode())
                .set(URL_PARSER_INFO.TITLE, title)
                .set(URL_PARSER_INFO.PARAGRAPHS_NUM, fileContents.size())
                .set(URL_PARSER_INFO.WORD_COUNT, content.length())
                .set(URL_PARSER_INFO.AVG_PARAGRAPHS_COUNT, content.length() / fileContents.size())
                .set(URL_PARSER_INFO.ERROR_MSG, "")
                .where(URL_PARSER_INFO.URL_ID.eq(urlId))
                .update();
    }


    /**
     * 通过keyInfo获取应用配置信息
     * @param keyInfo
     * @return
     */
    private String getApplicationConfigurationByKeyInfo(String keyInfo) {
        ApplicationConfigurationService service = CoreContextProvider.getContext().getBean(ApplicationConfigurationService.class);
        ApplicationConfigurationParam param = new ApplicationConfigurationParam();
        param.setKeyword(keyInfo);
        param.setPageNo(1);
        param.setPageSize(1);
        Result<Page<ApplicationConfiguration>> pageResult = service.getApplicationConfigurationList(param);
        List<ApplicationConfiguration> records = pageResult.getData().getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return StringConstant.BLANK;
        }

        return records.get(0).getValueInfo();
    }

    /**
     * 设置yayi128K参数
     */
    private Generate128KParam generate128KLLMParam(Integer maxNewTokens) {
        Generate128KParam generate128KParam = new Generate128KParam();
        generate128KParam.setAppKey(appKey);
        generate128KParam.setAppSecret(appSecret);
        generate128KParam.setMax_new_tokens(maxNewTokens);

        return generate128KParam;
    }


    public void cleanPrefixAndSuffixContent(String prefixContent, String suffixContent, Map<String, String> map) {
        Generate128KParam generateCleanPrefixContent128KParam = generate128KLLMParam(9999);
        Generate128KParam generateCleanSuffixContent128KParam = generate128KLLMParam(9999);
        CompletableFuture<String> prefixFuture = CompletableFuture.supplyAsync(() ->
                yayiServer.generate128KStr(prefixContent, generateCleanPrefixContent128KParam)
        );
        // 异步生成
        CompletableFuture<String> suffixFuture = CompletableFuture.supplyAsync(() ->
                yayiServer.generate128KStr(suffixContent, generateCleanSuffixContent128KParam)
        );
        // 等待两个任务都完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(prefixFuture, suffixFuture);
        // 在所有任务完成后继续执行
        allFutures.thenRun(() -> {
            try {
                map.put(TaskConstant.CLEAN_PREFIX_CONTENT_RESULT, prefixFuture.get());
                map.put(TaskConstant.CLEAN_SUFFIX_CONTENT_RESULT, suffixFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();
    }

}
