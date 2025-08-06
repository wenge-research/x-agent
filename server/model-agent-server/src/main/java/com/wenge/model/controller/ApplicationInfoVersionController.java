package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ApplicationInfoPageParam;
import com.wenge.model.dto.param.ApplicationInfoVersionParam;
import com.wenge.model.dto.param.ApplicationKnowledgeBindedParam;
import com.wenge.model.dto.param.PromptGenerateParam;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.entity.ApplicationInfoVersion;
import com.wenge.model.entity.ApplicationPublishRecord;
import com.wenge.model.service.ApplicationApiMarkdownService;
import com.wenge.model.service.ApplicationInfoService;
import com.wenge.model.service.ApplicationInfoVersionService;
import com.wenge.model.service.ApplicationPublishRecordService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

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
public class ApplicationInfoVersionController {

	/**
	 * 	应用信息服务类
	 */
	@Autowired
	private ApplicationInfoService applicationInfoService;
	@Autowired
	private ApplicationInfoVersionService applicationInfoVersionService;
	/**
	 * 查询应用版本信息列表
	 */
    @ApiOperation(value = "查询应用版本信息列表",tags = "查询应用版本信息列表", notes = "查询应用版本信息列表", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	//@PostMapping("/getApplicationVersionInfoList")
	public Result<Page<ApplicationInfoVersion>> getApplicationInfoList(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.getApplicationInfoList(applicationversionInfo);
	}
	/**
	 * 应用版本回退
	 */
    @ApiOperation(value = "应用版本回退",tags = "更新应用信息", notes = "更新应用信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	//@PostMapping("/backUpdateApplicationInfo")
	@OperaLogs
	public Result<ApplicationInfoVersion> updateApplicationInfo(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.backUpdateApplicationVersionInfo(applicationversionInfo);
	}
	/**
	 * 应用版本说明修改
	 */
	@ApiOperation(value = "应用版本说明修改",tags = "应用版本说明修改", notes = "应用版本说明修改", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	//@PostMapping("/updateApplicationVersionInfo")
	@OperaLogs
	public Result<ApplicationInfoVersion> updateApplicationVersionInfo(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.updateApplicationVersionInfo(applicationversionInfo);
	}
	/**
	 * 应用版本删除
	 */
	@ApiOperation(value = "应用版本删除",tags = "应用版本删除", notes = "应用版本删除", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	//@PostMapping("/deleteApplicationVersionInfo")
	@OperaLogs
	public Result<ApplicationInfoVersion> deleteApplicationVersionInfo(@RequestBody ApplicationInfoVersionParam applicationversionInfo) {
		return applicationInfoVersionService.deleteApplicationVersionInfo(applicationversionInfo);
	}
}