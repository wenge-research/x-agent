package com.wenge.oauth.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.dto.param.PermissionGrantParam;
import com.wenge.oauth.dto.param.RolePageParam;
import com.wenge.oauth.dto.param.RoleStatusUpdateParam;
import com.wenge.oauth.entity.Role;
import com.wenge.oauth.service.RoleService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
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
 * Description: 角色表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-01 20:03:04
 *
 */
@RestController
@RequestMapping("/role")
@Slf4j
@Api(tags = "角色表接口")
public class RoleController {

	/**
	 * 	角色表服务类
	 */
	@Autowired
	private RoleService roleService;

	/**
	 * 新增角色表
	 */
	@PostMapping("/addRole")
    @ApiOperation(value = "新增角色表",tags = "新增角色表", notes = "新增角色表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result addRole(@RequestBody Role role) {
		return roleService.addRole(role);
	}

	/**
	 * 查询角色表列表
	 */
	@PostMapping("/getRoleList")
    @ApiOperation(value = "查询角色表",tags = "查询角色表", notes = "查询角色表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<Role>> getRoleList(@RequestBody RolePageParam role) {
		return roleService.getRoleList(role);
	}

	/**
	 * 更新角色表
	 */
	@PostMapping("/updateRole")
    @ApiOperation(value = "更新角色表",tags = "更新角色表", notes = "更新角色表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result updateRole(@RequestBody Role role) {
		return roleService.updateRole(role);
	}

	/**
	 * 删除角色表
	 */
	@PostMapping("/deleteRole")
    @ApiOperation(value = "删除角色表",tags = "删除角色表", notes = "删除角色表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result deleteRole(@RequestBody ListStringParam idList) {
		return roleService.deleteRole(idList);
	}

	/**
	 * 分配权限
	 */
	@PostMapping("/grantPermission")
    @ApiOperation(value = "分配权限", tags = "分配权限", notes = "分配权限", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result grantPermission(@RequestBody PermissionGrantParam param) {
		return roleService.grantPermission(param);
	}

	/**
	 * 通过用户查询角色
	 */
	@ApiOperation(value = "通过用户查询角色", tags = "通过用户查询角色", notes = "通过用户查询角色", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getRole")
	public Result getRole(@RequestBody StringParam userId) {
		return roleService.getRole(userId);
	}

	/**
	 * 更新状态
	 */
	@ApiOperation(value = "更新状态", tags = "更新状态", notes = "更新状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateStatus")
	@OperaLogs
	public Result updateStatus(@RequestBody RoleStatusUpdateParam param) {
		return roleService.updateStatus(param);
	}
}