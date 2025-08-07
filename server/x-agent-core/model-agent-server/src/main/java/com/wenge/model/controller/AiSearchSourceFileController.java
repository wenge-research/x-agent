package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.AiSearchSourceFilePageParam;
import com.wenge.model.dto.result.AiSearchFileSizeCountResult;
import com.wenge.model.entity.AiSearchSourceFile;
import com.wenge.model.service.AiSearchSourceFileService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 文件接口
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-02-13 18:06:36
 */
@Api(tags = "Description: ai智能搜索接口   @Author: ZHAISHUAI   Version: 1.0   Create Date Time: 2025-02-13 17:46:36")
@RestController
@RequestMapping("/aiSearchSourceFile")
@Slf4j
public class AiSearchSourceFileController {

    /**
     * 文件服务类
     */
    @Autowired
    private AiSearchSourceFileService aiSearchSourceFileService;


    /**
     * 查询文件列表
     */
    @ApiOperation(value = "查询文件列表", tags = "查询文件列表", notes = "查询文件列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getFileList")
    public Result<Page<AiSearchSourceFile>> getFileList(@RequestBody AiSearchSourceFilePageParam param) {
        return aiSearchSourceFileService.getFileList(param);
    }

    /**
     * 收录数量
     */
    @ApiOperation(value = "收录数量")
    @GetMapping("/getFileSizeList")
    public Result<AiSearchFileSizeCountResult> getFileSizeList() {
        return aiSearchSourceFileService.getFileSizeList();
    }


}