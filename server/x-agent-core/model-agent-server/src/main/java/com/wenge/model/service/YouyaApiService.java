package com.wenge.model.service;


import com.wenge.model.dto.param.YouyaAddCatalogParam;
import com.wenge.model.dto.param.YouyaApiCallbackParam;
import com.wenge.model.dto.result.AddAnalysisDataResult;
import com.wenge.model.dto.result.AnalysisDataResults;
import com.wg.appframe.core.bean.Result;


public interface YouyaApiService {

    void callback(YouyaApiCallbackParam youyaApiCallbackParam);

    AddAnalysisDataResult addCatalog(YouyaAddCatalogParam youyaApiParam);

    Result<AnalysisDataResults> queryVideoInfo(YouyaAddCatalogParam youyaApiParam);
}
