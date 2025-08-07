package com.wenge.model.controller;


import com.wenge.model.dto.param.ApplicationOverviewIndicatorsParam;
import com.wenge.model.service.ApplicationAnalysisService;
import com.wenge.model.service.OperationManagementService;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: caohaifeng
 * @date: 2024/12/30 15:48
 * @Description: 运营管理-控制层
 * @Version:1.0
 */
@Api(tags = "运营管理-控制层")
@RestController
@RequestMapping("/operationManagement")
@Slf4j
public class OperationManagementController {

	/**
	 * 	应用信息服务类
	 */
	@Autowired
	private ApplicationAnalysisService applicationAnalysisService;


	/**
	 * 	运营服务类
	 */
	@Autowired
	private OperationManagementService operationManagementService;


	@ApiOperation(value = "应用总数")
	@GetMapping("/applicationCount")
	@UmsOperationLog(description = "运营-应用总数", logType = 1, belongModule = "operationsManagement", belongModuleName = "运营", objectType = "运营", objectName = "--", objectUuid = "--")
	public Result applicationCount() {
		return operationManagementService.applicationCount();
	}

	@ApiOperation(value = "知识库总数")
	@GetMapping("/knowledgeCount")
	@UmsOperationLog(description = "运营-知识库总数", logType = 1, belongModule = "operationsManagement", belongModuleName = "运营", objectType = "运营", objectName = "--", objectUuid = "--")
	public Result knowledgeCount() {
		return operationManagementService.knowledgeCount();
	}


	@ApiOperation(value = "总使用量、总使用用户数")
	@GetMapping("/getUsingInfo")
	@UmsOperationLog(description = "运营-总使用量、总使用用户数", logType = 1, belongModule = "operationsManagement", belongModuleName = "运营", objectType = "运营", objectName = "--", objectUuid = "--")
	public Result getUsingInfo() {
		return operationManagementService.getUsingInfo();
	}

	@ApiOperation(value = "产生问答总数")
	@GetMapping("/getQaCount")
	@UmsOperationLog(description = "运营-产生问答总数", logType = 1, belongModule = "operationsManagement", belongModuleName = "运营", objectType = "运营", objectName = "--", objectUuid = "--")
	public Result getQaCount() {
		return operationManagementService.getQaCount();
	}


	@ApiOperation(value = "运营-查阅问题排行榜Top50/topN")
	@PostMapping("/questionChartsTop50")
	@UmsOperationLog(description = "运营-查阅问题排行榜Top50/topN", logType = 1, belongModule = "appmanage", belongModuleName = "运营", objectType = "运营", objectName = "--", objectUuid = "--")
	public Result questionChartsTop50(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		return operationManagementService.questionChartsTop50(applicationOverviewIndicatorsParam);
	}


	@ApiOperation(value = "运营-token消耗")
	@PostMapping("/getTokenConsumption")
	@UmsOperationLog(description = "运营-token消耗", logType = 1, belongModule = "appmanage", belongModuleName = "运营", objectType = "运营", objectName = "--", objectUuid = "--")
	public Result getTokenConsumption(@RequestBody ApplicationOverviewIndicatorsParam applicationOverviewIndicatorsParam) {
		return operationManagementService.getTokenConsumption(applicationOverviewIndicatorsParam);
	}


}