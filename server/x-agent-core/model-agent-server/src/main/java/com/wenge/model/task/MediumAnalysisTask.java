package com.wenge.model.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.URLUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.core.update.UpdateChain;
import com.wenge.model.constants.MediaAnalysisConstant;
import com.wenge.model.dto.param.YouyaAddCatalogParam;
import com.wenge.model.dto.result.AddAnalysisDataResult;
import com.wenge.model.entity.File;
import com.wenge.model.entity.Folders;
import com.wenge.model.entity.MultiMediaData;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.mapper.es.MultiMediaDataMapper;
import com.wenge.model.service.*;
import com.wenge.oauth.constants.RedisConstant;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.redis.service.RedisService;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.param.KnowledgeParam;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.DocumentResult;
import com.wg.appframe.yayi.result.KnowledgeResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.wenge.model.constants.AnswerStrategyContant.CONTENT_DENSE_FILED;
import static com.wenge.model.entity.table.FileTableDef.FILE;

/**
 * 媒体解析
 */
@RestController
@Api(tags = "多媒体文件解析接口")
@RequestMapping("/agentTask/medium")
@Slf4j
public class MediumAnalysisTask {

    @Autowired
    private WosUtil wosUtil;


    @Autowired
    private FileService fileService;

    @Autowired
    private MultiMediaDataMapper multiMediaDataMapper;

    @Autowired
    private DenseVectorService denseVectorService;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private Map<String, MultimediaService> multimediaServiceMap;

    @Autowired
    private YayiServer yayiServer;

    @Value("${task.document.enable}")
    private boolean enable;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private RedisService redisService;

    @Autowired
    private YouyaApiService youyaApiService;

