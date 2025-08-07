package com.wenge.oauth.controller;

import com.wenge.oauth.annotation.UmsOperationLog;
import com.wenge.oauth.dto.param.UmsOperationParam;
import com.wenge.oauth.entity.UmsOperation;
import com.wenge.oauth.service.UmsOperationService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @description: 系统接口日志控制器
 * @author: caohaifeng
 * @date: 2024/8/23 15:02
 **/
@RestController
@RequestMapping("/umsOperation")
@Slf4j
public class UmsOperationController {


    @Autowired
    private UmsOperationService umsOperationService;

    @ApiOperation(value = "新增日志记录")
    @PostMapping("/addUmsOperationg")
    @UmsOperationLog(description = "新增日志记录", logType = 1, belongModule = "operateLog", belongModuleName = "日志管理", objectType = "日志", objectName = "新增日志", objectUuid = "--")
    public Result addUmsOperationg(@RequestBody UmsOperation umsOperation) {
        return umsOperationService.addUmsOperationg(umsOperation);
    }

    @ApiOperation(value = "查阅日志列表")
    @PostMapping("/umsOperationList")
    @UmsOperationLog(description = "查阅日志列表", logType = 1, belongModule = "operateLog", belongModuleName = "日志管理", objectType = "日志", objectName = "日志列表", objectUuid = "--")
    public Result umsOperationList(@RequestBody UmsOperationParam umsOperationParam) {
        try {
            return umsOperationService.umsOperationList(umsOperationParam);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

    @GetMapping("/getLogTypeList")
    public Result getLogTypeList() {
        return umsOperationService.getLogTypeList();
    }

}