package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.constants.MediaAnalysisConstant;
import com.wenge.model.dto.param.YouyaAddCatalogParam;
import com.wenge.model.dto.param.YouyaApiCallbackParam;
import com.wenge.model.dto.param.YouyaCallbackDataParam;
import com.wenge.model.dto.result.AddAnalysisDataResult;
import com.wenge.model.dto.result.AddCatalogResult;
import com.wenge.model.dto.result.AnalysisDataResults;
import com.wenge.model.dto.result.AudioAndVideoAnalysisResult;
import com.wenge.model.entity.*;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.mapper.YouyaAnalysisRelationMapper;
import com.wenge.model.mapper.YouyaAnalysisResultsMapper;
import com.wenge.model.mapper.YouyaDataEntriesMapper;
import com.wenge.model.mapper.es.MultiMediaDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.service.YouyaApiService;
import com.wenge.model.utils.DateUtil;
import com.wenge.model.utils.YouyaUtils;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.Generate30BResult;
import com.wg.appframe.yayi.result.KnowledgeResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.entity.table.FileTableDef.FILE;


@Service
@Slf4j
public class YouyaApiServiceImpl implements YouyaApiService {

    @Autowired
    private YouyaDataEntriesMapper youyaDataEntriesMapper;

    @Autowired
    private YouyaAnalysisResultsMapper youyaAnalysisResultsMapper;

    @Autowired
    private YouyaAnalysisRelationMapper youyaAnalysisRelationMapper;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private MultiMediaDataMapper multiMediaDataMapper;

    @Autowired
    private FileService fileService;

    @Value("${third.audio.yayiRewrite:false}")
    private Boolean yayiEnable;

    @Autowired
    private YayiServer yayiServer;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private WosUtil wosUtil;

