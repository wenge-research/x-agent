package com.wenge.model.controller;

import com.wenge.model.entity.AiSearchDataAnalysis;
import com.wenge.model.entity.AiSearchFileSubHistory;
import com.wenge.model.service.AiSearchDataAnalysisService;
import com.wenge.model.service.AiSearchFileSubHistoryService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Description: ai智能搜索数据统计相关接口")
@RestController
@RequestMapping("/aiSearchDataAnalysis")
@Slf4j
public class AiSearchDataAnalysisController {

    @Autowired
    private AiSearchFileSubHistoryService aiSearchFileSubHistoryService;

    @Autowired
    private AiSearchDataAnalysisService aiSearchDataAnalysisService;

    /**
     * 订阅或取消订阅
     */
    @ApiOperation(value = "订阅或取消订阅", tags = "订阅或取消订阅", notes = "订阅或取消订阅", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/subOrUnSub")
    public Result subOrUnSub(@RequestBody AiSearchFileSubHistory aiSearchFileSubHistory) {
       return aiSearchFileSubHistoryService.subOrUnSub(aiSearchFileSubHistory);
    }

    /**
     * 浏览文档
     */
    @ApiOperation(value = "浏览文档", tags = "浏览文档", notes = "浏览文档", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/read")
    public Result read(@RequestBody AiSearchDataAnalysis aiSearchDataAnalysis) {
        return aiSearchDataAnalysisService.read(aiSearchDataAnalysis);
    }

    /**
     * 点赞文档
     */
    @ApiOperation(value = "点赞文档", tags = "点赞文档", notes = "点赞文档", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/like")
    public Result like(@RequestBody AiSearchDataAnalysis aiSearchDataAnalysis) {
        return aiSearchDataAnalysisService.like(aiSearchDataAnalysis);
    }
}
