package com.wenge.model.controller;

import com.wenge.model.dto.param.DeleteKnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeDataPageParam;
import com.wenge.model.dto.param.ScoreDataParam;
import com.wenge.model.dto.param.importKnowledgeDataParam;
import com.wenge.model.dto.result.KnowledgeDataResult;
import com.wenge.model.dto.result.KnowledgeDataScopeResult;
import com.wenge.model.dto.result.ScoreDataResult;
import com.wenge.model.entity.KnowledgeData;
import com.wenge.model.service.KnowledgeDataService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 知识库-QA详细数据
 */
@Api(tags = "知识库-QA详细数据")
@RestController
@RequestMapping("/knowledgeData")
@Slf4j
public class knowledgeDataController {

    @Autowired
    private KnowledgeDataService knowledgeDataService;

    /**
     * 新增知识库数据
     */
    @ApiOperation(value = "新增知识库数据",tags = "新增知识库数据", notes = "新增知识库数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/addKnowledgeData")
    @OperaLogs
    public Result addKnowledgeData(@RequestBody KnowledgeData knowledgeData) {
        // 获取当前用户信息
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        if (tokenUserInfo == null) {
            return Result.fail("用户信息获取失败");
        }
        knowledgeData.setDataSource(1 + "");
        knowledgeData.setDeptId(tokenUserInfo.getDeptId());
        knowledgeData.setDeptName(tokenUserInfo.getDeptName());
        knowledgeData.setUserId(tokenUserInfo.getId() + "");
        knowledgeData.setUserName(tokenUserInfo.getUserName());
        // 简单处理防止""存日期报错
        if (StringUtils.isBlank(knowledgeData.getEffectiveEndTime())) {
            knowledgeData.setEffectiveEndTime("2100-01-01 00:00:00");
        }
        if (StringUtils.isBlank(knowledgeData.getEffectiveStartTime())) {
            knowledgeData.setEffectiveStartTime("1980-01-01 00:00:00");
        }
        return knowledgeDataService.addKnowledgeData(knowledgeData);
    }

    /**
     * 查询知识库数据
     */
    @ApiOperation(value = "查询知识库数据",tags = "查询知识库数据", notes = "查询知识库数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getKnowledgeDataList")
    public Result<EsPageInfo<KnowledgeDataResult>> getKnowledgeDataList(@RequestBody KnowledgeDataPageParam param) {
        return knowledgeDataService.getKnowledgeDataList(param);
    }

    /**
     * 导入知识库数据
     *
     * @return
     */
    @ApiOperation(value = "导入知识库数据",tags = "导入知识库数据", notes = "导入知识库数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/importKnowledgeData")
    @OperaLogs
    public Result<String> importKnowledgeData(importKnowledgeDataParam param) throws IOException {
        return knowledgeDataService.importKnowledgeData(param);
    }

    /**
     * 下载模板
     *
     * @return
     */
    @ApiOperation(value = "下载模板 @return",tags = "下载模板 @return", notes = "下载模板 @return", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @GetMapping("/downloadKnowledgeDataTemp")
    @OperaLogs
    public void downloadKnowledgeDataTemp(HttpServletResponse response) {
        knowledgeDataService.downloadKnowledgeDataTemp(response);
    }

    /**
     * 删除知识库数据
     */
    @ApiOperation(value = "删除知识库数据",tags = "删除知识库数据", notes = "删除知识库数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/deleteKnowledgeData")
    @OperaLogs
    public Result deleteKnowledgeData(@RequestBody DeleteKnowledgeDataParam param) {
        return knowledgeDataService.deleteKnowledgeData(param);
    }

    /**
     * 导出QA数据
     * @param vectorLibraryDataParam
     * @return
     */
    @PostMapping("/exportData")
    public void exportData(@RequestBody KnowledgeDataPageParam vectorLibraryDataParam, HttpServletResponse response) {
        knowledgeDataService.exportData(vectorLibraryDataParam, response);
    }

    /**
     * 查看知识库分数详情
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/getKnowledgeDataScope")
    @ApiOperation(value = "知识库根据相似度阀值对比查询相似数据列表")
    public Result<List<List<KnowledgeDataScopeResult>>> getKnowledgeDataScope(@RequestBody KnowledgeDataPageParam param) {
        return Result.success(knowledgeDataService.getKnowledgeDataScope(param));
    }

    /**
     * 查询删除列表
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/getDeleteKnowledgeData")
    @ApiOperation(value = "知识库根据相似度阀值对比查询相似数据列表")
    public Result<EsPageInfo<KnowledgeData>> getDeleteKnowledgeData(@RequestBody KnowledgeDataPageParam param) {
        return Result.success(knowledgeDataService.getDeleteKnowledgeData(param));
    }

    /**
     * 恢复删除数据
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/recoverKnowledgeData")
    @ApiOperation(value = "恢复删除数据")
    public Result<EsPageInfo<KnowledgeData>> recoverKnowledgeData(@RequestBody DeleteKnowledgeDataParam param) {
        return knowledgeDataService.recoverKnowledgeData(param);
    }
}
