package com.wenge.model.controller;


import com.wenge.model.dto.param.ReviewResultsParam;
import com.wenge.model.entity.ReviewResults;
import com.wenge.model.service.ReviewResultsService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/reviewResults")
@Slf4j
@Api(tags = "审查结果接口")
public class ReviewResultsController {

    @Autowired
    private ReviewResultsService reviewResultsService;

    @PostMapping("/batchSave")
    @ApiOperation(value = "新增审查结果", tags = "新增审查结果", notes = "新增审查结果", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result batchSave(@RequestBody ReviewResultsParam reviewResults) {
        return reviewResultsService.batchSave(reviewResults);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新审查结果", tags = "更新审查结果", notes = "更新审查结果", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result update(@RequestBody ReviewResultsParam reviewResults) {
        return reviewResultsService.update(reviewResults);
    }

    @PostMapping("/getList")
    @ApiOperation(value = "查询审查结果", tags = "查询审查结果", notes = "查询审查结果", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result getList(@RequestBody ReviewResults reviewResults) {
        return reviewResultsService.getList(reviewResults);
    }


    @PostMapping("/deleteByIds")
    @ApiOperation(value = "删除审查结果", tags = "删除审查结果", notes = "删除审查结果", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result deleteByIds(@RequestBody List<String> idList) {
        return reviewResultsService.deleteByIds(idList);
    }

}