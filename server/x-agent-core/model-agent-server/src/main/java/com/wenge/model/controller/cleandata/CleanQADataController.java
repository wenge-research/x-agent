package com.wenge.model.controller.cleandata;

import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.service.ApplicationAnalysisService;
import com.wenge.model.service.CleanQADataService;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "清洗历史缺失字段数据")
@RestController
@RequestMapping("/cleanQAData")
@Slf4j
public class CleanQADataController {

    /**
     * 应用信息服务类
     */
    @Autowired
    private CleanQADataService cleanQADataService;


    /**
     * @description: 使用对话日志中的userName字段补全userId字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    @ApiOperation(value = "使用对话日志中的userName字段补全userId字段")
    @GetMapping("/useingUserNameToUserId")
    @UmsOperationLog(description = "使用对话日志中的userName字段补全userId字段", logType = 3, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用管理", objectName = "--", objectUuid = "--")
    public Result useinguserNameToUserId() {
        return cleanQADataService.useinguserNameToUserId();
    }

    /**
     * @description: 使用对话日志中的feedbackUserId字段补全deptId相关字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    @ApiOperation(value = "使用对话日志中的feedbackUserId字段补全deptId相关字段")
    @GetMapping("/useingFeedbackUserIdToDeptId")
    @UmsOperationLog(description = "使用对话日志中的userName字段补全userId字段", logType = 3, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用管理", objectName = "--", objectUuid = "--")
    public Result useingFeedbackUserIdToDeptId() {
        return cleanQADataService.useingFeedbackUserIdToDeptId();
    }


    /**
     * @description: 补全QA问题中的userId deptId字段
     * @author: caohaifeng
     * @date: 2024/9/25 11:03
     **/
    @ApiOperation(value = "补全QA问题中的userId deptId字段")
    @GetMapping("/useingQAuserIdDeptId")
    @UmsOperationLog(description = "补全QA问题中的userId deptId字段", logType = 3, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用管理", objectName = "--", objectUuid = "--")
    public Result useingQAuserIdDeptId() {
        return cleanQADataService.useingQAuserIdDeptId();
    }



}
