package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.*;
import com.wenge.model.entity.File;
import com.wenge.model.service.FileService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.annotation.GlobalLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 文件接口
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 18:06:36
 */
@Api(tags = "Description: 文件接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-07 18:06:36")
@RestController
@RequestMapping("/file")
@Slf4j
public class FilesController {

    /**
     * 文件服务类
     */
    @Autowired
    private FileService fileService;

    /**
     * 新增文件
     */
    @ApiOperation(value = "新增文件", tags = "新增文件", notes = "新增文件-pdf、doc、docx", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addFile")
    @OperaLogs
    @GlobalLog(paramEnable = false)
    public Result addFile(FileAndLinkAddParam file) {
        return fileService.addFile(file);
    }

    /**
     * 编辑文件对应web链接
     */
    @ApiOperation(value = "编辑文件对应web链接", tags = "编辑文件对应web链接", notes = "编辑文件对应web链接", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/updateFileWebLink")
    @OperaLogs
    public Result updateFileWebLink(@RequestBody FileUpdateWebLinkParam file) {
        return fileService.updateFileWebLink(file);
    }

    /**
     * 查询文件列表
     */
    @ApiOperation(value = "查询文件列表", tags = "查询文件列表", notes = "查询文件列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getFileList")
    public Result<Page<File>> getFileList(@RequestBody FilePageParam file) {
        return fileService.getFileList(file);
    }

    /**
     * 删除文件
     */
    @ApiOperation(value = "删除文件", tags = "删除文件", notes = "删除文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteFile")
    @OperaLogs
    public Result deleteFile(@RequestBody FileDeleteParam param) {
        return fileService.deleteFile(param);
    }

    /**
     * 更新文件数据
     */
    @ApiOperation(value = "更新文件数据", tags = "更新文件数据", notes = "更新文件数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/update")
    @OperaLogs
    public Result update(@RequestBody File file) {
        // 简单处理防止""存日期报错
        if (StringUtils.isBlank(file.getPeriodEnd())) {
            file.setPeriodEnd(null);
        }
        if (StringUtils.isBlank(file.getPeriodStart())) {
            file.setPeriodStart(null);
        }
        return fileService.updateFile(file);
    }

    /**
     * 文件解析
     */
    @ApiOperation(value = "文件解析", tags = "文件解析", notes = "文件解析", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/analysisFile")
    @OperaLogs
    public Result analysisFile(@RequestBody FileAnalysisParam param) {
        return fileService.analysisFile(param);
    }

}