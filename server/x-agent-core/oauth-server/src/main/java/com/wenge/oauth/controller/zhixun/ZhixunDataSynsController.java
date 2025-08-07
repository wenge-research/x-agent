package com.wenge.oauth.controller.zhixun;


import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.dto.param.PermissionGrantParam;
import com.wenge.oauth.dto.param.RolePageParam;
import com.wenge.oauth.dto.param.RoleStatusUpdateParam;
import com.wenge.oauth.dto.param.UserBindingRoleParam;
import com.wenge.oauth.entity.Menu;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.entity.Role;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.service.MenuService;
import com.wenge.oauth.service.OauthDeptService;
import com.wenge.oauth.service.RoleService;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: caohaifeng
 * @date: 2025/2/25 16:23
 * @Description:  智巡api调用接口
 * @Version:1.0
 **/
@RestController
@RequestMapping("/zhixun/api")
@Slf4j
@Api(tags = "智巡api调用接口")
public class ZhixunDataSynsController {

	/**
	 * 	角色表服务类
	 */
	@Autowired
	private RoleService roleService;

	/**
	 * 	菜单表服务类
	 */
	@Autowired
	private MenuService menuService;

	/**
	 * 	部门信息服务类
	 */
	@Autowired
	private OauthDeptService oauthDeptService;


	/**
	 * 	用户表服务类
	 */
	@Autowired
	private UserService userService;


//	/**
//	 * 批量新增角色表
//	 */
//	@PostMapping("/addRoleList")
//	@ApiOperation(value = "批量新增角色表",tags = "批量新增角色表", notes = "批量新增角色表")
//	public Result addRoleList(@RequestBody List<Role> roles) {
//		if(CollectionUtil.isEmpty(roles)){
//			return Result.error("roles不能为空");
//		}
//		return roleService.addRoleList(roles);
//	}

	@PostMapping("/updateRoleList")
	@ApiOperation(value = "批量更新角色表",tags = "批量更新角色表", notes = "批量更新角色表")
	public Result updateRoleList(@RequestBody List<Role> roles) {
		if(CollectionUtil.isEmpty(roles)){
			return Result.error("roles不能为空");
		}
		return roleService.updateRoleList(roles);
	}

	@GetMapping("/delRoleByRoleId")
	@ApiOperation(value = "删除角色表",tags = "删除角色表", notes = "删除角色表")
	public Result delRoleByRoleId(@RequestParam("roleId") String roleId) {
		if (StringUtils.isBlank(roleId)) {
			return Result.error("role不能为空");
		}
		return roleService.delRoleByRoleId(roleId);
	}
//	======================================================================================================================
	@PostMapping("/grantPermission")
	@ApiOperation(value = "分配权限(添加角色和菜单的关联关系)", tags = "分配权限(添加角色和菜单的关联关系)", notes = "分配权限(添加角色和菜单的关联关系)", response = Result.class, httpMethod = "POST")
	public Result grantPermission(@RequestBody PermissionGrantParam param) {
		if (CollectionUtil.isEmpty(param.getMenuId())) {
			return Result.error("menuId不能为空");
		}
		if (StringUtils.isEmpty(param.getRoleId())) {
			return Result.error("roleId不能为空");
		}
		log.info("执行分配权限(添加角色和菜单的关联关系) grantPermission {}", param.toString());
		return roleService.grantPermission(param);
	}

//	======================================================================================================================

//	@PostMapping("/addMenuList")
//	@ApiOperation(value = "批量新增菜单表", tags = "批量新增菜单表", notes = "批量新增菜单表", response = Result.class, httpMethod = "POST")
//	public Result addMenuList(@RequestBody List<Menu> menus) {
//		return menuService.addMenuList(menus);
//	}

	@PostMapping("/updateMenuList")
	@ApiOperation(value = "批量更新菜单表",tags = "批量更新菜单表", notes = "批量更新菜单表")
	public Result updateMenuList(@RequestBody List<Menu> menus) {
		if(CollectionUtil.isEmpty(menus)){
			return Result.error("menus不能为空");
		}
		return menuService.updateMenuList(menus);
	}

	@GetMapping("/delMenuByMenuId")
	@ApiOperation(value = "删除菜单表",tags = "删除菜单表", notes = "删除菜单表")
	public Result delMenuByMenuId(@RequestParam("menuId") String menuId) {
		if (StringUtils.isBlank(menuId)) {
			return Result.error("menuId不能为空");
		}
		return menuService.delMenuByMenuId(menuId);
	}

//	======================================================================================================================

//	@PostMapping("/addOauthDeptList")
//	@ApiOperation(value = "批量新增部门表", tags = "批量新增部门表", notes = "批量新增部门表", response = Result.class, httpMethod = "POST")
//	public Result addOauthDeptList(@RequestBody List<OauthDept> oauthDepts) {
//		return oauthDeptService.addOauthDeptList(oauthDepts);
//	}

	@PostMapping("/updateOauthDeptList")
	@ApiOperation(value = "批量更新部门表",tags = "批量更新部门表", notes = "批量更新部门表")
	public Result updateOauthDeptList(@RequestBody List<OauthDept> oauthDepts) {
		if(CollectionUtil.isEmpty(oauthDepts)){
			return Result.error("oauthDepts不能为空");
		}
		return oauthDeptService.updateOauthDeptList(oauthDepts);
	}

	@GetMapping("/delOauthDeptByDeptId")
	@ApiOperation(value = "删除部门表",tags = "删除部门表", notes = "删除部门表")
	public Result delOauthDeptByDeptId(@RequestParam("deptId") String deptId) {
		if (StringUtils.isBlank(deptId)) {
			return Result.error("deptId不能为空");
		}
		return oauthDeptService.delOauthDeptByDeptId(deptId);
	}

//	======================================================================================================================

	@ApiOperation(value = "绑定角色(添加用户和角色的关联关系)", tags = "绑定角色(添加用户和角色的关联关系)", notes = "绑定角色(添加用户和角色的关联关系)", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/bindingRole")
	public Result bindingRole(@RequestBody UserBindingRoleParam param) {
		if (CollectionUtil.isEmpty(param.getUserIdList())) {
			return Result.error("userIdList不能为空");
		}
		if (CollectionUtil.isEmpty(param.getRoleIdList())) {
			return Result.error("roleIdList不能为空");
		}
		log.info("执行绑定角色(添加用户和角色的关联关系) bindingRole {}", param.toString());
		return userService.bindingRole(param);
	}

//	======================================================================================================================


	@ApiOperation(value = "批量更新用户表", tags = "批量新增用户表", notes = "批量新增用户表", response = Result.class, httpMethod = "POST")
	@PostMapping("/updateUserList")
	public Result updateUserList(@RequestBody List<OauthUser> users) {
		if(CollectionUtil.isEmpty(users)){
			return Result.error("users不能为空");
		}
		return userService.updateUserList(users);
	}










}