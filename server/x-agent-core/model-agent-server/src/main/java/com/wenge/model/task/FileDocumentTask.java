package com.wenge.model.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.constants.DocumentAnalysisConstant;
import com.wenge.model.constants.FileDataTypeConstants;
import com.wenge.model.constants.RedisKey;
import com.wenge.model.entity.File;
import com.wenge.model.entity.FileData;
import com.wenge.model.entity.FileLanguage;
import com.wenge.model.entity.Folders;
import com.wenge.model.enums.*;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.DenseVectorService;
import com.wenge.model.service.FileLanguageService;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.strategy.documentAnalysis.DocumentAnalysisStrategy;
import com.wenge.model.strategy.file.FileStrategy;
import com.wenge.model.strategy.file.FileStrategyFactory;
import com.wenge.model.utils.MyMultipartFileVo;
import com.wenge.model.utils.MyWordUtil;
import com.wenge.model.utils.WordBean;
import com.wenge.model.workflow.component.file.FileTransferMethod;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.result.DocumentResult;
import com.wg.appframe.yayi.result.KnowledgeResult;
import com.wg.appframe.yayi.result.YayiTranslationResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.constants.DocumentAnalysisConstant.DOCUMENT_ANALYSIS_SERVER_POLICY_ALIYUN;
import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

/**
 * 文档解析任务
 */
@RestController
@RequestMapping("/agentTask/document")
@Slf4j
public class FileDocumentTask {

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private FileService fileService;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private FileDataMapper fileDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private WosUtil wosUtil;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Value("${task.document.enable}")
    private boolean enable;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Map<String, DocumentAnalysisStrategy> analysisStrategyMap;

    @Autowired
    private FileLanguageService fileLanguageService;

    @Value("${task.document.pdfParseUrl:http://172.16.10.69:3009/analysis}")
    private String pdfParseUrl;

    @Value("${thinkTank.knowledgeId:a027f1d8e7aa4a0089e4c251714fce48}")
    private String thinkTankId;

