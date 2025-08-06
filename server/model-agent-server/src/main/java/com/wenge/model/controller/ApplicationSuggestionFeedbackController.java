package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.dto.param.ApplicationSuggestionFeedbackPageParam;
import com.wenge.model.entity.ApplicationSuggestionFeedback;
import com.wenge.model.service.ApplicationSuggestionFeedbackService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/suggestionFeedback")
@Slf4j
@Api("应用建议与反馈")
public class ApplicationSuggestionFeedbackController {

    @Autowired
    private ApplicationSuggestionFeedbackService applicationSuggestionFeedbackService;

    /**
     * 新增建议&反馈
     */
    @ApiOperation(value = "新增建议&反馈",tags = "新增建议&反馈", notes = "新增建议&反馈", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/add")
    @OperaLogs
    public Result add(@RequestBody ApplicationSuggestionFeedback applicationSuggestionFeedback) {
        return applicationSuggestionFeedbackService.add(applicationSuggestionFeedback);
    }


    /**
     * 删除审核记录
     */
    @ApiOperation(value = "删除建议&反馈",tags = "删除建议&反馈", notes = "删除建议&反馈", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/delete")
    @OperaLogs
    public Result delete(@RequestBody ApplicationSuggestionFeedback applicationSuggestionFeedback) {
        return applicationSuggestionFeedbackService.delete(applicationSuggestionFeedback);
    }

    /**
     * 查询 分页
     * @return
     */
    @ApiOperation(value = "查询建议&反馈 分页",tags = "查询建议&反馈 分页", notes = "查询建议&反馈 分页", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getListPage")
    public Result<Page<ApplicationSuggestionFeedback>> getListPage(@RequestBody ApplicationSuggestionFeedbackPageParam param) {
        return applicationSuggestionFeedbackService.getListPage(param);
    }

//    /**
//     * 编辑审核记录
//     */
//    @ApiOperation(value = "编辑审核记录",tags = "编辑审核记录", notes = "编辑审核记录", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
//    @PostMapping("/update")
//    @OperaLogs
//    public Result update(@RequestBody ApplicationSuggestionFeedback applicationSuggestionFeedback) {
//        return applicationSuggestionFeedbackService.update(applicationSuggestionFeedback);
//    }


    @ApiOperation(value = "根据id获取详情", tags = "根据id获取详情", notes = "根据id获取详情", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
    @GetMapping("/getInfoById")
    @OperaLogs
    public Result getInfoById(@RequestParam("id") Integer id) {
        return Result.success(applicationSuggestionFeedbackService.getById(id));
    }

    @ApiOperation(value = "导出建议&反馈")
    @PostMapping("/export")
    public void export(@RequestBody ApplicationSuggestionFeedbackPageParam param, HttpServletResponse response) {
        applicationSuggestionFeedbackService.export(param, response);
    }
}
