package com.wenge.model.controller;


import com.wenge.model.dto.param.KnowledgeApiBatchDeleteParamParam;
import com.wenge.model.dto.param.KnowledgeApiPageQueryParam;
import com.wenge.model.entity.KnowledgeApi;
import com.wenge.model.service.KnowledgeApiService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 知识库API采集
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-05-10 10:38:16
 */
@RestController
@RequestMapping("/knowledgeApi")
@Slf4j
@Api(tags = "知识库API采集接口")
public class KnowledgeApiController {


    @Autowired
    private KnowledgeApiService knowledgeApiService;


    /**
     * 新增或修改
     *
     * @param knowledgeApi
     * @return
     */
    @RequestMapping("/addOrUpdate")
    @ApiOperation(value = "新增或修改")
    public Result addOrUpdate(@RequestBody KnowledgeApi knowledgeApi) {
        return knowledgeApiService.addOrUpdate(knowledgeApi);
    }


    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    @RequestMapping("/pageQuery")
    @ApiOperation(value = "分页查询")
    public Result pageQuery(@RequestBody KnowledgeApiPageQueryParam param) {
        return knowledgeApiService.pageQuery(param);
    }


    /**
     * 根据id查询详情
     *
     * @param id
     * @return
     */
    @GetMapping("/queryDetailById")
    @ApiOperation(value = "根据id查询详情")
    public Result queryDetailById(@RequestParam(value = "id") Long id) {
        return knowledgeApiService.queryDetailById(id);
    }


    /**
     * 根据id列表批量删除
     *
     * @param param
     * @return
     */
    @PostMapping("/deleteByIdList")
    @ApiOperation(value = "根据id列表批量删除")
    public Result deleteByIdList(@RequestBody KnowledgeApiBatchDeleteParamParam param) {
        knowledgeApiService.deleteByIdList(param);
        return Result.success();
    }

    /**
     * 执行api
     *
     * @param knowledgeApi
     * @return
     */
    @PostMapping("/runApi")
    @ApiOperation(value = "执行api")
    public Result runApi(@RequestBody KnowledgeApi knowledgeApi) {
        return knowledgeApiService.runApi(knowledgeApi);
    }

}