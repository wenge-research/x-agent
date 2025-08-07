package com.wenge.model.service;

import com.wenge.model.dto.param.AddKnowledgeUrlDataParam;
import com.wenge.model.dto.param.DeleteKnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeUrlDataParam;
import com.wenge.model.entity.KnowledgeUrlData;
import com.wg.appframe.core.bean.Result;

import java.io.IOException;

public interface KnowledgeUrlDataService {
    Result addKnowledgeUrlData(AddKnowledgeUrlDataParam addKnowledgeUrlDataParam);


    Result deleteKnowledgeUrlData(DeleteKnowledgeDataParam param);

    Result addKnowledgeData(AddKnowledgeUrlDataParam param);

    Result updateKnowledgeData(KnowledgeUrlData param);

    Result getKnowledgeUrlDataDetail(KnowledgeUrlData param);

    Result getKnowledgeUrlDataListByParentId(KnowledgeUrlData param) throws IOException;


    Result getUrlDataList(KnowledgeUrlDataParam param);
}