    @PostConstruct
    public void init() {

        String value = redisService.get(RedisKey.PARSE_DOC, String.class);
        String localhostStr = NetUtil.getLocalhostStr();

        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisKey.PARSE_DOC);
            redisService.unlock(RedisConstant.LOCK_PARSE_DOC);
        }
    }

    public List<List<FileData>> parseFile(List<File> fileList) {
        // 获取所有文件夹
        Map<String, Folders> folderMap = getFoldersList();

        // 结果
        List<List<FileData>> result = Lists.newArrayList();

        long start01 = System.currentTimeMillis();
        log.info("==>解析文档定时器 解析逻辑执行parseFile start==== 时间毫秒： {}", (start01));

        DefaultHandleFile defaultHandleFile = new DefaultHandleFile(result, folderMap);
        YuanShuHandleFile yuanShuHandleFile = new YuanShuHandleFile(result, folderMap);
        // 遍历文件
        fileList.forEach(file -> {
            // 目前根据知识库id判断使用哪个解析策略
            String knowledgeId = file.getKnowledgeId();
            if (StringUtils.equals(knowledgeId, thinkTankId)) {
                yuanShuHandleFile.accept(file);
            } else {
                defaultHandleFile.accept(file);
            }
        });
        long start02 = System.currentTimeMillis();
        log.info("==>解析文档定时器 解析逻辑执行parseFile end==== 耗时毫秒： {}", (start02 - start01));
        return result;
    }

    private List<DocumentResult.FileContent> htmlAnalysis(File file) throws IOException {
        List<DocumentResult.FileContent> result = Lists.newArrayList();
        String content = HttpUtil.get(file.getFileUrl());
        if (StringUtils.isNotBlank(content) ) {
            // 获取数据

            Document parse = Jsoup.parse(content);

            String titleStr = parse.select("title").text();
            if (StringUtils.isBlank(titleStr)) {
                titleStr = file.getFileName();
            }

            Element contentDiv = parse.selectFirst("#content");
            String fileName = titleStr + ".docx";
            XWPFDocument document = new XWPFDocument();
            List<WordBean> wordBeanList = Lists.newArrayList();
            WordBean title = new WordBean();
            title.setType(ParagraphEnum.TITLE);
            title.setTitleLevel(HeadStyleEnum.H_1);
            title.setText(titleStr);
            wordBeanList.add(title);

            // 添加正文段落
            // 添加空值判断（关键修改）
            if (contentDiv != null) {
                contentDiv.select("h1, h2, p").forEach(element -> {
                    WordBean textBean = new WordBean();
                    textBean.setType(ParagraphEnum.TEXT);
                    textBean.setText(element.text());
                    wordBeanList.add(textBean);
                });
            }else {
                document.close();
                return Collections.emptyList();
            }
            MyWordUtil myWordUtil = new MyWordUtil(document);
            myWordUtil.createWord(wordBeanList);

            java.io.File wordFile = new java.io.File(fileName);
            // 保存文档到文件
            FileOutputStream out = null;
            FileInputStream fis = null;
            try {
                out = new FileOutputStream(wordFile);
                fis = new FileInputStream(wordFile);

                document.write(out);
                MultipartFile multipartFile = new MyMultipartFileVo(fileName, fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), fis);

                MinioInfoResult upload = wosUtil.upload(bucket, "html", multipartFile, true);

                file.setFileUrl(upload.getUrlPath());

                result = documentAnalysis(file);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (document != null) {
                    try {
                        document.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                wordFile.delete();
            }


        }

        return result;

    }

    /**
     * yayi解析单个文件
     *
     * @param file
     * @return 文件名 -> 解析后的内容
     */
    public List<FileData> parseOneFile(File file) {
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .notSelect(FileData::getContentDense)
                .eq(StringUtils.isNotBlank(file.getFileId()), FileData::getFileId, file.getFileId());
        // 查一下es，如果已经解析就跳过解析
        List<FileData> fileDataList = fileDataMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(fileDataList)) {
            List<List<FileData>> lists = parseFile(Collections.singletonList(file));
            if (CollectionUtil.isEmpty(lists)) {
                return Collections.emptyList();
            }
            fileDataList = lists.get(0);
        }
        Assert.notNull(fileDataList, "解析文件失败");
        return fileDataList;
    }

    /**
     * 手动解析指定文档
     */
    @PostMapping("/documentAnalysisById")
    public Result documentAnalysisById(@RequestParam("id") Long id) {
        Wrappers wrappers = Wrappers.init()
                .select(FILE.ALL_COLUMNS, KNOWLEDGE_INFO.KNOWLEDGE_ID, KNOWLEDGE_INFO.DOCUMENT_ANALYSIS_SERVER)
                .from(FILE)
                .innerJoin(FOLDERS).on(FILE.FOLDERS_ID.eq(FOLDERS.FOLDERS_ID))
                .innerJoin(KNOWLEDGE_INFO).on(FOLDERS.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
                .and(FILE.ID.eq(id))
                .and(FILE.TYPE.eq(FileTypeEnum.FILE.getType()))
                .orderBy(FILE.CREATE_TIME.asc())
                .limit(1);
        File file = fileService.getOne(wrappers);
        if (file != null) {
            CompletableFuture.runAsync(() -> parseOneFile(file));
        }
        return Result.success();
    }

    /**
     * 解密文件，并将解密后的文件覆盖原文件，如果解密失败，返回false跳过本次执行
     */
    private boolean decrypt(File file) throws Exception {
        URL url = new URL(file.getFileUrl());
        String path = url.getPath();
        InputStream input = wosUtil.getInput(bucket, path.substring(path.lastIndexOf('/') + 1));
        // 获取对应文档类型的解密策略，如果没有对应的解密策略，则跳过
        FileStrategy strategy = FileStrategyFactory.createStrategy(input, file.getFileType());
        if (strategy == null) {
            return true;
        }
        // 解密并覆盖原文件
        InputStream decrypt = null;
        try {
            decrypt = strategy.decrypt(file.getPassword());
        } catch (BadPasswordException e) {
            // 解密失败，则更新状态
            updateDecryptStatus(file, "解密失败:" + e.getMessage());
            return true;
        }

        wosUtil.delete(bucket, file.getFileUrl().substring(file.getFileUrl().lastIndexOf('/') + 1));
        MinioInfoResult upload = wosUtil.upload(bucket, "document", decrypt, System.currentTimeMillis() + "." + file.getFileType(), true);
        // 这里不知道为什么返回的url是对不上的，但是fileStoreKey是对的，所以拼上去
        file.setFileUrl(upload.getUrlPath().substring(0, upload.getUrlPath().lastIndexOf('/')) + "/" + upload.getFileStoreKey());
        file.setOriginalUrl(upload.getMinioUrl());
        updateFileUrl(file);
        return false;
    }

    /**
     * 数据转化对象
     *
     * @param content
     * @param folderMap
     * @param file
     * @param format
     * @return
     */
    private FileData dataTransform(KnowledgeResult.TitleContent content, Map<String, Folders> folderMap, File file, String format) {
        String accountName = AppContextHolder.getAccountName();
        FileData fileData = new FileData();
        Folders folders = folderMap.get(file.getFoldersId());
        fileData.setKnowledgeId(folders.getKnowledgeId());
        fileData.setFoldersId(file.getFoldersId());
        fileData.setFileId(file.getFileId());
        fileData.setFileName(file.getFileName());
        fileData.setType(FileDataTypeConstants.FILE);
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
        fileData.setContent(fullContent);
        //fileData.setParaNum(content.getPara_num());
        //fileData.setPageNum(content.getInfo().getPage_num());
        denseVectorService.modelEncode(fileData.getContent(), folders.getKnowledgeId(), fileData, CONTENT_DENSE_FILED);
        //fileData.setContentDense(knowledgeDataService.modelEncode(fileData.getContent()));
        fileData.setUpdateTime(format);
        fileData.setUpdateUser(accountName);
        return fileData;
    }

    /**
     * 获取上传成功的文件，以便后续用于解析
     */
    private List<File> getFileList() {
        Wrappers wrappers = Wrappers.init()
                .select(FILE.ALL_COLUMNS, KNOWLEDGE_INFO.KNOWLEDGE_ID, KNOWLEDGE_INFO.DOCUMENT_ANALYSIS_SERVER)
                .from(FILE)
                .innerJoin(FOLDERS).on(FILE.FOLDERS_ID.eq(FOLDERS.FOLDERS_ID))
                .innerJoin(KNOWLEDGE_INFO).on(FOLDERS.KNOWLEDGE_ID.eq(KNOWLEDGE_INFO.KNOWLEDGE_ID))
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(StringConstant.BLANK))
                .and(FILE.STATUS.ne(FileStatusEnum.SUCCESS.getCode()))
                .and(FILE.STATUS.ne(FileStatusEnum.PARSING.getCode()))
                .and(FILE.TYPE.eq(FileTypeEnum.FILE.getType()))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.FOLDERS_ID.ne(StringConstant.BLANK))
                .and(FILE.ERROR_NUM.lt(5))
                .orderBy(FILE.CREATE_TIME.asc())
                .limit(100);
        List<File> list = fileService.list(wrappers);
        return list;
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

    /**
     * 更新状态，解析中
     */
    private void updateStatus(File file) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.PARSING.getCode())
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 清空该文件的所有es数据
     */
    private void clearEsData(File file) {
        if (StringUtils.isBlank(file.getFileId())) {
            return;
        }
        LambdaEsQueryWrapper<FileData> esQueryWrapper = EsWrappers.lambdaQuery(FileData.class)
                .eq(FileData::getFileId, file.getFileId());
        fileDataMapper.delete(esQueryWrapper);
    }

    /**
     * 文档转文本
     */
    public List<DocumentResult.FileContent> documentAnalysis(File file) {
        String documentAnalysisServer = file.getDocumentAnalysisServer();
        if (StringUtils.equals(documentAnalysisServer, DocumentAnalysisConstant.DOCUMENT_ANALYSIS_SERVER_YAYI) || StringUtils.isBlank(documentAnalysisServer)) {
            // 如果是yayi解析策略或解析策略为空，切换到新的yayi内容解析策略
            documentAnalysisServer = DocumentAnalysisConstant.DOCUMENT_CONTENT_ANALYSIS_SERVER_YAYI;
        }

        DocumentAnalysisStrategy documentAnalysisStrategy = analysisStrategyMap.get(documentAnalysisServer);
        if (null == documentAnalysisStrategy) {
            // 更新状态，失败
            updateStatusFail(file, "文档解析失败");
            return Lists.newArrayList();
        }
        return documentAnalysisStrategy.documentAnalysis(file);
    }

    /**
     * 更新状态，失败
     */
    private void updateStatusFail(File file, DocumentResult documentResult) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.FAIL.getCode())
                .set(FILE.ERROR_MSG, documentResult.getMsg())
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 文本内容重新排版
     */
    private List<KnowledgeResult.TitleContent> reformat(List<DocumentResult.FileContent> fileContents) {
        KnowledgeParam param = new KnowledgeParam();
        KnowledgeParam.Content knowledgeContent = new KnowledgeParam.Content();
        param.setModel(KnowledgeParam.KNOWLEDGE_SPLIT_CONFIG);
        knowledgeContent.setRobot_id(IdUtil.simpleUUID());
        knowledgeContent.setFunction("updateDocs");
        //KnowledgeParam.FileContent fileContent = new KnowledgeParam.FileContent();
        List<KnowledgeParam.FileContent> fileContentList = BeanUtil.copyToList(fileContents, KnowledgeParam.FileContent.class);
        fileContentList.forEach(item -> {
            KnowledgeParam.FileInfo info = item.getInfo();
            info.setFile_type("text");
            info.setFile_id(knowledgeContent.getRobot_id());
            info.setFile_name(knowledgeContent.getRobot_id());
            info.setPage_num(item.getPage_num());
            item.setInfo(info);
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
                    if (CollectionUtil.isEmpty(titleContent)) {
                        titleContent = chunkResult.getTable_content();
                    }
                    return titleContent;
                }
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 更新统计
     */
    private void updateStatusSuccess(File file, long total, int size) {
        UpdateChain.of(File.class)
                .set(FILE.WORD_COUNT, total)
                .set(FILE.STATUS, FileStatusEnum.SUCCESS.getCode())
                .set(FILE.PARAGRAPHS_NUM, size)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新状态，失败
     */
    public static void updateStatusFail(File file, String msg) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.FAIL.getCode())
                .set(FILE.ERROR_MSG, msg)
                .set(FILE.ERROR_NUM, file.getErrorNum() + 1)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新解密状态
     */
    private void updateDecryptStatus(File file, String msg) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.DECRYPT_FAIL.getCode())
                .set(FILE.ERROR_MSG, msg)
                .set(FILE.ERROR_NUM, file.getErrorNum() + 1)
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }

    /**
     * 更新上传的url
     */
    private void updateFileUrl(File file) {
        UpdateChain.of(File.class)
                .set(FILE.FILE_URL, file.getFileUrl())
                .set(FILE.ORIGINAL_URL, file.getOriginalUrl())
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
    }


    class DefaultHandleFile implements Consumer<File> {

        private final List<List<FileData>> result;
        private final Map<String, Folders> folderMap;

        public DefaultHandleFile(List<List<FileData>> result, Map<String, Folders> folderMap) {
            this.result = result;
            this.folderMap = folderMap;
        }

        @Override
        public void accept(File file) {
            try {
                // 更新状态，解析中
                updateStatus(file);
                log.info("==>解析文档定时器 解析解析 fileList 更新状态： {} , {}", file.getFileId(), file.getFileName());

                // 判断文件是否经过加密,如果是通过密码进行解密再解析
                if (StringUtils.isNotBlank(file.getPassword()) && decrypt(file)) {
                    return;
                }

                log.info("==>解析文档定时器 解析解析 fileList 文档转文本： {} , {}", file.getFileId(), file.getFileName());
                List<DocumentResult.FileContent> fileContents = null;
                if (StringUtils.equals(file.getFileType(), "html")) {
                    fileContents = htmlAnalysis(file);
                } else {
                    // 文档转文本
                    fileContents = documentAnalysis(file);
                }
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
                List<KnowledgeResult.TitleContent> reformat = null;
                if (DOCUMENT_ANALYSIS_SERVER_POLICY_ALIYUN.equals(file.getDocumentAnalysisServer())) {
                    // policy-aliyun 策略就无需在重新整理文档
                    reformat = fileContents.stream().map(p -> {
                        KnowledgeResult.TitleContent titleContent = new KnowledgeResult.TitleContent();
                        titleContent.setText(p.getText());
                        return titleContent;
                    }).collect(Collectors.toList());
                } else {
                    // 需要重新整理文档
                    reformat = reformat(fileContents);
                    if (CollectionUtil.isEmpty(reformat)) {
                        updateStatusFail(file, "段落重新排版异常");
                        return;
                    } else {
                        // 判断所有段落是否为空
                        boolean allMatch = reformat.stream().allMatch(p -> StringUtils.isBlank(p.getText()));
                        if (allMatch) {
                            updateStatusFail(file, "段落重新排版异常");
                            return;
                        }
                    }
                }

                if (null == reformat) {
                    updateStatusFail(file, "段落重新排版异常");
                    return;
                }

                // 清空该文件的所有es数据
                clearEsData(file);
                log.info("==>解析文档定时器 解析解析 fileList 清空文件es数据： {} , {}", file.getFileId(), file.getFileName());

                String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
                // 将文档的文本数据保存到es
                List<FileData> fileDataList = reformat.stream().map(content -> dataTransform(content, folderMap, file, format)).collect(Collectors.toList());
                log.info("==>解析文档定时器 解析解析  fileDataList 分片数量量：{}，{}，{}", fileDataList.size(), file.getFileId(), file.getFileName());
                int total = fileDataList.stream().mapToInt(fileData -> fileData.getContent().length()).sum();
                fileDataMapper.insertBatch(fileDataList);
                // 记录结果
                result.add(fileDataList);

                // 更新统计
                updateStatusSuccess(file, total, fileDataList.size());
                log.info("==>解析文档定时器 解析解析  fileDataList 更新统计：{}，{}，{}，{}", fileDataList.size(), file.getFileId(), file.getFileName(), total);
            } catch (Exception e) {
                log.error("解析文件失败:{},msg:{}", file.getFileId(), e.getMessage(), e);
                // 更新状态，失败
                updateStatusFail(file, e.getMessage());
            }
        }
    }

    class YuanShuHandleFile implements Consumer<File> {

        private List<List<FileData>> result;
        private Map<String, Folders> folderMap;

        public YuanShuHandleFile(List<List<FileData>> result, Map<String, Folders> folderMap) {
            this.result = result;
            this.folderMap = folderMap;
        }

        @Override
        public void accept(File file) {
            try {
                // 外文源文件链接
                String foreignUrl = file.getFileUrl();
                String foreignName = file.getFileName();
                // 解析出外文文本
                List<String> contents = FileTransferMethod.REMOTE_URL.getHandler().apply(foreignUrl);
                if (CollectionUtil.isEmpty(contents)) {
                    log.info("==>解析文档定时器 元枢外文源文件内容为空： {} , {}", file.getFileId(), file.getFileName());
                    return;
                }
                String content = String.join(StringConstant.BLANK, contents);
                // 1. 翻译为中文
                YayiTranslationResult translationResult = yayiServer.yayiTranslate(content, LanguageEnum.EN.getCode(), LanguageEnum.ZH.getCode());
                if (translationResult.getCode() != 200) {
                    log.info("==>解析文档定时器 元枢外文源文件翻译失败： {} , {}", file.getFileId(), file.getFileName());
                    return;
                }

                String translatedText = translationResult.getData().getTgt();
                // 处理下翻译文本
                translatedText = translatedText.replaceAll("。{2,}", "。");
                log.debug("==>解析文档定时器 元枢外文源文件翻译结果： {}", translatedText);
                // 2. 上传文档做解析
                InputStream inputStream = createDocxInputStream(translatedText);
                MinioInfoResult minioInfoResult = wosUtil.upload(bucket, "yuanshu", inputStream, file.getFileName() + ".docx", true);
                String urlPath = minioInfoResult.getUrlPath();
                File newFile = BeanUtil.copyProperties(file, File.class);
                newFile.setFileUrl(urlPath);
                // 3. 调用接口获取pdf中的图片链接
                String param = String.format("{\"id\": \"test\",\"content\":{\"mode\": 0,\"pdf_split_pagenum\": 99999,\"finetable\":1,\"formula\":1},\"url\":\"%s\"}", foreignUrl);
                log.info("==>解析文档定时器 获取文件中的pdf图片请求参数： {}", param);
                String response = HttpUtil.post(pdfParseUrl, param);
                log.info("==>解析文档定时器 获取文件中的pdf图片返回结果： {}", response);
                JSONObject jsonObject = JSONUtil.parseObj(response);
                JSONArray contentArray = (JSONArray) jsonObject.getByPath("data.file_content.content");
                String pictures = contentArray.stream()
                        .map(JSONUtil::parseObj)
                        .map(obj -> obj.getStr("url"))
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining("#"));
                // 保存原文fileLanguage
                FileLanguage originalFileLang = new FileLanguage();
                originalFileLang.setFileId(file.getFileId());
                originalFileLang.setLanguage(LanguageEnum.EN.getName());
                originalFileLang.setFileUrl(foreignUrl);
                originalFileLang.setPictures(pictures);
                fileLanguageService.save(originalFileLang);
                // 保存译文fileLanguage
                FileLanguage translatedFileLanguage = new FileLanguage();
                translatedFileLanguage.setFileId(file.getFileId());
                translatedFileLanguage.setLanguage(LanguageEnum.ZH.getName());
                translatedFileLanguage.setFileUrl(urlPath);
                translatedFileLanguage.setPictures(pictures);
                fileLanguageService.save(translatedFileLanguage);

                // 执行默认的解析逻辑
                new DefaultHandleFile(result, folderMap).accept(newFile);
            } catch (Exception e) {
                log.error("解析文件失败:{},msg:{}", file.getFileId(), e.getMessage(), e);
                // 更新状态，失败
                updateStatusFail(file, e.getMessage());
            }
        }

        private InputStream createDocxInputStream(String translatedText) {
            try (XWPFDocument document = new XWPFDocument()) {
                // 1. 将字符串按换行符分割为多行（处理\n）
                String[] lines = translatedText.split("\n");

                // 2. 逐行添加段落到文档
                for (String line : lines) {
                    XWPFParagraph paragraph = document.createParagraph();  // 创建新段落
                    XWPFRun run = paragraph.createRun();  // 段落中的文本运行（可设置样式）
                    run.setText(line);  // 设置文本内容
                    // 可选：设置字体样式（如字号、字体、颜色）
                    run.setFontSize(12);
                    run.setFontFamily("微软雅黑");  // 系统默认字体，无需额外嵌入
                }
                // 3. 将文档写入内存输出流
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                document.write(outputStream);

                // 4. 关闭文档（释放资源）
                document.close();

                // 5. 将输出流转换为输入流并返回
                return new ByteArrayInputStream(outputStream.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
