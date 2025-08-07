package com.wenge.model.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ServiceMenuPageParam;
import com.wenge.model.entity.ServiceMenu;
import com.wenge.model.service.ServiceMenuService;
import com.wenge.oauth.annotation.OperaLogs;
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
 * Description: 服务菜单接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-05 11:34:24
 *
 */
@RestController
@RequestMapping("/serviceMenu")
@Slf4j
@Api(tags = "服务菜单接口")
public class ServiceMenuController {

	/**
	 * 	服务菜单服务类
	 */
	@Autowired
	private ServiceMenuService serviceMenuService;

	/**
	 * 新增服务菜单
	 */
	@PostMapping("/addServiceMenu")
	@ApiOperation(value = "新增服务菜单", tags = "新增服务菜单", notes = "新增服务菜单", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result addServiceMenu(@RequestBody ServiceMenu serviceMenu) {
		return serviceMenuService.addServiceMenu(serviceMenu);
	}

	/**
	 * 查询服务菜单列表
	 */
	@PostMapping("/getServiceMenuList")
	@ApiOperation(value = "查询服务菜单", tags = "查询服务菜单", notes = "查询服务菜单", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<ServiceMenu>> getServiceMenuList(@RequestBody ServiceMenuPageParam param) {
		return serviceMenuService.getServiceMenuList(param);
	}

	/**
	 * 更新服务菜单
	 */
	@PostMapping("/updateServiceMenu")
	@ApiOperation(value = "更新服务菜单", tags = "更新服务菜单", notes = "更新服务菜单", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateServiceMenu(@RequestBody ServiceMenu serviceMenu) {
		return serviceMenuService.updateServiceMenu(serviceMenu);
	}

	/**
	 * 删除服务菜单
	 */
	@PostMapping("/deleteServiceMenu")
	@ApiOperation(value = "删除服务菜单", tags = "删除服务菜单", notes = "删除服务菜单", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result deleteServiceMenu(@RequestBody List<String> idList) {
		return serviceMenuService.deleteServiceMenu(idList);
	}

	/**
	 * 查询服务菜单类型
	 */
	@PostMapping("/getServiceTypeList")
	@ApiOperation(value = "查询服务菜单", tags = "查询服务菜单", notes = "查询服务菜单", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<String>> getServiceTypeList(@RequestBody ServiceMenuPageParam param) {
		return serviceMenuService.getServiceTypeList(param);
	}
}