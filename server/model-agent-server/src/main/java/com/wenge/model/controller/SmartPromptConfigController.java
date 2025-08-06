package com.wenge.model.controller;


import com.wenge.model.entity.SmartPromptConfig;
import com.wenge.model.service.SmartPromptConfigService;
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
 * Description: 文档提示词配置表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-16 17:26:10
 *
 */
@RestController
@RequestMapping("/smartPromptConfig")
@Slf4j
@Api(tags = "文档提示词配置表接口")
public class SmartPromptConfigController {

	/**
	 * 	文档提示词配置表服务类
	 */
	@Autowired
	private SmartPromptConfigService smartPromptConfigService;

	/**
	 * 新增文档提示词配置表
	 */
	@PostMapping("/addSmartPromptConfig")
	@ApiOperation(value = "新增文档提示词配置表", tags = "新增文档提示词配置表", notes = "新增文档提示词配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addSmartPromptConfig(@RequestBody SmartPromptConfig smartPromptConfig) {
		return smartPromptConfigService.addSmartPromptConfig(smartPromptConfig);
	}

	/**
	 * 查询文档提示词配置表列表
	 */
	@PostMapping("/getSmartPromptConfigList")
	@ApiOperation(value = "查询文档提示词配置表", tags = "查询文档提示词配置表", notes = "查询文档提示词配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getSmartPromptConfigList(@RequestBody SmartPromptConfig smartPromptConfig) {
		return smartPromptConfigService.getSmartPromptConfigList(smartPromptConfig);
	}

	/**
	 * 更新文档提示词配置表
	 */
	@PostMapping("/updateSmartPromptConfig")
	@ApiOperation(value = "更新文档提示词配置表", tags = "更新文档提示词配置表", notes = "更新文档提示词配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateSmartPromptConfig(@RequestBody SmartPromptConfig smartPromptConfig) {
		return smartPromptConfigService.updateSmartPromptConfig(smartPromptConfig);
	}

	/**
	 * 删除文档提示词配置表
	 */
	@PostMapping("/deleteSmartPromptConfig")
	@ApiOperation(value = "删除文档提示词配置表", tags = "删除文档提示词配置表", notes = "删除文档提示词配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteSmartPromptConfig(@RequestBody List<String> idList) {
		return smartPromptConfigService.deleteSmartPromptConfig(idList);
	}

}