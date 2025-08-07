package com.wenge.model.controller;

import com.wenge.model.dto.param.MultiMediaDataPageParam;
import com.wenge.model.entity.MultiMediaData;
import com.wenge.model.service.MultiMediaDataService;
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
@RequestMapping("/multiMediaData")
@Slf4j
@Api("多媒体文件切片接口")
public class MultiMediaDataController {

    @Autowired
    private MultiMediaDataService multiMediaDataService;

    /**
     * 新增文件切片
     */
    @ApiOperation(value = "新增文件切片",tags = "新增文件切片", notes = "新增文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addFileData")
    @OperaLogs
    public Result addFileData(@RequestBody MultiMediaData multiMediaData) {
        return multiMediaDataService.addFileData(multiMediaData);
    }

    /**
     * 删除文件切片
     */
    @ApiOperation(value = "删除文件切片",tags = "删除文件切片", notes = "删除文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteFileData")
    @OperaLogs
    public Result deleteFileData(@RequestBody ListStringParam fileDataId) {
        return multiMediaDataService.deleteFileData(fileDataId);
    }

    /**
     * 查询文件切片
     */
    @ApiOperation(value = "查询文件切片",tags = "查询文件切片", notes = "查询文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getFileDatas")
    public Result<EsPageInfo<MultiMediaData>> getFileDatas(@RequestBody MultiMediaDataPageParam param) {
        return multiMediaDataService.getFileDatas(param);
    }

    /**
     * 文件切片拼接
     */
    @ApiOperation(value = "文件切片拼接",tags = "文件切片拼接", notes = "文件切片拼接", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getDataByFileId")
    public Result<String> getDataByFileId(@RequestBody MultiMediaDataPageParam param) {
        return multiMediaDataService.getDataByFileId(param);
    }
}