    @Autowired
    private DenseVectorService denseVectorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callback(YouyaApiCallbackParam youyaApiCallbackParam) {
        log.info("优雅回调函数: {}", JSONUtil.toJsonStr(youyaApiCallbackParam));
        YouyaAnalysisRelation youyaAnalysisRelation =
                youyaAnalysisRelationMapper.selectOneById(youyaApiCallbackParam.getId());

        YouyaAnalysisResults youyaAnalysisResults = youyaAnalysisResultsMapper
                .selectOneById(youyaAnalysisRelation.getRequestId());

        youyaAnalysisRelation.setStatus(1);
        youyaAnalysisRelation.setTime(youyaApiCallbackParam.getTime());
        youyaAnalysisRelation.setMsg(youyaApiCallbackParam.getMsg());
        youyaAnalysisRelation.setCode(youyaApiCallbackParam.getCode());

        List<YouyaDataEntries> dataEntriesList = new ArrayList<>();
        String content = "";
        for (YouyaCallbackDataParam callbackDataParam : youyaApiCallbackParam.getData()) {
            // video为空表示整段视频描述
            if (StringUtils.isBlank(callbackDataParam.getVideo())) {
                content = callbackDataParam.getCaption();
            }
            YouyaDataEntries dataEntries =  new YouyaDataEntries();
            BeanUtils.copyProperties(callbackDataParam,dataEntries);
            dataEntries.setStart(callbackDataParam.getStart());
            dataEntries.setLength(callbackDataParam.getLength());
            dataEntries.setAnalysisId(youyaAnalysisRelation.getAnalysisId());
            dataEntries.setVideoCover(callbackDataParam.getVideo_cover());
            dataEntries.setVideoPreview(callbackDataParam.getVideo_preview());
            dataEntries.setVideo(callbackDataParam.getVideo());
            dataEntriesList.add(dataEntries);
        }
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.where(" request_id = '" + youyaAnalysisRelation.getRequestId() + "'");
        queryWrapper.where(" id != '" + youyaAnalysisRelation.getAnalysisId() + "'");
        queryWrapper.where(" status = 0" );
        long count = youyaAnalysisRelationMapper.selectCountByQuery(queryWrapper);
        if (count <= 0) {
            youyaAnalysisResults.setStatus(1);
            youyaAnalysisResults.setEndTime(DateUtil.getCurrentTime());
            youyaAnalysisResultsMapper.update(youyaAnalysisResults);

            File file = fileService.queryByFileId(youyaAnalysisRelation.getRequestId());
            if (ObjectUtils.isNotEmpty(file)) {
                if (StringUtils.isNotBlank(content)) {
                    log.info("调用视频转文本接口转换视频文件后的完整文本为：{}", content);
                    // 雅意重写
                    if (yayiEnable) {
                        String yayiRewrite = yayiRewrite(content);
                        if (StrUtil.isNotBlank(yayiRewrite)) {
                            log.info("调用视频转文本接口转换视频文件后的完整文本经过雅意重写后的文本为：{}", yayiRewrite);
                            // 这里文件需要做一下转换，文本改为docx格式，交给雅意分片
                            try {
                                convert(file, content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // 文档转文本
                            List<ContentParsingNewVersionResult.ContentValue> fileContents = documentAnalysis(file);
                            if (CollectionUtil.isEmpty(fileContents)) {
                                updateStatusFail(file, "解析原文当数据为空");
                                return;
                            } else {
                                // 判断所有段落是否为空
                                boolean allMatch = fileContents.stream().allMatch(p -> StringUtils.isBlank(p.getText()));
                                if (allMatch) {
                                    updateStatusFail(file, "解析原文当数据为空");
                                }
                            }

                            // 调用知识库能力，将文档重新排列
                            List<KnowledgeResult.TitleContent> reformat = reformat(fileContents);
                            if (CollectionUtil.isEmpty(reformat)) {
                                updateStatusFail(file, "段落重新排版异常");
                                return;
                            } else {
                                // 判断所有段落是否为空
                                boolean allMatch = reformat.stream().allMatch(p -> StringUtils.isBlank(p.getText()));
                                if (allMatch) {
                                    updateStatusFail(file, "段落重新排版异常");
                                }
                            }
                            String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), com.wg.appframe.core.utils.DateUtil.DEFAULT_FORMAT);
                            // 将文档的文本数据保存到es
                            List<Folders> foldersList = foldersService.list();

                            HashMap<String, Folders> folderMap = foldersList.stream().collect(Collectors.toMap(
                                    Folders::getFoldersId,
                                    p -> p,
                                    (k1, k2) -> k1,
                                    Maps::newHashMap
                            ));
                            List<MultiMediaData> multiMediaDataList = reformat.stream().map(text -> dataTransform(text, folderMap, file, format)).collect(Collectors.toList());

                            multiMediaDataMapper.insertBatch(multiMediaDataList);
                            // 多媒体文件处理完成
                            updateStatusSuccess(file, multiMediaDataList.stream().mapToInt(data -> data.getContent().length()).sum(), multiMediaDataList.size());

                        }
                    }
                }
            }
        }
        youyaDataEntriesMapper.insertBatch(dataEntriesList);
        youyaAnalysisRelationMapper.update(youyaAnalysisRelation);
    }

    /**
     * 文本内容重新排版
     */
    private List<KnowledgeResult.TitleContent> reformat(List<ContentParsingNewVersionResult.ContentValue> fileContents) {
        KnowledgeParam param = new KnowledgeParam();
        KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
        param.setModel(KnowledgeParam.KNOWLEDGE_SPLIT_CONFIG);
        knowledgeContent.setRobot_id(IdUtil.simpleUUID());
        knowledgeContent.setFunction("updateDocs");
        //KnowledgeParam.FileContent fileContent = new KnowledgeParam.FileContent();
        List<KnowledgeParam.FileContent> fileContentList = BeanUtil.copyToList(fileContents, KnowledgeParam.FileContent.class);
        fileContentList.forEach(item->{
            KnowledgeParam.FileInfo fileInfo = new KnowledgeParam.FileInfo();
            fileInfo.setFile_id(knowledgeContent.getRobot_id());
            fileInfo.setFile_name(knowledgeContent.getRobot_id());
            fileInfo.setFile_type("txt");
            fileInfo.setPage_num(item.getPage_num());
            item.setInfo(fileInfo);
        });
        knowledgeContent.setFile_content(fileContentList);
        param.setContent(knowledgeContent);
        KnowledgeResult knowledge = yayiServer.knowledgeSplit(param);
        KnowledgeResult.DocumentData data = knowledge.getData();
        if (null != data) {
            List<KnowledgeResult.ChunkResult> chunkResultList = data.getChunk_result();
            if (CollectionUtil.isNotEmpty(chunkResultList)) {
                KnowledgeResult.ChunkResult chunkResult = chunkResultList.get(0);
                if (null != chunkResult) {
                    List<KnowledgeResult.TitleContent> titleContent = chunkResult.getTitle_content();
                    return titleContent;
                }
            }
        }
        return Lists.newArrayList();
    }


