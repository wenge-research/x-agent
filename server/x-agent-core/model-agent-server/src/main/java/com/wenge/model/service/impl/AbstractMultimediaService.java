package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.File;
import com.wenge.model.entity.FileData;
import com.wenge.model.entity.Folders;
import com.wenge.model.enums.FileStatusEnum;
import com.wenge.model.enums.MultimediaEnum;
import com.wenge.model.mapper.FileMapper;
import com.wenge.model.mapper.es.FileDataMapper;
import com.wenge.model.service.MultimediaService;
import com.wenge.model.service.YouyaApiService;

import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import com.wg.appframe.wos.dto.result.MinioInfoResult;
import com.wg.appframe.wos.utils.WosUtil;
import com.wg.appframe.yayi.api.YayiServer;
import com.wg.appframe.yayi.result.ImageIdentifyResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.FileTableDef.FILE;
import static com.wenge.model.entity.table.FoldersTableDef.FOLDERS;

/**
 * 文件抽象类 封装文件的共有属性、方法
 */
@Slf4j
public abstract class AbstractMultimediaService extends ServiceImpl<FileMapper, File> implements MultimediaService {
    protected List<String> FILE_TYPE_LIST;

    @Autowired
    private WosUtil wosUtil;

    @Autowired
    private FileDataMapper fileDataMapper;

    @Value("${appframe.minio.bucket}")
    private String bucket;

    @Autowired
    private YayiServer yayiServer;

    @Resource(name = "imageAnalysisPool")
    private ThreadPoolExecutor imageAnalysisPool;

    @Autowired
    private YouyaApiService youyaApiService;

