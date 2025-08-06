package com.wenge.model.controller;

import com.wenge.model.dto.param.ApplicationInfoDialogueParam;
import com.wenge.model.entity.ApplicationInfoDialogue;
import com.wenge.model.service.ApplicationInfoDialogueRecordService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Description: 应用信息对话日志信息接口
 * @Author: yijiazheng
 * Version: 1.0
 * Create Date Time: 2025-04-08 11:02:51
 *
 */
@Api(tags = "Description: 应用信息对话日志信息接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2025-04-08 11:02:51")
@RestController
@RequestMapping("/applicationDialogueRecordInfo")
@Slf4j
public class ApplicationInfoDialogueRecordController {
    @Resource
    private ApplicationInfoDialogueRecordService applicationInfoDialogueRecordService;
    /**
     * 插入应用信息对话日志信息
     */
    @ApiOperation(value = "插入应用信息对话日志信息",tags = "插入应用信息对话日志信息", notes = "插入应用信息对话日志信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/saveDialogueRecord")
    @OperaLogs
    public Result<ApplicationInfoDialogue> saveApplicationInfoDialogue(@RequestBody ApplicationInfoDialogueParam applicationInfoDialogueParam) {
        return applicationInfoDialogueRecordService.saveApplicationInfoDialogue(applicationInfoDialogueParam);
    }
}
