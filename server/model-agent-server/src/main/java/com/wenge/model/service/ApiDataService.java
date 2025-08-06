package com.wenge.model.service;

import com.wenge.model.dto.param.ApiDataPageParam;
import com.wenge.model.entity.ApiData;
import com.wenge.model.entity.KnowledgeApi;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;

import java.util.List;

public interface ApiDataService {

    Result addApiData(ApiData apiData);


    Result deleteApiData(ListStringParam apiDataId);

    Result getApiDatas(ApiDataPageParam param);

    Result<String> getApiDataByApiId(ApiDataPageParam param);

    Result copyApiData(ApiData apiData);

    List<ApiData> getApiDatasByApiId(ApiDataPageParam param);

    /**
     * 清空该文件的所有es数据
     */
    public void clearEsData(KnowledgeApi knowledgeApi);

    List<ApiData> getApiDataByKnowledgeId(ApiDataPageParam param);
}
