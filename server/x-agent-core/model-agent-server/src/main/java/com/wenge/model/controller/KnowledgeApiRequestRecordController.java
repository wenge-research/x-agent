package com.wenge.model.controller;


import com.wenge.model.dto.param.KnowledgeApiRequestRecordPageQueryParam;
import com.wenge.model.service.KnowledgeApiRequestRecordService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 知识库API采集任务记录
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-05-10 10:38:16
 */
@RestController
@RequestMapping("/knowledgeApiRequestRecord")
@Slf4j
@Api(tags = "知识库API采集任务记录")
public class KnowledgeApiRequestRecordController {


    @Autowired
    private KnowledgeApiRequestRecordService knowledgeApiRequestRecordService;


    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @RequestMapping("/pageQuery")
    @ApiOperation(value = "分页查询")
    public Result pageQuery(@RequestBody KnowledgeApiRequestRecordPageQueryParam param) {
        return knowledgeApiRequestRecordService.pageQuery(param);
    }

}