    /**
     * 数据转化对象
     * @param content
     * @param folderMap
     * @param file
     * @param format
     * @return
     */
    private MultiMediaData dataTransform(KnowledgeResult.TitleContent content, Map<String, Folders> folderMap, File file, String format) {
        String accountName = AppContextHolder.getAccountName();
        MultiMediaData multiMediaData = new MultiMediaData();
        Folders folders = folderMap.get(file.getFoldersId());
        multiMediaData.setKnowledgeId(folders.getKnowledgeId());
        multiMediaData.setFoldersId(file.getFoldersId());
        multiMediaData.setMultiMediaId(file.getFileId());
        multiMediaData.setMultiMediaName(file.getFileName());
        String text = content.getText();
        String[] split = text.split(" ");
        // 整理第一段，如果有标题就用标题，再判断如果文件名称有，就用文件名称
        if (file.getFileUrl().contains(split[0])) {
            split[0] = "";
            if (StringUtils.isNotBlank(content.getTitle())) {
                split[0] = content.getTitle();
            } else if (StringUtils.isNotBlank(file.getFileName())) {
                split[0] = file.getFileName().replaceAll("\\..*", "");
            }
        }
        String fullContent = String.join(" ", split);
        multiMediaData.setContent(fullContent);
        //multiMediaData.setParaNum(content.getPara_num());
        //multiMediaData.setPageNum(content.getInfo().getPage_num());
        //multiMediaData.setContentDense(knowledgeDataService.modelEncode(multiMediaData.getContent()));

        denseVectorService.modelEncode(multiMediaData.getContent(), folders.getKnowledgeId(), multiMediaData, CONTENT_DENSE_FILED);
        multiMediaData.setUpdateTime(format);
        multiMediaData.setUpdateUser(accountName);
        return multiMediaData;
    }


    /**
     * 文档转文本
     */
    private List<ContentParsingNewVersionResult.ContentValue> documentAnalysis(File file) {
        // 文件路径转码
        String encode = URLUtil.encode(file.getFileUrl());
        ContentParsingNewVersionResult documentResult = yayiServer.contentParsingNewVersion(encode);
        if (null == documentResult) {
            return Lists.newArrayList();
        }
        ContentParsingNewVersionResult.ContentParsingNewVersionData data = documentResult.getData();
        if (null == data) {
            return Lists.newArrayList();
        }
        // DocumentResult.DocumentData data = documentResult.getData();
        // List<DocumentResult.FileContent> fileContents = data.getFile_content();
        ContentParsingNewVersionResult.FileContent fileContents = data.getFile_content();
        if (null == fileContents) {
            return Lists.newArrayList();
        }
        List<ContentParsingNewVersionResult.ContentValue> content = fileContents.getContent();
        if (CollectionUtil.isEmpty(content)) {
            return Lists.newArrayList();
        }
        return content;
    }


