package com.wenge.model.controller;


import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.dto.param.ApplicationConfigEditParam;
import com.wenge.model.entity.ApplicationPluginData;
import com.wenge.model.service.ApplicationPluginDataService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 服务类应用插件关联表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 15:31:30
 *
 */
@RestController
@RequestMapping("/applicationPluginData")
@Slf4j
@Api(tags = "应用插件关联表接口")
public class ApplicationPluginDataController {

	/**
	 * 	服务类应用插件关联表
	 */
	@Autowired
	private ApplicationPluginDataService applicationPluginDataService;

	/**
	 * 新增应用插件关联表
	 */
	@PostMapping("/addApplicationPluginData")
	@ApiOperation(value = "新增应用插件关联表", tags = "新增应用插件关联表", notes = "新增应用插件关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addApplicationPluginData(@RequestBody ApplicationConfigEditParam param) {
		return applicationPluginDataService.addApplicationPluginData(param);
	}

	/**
	 * 查询列表应用插件关联表
	 */
	@PostMapping("/getApplicationPluginDataList")
	@ApiOperation(value = "查询列表应用插件关联表", tags = "查询列表应用插件关联表", notes = "查询列表应用插件关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<ApplicationPluginData>> getApplicationPluginDataList(@RequestBody AppConfigListParam param) {
		return applicationPluginDataService.getApplicationPluginDataList(param);
	}

	/**
	 * 更新应用插件关联表
	 */
	@PostMapping("/updateApplicationPluginData")
	@ApiOperation(value = "更新应用插件关联表", tags = "更新应用插件关联表", notes = "更新应用插件关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateApplicationPluginData(@RequestBody ApplicationPluginData applicationPluginData) {
		return applicationPluginDataService.updateApplicationPluginData(applicationPluginData);
	}

	/**
	 * 删除应用插件关联表
	 */
	@PostMapping("/deleteApplicationPluginData")
	@ApiOperation(value = "删除应用插件关联表", tags = "删除应用插件关联表", notes = "删除应用插件关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteApplicationPluginData(@RequestBody List<String> idList) {
		return applicationPluginDataService.deleteApplicationPluginData(idList);
	}

}