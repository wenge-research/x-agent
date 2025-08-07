package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.FoldersDeleteParam;
import com.wenge.model.dto.param.FoldersPageParam;
import com.wenge.model.entity.Folders;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 文件夹 服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 17:48:54
 *
 */
public interface FoldersService extends IService<Folders> {

    Result<Folders> addFolders(Folders folders);

    Folders createFolders(Folders folders);

    Result<Page<Folders>> getFoldersList(FoldersPageParam folders);

    Result updateFolders(Folders folders);

    Result deleteFolders(FoldersDeleteParam param);

    Folders getByFoldersId(String foldersId);

    Folders getByFoldersName(String foldersName, String knowledgeId);

    /**
     * 根据父文件夹id查询指定文件夹
     * @param name
     * @param knowledgeId
     * @param parentId
     * @return
     */
    Folders getByDesignatedFoldersName(String name, String knowledgeId, String parentId);

    /**
     * 获取知识库文件夹
     * @param knowledgeIds
     * @return
     */
    List<Folders> getFoldersForYayiDoc(List<String> knowledgeIds);

    /**
     * 根据文件夹id获取知识库id
     * @param foldersIds
     * @return
     */
    List<Folders> getKnowledgeIdsByFoldersId(List<String> foldersIds);

    /**
     * 查询当前文件夹及下的所有文件夹
     * @param folderId
     * @param allFolders
     * @return
     */
    List<Folders> getTreeByFolderId(String folderId, List<Folders> allFolders);

    /**
     * 根据文件夹名称创建文件夹
     * @param foldersName
     * @param knowledgeId
     * @return
     */
    String addFoldersByName(String foldersName, String knowledgeId);

    List<Folders> getListByFoldersName(String foldersName, List<String> knowledgeIds);

}