    @PostConstruct
    public void init() {
        String value = redisService.get(RedisConstant.RUN_MEDIUM, String.class);
        String localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_MEDIUM);
            redisService.unlock(RedisConstant.LOCK_MEDIUM);
        }

        value = redisService.get(RedisConstant.RUN_IMAGE, String.class);
        localhostStr = NetUtil.getLocalhostStr();
        if (StringUtils.isNotBlank(value) && value.startsWith(localhostStr)) {
            redisService.del(RedisConstant.RUN_IMAGE);
            redisService.unlock(RedisConstant.LOCK_IMAGE);
        }
    }

    @RequestMapping("/mediumAnalysis")
    // @Scheduled(cron = "${task.media.cron}")
    public void mediumAnalysis() {
        if (!enable) {
            return;
        }

        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_MEDIUM);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_MEDIUM);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_MEDIUM, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 获取所有未成功解析的音频和视频文件
            List<File> fileList = fileService.getFailedMediumFile();
            if (CollectionUtil.isEmpty(fileList)) {
                return;
            }
            log.info("*************** start-videoAndAudio-启动定时任务解析所有未成功解析的音频、视频文件已开始");
            // 由于第三方服务器解析性能问题，这里未采用异步多线程
            fileList.forEach(this::parseVideo);
            log.info("*************** end-videoAndAudio-定时任务解析所有未成功解析的音频、视频文件已结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_MEDIUM);
            redisService.unlock(RedisConstant.LOCK_MEDIUM);
        }
     }

    @RequestMapping("/imageAnalysis")
    // @Scheduled(cron = "${task.image.cron}")
    public void imageAnalysis() {
        if (!enable) {
            return;
        }
        try {
            boolean lock = redisService.lock(RedisConstant.LOCK_IMAGE);
            if (!lock) {
                log.info("==>任务未获取到锁，跳过当前任务");
                return;
            }

            boolean hasKey = redisService.hasKey(RedisConstant.RUN_IMAGE);
            if (hasKey) {
                log.info("==>任务正在执行，跳过当前任务");
                return;
            }

            String localhostStr = NetUtil.getLocalhostStr();
            String currentDateString = DateUtil.getCurrentDateString();
            redisService.set(RedisConstant.RUN_IMAGE, localhostStr + "_" + currentDateString, 60 * 60 * 24 * 7);

            // 获取所有未成功解析的图片文件
            List<File> imageFileList = fileService.getFailedImageFile();
            if (CollectionUtil.isEmpty(imageFileList)) {
                return;
            }
            log.info("*************** start-image-启动定时任务解析所有未成功解析的图片已开始");
            // 由于第三方服务器解析性能问题，这里未采用异步多线程
            imageFileList.forEach(this::parse);
            log.info("*************** end-image-定时任务解析所有未成功解析的图片已结束");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisService.del(RedisConstant.RUN_IMAGE);
            redisService.unlock(RedisConstant.LOCK_IMAGE);
        }
  }

    @GetMapping("/singleMediumAnalysis")
    @ApiOperation("根据多媒体文件id解析文件")
    @ApiImplicitParam(name = "fileId", value = "多媒体文件id", required = true)
    public Result singleMediumAnalysis(@RequestParam("fileId") String fileId) {
        Wrappers wrappers = Wrappers.init()
                .and(FILE.FILE_ID.eq(fileId))
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(""))
                .and(FILE.STATUS.in(FileStatusEnum.SUCCESS.getCode(), FileStatusEnum.PARSING.getCode(),
                        FileStatusEnum.FAIL.getCode(), FileStatusEnum.UPLOAD_SUCCESS.getCode()))
                .and(FILE.TYPE.in(MultimediaEnum.IMAGE.getCode(), MultimediaEnum.AUDIO.getCode(), MultimediaEnum.VIDEO.getCode()))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.FOLDERS_ID.ne(""));
        File file = fileService.getOne(wrappers);
        if (file==null) {
            return Result.success();
        }

        log.info("*************** 手动触发多媒体解析按钮开始解析文档【{}】", file.getFileName());
        if (MultimediaEnum.VIDEO.getCode().equals(file.getType())) {
            CompletableFuture.runAsync(() -> parseVideo(file));
        } else {
            CompletableFuture.runAsync(() -> parse(file));
        }
        return Result.success();
    }

    private void parseVideo(File file) {
        MultimediaEnum multimediaEnum = MultimediaEnum.getByFileType(file.getFileType());
        if (multimediaEnum == null) {
            updateStatusFail(file, "尚未支持的文件类型");
            return;
        }
        // 视频文件需要调用优雅的视频分片接口
        if (MultimediaEnum.VIDEO.getCode().equals(file.getType())) {
            YouyaAddCatalogParam youyaApiParam = new YouyaAddCatalogParam();
            youyaApiParam.setVideoUrl(file.getFileUrl());
            youyaApiParam.setFileId(file.getFileId());
            AddAnalysisDataResult addAnalysisDataResult =  youyaApiService.addCatalog(youyaApiParam);
            log.info("调用创建视频分片接口: {}", addAnalysisDataResult.getRequestId());
        }

        // 更新状态，解析中
        updateStatus(file);
    }

    private void parse(File file) {
        int initStatus = file.getStatus();
        MultimediaEnum multimediaEnum = MultimediaEnum.getByFileType(file.getFileType());
        if (multimediaEnum == null) {
            updateStatusFail(file, "尚未支持的文件类型");
            return;
        }
        MultimediaService multimediaService = multimediaServiceMap.get(multimediaEnum.getName());
        try {

            // 更新状态，解析中
            updateStatus(file);

            // 清空该文件的所有es数据
            clearEsData(file);

            // 开始解析文本
            String text = multimediaService.analysis(file);

            // 如果当前文件在解析过程出现第三方解析系统繁忙响应，需要延后处理该文件
            if (StringUtils.equals(text, MediaAnalysisConstant.QUEUE_FLAG)) {
                updateInitStatus(file, initStatus, "后台任务处理繁忙，请稍后重试");
                log.info("由于后台处理任务繁忙，文件【"+ file.getFileName() + "】状态已更新为最初状态");
                return;
            }

            // 这里文件需要做一下转换，文本改为docx格式，交给雅意分片
            convert(file, text);

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
            String format = LocalDateTimeUtil.format(LocalDateTimeUtil.now(), DateUtil.DEFAULT_FORMAT);
            // 将文档的文本数据保存到es
            List<Folders> foldersList = foldersService.list();

            HashMap<String, Folders> folderMap = foldersList.stream().collect(Collectors.toMap(
                    Folders::getFoldersId,
                    p -> p,
                    (k1, k2) -> k1,
                    Maps::newHashMap
            ));
            List<MultiMediaData> multiMediaDataList = reformat.stream().map(content -> dataTransform(content, folderMap, file, format)).collect(Collectors.toList());

            multiMediaDataMapper.insertBatch(multiMediaDataList);
            // 多媒体文件处理完成
            updateStatusSuccess(file, multiMediaDataList.stream().mapToInt(data -> data.getContent().length()).sum(), multiMediaDataList.size());
        } catch (Exception e) {
            updateStatusFail(file, e.getMessage());
        }
    }

    /**
     * 清空该文件的所有es数据
     */
    private void clearEsData(File file) {
        LambdaEsQueryWrapper<MultiMediaData> esQueryWrapper = EsWrappers.lambdaQuery(MultiMediaData.class)
                .eq(MultiMediaData::getMultiMediaId, file.getFileId());
        multiMediaDataMapper.delete(esQueryWrapper);
    }

    /**
     * 更新状态，解析中
     */
    private void updateStatus(File file) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.PARSING.getCode())
                .set(FILE.ERROR_MSG, "")
                .where(FILE.FILE_ID.eq(file.getFileId()))
                .update();
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
     * 更新状态，失败
     */
    private void updateStatusFail(File file, DocumentResult documentResult) {
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.FAIL.getCode())
                .set(FILE.ERROR_MSG, documentResult.getMsg())
                .set(FILE.ERROR_NUM, file.getErrorNum() + 1)
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

}
