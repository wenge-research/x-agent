package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ApplicationDatasetParam;
import com.wenge.model.entity.ApplicationDataset;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.service.ApplicationDatasetService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ApplicationEvaluationController
 * 
 * @author yjz
 * @date 2025-04-22
 */
@RestController
@RequestMapping("/applicationDateset")
@Api(tags = "Description:智能测评接口   @Author: yjz   Version: 1.0   Create Date Time: 2025-04-22 19:02:51")
@Slf4j
public class ApplicationDatasetController
{
    @Autowired
    private ApplicationDatasetService applicationDatesetService;

    /**
     * 查询ApplicationEvaluation列表
     */
    @PostMapping("/list")
    public Result<Page<ApplicationDataset>> list(@RequestBody ApplicationDatasetParam applicationDatasetParam)
    {
        return applicationDatesetService.selectApplicationEvaluationList(applicationDatasetParam);
    }
    /**
     * 获取ApplicationEvaluation详细信息
     */
    @PostMapping(value = "/select")
    public Result<List<ApplicationDataset>> getInfo(@RequestBody ApplicationDatasetParam applicationDatasetParam)
    {
        return applicationDatesetService.selectApplicationEvaluationById(applicationDatasetParam);
    }

    /**
     * 新增ApplicationEvaluation
     */
    @PostMapping("/insert")
    public Result<ApplicationDataset> add(@RequestBody ApplicationDatasetParam applicationDatasetParam)
    {
        return applicationDatesetService.insertApplicationEvaluation(applicationDatasetParam);
    }

    /**
     * 修改ApplicationEvaluation
     */
    @PostMapping("/update")
    public Result<ApplicationDataset> edit(@RequestBody ApplicationDatasetParam applicationDatesetParam)
    {
        return applicationDatesetService.updateApplicationEvaluation(applicationDatesetParam);
    }

    /**
     * 删除ApplicationEvaluation
     */
    @PostMapping("/delete")
    public Result<ApplicationDataset> remove(@RequestBody ApplicationDatasetParam applicationDatesetParam)
    {
        return applicationDatesetService.deleteApplicationEvaluationById(applicationDatesetParam);
    }
    /**
     * 导出xlsx文件
     */
    @ApiOperation(value = "导出xlsx文件",tags = "应用导出xlsx文件", notes = "应用导出xlsx文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/export")
    public Result export(@RequestBody ApplicationDatasetParam applicationDatesetParam, HttpServletResponse response) throws IOException {
        return applicationDatesetService.export(applicationDatesetParam,response);
    }
    /**
     * 导入xlsx文件
     */
    @ApiOperation(value = "导入xlsx文件",tags = "导入xlsx文件", notes = "应用导入xlsx文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/import")
    public Result importData(MultipartFile file,String datasetId) throws IOException {
        return applicationDatesetService.importData(file,datasetId);
    }
}