    /**
     * 修改fileUrl
     * @param file
     * @param text
     * @throws IOException
     */
    private void convert(File file, String text) throws Exception {
        Path path = Paths.get("temp.docx");
        try (XWPFDocument document = new XWPFDocument()){
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText(text);
            BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path));
            document.write(outputStream);
            outputStream.close();
            InputStream inputStream = Files.newInputStream(path);
            MinioInfoResult upload = wosUtil.upload(bucket, "medium", inputStream, System.currentTimeMillis() + ".docx", true);
            file.setFileUrl(upload.getUrlPath().substring(0, upload.getUrlPath().lastIndexOf('/')) + "/" + upload.getFileStoreKey());
        } finally {
            Files.delete(path);
        }
    }

    /**
     * 调用雅意功能，修正语音识别出的文本可能产生的语法、语义错误
     */
    private String yayiRewrite(String transcription) {
        Generate30BResult result = yayiServer.generate30B("【"+transcription + "】 【】中的文本为视频识别后的文本，请检查上述文本是否有单词，语句错误的问题，如果存在修改为符合人类阅读习惯的语句，只输出修改后的文本。只输出修正后的文本格式，其他描述不要。");
        if (result.getCode() != 200) {
            return "";
        }
        Generate30BResult.GenerateData data = result.getData();
        return data.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 更新状态为文件初始状态
     */
    public void updateInitStatus(File file, Integer initStatus, String errorMsg) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, initStatus)
                .set(FILE.ERROR_MSG, errorMsg)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新统计
     */
    private void updateStatusSuccess(File file, long total, int size) {
        UpdateChain.of(File.class)
                .set(FILE.WORD_COUNT, total)
                .set(FILE.STATUS, FileStatusEnum.SUCCESS.getCode())
                .set(FILE.PARAGRAPHS_NUM, size)
                .set(FILE.ERROR_MSG, "")
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新状态，失败
     */
    private void updateStatusFail(File file, String msg) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.FAIL.getCode())
                .set(FILE.ERROR_MSG, msg)
                .set(FILE.ERROR_NUM, file.getErrorNum() + 1)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    @Override
    public AddAnalysisDataResult addCatalog(YouyaAddCatalogParam youyaApiParam) {
        AddCatalogResult addCatalogResult = YouyaUtils.addCatalog(youyaApiParam);
        // 如果存在文件id，则以文件id为准
        String requestId = youyaApiParam.getFileId();
        if (StringUtils.isBlank(requestId)) {
            requestId = IdUtil.simpleUUID();
        }
        List<YouyaAnalysisRelation> analysisRelationList = new ArrayList<>();
        //入库
        YouyaAnalysisResults youyaAnalysisResults = new YouyaAnalysisResults();
        if (addCatalogResult.getData() != null && addCatalogResult.getData().length >0) {

            youyaAnalysisResults.setRequestId(requestId);
            youyaAnalysisResults.setStatus(0);
            youyaAnalysisResults.setCreateTime(DateUtil.getCurrentTime());
            youyaAnalysisResults.setVideoUrl(youyaApiParam.getVideoUrl());

            for (String taskId : addCatalogResult.getData()) {
                YouyaAnalysisRelation youyaAnalysisRelation = new YouyaAnalysisRelation();
                youyaAnalysisRelation.setAnalysisId(taskId);
                youyaAnalysisRelation.setRequestId(requestId);
                youyaAnalysisRelation.setStatus(0);
                analysisRelationList.add(youyaAnalysisRelation);
            }
        }
        youyaAnalysisResultsMapper.insert(youyaAnalysisResults);
        youyaAnalysisRelationMapper.insertBatch(analysisRelationList);
        AddAnalysisDataResult analysisDataResults = new AddAnalysisDataResult();
        analysisDataResults.setRequestId(requestId);
        return analysisDataResults;
    }

    @Override
    public Result<AnalysisDataResults> queryVideoInfo(YouyaAddCatalogParam youyaApiParam) {
        AnalysisDataResults analysisDataResults = new AnalysisDataResults();

        YouyaAnalysisResults aalysisResults = youyaAnalysisResultsMapper.selectOneById(youyaApiParam.getRequestId());
        BeanUtils.copyProperties(aalysisResults,analysisDataResults);

        if (aalysisResults.getStatus() == 1) {
            QueryWrapper queryWrapper = QueryWrapper.create();
            queryWrapper.where(" request_id = '" + youyaApiParam.getRequestId() + "'");
            List<YouyaAnalysisRelation> analysisRelationList = youyaAnalysisRelationMapper.selectListByQuery(queryWrapper);
            List<String> analysisId = analysisRelationList.stream()
                    .map(YouyaAnalysisRelation::getAnalysisId)
                    .collect(Collectors.toList());
            QueryWrapper queryWrapper2 = QueryWrapper.create();
            queryWrapper2.where(" analysis_id in (" + buildPlaceholders(analysisId.size()) + ")",
                    analysisId.toArray());
            queryWrapper2.orderBy( "id desc" );
            List<YouyaDataEntries> dataEntriesList = youyaDataEntriesMapper.selectListByQuery(queryWrapper2);
            analysisDataResults.setDataEntriesList(dataEntriesList);
        }

        return Result.success(analysisDataResults);
    }

    private static String buildPlaceholders(int count) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                placeholders.append(", ");
            }
            placeholders.append("?");
        }
        return placeholders.toString();
    }

    /**
     * 获取所有文件夹
     */
    private Map<String, Folders> getFoldersList() {
        List<Folders> foldersList = foldersService.list();
        return foldersList.stream().collect(Collectors.toMap(
                Folders::getFoldersId,
                p -> p,
                (k1, k2) -> k1,
                Maps::newHashMap
        ));
    }

}
