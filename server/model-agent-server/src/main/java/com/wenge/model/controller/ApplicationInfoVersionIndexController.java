package com.wenge.model.controller;

import com.wenge.model.dto.param.ApplicationInfoVersionParam;
import com.wenge.model.entity.ApplicationInfoVersionIndex;
import com.wenge.model.service.ApplicationInfoVersionIndexService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 应用版本信息接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-03 19:02:51
 *
 */
@Api(tags = "Description: 应用版本信息接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-03 19:02:51")
@RestController
@RequestMapping("/applicationVersionInfo")
@Slf4j
public class ApplicationInfoVersionIndexController {

	@Autowired
	private ApplicationInfoVersionIndexService applicationInfoVersionService;
	/**
	 * 查询应用版本信息列表
	 */
    @ApiOperation(value = "查询应用版本信息列表",tags = "查询应用版本信息列表", notes = "查询应用版本信息列表", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationVersionInfoList")
	public Result<EsPageInfo<ApplicationInfoVersionIndex>> getApplicationInfoList(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.getApplicationInfoList(applicationversionInfo);
	}
	/**
	 * 应用版本回退
	 */
    @ApiOperation(value = "应用版本回退",tags = "更新应用信息", notes = "更新应用信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/backUpdateApplicationInfo")
	@OperaLogs
	public Result<ApplicationInfoVersionIndex> updateApplicationInfo(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.backUpdateApplicationVersionInfo(applicationversionInfo);
	}
	/**
	 * 应用版本说明修改
	 */
	@ApiOperation(value = "应用版本说明修改",tags = "应用版本说明修改", notes = "应用版本说明修改", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateApplicationVersionInfo")
	@OperaLogs
	public Result<ApplicationInfoVersionIndex> updateApplicationVersionInfo(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.updateApplicationVersionInfo(applicationversionInfo);
	}
	/**
	 * 应用版本删除
	 */
	@ApiOperation(value = "应用版本删除",tags = "应用版本删除", notes = "应用版本删除", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteApplicationVersionInfo")
	@OperaLogs
	public Result<ApplicationInfoVersionIndex> deleteApplicationVersionInfo(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.deleteApplicationVersionInfo(applicationversionInfo);
	}
}