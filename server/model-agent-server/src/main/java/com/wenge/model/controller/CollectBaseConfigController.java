package com.wenge.model.controller;


import com.wenge.model.dto.param.*;
import com.wenge.model.service.CollectBaseConfigService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
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
 * Description: 数据集的表配置接口
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 20:14:11
 *
 */
@RestController
@RequestMapping("/collectBaseConfig")
@Slf4j
@Api(tags = "数据集的表配置接口")
public class CollectBaseConfigController {

	/**
	 * 	数据集的表配置服务类
	 */
	@Autowired
	private CollectBaseConfigService collectBaseConfigService;

	/**
	 * 获取数据集的表配置
	 */
	@PostMapping("/getCollectBaseConfig")
	@ApiOperation(value = "获取数据集的表配置", tags = "获取数据集的表配置", notes = "获取数据集的表配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getCollectBaseConfig(@RequestBody StringParam param) {
		return collectBaseConfigService.getCollectBaseConfig(param);
	}

	/**
	 * 新增数据集的表配置
	 */
	@PostMapping("/addCollectBaseConfig")
	@ApiOperation(value = "新增数据集的表配置", tags = "新增数据集的表配置", notes = "新增数据集的表配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addCollectBaseConfig(CollectConfigAddParam collectBaseConfig) {
		return collectBaseConfigService.addCollectBaseConfig(collectBaseConfig);
	}

	/**
	 * 删除数据集的表配置
	 */
	@PostMapping("/deleteCollectBaseConfig")
	@ApiOperation(value = "删除数据集的表配置", tags = "删除数据集的表配置", notes = "删除数据集的表配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteCollectBaseConfig(@RequestBody List<String> idList) {
		return collectBaseConfigService.deleteCollectBaseConfig(idList);
	}

	/**
	 * 编辑数据集的数据
	 */
	@PostMapping("/editCollectData")
	@ApiOperation(value = "编辑数据集的数据", tags = "编辑数据集的数据", notes = "编辑数据集的数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result editCollectData(@RequestBody CollectDataEditParam editParam) {
		return collectBaseConfigService.editCollectData(editParam);
	}

	/**
	 * 编辑数据库
	 */
	@PostMapping("/editCollectConfig")
	@ApiOperation(value = "编辑数据库", tags = "编辑数据库", notes = "编辑数据库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result editCollectConfig(@RequestBody CollectConfigEditParam param) {
		return collectBaseConfigService.editCollectConfig(param);
	}

	/**
	 * 获取数据集的表数据
	 */
	@PostMapping("/getCollectDataList")
	@ApiOperation(value = "获取数据集的表数据", tags = "获取数据集的表数据", notes = "获取数据集的表数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getCollectDataList(@RequestBody CollectDataListParam param) {
		return collectBaseConfigService.getCollectDataList(param);
	}

	/**
	 * 获取问数配置表数据
	 */
	@PostMapping("/getWenShuConfig")
	@ApiOperation(value = "获取问数配置表数据", tags = "获取问数配置表数据", notes = "获取问数配置表数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getWenShuConfig(@RequestBody WenShuConfigGetParam param) {
		return collectBaseConfigService.getWenShuConfig(param);
	}

}