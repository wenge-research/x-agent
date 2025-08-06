package com.wenge.model.service;

import com.wenge.model.dto.param.DeleteKnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeDataPageParam;
import com.wenge.model.dto.param.importKnowledgeDataParam;
import com.wenge.model.dto.result.KnowledgeDataResult;
import com.wenge.model.dto.result.KnowledgeDataScopeResult;
import com.wenge.model.entity.KnowledgeData;
import com.wg.appframe.core.bean.Result;
import org.dromara.easyes.core.biz.EsPageInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface KnowledgeDataService {
    Result addKnowledgeData(KnowledgeData knowledgeData);

    Result<EsPageInfo<KnowledgeDataResult>> getKnowledgeDataList(KnowledgeDataPageParam param);

    Result<String> importKnowledgeData(importKnowledgeDataParam param);

    void downloadKnowledgeDataTemp(HttpServletResponse response);

    Result deleteKnowledgeData(DeleteKnowledgeDataParam param);

    //List<Double> modelEncode(String query, String knowledgeId, Object data, String field);

    //List<Double> modelEncode(String query);

    void exportData(KnowledgeDataPageParam vectorLibraryDataParam, HttpServletResponse response);

    void deleteByTitle(String title, List<String> knowledgeIds);

    List<List<KnowledgeDataScopeResult>> getKnowledgeDataScope(KnowledgeDataPageParam param);

    EsPageInfo<KnowledgeData> getDeleteKnowledgeData(KnowledgeDataPageParam param);


    Result recoverKnowledgeData(DeleteKnowledgeDataParam param);

    List<KnowledgeData> getKnowledgeData(List<String> knowledgeIds);

    public void updateKnowledgeData(List<KnowledgeData> knowledgeDataList);
}
