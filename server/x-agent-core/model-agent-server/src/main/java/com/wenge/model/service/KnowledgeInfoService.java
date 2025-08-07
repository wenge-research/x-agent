package com.wenge.model.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.FixKnowledgeDataTypeParam;
import com.wenge.model.dto.param.KnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeInfoPageParam;
import com.wenge.model.dto.param.StatusUpdateParam;
import com.wenge.model.dto.result.DeptKnowleageResult;
import com.wenge.model.dto.result.KnowledgeNameInfoResult;
import com.wenge.model.entity.KnowledgeInfo;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: 知识库管理服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 14:10:23
 *
 */
public interface KnowledgeInfoService extends IService<KnowledgeInfo> {

    Result addKnowledgeInfo(KnowledgeInfo knowledgeInfo);

    Result<Page<KnowledgeInfo>> getKnowledgeInfoList(KnowledgeInfoPageParam knowledgeInfo);

    Result updateKnowledgeInfo(KnowledgeInfo knowledgeInfo);

    Result deleteKnowledgeInfo(List<String> idList);

    Result updateStatus(StatusUpdateParam param);

    /**
     * 获取应用绑定的有效的知识库
     * @param applicationId
     * @return
     */
    List<KnowledgeInfo> effectiveKnowledge(String applicationId);

    KnowledgeInfo getByName(String name);

    /**
     * 根据id获取知识库
     * @return
     */
    KnowledgeInfo getById(String knowledgeId);

    /**
     * 获取所有知识库
     */
    List<KnowledgeInfo> getAllKnowledgeInfo();

    /**
     * 根据id获取知识库
     *
     * @return
     */
    List<KnowledgeInfo> getByIds(List<String> knowledgeIds);

    /**
     * 知识库数量统计
     * @param knowledgeDataParam
     * @return
     */
    Result knowledgeCount(KnowledgeDataParam knowledgeDataParam);


    /**
     * 知识库数量统计
     * @param knowledgeDataParam
     * @return
     */
    Result knowledgeCount2(KnowledgeDataParam knowledgeDataParam);

    /**
     * 知识库关联应用总数
     * @param knowledgeDataParam
     * @return
     */
    Result applicationCount(KnowledgeDataParam knowledgeDataParam);

    /**
     * 知识库增长趋势
     * @param knowledgeDataParam
     * @return
     */
    Result knowledgeCountTrend(KnowledgeDataParam knowledgeDataParam);


    Result knowledgeCountTrend2(KnowledgeDataParam knowledgeDataParam);

    /**
     * 查询知识库累表
     * @param knowledgeInfo
     * @return
     */
    List<KnowledgeInfo> getKnowledgeList(KnowledgeInfoPageParam knowledgeInfo);

    /**
     * 查询部门用户关联知识库数据
     * @param knowledgeInfo
     * @return
     */
    Result<DeptKnowleageResult> getDeptKnowledgeData(KnowledgeInfoPageParam knowledgeInfo);

    /**
     * 同步知识库数据
     */
    void summaryData();

    Result<Page<KnowledgeNameInfoResult>> queryAllKnowledgeNameList(KnowledgeInfoPageParam knowledgeInfo);

    Result setPreset(KnowledgeInfoPageParam knowledgeInfo);

    /**
     * 修复知识库分类：
     *  1. 将一级目录非全部的目录改成pid为全部目录的id，
     *      如果不存在则创建全部目录
     *  2. 将知识库下面没有分类的数据全部放到全部目录下
     * @return
     */
    Result fixKnowledgeDataType(FixKnowledgeDataTypeParam fixKnowledgeDataTypeParam);

}