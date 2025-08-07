package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ApplicationInfoDialogueParam;
import com.wenge.model.dto.param.ApplicationInfoVersionParam;
import com.wenge.model.entity.ApplicationInfoDialogue;
import com.wenge.model.entity.ApplicationInfoVersion;
import com.wenge.model.service.ApplicationInfoDialogueService;
import com.wenge.model.service.ApplicationInfoVersionService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/applicationDialogueInfo")
@Slf4j
public class ApplicationInfoDialogueController {
	@Resource
	private ApplicationInfoDialogueService applicationInfoDialogueService;

	/**
	 * 查询应用信息对话日志信息列表
	 */
    @ApiOperation(value = "查询应用信息对话日志信息列表",tags = "查询应用信息对话日志信息列表", notes = "查询应用信息对话日志信息列表", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationInfoDialogueList")
	public Result<Page<ApplicationInfoDialogue>> getApplicationInfoDialogueList(@RequestBody ApplicationInfoDialogueParam applicationInfoDialogueParam) {
		return applicationInfoDialogueService.getApplicationInfoDialogueList(applicationInfoDialogueParam);
	}
	/**
	 * 插入应用信息对话日志信息
	 */
	@ApiOperation(value = "插入应用信息对话日志信息",tags = "插入应用信息对话日志信息", notes = "插入应用信息对话日志信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/insertApplicationInfoDialogue")
	@OperaLogs
	public Result<ApplicationInfoDialogue> insertApplicationInfoDialogue(@RequestBody ApplicationInfoDialogueParam applicationInfoDialogueParam) {
		return applicationInfoDialogueService.insertApplicationInfoDialogue(applicationInfoDialogueParam);
	}
	/**
	 * 插入应用信息对话日志信息
	 */
	@ApiOperation(value = "插入应用信息对话日志信息",tags = "插入应用信息对话日志信息", notes = "插入应用信息对话日志信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/saveApplicationInfoDialogue")
	@OperaLogs
	public Result<ApplicationInfoDialogue> saveApplicationInfoDialogue(@RequestBody ApplicationInfoDialogueParam applicationInfoDialogueParam) {
		return applicationInfoDialogueService.insertApplicationInfoDialogue(applicationInfoDialogueParam);
	}
	/**
	 * 应用信息对话日志信息删除
	 */
	@ApiOperation(value = "应用信息对话日志信息删除",tags = "应用信息对话日志信息删除", notes = "应用信息对话日志信息删除", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteApplicationInfoDialogue")
	@OperaLogs
	public Result<ApplicationInfoDialogue> deleteApplicationInfoDialogue(@RequestParam("id") Integer id) {
		return null;
	}
}