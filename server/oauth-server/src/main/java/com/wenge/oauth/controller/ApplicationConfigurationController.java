package com.wenge.oauth.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wenge.oauth.dto.param.ApplicationConfigurationParam;
import com.wenge.oauth.entity.ApplicationConfiguration;
import com.wenge.oauth.service.ApplicationConfigurationService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: caohaifeng
 * @date: 2024/8/30 11:29
 * @Description: 应用nacos配置管理controller
 * @Version:1.0
 **/
@RestController
@RequestMapping("/applicationConfiguration")
@Slf4j
@Api(tags = "应用nacos配置接口")
public class ApplicationConfigurationController {

	/**
	 * 	应用nacos配置服务类
	 */
	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	/**
	 * 新增应用nacos配置
	 */
	@PostMapping("/addApplicationConfig")
	@ApiOperation(value = "新增应用nacos配置")
	@UmsOperationLog(description = "应用配置管理-新增应用配置", logType = 2, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result addApplicationConfig(@RequestBody ApplicationConfiguration param) {
		return applicationConfigurationService.addApplicationConfiguration(param);
	}

	/**
	 * 查询应用nacos配置列表
	 */
	@PostMapping("/getApplicationConfigList")
	@ApiOperation(value = "应用配置管理-查阅应用配置")
	@UmsOperationLog(description = "应用配置管理-查阅应用配置", logType = 1, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result<Page<ApplicationConfiguration>>  getApplicationConfigList(@RequestBody ApplicationConfigurationParam param) {
		return applicationConfigurationService.getApplicationConfigurationList(param);
	}

	/**
	 * 更新应用nacos配置
	 */
	@PostMapping("/updateApplicationConfig")
	@ApiOperation(value = "应用配置管理-修改应用配置")
	@UmsOperationLog(description = "应用配置管理-修改应用配置", logType = 3, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result updateApplicationConfig(@RequestBody ApplicationConfiguration applicationConfiguration) {
		return applicationConfigurationService.updateApplicationConfiguration(applicationConfiguration);
	}

	/**
	 * 删除应用nacos配置
	 */
	@PostMapping("/deleteApplicationConfig")
	@ApiOperation(value = "删除应用nacos配置")
	@UmsOperationLog(description = "应用配置管理-删除应用配置", logType = 4, belongModule = "appmanage", belongModuleName = "应用管理", objectType = "应用", objectName = "--", objectUuid = "--")
	public Result deleteApplicationConfig(@RequestBody ApplicationConfigurationParam applicationConfigurationParam) {
		return applicationConfigurationService.deleteApplicationConfiguration(applicationConfigurationParam);
	}

}