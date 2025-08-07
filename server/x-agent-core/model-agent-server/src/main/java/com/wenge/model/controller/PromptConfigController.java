package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.PromptConfigListParam;
import com.wenge.model.entity.PromptConfig;
import com.wenge.model.service.PromptConfigService;
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
 * Description: 提示词配置接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-12-18 11:02:37
 *
 */
@RestController
@RequestMapping("/promptConfig")
@Slf4j
@Api(tags = "提示词配置接口")
public class PromptConfigController {

	/**
	 * 	提示词配置服务类
	 */
	@Autowired
	private PromptConfigService promptConfigService;

	/**
	 * 新增提示词配置
	 */
	@PostMapping("/addPromptConfig")
	@ApiOperation(value = "新增提示词配置", tags = "新增提示词配置", notes = "新增提示词配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addPromptConfig(@RequestBody PromptConfig promptConfig) {
		return promptConfigService.addPromptConfig(promptConfig);
	}

	/**
	 * 查询提示词配置列表
	 */
	@PostMapping("/getPromptConfigList")
	@ApiOperation(value = "查询提示词配置", tags = "查询提示词配置", notes = "查询提示词配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<PromptConfig>> getPromptConfigList(@RequestBody PromptConfigListParam promptConfig) {
		return promptConfigService.getPromptConfigList(promptConfig);
	}

	// /**
	//  * 更新提示词配置
	//  */
	// @PostMapping("/updatePromptConfig")
	// @ApiOperation(value = "更新提示词配置", tags = "更新提示词配置", notes = "更新提示词配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	// public Result updatePromptConfig(@RequestBody PromptConfig promptConfig) {
	// 	return promptConfigService.updatePromptConfig(promptConfig);
	// }

	/**
	 * 删除提示词配置
	 */
	@PostMapping("/deletePromptConfig")
	@ApiOperation(value = "删除提示词配置", tags = "删除提示词配置", notes = "删除提示词配置", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deletePromptConfig(@RequestBody List<String> idList) {
		return promptConfigService.deletePromptConfig(idList);
	}

	/**
	 * 将提示词设为预设
	 * @param promptConfigListParam
	 * @return Result
	 */
	@PostMapping("/setPreset")
	public Result setPreset(@RequestBody PromptConfigListParam promptConfigListParam) {
		return promptConfigService.setPreset(promptConfigListParam);
	}

}