    @Override
    public Result addFile(FileAddParam param){
        if (CollectionUtil.isEmpty(param.getFiles())) {
            return Result.success();
        }
        for (MultipartFile file : param.getFiles()) {
            if (file == null) {
                return Result.fail("文件不能为空");
            } else {
                boolean anyMatch = FILE_TYPE_LIST.stream().anyMatch(p -> Objects.requireNonNull(file.getOriginalFilename()).endsWith(p));
                if (!anyMatch) {
                    return Result.fail("不支持该文件类型，请上传" + FILE_TYPE_LIST.stream().map(p -> p.replace(".", "")).collect(Collectors.joining(",")) + "类型的文件");
                }
            }
        }

        List<MultipartFile> files = param.getFiles();
        // 获取文件信息
        List<File> fileList = files.stream().map(file -> getFile(file, param.getFoldersId())).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(fileList)) {
            // 上传文件,
            fileList.forEach(this::uploadFile);
        }
        return Result.success();
    }

    @Override
    public Result updateFile(File file){
        updateById(file);
        return Result.success();
    }

    @Override
    public Result<File> getByFileId(String fileId) {
        File file = mapper.selectOneByQuery(Wrappers.init().where(FILE.FILE_ID.eq(fileId)));
        return Result.success(file);
    }

    @Override
    public Result<Page<File>> getFileList(FilePageParam file){
        List<String> foldersIds = Lists.newArrayList();
        // 这里添加-1的目录，是为了防止查询到非当前知识库的文件
        foldersIds.add("-1");
        // 获取文件夹id
        if (StringUtils.isNotBlank(file.getFolderId())) {
            foldersIds.add(file.getFolderId());
        } else {
            List<Folders> list = Folders.creat()
                    .where(FOLDERS.KNOWLEDGE_ID.eq(file.getKnowledgeId()))
                    .lists();
            //if (CollectionUtil.isEmpty(list)) {
            //	return Result.success(new Page<>());
            //}
            List<String> foldersIdList = list.stream().map(Folders::getFoldersId).collect(Collectors.toList());
            foldersIds.addAll(foldersIdList);
        }

        // 如果有传入文件id，则检索该文件夹下的文件，如果没有文件夹id，则查询该知识库下所有文件夹的文件
        Wrappers<Object> wrapper = Wrappers.init()
                .where(FILE.FOLDERS_ID.in(foldersIds))
                .orderBy(FILE.CREATE_TIME.desc());
        Page<File> page = page(Page.of(file.getPageNo(), file.getPageSize()), wrapper);
        return Result.success(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteFile(FileDeleteParam param) {
        if (CollectionUtil.isEmpty(param.getIdList()) && CollectionUtil.isEmpty(param.getFoldersIdList())) {
            return Result.success();
        }
        // 删除文件
        Wrappers wrappers = Wrappers.init()
                .where(CollectionUtil.isNotEmpty(param.getIdList()), FILE.FILE_ID.in(param.getIdList()))
                .and(CollectionUtil.isNotEmpty(param.getFoldersIdList()), FILE.FOLDERS_ID.in(param.getFoldersIdList()));
        mapper.deleteByQuery(wrappers);

        // 删除文件数据
        LambdaEsQueryWrapper<FileData> wrapper = EsWrappers.lambdaQuery(FileData.class)
                .in(CollectionUtil.isNotEmpty(param.getIdList()), FileData::getFileId, param.getIdList())
                .in(CollectionUtil.isNotEmpty(param.getFoldersIdList()), FileData::getFoldersId, param.getFoldersIdList());
        fileDataMapper.delete(wrapper);

        return Result.success();
    }

    @Override
    public Result analysisImage(ImageAnalysisParam param) {
        List<String> imageUrlList = param.getImageUrlList();
        if (CollectionUtil.isEmpty(imageUrlList)) {
            return Result.success(new ArrayList<>());
        }

        // 使用 Future 来存储每个任务的结果
        List<Future<ImageIdentifyResult.ContentValue>> futures = new ArrayList<>();
        // 提交任务到线程池
        for (String imageUrl : imageUrlList) {
            Future<ImageIdentifyResult.ContentValue> future = imageAnalysisPool.submit(() -> imageAnalysis(imageUrl));
            futures.add(future);
        }

        List<ImageIdentifyResult.ContentValue> allImageContents = Lists.newArrayList();
        // 获取所有任务的结果
        for (Future<ImageIdentifyResult.ContentValue> future : futures) {
            try {
                ImageIdentifyResult.ContentValue content = future.get();
                if (null != content && StringUtils.isNotBlank(content.getCaption())) {
                    allImageContents.add(content);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error analysis image: ", e);
            }
        }

        return Result.success(allImageContents);
    }

    /**
     * 修改fileUrl
     * @param file
     * @param text
     * @throws IOException
     */
    private void convertToDocx(File file, String text) throws Exception {
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
     * 文档转文本
     */
    private ImageIdentifyResult.ContentValue imageAnalysis(String imageUrl) {
        if (StringUtils.isBlank(imageUrl)) {
            return null;
        }
        ImageIdentifyResult imageIdentifyResult = yayiServer.imageIdentify(imageUrl);
        if (null == imageIdentifyResult) {
            return null;
        }
        ImageIdentifyResult.ImageIdentifyData data = imageIdentifyResult.getData();
        if (null == data) {
            return null;
        }
        ImageIdentifyResult.FileContent fileContents = data.getFile_content();
        if (null == fileContents) {
            return null;
        }
        return fileContents.getContent();
    }

    /**
     * 上传文件到minio
     */
    public void uploadFile(File file) {
        try {
            save(file);

            MinioInfoResult upload = wosUtil.upload(bucket, "multimedia", file.getMultipartFile(), true);
            file.setFileUrl(upload.getUrlPath());
            file.setOriginalUrl(upload.getMinioUrl());

            Integer status = FileStatusEnum.UPLOAD_SUCCESS.getCode();
            // 视频文件需要调用优雅的视频分片接口
            if (MultimediaEnum.VIDEO.getCode().equals(file.getType())) {
                YouyaAddCatalogParam youyaApiParam = new YouyaAddCatalogParam();
                youyaApiParam.setVideoUrl(file.getFileUrl());
                youyaApiParam.setFileId(file.getFileId());
                youyaApiService.addCatalog(youyaApiParam);
                status = FileStatusEnum.PARSING.getCode();
            }

            // 更新文件状态,更新文件url,更新原始文件url
            UpdateChain.of(File.class)
                    .set(FILE.STATUS, status)
                    .set(FILE.FILE_URL, file.getFileUrl())
                    .set(FILE.ORIGINAL_URL, file.getOriginalUrl())
                    .where(FILE.FILE_ID.eq(file.getFileId()))
                    .update();
        } catch (Exception e) {
            log.info("上传失败，id：{}，msg:{}", file.getFileId(), e.getMessage(), e);
            // 更新文件状态
            UpdateChain.of(File.class)
                    .set(FILE.STATUS, FileStatusEnum.UPLOAD_FAIL.getCode())
                    .set(FILE.FILE_URL, file.getFileUrl())
                    .set(FILE.ORIGINAL_URL, file.getOriginalUrl())
                    .where(FILE.FILE_ID.eq(file.getFileId()))
                    .update();
        }
    }

    /**
     * 获取文件信息
     * @param file
     * @param foldersId
     * @return
     */
    protected abstract File getFile(MultipartFile file, String foldersId);
}
