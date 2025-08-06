package com.wenge.model.controller;

import com.wenge.model.dto.param.CorrectWordPageParam;
import com.wenge.model.dto.param.ImportCorrectWordDataParam;

import com.wenge.model.entity.CorrectWordKey;
import com.wenge.model.service.CorrectWordKeyService;
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
@RequestMapping("/correctWord")
@Slf4j
@Api(tags = "纠错词接口")
public class CorrectWordKeyController {

    /**
     * 	纠错词配置表服务类
     */
    @Autowired
    private CorrectWordKeyService correctWordKeyService;

    /**
     * 新增纠错词
     */
    @ApiOperation(value = "新增纠错词",tags = "新增纠错词", notes = "新增纠错词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addCorrectWord")
    @OperaLogs
    public Result addCorrectWord(@RequestBody CorrectWordKey correctWordKey) {
        return correctWordKeyService.addCorrectWord(correctWordKey);
    }

    /**
     * 编辑纠错词
     */
    @ApiOperation(value = "编辑纠错词",tags = "编辑纠错词", notes = "编辑纠错词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/editCorrectWord")
    @OperaLogs
    public Result editCorrectWord(@RequestBody CorrectWordKey correctWordKey) {
        return correctWordKeyService.editCorrectWord(correctWordKey);
    }

    /**
     * 编辑纠错词
     */
    @ApiOperation(value = "编辑纠错词",tags = "编辑纠错词", notes = "编辑纠错词", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteCorrectWord")
    @OperaLogs
    public Result deleteCorrectWord(@RequestBody List<String> idList) {
        return correctWordKeyService.deleteCorrectWord(idList);
    }

    /**
     * 查询纠错词详情
     */
    @ApiOperation(value = "查询纠错词详情",tags = "查询纠错词详情", notes = "查询纠错词详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/queryDetails")
    @OperaLogs
    public Result queryDetails(@RequestBody CorrectWordKey correctWordKey) {
        return correctWordKeyService.queryDetails(correctWordKey);
    }

    /**
     * 查询纠错词列表
     */
    @ApiOperation(value = "查询纠错词列表",tags = "查询纠错词列表", notes = "查询纠错词列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/queryList")
    @OperaLogs
    public Result queryList(@RequestBody CorrectWordPageParam correctWordPageParam) {
        return correctWordKeyService.queryList(correctWordPageParam);
    }

    /**
     * 查询纠错词类型下拉列表
     */
    @ApiOperation(value = "查询纠错词类型下拉列表",tags = "查询纠错词类型下拉列表", notes = "查询纠错词类型下拉列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/querySelect")
    @OperaLogs
    public Result querySelect() {
        return correctWordKeyService.querySelect();
    }

    /**
     * 导入纠错词数据
     *
     * @return
     */
    @ApiOperation(value = "导入纠错词数据", tags = "导入纠错词数据", notes = "导入纠错词数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/importCorrectWordData")
    @OperaLogs
    public Result<String> importCorrectWordData(ImportCorrectWordDataParam param) throws IOException {
        return correctWordKeyService.importCorrectWordData(param);
    }


    @ApiOperation(value = "下载导入纠错词模板",tags = "下载导入纠错词模板", notes = "下载导入纠错词模板", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @GetMapping("/downloadCorrectWordDataTemp")
    @OperaLogs
    public void downloadInterceptWordDataTemp(HttpServletResponse response) {
        correctWordKeyService.downloadCorrectWordDataTemp(response);
    }
}
