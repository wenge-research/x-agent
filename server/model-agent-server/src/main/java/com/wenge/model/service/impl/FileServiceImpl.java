package com.wenge.model.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.constants.FileTypeConstant;
import com.wenge.model.constants.KnowledgeConstant;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.*;
import com.wenge.model.enums.FileAddSourceEnum;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.enums.FileTypeEnum;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.mapper.FileMapper;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.FileDataService;
import com.wenge.model.service.FileLanguageService;
import com.wenge.model.service.FileService;
import com.wenge.model.service.FoldersService;
import com.wenge.model.utils.Word2PdfUtils;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.utils.DateUtil;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.result.ContentParsingNewVersionResult;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;
import static com.wenge.model.entity.table.KnowledgeInfoTableDef.KNOWLEDGE_INFO;

/**
 * Description: 文件服务实现类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 */
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    /**
     * 文件数据库处理类
     */
    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileDataMapper fileDataMapper;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private WosUtil wosUtil;

    @Autowired
    private FoldersService foldersService;

    @Autowired
    private YayiServer yayiServer;

    @Autowired
    private FileLanguageService fileLanguageService;

    @Resource(name = "fileAnalysisPool")
    private ThreadPoolExecutor fileAnalysisPool;

    @Autowired
    private FileDataService fileDataService;

    private static final List<String> FILE_TYPE_LIST = ListUtil.toList(".doc", ".docx", ".pdf", ".txt", ".ofd", ".ppt", ".pptx", ".html");

    @Override
    public Result addFile(FileAndLinkAddParam param) {
        if (CollectionUtil.isEmpty(param.getFiles())) {
            return Result.success();
        }
        for (FileAddWebLinkParam files : param.getFiles()) {
            MultipartFile file = files.getFile();
            if (file == null) {
                return Result.fail("文件不能为空");
            } else {
                boolean anyMatch = FILE_TYPE_LIST.stream().anyMatch(p -> Objects.requireNonNull(file.getOriginalFilename()).endsWith(p));
                if (!anyMatch) {
                    return Result.fail("不支持该文件类型，请上传doc,docx,pdf,txt,ofd,ppt,pptx,html类型的文件");
                }
            }
        }

        List<FileAddWebLinkParam> files = param.getFiles();
        // 获取文件信息
        List<File> fileList = files.stream().map(file -> getFile(file, param.getFoldersId(), param.getType())).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(fileList)) {
            // 上传文件,
            fileList.forEach(this::uploadFile);
        }
        return Result.success();
    }

    @Override
    public Result saveCollectPlatformFile(CollectPlatformFileSaveParam param) {
        List<CollectPlatformFileSaveParam.FileDataInfo> fileDataInfos = param.getFileDataInfos();
        if (CollectionUtil.isEmpty(fileDataInfos)) {
            return Result.success();
        }

        // 根据文件名称判断当前文件夹中的文件是否已存在，已存在的文件无需更新
        String parentFoldersId = param.getFoldersId();
        String knowledgeId = param.getKnowledgeId();
        int collectPlatformTaskId = param.getTaskId();
        Folders collectFolders = foldersService.getByDesignatedFoldersName(KnowledgeConstant.COLLECT_DATA_FOLDER_NAME, knowledgeId, parentFoldersId);
        if (null == collectFolders) {
            // 如果名为“采集数据”得文件夹不存在，就创建
            Folders newFolders = Folders.creat();
            newFolders.setParentId(parentFoldersId);
            newFolders.setKnowledgeId(knowledgeId);
            newFolders.setName(KnowledgeConstant.COLLECT_DATA_FOLDER_NAME);
            collectFolders = foldersService.createFolders(newFolders);

            // 直接保存数据
            saveCollectPlatformData(fileDataInfos, collectFolders.getFoldersId(), collectPlatformTaskId);
        } else {
            // 如果知识库没有推送的文件，就更新
            String collectFoldersId = collectFolders.getFoldersId();
            List<String> pushedFileNames = fileDataInfos.stream().map(CollectPlatformFileSaveParam.FileDataInfo::getFileName).collect(Collectors.toList());
            Wrappers existQueryWrappers = Wrappers.init()
                    .where(FILE.FILE_NAME.in(pushedFileNames))
                    .and(FILE.FOLDERS_ID.eq(collectFoldersId))
                    .and(FILE.COLLECT_PLATFORM_TASK_ID.eq(param.getTaskId()));
            List<File> existFiles = mapper.selectListByQuery(existQueryWrappers);

            // 获取新增的推送数据
            Set<String> existingFileNames = existFiles.stream()
                    .map(File::getFileName)
                    .collect(Collectors.toSet());
            List<CollectPlatformFileSaveParam.FileDataInfo> newFileDataInfos = fileDataInfos.stream()
                    .filter(fileDataInfo -> !existingFileNames.contains(fileDataInfo.getFileName()))
                    .collect(Collectors.toList());
            saveCollectPlatformData(newFileDataInfos, collectFoldersId, collectPlatformTaskId);
        }

        return Result.success();
    }

    @Override
    public Result<Page<File>> getFileList(FilePageParam file) {
        List<String> foldersIds = Lists.newArrayList();
        String folderId = file.getFolderId();
        String knowledgeId = file.getKnowledgeId();
        List<Folders> allFolders = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(file.getKnowledgeIds())) {
            // 有文件夹才可以导入文件
            allFolders = Folders.creat()
                    .where(FOLDERS.KNOWLEDGE_ID.in(file.getKnowledgeIds()))
                    .lists();
            // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
            foldersIds.add("-1");
            if (StringUtils.isNotBlank(folderId)) {
                // 参数中的文件夹id不为空，获取当前文件夹及其子文件夹的所有文件夹集合
                allFolders = foldersService.getTreeByFolderId(folderId, allFolders);
            }
            List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());
            foldersIds.addAll(foldersIdList);
        } else {
            if (StringUtils.isNotBlank(knowledgeId)) {
                // 有文件夹才可以导入文件
                allFolders = Folders.creat()
                        .where(FOLDERS.KNOWLEDGE_ID.eq(knowledgeId))
                        .lists();
                // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
                foldersIds.add("-1");
                if (StringUtils.isNotBlank(folderId)) {
                    // 参数中的文件夹id不为空，获取当前文件夹及其子文件夹的所有文件夹集合
                    allFolders = foldersService.getTreeByFolderId(folderId, allFolders);
                }
                List<String> foldersIdList = allFolders.stream().map(Folders::getFoldersId).collect(Collectors.toList());
                foldersIds.addAll(foldersIdList);
            }
        }
        // 需要先根据文件内容查询出包含关键字的文档
        FileDataPageParam param = new FileDataPageParam();
        param.setContent(file.getKeywords());
        param.setKnowledgeId(file.getKnowledgeId());
        List<FileData> fileDataList = fileDataService.getFileDataKnowledges(param);
        List<String> fileIds =  fileDataList.stream().map(FileData::getFileId).distinct().collect(Collectors.toList());
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        fileIds.add("-1");

        // 如果有传入文件id，则检索该文件夹下的文件，如果没有文件夹id，则查询该知识库下所有文件夹的文件
        Wrappers<Object> wrapper = Wrappers.init()
                .where(CollectionUtil.isNotEmpty(foldersIds), FILE.FOLDERS_ID.in(foldersIds))
                .and(FILE.FILE_NAME.like(file.getFileName()))
                .and(CollectionUtil.isNotEmpty(file.getFileTypes()), FILE.TYPE.in(file.getFileTypes()))
                .and(CollectionUtil.isNotEmpty(file.getFileIds()), FILE.FILE_ID.in(file.getFileIds()))
                // 存在关键字则根据关键字查询
                .and(StringUtils.isNotBlank(file.getKeywords()), FILE.FILE_ID.in(fileIds))
                .orderBy(FILE.CREATE_TIME.desc());
        Page<File> page = page(Page.of(file.getPageNo(), file.getPageSize()), wrapper);

        // 计算平均段落长度
        List<File> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            records.forEach(v -> {
                if (!Objects.isNull(v.getWordCount()) && !Objects.isNull(v.getParagraphsNum())) {
                    v.setAverageParagraphLength(v.getWordCount() / v.getParagraphsNum());
                } else {
                    v.setAverageParagraphLength(0);
                }
            });
        }

        return Result.success(page);
    }


    @Override
    public Result updateFile(File file) {
        updateById(file);
        // 更新切片有效时间
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .notSelect(FileData::getContentDense)
                .eq(FileData::getFileId, file.getFileId());
        List<FileData> fileData = fileDataMapper.selectList(wrapper);
        fileData.forEach(data -> {
            data.setEffectiveStartTime(file.getPeriodStart());
            data.setEffectiveEndTime(file.getPeriodEnd());
            data.setStatus(file.getEnable());
        });
        fileDataMapper.updateBatchByIds(fileData);
        return Result.success();
    }

    @Override
    public Result updateFileWebLink(FileUpdateWebLinkParam file) {
        File byId = this.getById(file.getId());
        byId.setWebLink(file.getWebLink());
        updateById(byId);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteFile(FileDeleteParam param) {
        if (CollectionUtil.isEmpty(param.getIdList()) && CollectionUtil.isEmpty(param.getFoldersIdList())) {
            return Result.success();
        }

        // 查询准备删除的文件数据
        Wrappers wrappers = Wrappers.init()
                .where(CollectionUtil.isNotEmpty(param.getIdList()), FILE.FILE_ID.in(param.getIdList()))
                .and(CollectionUtil.isNotEmpty(param.getFoldersIdList()), FILE.FOLDERS_ID.in(param.getFoldersIdList()));
        List<File> files = mapper.selectListByQuery(wrappers);
        List<String> fileIdList = files.stream().map(File::getFileId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(fileIdList)) {
            return Result.success();
        }

        // 删除文件ES数据
        if (CollectionUtil.isNotEmpty(fileIdList)) {
            LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                    .in(FileData::getFileId, fileIdList);
            fileDataMapper.delete(wrapper);
        }

        // 删除知识库
        List<String> foldersIdList = files.stream().map(File::getFoldersId).distinct().collect(Collectors.toList());
        List<Folders> byFolders = foldersService.getKnowledgeIdsByFoldersId(foldersIdList);
        if (CollectionUtil.isNotEmpty(byFolders)) {
            deleteYayKnowledge(byFolders.get(0).getKnowledgeId(), fileIdList);
        }

        // 删除文件
        Wrappers<Object> where = Wrappers.init()
                .where(FILE.FILE_ID.in(fileIdList));
        mapper.deleteByQuery(where);

        // 删除minio文件
        deleteMinio(files);
        return Result.success();
    }

    /**
     * 获取上传成功的视频、音频，以便后续用于解析
     */
    @Override
    public List<File> getFailedMediumFile() {
        Wrappers wrappers = Wrappers.init()
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(""))
                .and(FILE.STATUS.in(FileStatusEnum.FAIL.getCode(), FileStatusEnum.UPLOAD_SUCCESS.getCode()))
                .and(FILE.TYPE.in(MultimediaEnum.AUDIO.getCode(), MultimediaEnum.VIDEO.getCode()))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.DELETE_FLAG.eq(0))
                .and(FILE.FOLDERS_ID.ne(""))
                .and(FILE.ERROR_NUM.lt(3));
        return mapper.selectListByQuery(wrappers);
    }

    @Override
    public List<File> getFailedImageFile() {
        Wrappers wrappers = Wrappers.init()
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(""))
                .and(FILE.STATUS.in(FileStatusEnum.FAIL.getCode(), FileStatusEnum.UPLOAD_SUCCESS.getCode()))
                .and(FILE.TYPE.eq(MultimediaEnum.IMAGE.getCode()))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.DELETE_FLAG.eq(0))
                .and(FILE.FOLDERS_ID.ne(""))
                .and(FILE.ERROR_NUM.lt(3));
        return mapper.selectListByQuery(wrappers);
    }

    @Override
    public List<File> getWordFile() {
        Wrappers wrappers = Wrappers.init()
                .and(orWrapper -> {
                    orWrapper.or(FILE.TRANS_PDF_URL.isNull())
                            .or(FILE.TRANS_PDF_URL.eq(""));
                })
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(""))
                .and(FILE.FILE_TYPE.in(FileTypeConstant.DOC, FileTypeConstant.DOCX))
                .and(FILE.DELETE_FLAG.eq(0))
                .limit(100);
        return mapper.selectListByQuery(wrappers);
    }

    /**
     * 获取解析中的多媒体解析任务
     */
    @Override
    public List<File> getParsingMediumFile() {
        Wrappers wrappers = Wrappers.init()
                .and(FILE.FILE_URL.isNull())
                .and(FILE.FILE_URL.eq(""))
                .and(FILE.STATUS.eq(FileStatusEnum.PARSING.getCode()))
                .and(FILE.DELETE_FLAG.eq(0))
                .and(FILE.TYPE.in(MultimediaEnum.IMAGE.getCode(), MultimediaEnum.AUDIO.getCode(), MultimediaEnum.VIDEO.getCode()));
        return mapper.selectListByQuery(wrappers);
    }

    @Override
    public List<File> getFileByType(Integer type) {
        Wrappers wrappers = Wrappers.init()
                .and(FILE.FILE_URL.isNotNull())
                .and(FILE.FILE_URL.ne(""))
                .and(FILE.TYPE.eq(type))
                .and(FILE.FOLDERS_ID.isNotNull())
                .and(FILE.FOLDERS_ID.ne(""))
                .and(FILE.DELETE_FLAG.eq(0));
        return mapper.selectListByQuery(wrappers);
    }

    /**
     * 上传文件到minio
     */
    public void uploadFile(File file) {
        try {
            save(file);

            // 转换word为pdf
            MinioInfoResult uploadPDF = null;
            MinioInfoResult upload = null;
            InputStream inputStream = null;
            String fileType = file.getFileType();
            try {
                inputStream = file.getMultipartFile().getInputStream();

                if (StringUtils.equals(fileType.toLowerCase(), FileTypeConstant.DOC) || StringUtils.equals(fileType.toLowerCase(), FileTypeConstant.DOCX)) {
                    byte[] bytes = IoUtil.readBytes(inputStream);
                    // 这里转换可能出现报错,转换异常不影响正常业务流程，所以不需要处理
                    MultipartFile pdfFile = null;
                    try {
                        pdfFile = Word2PdfUtils.docToPdf(file.getFileName(), bytes);
                    } catch (Exception e) {
                        log.error("word文件转成pdf文件出现异常，id：{}，msg:{}", file.getFileId(), e.getMessage(), e);
                    }
                    if (pdfFile != null) {
                        uploadPDF = wosUtil.upload(bucket, "documentPdf", pdfFile, true);
                    }
                }

                // 上传原始文件
                if (StringUtils.isBlank(file.getFileUrl())) {
                    upload = wosUtil.upload(bucket, "document", file.getMultipartFile(), true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (inputStream != null) {
                    IoUtil.close(inputStream);
                }
            }

            if (StringUtils.isBlank(file.getFileUrl()) && null != upload) {
                file.setFileUrl(upload.getUrlPath());
                file.setOriginalUrl(upload.getMinioUrl());
                if (uploadPDF == null) {
                    // 如果是pdf文件，transPdfUrl取自原始上传路径
                    if (StringUtils.equals(fileType.toLowerCase(), FileTypeConstant.PDF)) {
                        file.setTransPdfUrl(upload.getUrlPath());
                    }
                } else {
                    // 说明是上传的word转换了pdf
                    file.setTransPdfUrl(uploadPDF.getUrlPath());
                }
            }
            // 更新文件状态,更新文件url,更新原始文件url
            UpdateChain.of(File.class)
                    .set(FILE.STATUS, FileStatusEnum.UPLOAD_SUCCESS.getCode())
                    .set(FILE.FILE_URL, file.getFileUrl())
                    .set(FILE.TRANS_PDF_URL, file.getTransPdfUrl())
                    .set(FILE.ORIGINAL_URL, file.getOriginalUrl())
                    .where(FILE.FILE_ID.eq(file.getFileId()))
                    .update();
        } catch (Exception e) {
            log.info("上传失败，id：{}，msg:{}", file.getFileId(), e.getMessage(), e);
            // 更新文件状态
            UpdateChain.of(File.class)
                    .set(FILE.STATUS, FileStatusEnum.UPLOAD_FAIL.getCode())
                    .where(FILE.FILE_ID.eq(file.getFileId()))
                    .update();
        }
    }

    /**
     * 获取文件信息
     *
     * @param files
     * @param foldersId
     * @return
     */
    private File getFile(FileAddWebLinkParam files, String foldersId, Integer types) {
        MultipartFile file = files.getFile();
        String fileUrl = files.getFileUrl();
        File fileDetail = new File();
        fileDetail.setFileId(IdUtil.simpleUUID());
        fileDetail.setFoldersId(foldersId);
        fileDetail.setFileName(file.getOriginalFilename());
        int indexOf = file.getOriginalFilename().lastIndexOf(".");
        String type = file.getOriginalFilename().substring(indexOf + 1);
        fileDetail.setFileType(type);
        fileDetail.setFileSize(file.getSize());
        fileDetail.setMultipartFile(file);
        fileDetail.setType(types);
        fileDetail.setWebLink(files.getWebLink());
        fileDetail.setFileUrl(fileUrl);
        fileDetail.setOriginalUrl(fileUrl);
        return fileDetail;
    }

    /**
     * 更新文件状态为上传成功
     *
     * @param files 文件
     */
    @Override
    public void updateStatusUploadSuccess(List<File> files) {
        if (CollectionUtil.isEmpty(files)) {
            return;
        }

        List<String> fileIds = files.stream().map(File::getFileId).collect(Collectors.toList());
        UpdateChain.of(File.class)
                .set(FILE.STATUS, FileStatusEnum.UPLOAD_SUCCESS.getCode())
                .set(FILE.ERROR_MSG, "")
                .set(FILE.ERROR_NUM, 0)
                .where(FILE.FILE_ID.in(fileIds))
                .update();
    }

    @Override
    public File getByName(String name, String foldersId) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(foldersId)) {
            return null;
        }

        return Wrappers.of(mapper)
                .where(FILE.FOLDERS_ID.eq(foldersId))
                .and(FILE.FILE_NAME.eq(name))
                .and(FILE.TYPE.eq(FileTypeEnum.FILE.getType()))
                //.and(FILE.STATUS.eq(FileStatusEnum.SUCCESS.getCode()))
                .limit(1)
                .one();
    }

    @Override
    public void updateFileTime(String fileId, String currentTime) {
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        UpdateChain.create(mapper)
                .set(FILE.UPDATE_TIME, currentTime)
                .where(FILE.FILE_ID.eq(fileId))
                .and(FILE.TYPE.eq(FileTypeEnum.FILE.getType()))
                .update();
    }

    @Override
    public void deleteFileLeTimeYayi(String time, String knowledgeId, String folderId) {
        if (StringUtils.isBlank(time)) {
            return;
        }

        // 获取小于指定时间的文件
        List<File> list = Wrappers.of(mapper)
                .where(FILE.UPDATE_TIME.lt(time))
                .and(FILE.TYPE.eq(FileTypeEnum.FILE.getType()))
                .and(FILE.FOLDERS_ID.eq(folderId))
                .list();
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> fileIdList = list.stream().map(File::getFileId).distinct().collect(Collectors.toList());

        // 先删除雅意知识库的数据
        deleteYayKnowledge(knowledgeId, fileIdList);
        // 删除文件
        UpdateChain.create(mapper)
                .where(FILE.FILE_ID.in(fileIdList))
                .remove();
        // 删除minio文件
        deleteMinio(list);
    }

    @Override
    public List<File> getByFoldersIdForYayiDoc(List<String> foldersId) {
        if (CollectionUtil.isEmpty(foldersId)) {
            return Lists.newArrayList();
        }

        return Wrappers.of(mapper)
                .where(FILE.FOLDERS_ID.in(foldersId))
                .and(FILE.TYPE.eq(FileTypeEnum.YAYI_DOC.getType()))
                .list();
    }

    @Override
    public List<File> getFoldersByFileid(List<String> fileIdList) {
        if (CollectionUtil.isEmpty(fileIdList)) {
            return Lists.newArrayList();
        }

        return Wrappers.of(mapper)
                .select(FILE.FOLDERS_ID, FILE.FILE_ID)
                .where(FILE.FILE_ID.in(fileIdList))
                .list();
    }

    @Override
    public Map<String, Long> getFileCount() {
        // 根据知识库id分组，查询文件数量
        List<File> list = Wrappers.of(mapper)
                .select("knowledge_info.knowledge_id as knowledgeId", "count(0) as count")
                .from(KnowledgeInfo.class)
                .leftJoin(Folders.class).on(KNOWLEDGE_INFO.KNOWLEDGE_ID.eq(FOLDERS.KNOWLEDGE_ID))
                .leftJoin(File.class).on(FILE.FOLDERS_ID.eq(FOLDERS.FOLDERS_ID))
                .groupBy(KNOWLEDGE_INFO.KNOWLEDGE_ID)
                .list();
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }

        return list.stream().collect(Collectors.toMap(File::getKnowledgeId, File::getCount));
    }


    @Override
    public Result analysisFile(FileAnalysisParam param) {
        List<String> fileUrlList = param.getFileUrlList();
        if (CollectionUtil.isEmpty(fileUrlList)) {
            return Result.success(new ArrayList<>());
        }

        // 使用 Future 来存储每个任务的结果
        List<Future<List<DocumentResult.FileContent>>> futures = new ArrayList<>();
        // 提交任务到线程池
        for (String fileUrl : fileUrlList) {
            Future<List<DocumentResult.FileContent>> future = fileAnalysisPool.submit(() -> documentContentAnalysis(fileUrl));
            futures.add(future);
        }

        List<DocumentResult.FileContent> allFileContents = Lists.newArrayList();
        // 获取所有任务的结果
        for (Future<List<DocumentResult.FileContent>> future : futures) {
            try {
                allFileContents.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error analysis file: ", e);
            }
        }

        return Result.success(allFileContents);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFileFromNode(String fileId, String fileUrl, String fileName, String foldersName, String knowledgeId) {
        if (StringUtils.isBlank(fileId) || StringUtils.isBlank(foldersName) || StringUtils.isBlank(fileUrl) || StringUtils.isBlank(fileName) || StringUtils.isBlank(knowledgeId)) {
            log.info("param  is blank");
            return;
        }

        // 创建文件夹
        String foldersId = foldersService.addFoldersByName(foldersName, knowledgeId);

        // 创建文件记录
        File file = new File();
        file.setFileId(IdUtil.simpleUUID());
        file.setFileName(fileName);
        file.setFileUrl(fileUrl);
        file.setFoldersId(foldersId);
        file.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
        file.setType(FileTypeEnum.FILE.getType());
        file.setStatus(FileStatusEnum.UPLOAD_SUCCESS.getCode());

        // 保存文件
        save(file);
    }

    @Override
    public List<FileLanguage> getLanguageFile(String fileId) {
        return fileLanguageService.getLanguageFile(fileId);
    }


    /**
     * yayi文档内容解析
     *
     * @param fileUrl 文件url
     * @return
     */
    private List<DocumentResult.FileContent> documentContentAnalysis(String fileUrl) {
        List<DocumentResult.FileContent> fileContents = Lists.newArrayList();
        ContentParsingNewVersionResult contentParsingNewVersionResult = yayiServer.contentParsingNewVersion(fileUrl);
        ContentParsingNewVersionResult.ContentParsingNewVersionData data = contentParsingNewVersionResult.getData();
        if (null != data && null != data.getFile_content()) {
            List<ContentParsingNewVersionResult.ContentValue> content = data.getFile_content().getContent();
            if (CollectionUtil.isNotEmpty(content)) {
                fileContents = BeanUtil.copyToList(content, DocumentResult.FileContent.class);
                DocumentResult.Info info = new DocumentResult.Info();
                info.setFile_name(data.getFile_content().getTitle());
                info.setPage_num(1);
                info.setFile_type(data.getFile_content().getType());
                fileContents.forEach(v -> {
                    v.setInfo(info);
                });
            }
        }


        return fileContents;
    }

    /**
     * 删除知识库
     *
     * @param knowledgeId
     * @param fileIdList
     */
    private void deleteYayKnowledge(String knowledgeId, List<String> fileIdList) {
        if (CollectionUtil.isEmpty(fileIdList)) {
            return;
        }
        // 删除文件ES数据
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .in(CollectionUtil.isNotEmpty(fileIdList), FileData::getFileId, fileIdList);
        fileDataMapper.delete(wrapper);
        // 删掉知识库数据
        //KnowledgeParam param = new KnowledgeParam();
        //KnowledgeParam.Content content = new KnowledgeParam.Content();
        //content.setFunction(YayiFileDocumentTask.FUNCTION_DELETE_ROBOT_FILE);
        //content.setRobot_id(knowledgeId);
        //content.setFile_id_list(fileIdList);
        //
        //param.setContent(content);
        //param.setModel(KnowledgeParam.KNOWLEDGE_CONFIG);
        //log.info("[删除文档] 开始调用算法接口");
        //KnowledgeResult knowledge = yayiServer.knowledge(param);
        //log.info("[删除文档] 成功，responseBody = {}", JSON.toJSONString(knowledge));
    }

    /**
     * 删除minio文件
     *
     * @param files
     */
    private void deleteMinio(List<File> files) {
        if (CollectionUtil.isNotEmpty(files)) {
            for (File file : files) {
                try {
                    String fileUrl = file.getFileUrl();
                    if (StringUtils.isNotBlank(fileUrl)) {
                        String fileName = wosUtil.getFileNameByUrl(fileUrl);
                        wosUtil.delete(bucket, fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String transPdfUrl = file.getTransPdfUrl();
                    if (StringUtils.isNotBlank(transPdfUrl)) {
                        String fileName = wosUtil.getFileNameByUrl(transPdfUrl);
                        wosUtil.delete(bucket, fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public File queryByFileId(String fileId) {
        Wrappers wrappers = Wrappers.init()
                .where(StringUtils.isNotBlank(fileId), FILE.FILE_ID.eq(fileId));
        return getOne(wrappers);
    }



    /**
     * 保存采集平台推送的数据
     */
    private void saveCollectPlatformData(List<CollectPlatformFileSaveParam.FileDataInfo> fileDataInfos, String folderId, Integer collectPlatformTaskId) {
        if (CollectionUtil.isEmpty(fileDataInfos)) {
            return;
        }

        List<File> files = new ArrayList<>();
        for (CollectPlatformFileSaveParam.FileDataInfo fileDataInfo : fileDataInfos) {
            File file = new File();
            file.setFileId(IdUtil.simpleUUID());
            file.setFileUrl(fileDataInfo.getFileUrl());
            file.setFoldersId(folderId);
            file.setType(FileTypeEnum.FILE.getType());
            file.setCollectPlatformTaskId(collectPlatformTaskId);
            file.setStatus(FileStatusEnum.UPLOAD_SUCCESS.getCode());
            file.setSource(FileAddSourceEnum.PULL.getCode());
            file.setFileName(fileDataInfo.getFileName());
            file.setDeleteFlag("0");
            file.setEnable("是");
            file.setErrorNum(0);
            String currentTime = DateUtil.getCurrentDateString();
            file.setCreateTime(currentTime);
            file.setUpdateTime(currentTime);
            int indexOf = file.getFileUrl().lastIndexOf(".");
            String type = file.getFileUrl().substring(indexOf + 1);
            file.setFileType(type);
            files.add(file);
        }
        this.saveBatch(files);
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
}