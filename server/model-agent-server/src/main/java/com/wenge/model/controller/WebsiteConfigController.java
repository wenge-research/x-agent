package com.wenge.model.controller;

import com.wenge.model.entity.WebsiteConfig;
import com.wenge.model.service.WebsiteConfigService;
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
 * Description: 网址置表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 15:33:14
 *
 */
@RestController
@RequestMapping("/websiteConfig")
@Slf4j
@Api(tags = "网址置表接口")
public class WebsiteConfigController {

	/**
	 * 	网址置表服务类
	 */
	@Autowired
	private WebsiteConfigService websiteConfigService;

	/**
	 * 新增网址置表
	 */
	@PostMapping("/addWebsiteConfig")
	@ApiOperation(value = "新增网址置表", tags = "新增网址置表", notes = "新增网址置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addWebsiteConfig(@RequestBody WebsiteConfig websiteConfig) {
		return websiteConfigService.addWebsiteConfig(websiteConfig);
	}

	/**
	 * 查询网址置表列表
	 */
	@PostMapping("/getWebsiteConfigList")
	@ApiOperation(value = "查询网址置表", tags = "查询网址置表", notes = "查询网址置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getWebsiteConfigList(@RequestBody WebsiteConfig websiteConfig) {
		return websiteConfigService.getWebsiteConfigList(websiteConfig);
	}

	/**
	 * 更新网址置表
	 */
	@PostMapping("/updateWebsiteConfig")
	@ApiOperation(value = "更新网址置表", tags = "更新网址置表", notes = "更新网址置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateWebsiteConfig(@RequestBody WebsiteConfig websiteConfig) {
		return websiteConfigService.updateWebsiteConfig(websiteConfig);
	}

	/**
	 * 删除网址置表
	 */
	@PostMapping("/deleteWebsiteConfig")
	@ApiOperation(value = "删除网址置表", tags = "删除网址置表", notes = "删除网址置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteWebsiteConfig(@RequestBody List<String> idList) {
		return websiteConfigService.deleteWebsiteConfig(idList);
	}

}