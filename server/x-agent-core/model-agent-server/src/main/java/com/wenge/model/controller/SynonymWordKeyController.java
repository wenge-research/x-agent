package com.wenge.model.controller;

import com.wenge.model.dto.param.ImportSynonymWordDataParam;
import com.wenge.model.dto.param.SynonymWordPageParam;
import com.wenge.model.entity.SynonymWordKey;
import com.wenge.model.service.SynonymWordKeyService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/synonymWord")
@Slf4j
@Api(tags = "文档提示词配置表接口")
public class SynonymWordKeyController {

    /**
     * 	文档提示词配置表服务类
     */
    @Autowired
    private SynonymWordKeyService synonymWordKeyService;

    /**
     * 新增同义词
     */
    @ApiOperation(value = "新增同义词",tags = "新增同义词", notes = "新增同义词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addSynonymWordKey")
    @OperaLogs
    public Result addSynonymWordKey(@RequestBody SynonymWordKey synonymWordKey) {
        return synonymWordKeyService.addSynonymWordKey(synonymWordKey);
    }

    /**
     * 编辑同义词
     */
    @ApiOperation(value = "编辑同义词",tags = "编辑同义词", notes = "编辑同义词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/editSynonymWord")
    @OperaLogs
    public Result editSynonymWord(@RequestBody SynonymWordKey synonymWordKey) {
        return synonymWordKeyService.editSynonymWord(synonymWordKey);
    }

    /**
     * 编辑同义词
     */
    @ApiOperation(value = "删除同义词",tags = "编辑同义词", notes = "编辑同义词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteSynonymWords")
    @OperaLogs
    public Result deleteSynonymWords(@RequestBody List<String> idList) {
        return synonymWordKeyService.deleteSynonymWords(idList);
    }

    /**
     * 查询同义词详情
     */
    @ApiOperation(value = "查询同义词详情",tags = "查询同义词详情", notes = "查询同义词详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/queryDetails")
    @OperaLogs
    public Result queryDetails(@RequestBody SynonymWordKey synonymWordKey) {
        return synonymWordKeyService.queryDetails(synonymWordKey);
    }

    /**
     * 查询同义词列表
     */
    @ApiOperation(value = "查询同义词列表",tags = "查询同义词列表", notes = "查询同义词列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/queryList")
    @OperaLogs
    public Result queryList(@RequestBody SynonymWordPageParam synonymWordPageParam) {
        return synonymWordKeyService.queryList(synonymWordPageParam);
    }

    /**
     * 查询同义词类型下拉列表
     */
    @ApiOperation(value = "查询同义词类型下拉列表",tags = "查询同义词类型下拉列表", notes = "查询同义词类型下拉列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/querySelect")
    @OperaLogs
    public Result querySelect() {
        return synonymWordKeyService.querySelect();
    }

    /**
     * 导入纠错词数据
     *
     * @return
     */
    @ApiOperation(value = "导入同义词数据", tags = "导入同义词数据", notes = "导入同义词数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/importSynonymWordData")
    @OperaLogs
    public Result<String> importSynonymWordData(ImportSynonymWordDataParam param) throws IOException {
        return synonymWordKeyService.importSynonymWordData(param);
    }

    @ApiOperation(value = "下载导入同义词模板",tags = "下载导入同义词模板", notes = "下载导入同义词模板", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @GetMapping("/downloadSynonymWordDataTemp")
    @OperaLogs
    public void downloadSynonymWordDataTemp(HttpServletResponse response) {
        synonymWordKeyService.downloadSynonymWordDataTemp(response);
    }
}
