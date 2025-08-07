package com.wenge.model.controller.api;

import com.wenge.model.dto.param.YouyaAddCatalogParam;
import com.wenge.model.dto.param.YouyaApiCallbackParam;
import com.wenge.model.dto.result.AddAnalysisDataResult;
import com.wenge.model.dto.result.AnalysisDataResults;
import com.wenge.model.entity.YouyaAnalysisResults;
import com.wenge.model.service.YouyaApiService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.wg.appframe.core.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/youya")
@Slf4j
public class YouyaApiController {


    @Autowired
    private YouyaApiService youyaApiService;

    @ApiOperation(value = "api接口回调", tags = "优雅api", notes = "优雅api")
    @PostMapping("/callback")
    public Result callback(@RequestBody YouyaApiCallbackParam youyaApiCallbackParam) {
        youyaApiService.callback(youyaApiCallbackParam);
        return Result.success();
    }

    @ApiOperation(value = "创建编目拆条任务", tags = "优雅api", notes = "优雅api")
    @PostMapping("/addCatalog")
    public Result<AddAnalysisDataResult> addCatalog(@RequestBody YouyaAddCatalogParam youyaApiParam) {
        if (youyaApiParam == null || youyaApiParam.getVideoUrl() == null) {
            return Result.fail("视频路径参数不能为空");
        }
        return Result.success(youyaApiService.addCatalog(youyaApiParam));
    }

    @ApiOperation(value = "查询解析结果信息", tags = "优雅api", notes = "优雅api")
    @PostMapping("/queryVideoInfo")
    public Result<AnalysisDataResults> queryVideoInfo(@RequestBody YouyaAddCatalogParam youyaApiParam) {
        if (youyaApiParam == null || youyaApiParam.getRequestId() == null) {
            return Result.fail("请求id参数不能为空");
        }
        return youyaApiService.queryVideoInfo(youyaApiParam);
    }
}
