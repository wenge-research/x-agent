package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.model.constants.AnswerStrategyContant;
import com.wenge.model.dto.param.ApplicationDatasetParam;
import com.wenge.model.dto.param.ApplicationEvaluationParam;
import com.wenge.model.entity.ApplicationDataset;
import com.wenge.model.entity.ApplicationEvaluation;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.mapper.ApplicationInfoMapper;
import com.wenge.model.service.ApplicationEvaluationService;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.LlmInfoService;
import com.wenge.model.service.impl.DialogueServiceImpl;
import com.wenge.model.strategy.llmStrategy.LlmStrategy;
import com.wenge.model.utils.AnswerUtils;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.wenge.model.entity.table.ApplicationInfoTableDef.APPLICATION_INFO;

/**
 * ApplicationEvaluationController
 * 
 * @author yjz
 * @date 2025-04-22
 */
@RestController
@RequestMapping("/applicationEvaluation")
@Api(tags = "Description:智能测评接口   @Author: yjz   Version: 1.0   Create Date Time: 2025-04-22 19:02:51")
@Slf4j
public class ApplicationEvaluationController
{
    @Autowired
    private ApplicationEvaluationService applicationEvaluationService;
    @Autowired
    private LlmInfoService llmInfoService;
    @Autowired
    private AnswerUtils answerUtils;
    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;
    /**
     * 查询ApplicationEvaluation列表
     */
    @PostMapping("/list")
    public Result<Page<ApplicationEvaluation>> list(@RequestBody ApplicationEvaluationParam applicationEvaluation)
    {
        return applicationEvaluationService.selectApplicationEvaluationList(applicationEvaluation);
    }
    /**
     * 获取ApplicationEvaluation详细信息
     */
    @PostMapping(value = "/select")
    public Result<ApplicationEvaluation> getInfo(@RequestBody ApplicationEvaluationParam applicationEvaluation)
    {
        return applicationEvaluationService.selectApplicationEvaluationById(applicationEvaluation);
    }

    /**
     * 新增ApplicationEvaluation
     */
    @PostMapping("/insert")
    public Result<ApplicationEvaluation> add(@RequestBody ApplicationEvaluationParam applicationEvaluation)
    {
        return applicationEvaluationService.insertApplicationEvaluation(applicationEvaluation);
    }

    /**
     * 修改ApplicationEvaluation
     */
    @PostMapping("/update")
    public Result<ApplicationEvaluation> edit(@RequestBody ApplicationEvaluationParam applicationEvaluation)
    {
        return applicationEvaluationService.updateApplicationEvaluation(applicationEvaluation);
    }

    /**
     * 删除ApplicationEvaluation
     */
    @PostMapping("/delete")
    public Result<ApplicationEvaluation> remove(@RequestBody ApplicationEvaluationParam applicationEvaluation)
    {
        return applicationEvaluationService.deleteApplicationEvaluationById(applicationEvaluation);
    }
    /**
     * 根据modelId调取大模型
     */
    @PostMapping("/modelId")
    public Result<List<ApplicationDataset>> answerModel(@RequestBody ApplicationEvaluationParam applicationEvaluation)
    {
        return applicationEvaluationService.answerModel(applicationEvaluation);
    }
    /**
     * 导出xlsx文件
     */
    @ApiOperation(value = "导出xlsx文件",tags = "应用导出xlsx文件", notes = "应用导出xlsx文件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/export")
    public Result export(@RequestBody ApplicationEvaluationParam applicationDatesetParam, HttpServletResponse response) throws IOException {
        return applicationEvaluationService.export(applicationDatesetParam,response);
    }
    @ApiOperation(value = "下载测评模板",tags = "下载测评模板", notes = "下载测评模板", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @GetMapping("/downloadEvaluationDataTemp")
    @OperaLogs
    public void downloadInterceptWordDataTemp(HttpServletResponse response) {
        applicationEvaluationService.downloadInterceptWordDataTemp(response);
    }
    @ApiOperation(value = "下载测评模板2",tags = "下载测评模板2", notes = "下载测评模板2", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @GetMapping("/downloadEvaluationDataTemp2")
    @OperaLogs
    public void downloadInterceptWordDataTemp2(HttpServletResponse response) {
        applicationEvaluationService.downloadInterceptWordDataTemp2(response);
    }
}
