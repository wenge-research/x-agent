package com.wenge.model.controller;

import com.wenge.model.dto.param.FileDataPageParam;
import com.wenge.model.entity.FileData;
import com.wenge.model.service.FileDataService;
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
@RequestMapping("/fileData")
@Slf4j
@Api("文件切片接口")
public class FileDataController {

    @Autowired
    private FileDataService fileDataService;

    /**
     * 新增文件切片
     */
    @ApiOperation(value = "新增文件切片",tags = "新增文件切片", notes = "新增文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addFileData")
    @OperaLogs
    public Result addFileData(@RequestBody FileData fileData) {
        return fileDataService.addFileData(fileData);
    }

    /**
     * 复制文件切片
     */
    @ApiOperation(value = "复制文件切片", tags = "复制文件切片", notes = "复制文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/copyFileData")
    @OperaLogs
    public Result copyFileData(@RequestBody FileData fileData) {
        return fileDataService.copyFileData(fileData);
    }

    /**
     * 删除文件切片
     */
    @ApiOperation(value = "删除文件切片",tags = "删除文件切片", notes = "删除文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteFileData")
    @OperaLogs
    public Result deleteFileData(@RequestBody ListStringParam fileDataId) {
        return fileDataService.deleteFileData(fileDataId);
    }

    /**
     * 查询文件切片
     */
    @ApiOperation(value = "查询文件切片",tags = "查询文件切片", notes = "查询文件切片", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getFileDatas")
    public Result<EsPageInfo<FileData>> getFileDatas(@RequestBody FileDataPageParam param) {
        return fileDataService.getFileDatas(param);
    }

    /**
     * 文件切片拼接
     */
    @ApiOperation(value = "文件切片拼接",tags = "文件切片拼接", notes = "文件切片拼接", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getDataByFileId")
    public Result<String> getDataByFileId(@RequestBody FileDataPageParam param) {
        return fileDataService.getDataByFileId(param);
    }

    /**
     * 文件切片搜索(根据关键字)
     */
    @ApiOperation(value = "文件切片搜索(根据关键字)",tags = "文件切片搜索(根据关键字)", notes = "文件切片搜索(根据关键字)", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/searchDataByKeywords")
    public Result<String> searchDataByKeywords(@RequestBody FileDataPageParam param) {
        return fileDataService.searchDataByKeywords(param);
    }
}
