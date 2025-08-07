package com.wenge.model.controller;


import com.wenge.model.dto.param.AppConfigListParam;
import com.wenge.model.entity.ApplicationPlugin;
import com.wenge.model.service.ApplicationPluginService;
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
 * Description: 应用插件接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-28 10:38:16
 *
 */
@RestController
@RequestMapping("/applicationPlugin")
@Slf4j
@Api(tags = "应用插件接口")
public class ApplicationPluginController {

	/**
	 * 	应用插件服务类
	 */
	@Autowired
	private ApplicationPluginService applicationPluginService;

	/**
	 * 新增应用插件
	 */
	@PostMapping("/addApplicationPlugin")
	@ApiOperation(value = "新增应用插件", tags = "新增应用插件", notes = "新增应用插件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addApplicationPlugin(@RequestBody ApplicationPlugin plugin) {
		return applicationPluginService.addApplicationPlugin(plugin);
	}

	/**
	 * 查询应用插件列表
	 */
	@PostMapping("/getApplicationPluginList")
	@ApiOperation(value = "查询应用插件", tags = "查询应用插件", notes = "查询应用插件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<ApplicationPlugin>>  getApplicationPluginList(@RequestBody AppConfigListParam param) {
		return applicationPluginService.getApplicationPluginList(param);
	}

	/**
	 * 更新应用插件
	 */
	@PostMapping("/updateApplicationPlugin")
	@ApiOperation(value = "更新应用插件", tags = "更新应用插件", notes = "更新应用插件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateApplicationPlugin(@RequestBody ApplicationPlugin applicationPlugin) {
		return applicationPluginService.updateApplicationPlugin(applicationPlugin);
	}

	/**
	 * 删除应用插件
	 */
	@PostMapping("/deleteApplicationPlugin")
	@ApiOperation(value = "删除应用插件", tags = "删除应用插件", notes = "删除应用插件", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteApplicationPlugin(@RequestBody List<String> idList) {
		return applicationPluginService.deleteApplicationPlugin(idList);
	}

}