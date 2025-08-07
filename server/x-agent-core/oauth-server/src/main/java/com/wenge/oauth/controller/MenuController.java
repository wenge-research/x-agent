package com.wenge.oauth.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.dto.param.MenuHiddenParam;
import com.wenge.oauth.dto.param.MenuPageParam;
import com.wenge.oauth.dto.param.MenuUpdateStatusParam;
import com.wenge.oauth.entity.Menu;
import com.wenge.oauth.service.MenuService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
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
 * Description: 菜单表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:03:36
 *
 */
@RestController
@RequestMapping("/menu")
@Slf4j
@Api(tags = "菜单表接口")
public class MenuController {

	/**
	 * 	菜单表服务类
	 */
	@Autowired
	private MenuService menuService;

	/**
	 * 新增菜单表
	 */
	@PostMapping("/addMenu")
    @ApiOperation(value = "新增菜单表",tags = "新增菜单表", notes = "新增菜单表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result addMenu(@RequestBody Menu menu) {
		return menuService.addMenu(menu);
	}

	/**
	 * 查询菜单表列表
	 */
	@PostMapping("/getMenuList")
    @ApiOperation(value = "查询菜单表",tags = "查询菜单表", notes = "查询菜单表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<Menu>> getMenuList(@RequestBody MenuPageParam param) {
		return menuService.getMenuList(param);
	}

	/**
	 * 更新菜单表
	 */
	@PostMapping("/updateMenu")
    @ApiOperation(value = "更新菜单表",tags = "更新菜单表", notes = "更新菜单表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result updateMenu(@RequestBody Menu menu) {
		return menuService.updateMenu(menu);
	}

	/**
	 * 删除菜单表
	 */
	@PostMapping("/deleteMenu")
    @ApiOperation(value = "删除菜单表",tags = "删除菜单表", notes = "删除菜单表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result deleteMenu(@RequestBody List<String> idList) {
		return menuService.deleteMenu(idList);
	}

	/**
	 * 通过角色查询权限列表树形结构
	 */
	@PostMapping("/getPermissionTree")
	@ApiOperation(value = "通过角色查询权限列表", tags = "通过角色查询权限列表", notes = "通过角色查询权限列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result<List<Menu>> getPermissionTree(@RequestBody StringParam roleId) {
		return menuService.getPermission(roleId);
	}

	/**
	 * 通过用户查询权限列表
	 */
	@PostMapping("/getUserPermission")
	@ApiOperation(value = "通过用户查询权限列表", tags = "通过用户查询权限列表", notes = "通过用户查询权限列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result<List<Menu>> getUserPermission(@RequestBody StringParam userId) {
		return menuService.getUserPermission(userId);
	}

	/**
	 * 获取所有权限列表，树形结构，根据当前登录人员已有的角色权限
	 */
	@PostMapping("/getAllMenuTree")
	@ApiOperation(value = "获取所有权限列表，树形结构", tags = "获取所有权限列表，树形结构", notes = "获取所有权限列表，树形结构", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result<List<Menu>> getAllMenuTree(@RequestBody EmptyParam param) {
		return menuService.getAllMenuTree(param);
	}


	/**
	 * 通过角色查询权限id列表
	 */
	@PostMapping("/getMenuIdList")
	@ApiOperation(value = "通过角色查询权限id列表", tags = "通过角色查询权限id列表", notes = "通过角色查询权限id列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result<List<String>> getMenuIdListByRole(@RequestBody StringParam roleId) {
		return menuService.getMenuIdListByRole(roleId);
	}

	/**
	 * 更新菜单状态
	 */
	@PostMapping("/updateStatus")
	@ApiOperation(value = "更新菜单状态", tags = "更新菜单状态", notes = "更新菜单状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result updateStatus(@RequestBody MenuUpdateStatusParam param) {
		return menuService.updateStatus(param);
	}

	/**
	 * 更新隐藏状态
	 */
	@PostMapping("/updateHidden")
	@ApiOperation(value = "更新隐藏状态", tags = "更新隐藏状态", notes = "更新隐藏状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result updateHidden(@RequestBody MenuHiddenParam param) {
		return menuService.updateHidden(param);
	}
}