package com.wenge.model.controller;

import com.wenge.model.dto.param.HKParam;
import com.wenge.model.service.HKPOCService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 香港文件内容提取
 */
@RestController
@RequestMapping("/hk")
@Slf4j
@Api(tags = "关芯智巡的事项判别表接口")
public class HKPOCController {

    @Autowired
    private HKPOCService hKPOCService;

    /**
     * 选择文件获取请求id
     */
    @PostMapping("/queryRequestId")
    @ApiOperation(value = "选择文件获取请求id", tags = "选择文件获取请求id", notes = "选择文件获取请求id", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result queryRequestId(@RequestBody HKParam hkParam) {
        return hKPOCService.queryRequestId(hkParam);
    }


    @PostMapping("/questFilesByRequestId")
    @ApiOperation(value = "查询文件id列表根据请求id", tags = "查询文件id列表根据请求id", notes = "查询文件id列表根据请求id", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result questFilesByRequestId(@RequestBody HKParam hkParam) {
        return hKPOCService.questFilesByRequestId(hkParam);
    }

    @ApiOperation(value = "根据请求id查询文件列表", tags = "根据请求id查询文件列表", notes = "根据请求id查询文件列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getFileListByFileIds")
    public Result getFileListByFileIds(@RequestBody HKParam hkParam) {
        return hKPOCService.getFileListByFileIds(hkParam);
    }

}
