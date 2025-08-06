package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.File;
import com.wenge.model.entity.FileLanguage;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * Description: 文件服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 *
 */
public interface FileService extends IService<File> {

    Result addFile(FileAndLinkAddParam file);

    /**
     * 新增采集平台文件
     * @param param
     * @return
     */
    Result saveCollectPlatformFile(CollectPlatformFileSaveParam param);

    Result<Page<File>> getFileList(FilePageParam file);

    Result updateFile(File file);
    Result updateFileWebLink(FileUpdateWebLinkParam file);

    Result deleteFile(FileDeleteParam param);

    List<File> getFailedMediumFile();

    List<File> getFailedImageFile();

    List<File> getWordFile();

    List<File> getParsingMediumFile();

    List<File> getFileByType(Integer type);

    void updateStatusUploadSuccess(List<File> files);

    /**
     * 根据文件名获取文件
     *
     * @param name
     * @param foldersId
     * @return
     */
    File getByName(String name, String foldersId);

    /**
     * 更新文件时间
     *
     * @param fileId
     */
    void updateFileTime(String fileId, String currentTime);

    /**
     * 删除文件，按照时间条件删除，并且删除雅意知识库
     *
     * @param time
     */
    void deleteFileLeTimeYayi(String time, String knowledgeId, String folderId);

    /**
     * 通过文件夹id获取文件
     */
    List<File> getByFoldersIdForYayiDoc(List<String> foldersId);

    /**
     * 通过文件id获取文件夹id
     * @param fileIdList
     * @return
     */
    List<File> getFoldersByFileid(List<String> fileIdList);

    /**
     * 获取每个知识库的文件数量
     * @return
     */
    Map<String, Long> getFileCount();

    /**
     * 文件解析
     * @param param
     */
    Result analysisFile(FileAnalysisParam param);

    /**
     * 知识库写入节点的保存文件
     *
     * @param fileId
     */
    void saveFileFromNode(String fileId, String fileUrl, String fileName, String foldersName, String knowledgeId);

    File queryByFileId(String fileId);

    /**
     * 获取各个语言版本文件的url
     *
     * @param fileId
     * @return
     */
    List<FileLanguage> getLanguageFile(String fileId);
}