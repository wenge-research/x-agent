package com.wenge.model.service;

import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.KnowledgeDataTypeParam;
import com.wenge.model.entity.KnowledgeDataType;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;

import java.util.List;
import java.util.Map;

/**
 * Description: 知识数据分类服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 18:21:03
 *
 */
public interface KnowledgeDataTypeService extends IService<KnowledgeDataType> {

    Result addKnowledgeDataType(KnowledgeDataType knowledgeDataType);

    Result getKnowledgeDataTypeList(KnowledgeDataTypeParam knowledgeDataType);

    Result updateKnowledgeDataType(KnowledgeDataType knowledgeDataType);

    Result deleteKnowledgeDataType(ListStringParam idList);

    Map<Long, String> getTypeMap(String knowledgeId);

    /**
     * 查询当前分类及下的所有分类
     * @param id
     * @param dataTypeList
     * @return
     */
    List<KnowledgeDataType> getTreeById(Long id, List<KnowledgeDataType> dataTypeList);

}