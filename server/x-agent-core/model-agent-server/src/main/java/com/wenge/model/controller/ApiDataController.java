package com.wenge.model.controller;

import com.wenge.model.dto.param.ApiDataPageParam;
import com.wenge.model.entity.ApiData;
import com.wenge.model.service.ApiDataService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiData")
@Slf4j
@Api("api切片接口")
public class ApiDataController {

    @Autowired
    private ApiDataService apiDataService;

    /**
     * 新增文件切片
     */
    @ApiOperation(value = "新增Api切片",tags = "新增Api切片", notes = "新增Api切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addApiData")
    @OperaLogs
    public Result addApiData(@RequestBody ApiData apiData) {
        return apiDataService.addApiData(apiData);
    }

    /**
     * 复制Api切片
     */
    @ApiOperation(value = "复制Api切片", tags = "复制Api切片", notes = "复制Api切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/copyApiData")
    @OperaLogs
    public Result copyApiData(@RequestBody ApiData apiData) {
        return apiDataService.copyApiData(apiData);
    }

    /**
     * 删除Api切片
     */
    @ApiOperation(value = "删除Api切片",tags = "删除Api切片", notes = "删除Api切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteApiData")
    @OperaLogs
    public Result deleteApiData(@RequestBody ListStringParam apiDataId) {
        return apiDataService.deleteApiData(apiDataId);
    }

    /**
     * 查询Api切片
     */
    @ApiOperation(value = "查询Api切片",tags = "查询Api切片", notes = "查询Api切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getApiDatas")
    public Result<EsPageInfo<ApiData>> getApiDatas(@RequestBody ApiDataPageParam param) {
        return apiDataService.getApiDatas(param);
    }

    /**
     * api切片拼接
     */
    @ApiOperation(value = "api切片拼接",tags = "api切片拼接", notes = "api切片拼接", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getApiDataByApiId")
    public Result<String> getApiDataByApiId(@RequestBody ApiDataPageParam param) {
        return apiDataService.getApiDataByApiId(param);
    }
}
