package com.wenge.oauth.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.dto.param.*;
import com.wenge.oauth.entity.OauthUser;
import com.wenge.oauth.service.UserService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 用户表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-05 14:13:29
 *
 */
@ApiOperation(value = "Description: 用户表接口")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	/**
	 * 	用户表服务类
	 */
	@Autowired
	private UserService userService;
	/**
	 * 新增用户表
	 */
    @ApiOperation(value = "新增用户表",tags = "新增用户表", notes = "新增用户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addUser")
	@OperaLogs
	public Result addUser(@RequestBody OauthUser oauthUser) {
		return userService.addUser(oauthUser);
	}

	/**
	 * 查询用户表列表
	 */
    @ApiOperation(value = "查询用户表列表",tags = "查询用户表列表", notes = "查询用户表列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getUserList")
	public Result<Page<OauthUser>> getUserList(@RequestBody UserParam user) {
		return userService.getUserList(user);
	}

	/**
	 * 删除用户表
	 */
    @ApiOperation(value = "删除用户表",tags = "删除用户表", notes = "删除用户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteUser")
	@OperaLogs
	public Result deleteUser(@RequestBody ListStringParam idList) {
		return userService.deleteUser(idList);
	}

	/**
	 * 更新租户id
	 */
	@ApiOperation(value = "更新租户id", tags = "更新租户id", notes = "更新租户id", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateTenant")
	@OperaLogs
	public Result updateTenant(@RequestBody UpdateTenantParam param) {
		return userService.updateTenant(param);
	}

	/**
	 * 绑定角色
	 */
	@ApiOperation(value = "绑定角色", tags = "绑定角色", notes = "绑定角色", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/bindingRole")
	@OperaLogs
	public Result bindingRole(@RequestBody UserBindingRoleParam param) {
		return userService.bindingRole(param);
	}

	/**
	 * 注销
	 */
	@ApiOperation(value = "注销", tags = "注销", notes = "注销", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deregisterUser")
	@OperaLogs
	public Result deregisterUser(@RequestBody ListStringParam userIdList) {
		return userService.deregisterUser(userIdList);
	}

	/**
	 * 解锁
	 */
	@ApiOperation(value = "解锁", tags = "注销", notes = "注销", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/unlock")
	@OperaLogs
	public Result unlock(@RequestBody ListStringParam userIdList) {
		return userService.unlock(userIdList);
	}

	/**
	 * 根据租户id查询用户表，用户是管理员级别
	 */
	@ApiOperation(value = "根据租户id查询用户表，用户是管理员级别", tags = "根据租户id查询用户表，用户是管理员级别", notes = "根据租户id查询用户表，用户是管理员级别", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getUserByTenant")
	@OperaLogs
	public Result getUserByTenant(@RequestBody StringParam tenantId) {
		return userService.getManageUserByTenant(tenantId);
	}

	/**
	 * 根据租户id查询用户表，用户是管理员级别
	 */
	@ApiOperation(value = "根据租户id查询用户表", tags = "根据租户id查询用户表", notes = "根据租户id查询用户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getAllUserByTenant")
	@OperaLogs
	public Result getAllUserByTenant(@RequestBody StringParam tenantId) {
		return userService.getAllUserByTenant(tenantId);
	}

	/**
	 * 获取管理员用户
	 */
	@ApiOperation(value = "获取管理员用户", tags = "获取管理员用户", notes = "获取管理员用户", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getManageUser")
	@OperaLogs
	public Result getManageUser(@RequestBody StringParam param) {
		return userService.getManageUser(param);
	}

	/**
	 * 审核工作人员身份
	 */
	@ApiOperation(value = "审核工作人员身份", tags = "审核工作人员身份", notes = "审核工作人员身份", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/checkStaff")
	@OperaLogs
	public Result checkStaff(@RequestBody StaffCheckParam param) {
		return userService.checkStaff(param);
	}

	/**
	 * 填写个人资料
	 */
	@ApiOperation(value = "填写个人资料", tags = "填写个人资料", notes = "填写个人资料", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/fillInPersonal")
	@OperaLogs
	public Result fillInPersonal(@RequestBody PersonalFillInParam param) {
		return userService.fillInPersonal(param);
	}

	/**
	 * 获取用户详情
	 */
	@ApiOperation(value = "获取用户详情",tags = "获取用户详情", notes = "获取用户详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getUserDetail")
	public Result<OauthUser> getUserDetail(@RequestBody StringParam userId) {
		return userService.getUserDetail(userId);
	}

	/**
	 * 用户自己修改密码
	 */
	@ApiOperation(value = "用户自己修改密码",tags = "用户自己修改密码", notes = "用户自己修改密码", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/changPw")
	public Result changPw(@RequestBody PwChangParam param) {
		return userService.changPw(param);
	}

	/**
	 * 通过手机号找回密码
	 * (根据手机号查询账号)
	 */
	@ApiOperation(value = "通过手机号找回密码",tags = "通过手机号找回密码", notes = "通过手机号找回密码", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/retrievePassword")
	public Result retrievePassword(@RequestBody RetrievePasswordParam param) {
		return userService.retrievePassword(param);
	}

	/**
	 * 通过手机号找回密码
	 * (这里通过token获取账号)
	 */
	@ApiOperation(value = "通过手机号修改密码",tags = "通过手机号修改密码", notes = "通过手机号找回密码", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updatePassword")
	public Result updatePassword(@RequestBody RetrievePasswordParam param) {
		return userService.updatePassword(param);
	}

	/**
	 * 查询当前账号信息
	 */
	@ApiOperation(value = "查询当前账号信息",tags = "查询当前账号信息", notes = "查询当前账号信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/currentAccount")
	public Result currentAccount() {
		return userService.currentAccount();
	}
	/**
	 * 修改当前账号信息
	 */
	@ApiOperation(value = "修改当前账号信息",tags = "修改当前账号信息", notes = "修改当前账号信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateCurrentAccount")
	public Result updateCurrentAccount(@RequestBody OauthUser oauthUser) {
		return userService.updateCurrentAccount(oauthUser);
	}
	/**
	 * 修改当前账号手机号
	 */
	@ApiOperation(value = "修改当前账号信息",tags = "修改当前账号信息", notes = "修改当前账号信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updatePhone")
	public Result updatePhone(@RequestBody RetrievePasswordParam param) {
		return userService.updatePhone(param);
	}

}
