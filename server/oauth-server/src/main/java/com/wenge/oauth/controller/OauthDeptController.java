package com.wenge.oauth.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.dto.param.DeptStreetByTenantParam;
import com.wenge.oauth.dto.param.OauthDeptParam;
import com.wenge.oauth.dto.result.OauthDeptResult;
import com.wenge.oauth.entity.OauthDept;
import com.wenge.oauth.service.OauthDeptService;
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
 * Description: 部门信息接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-25 10:27:28
 *
 */
@RestController
@RequestMapping("/oauthDept")
@Slf4j
@Api(tags = "部门信息接口")
public class OauthDeptController {

	/**
	 * 	部门信息服务类
	 */
	@Autowired
	private OauthDeptService oauthDeptService;

	/**
	 * 新增部门信息
	 */
	@PostMapping("/addOauthDept")
	@ApiOperation(value = "新增部门信息", tags = "新增部门信息", notes = "新增部门信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result addOauthDept(@RequestBody OauthDept oauthDept) {
		return oauthDeptService.addOauthDept(oauthDept);
	}

	/**
	 * 分页查询部门信息列表
	 */
	@PostMapping("/getOauthDeptList")
	@ApiOperation(value = "查询部门信息", tags = "查询部门信息", notes = "查询部门信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<OauthDept>> getOauthDeptList(@RequestBody OauthDeptParam oauthDept) {
		return oauthDeptService.getOauthDeptList(oauthDept);
	}

	///**
	// * 更新部门信息
	// */
	//@PostMapping("/updateOauthDept")
	//@ApiOperation(value = "更新部门信息", tags = "更新部门信息", notes = "更新部门信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	//public Result updateOauthDept(@RequestBody OauthDept oauthDept) {
	//	return oauthDeptService.updateOauthDept(oauthDept);
	//}

	/**
	 * 删除部门信息
	 */
	@PostMapping("/deleteOauthDept")
	@ApiOperation(value = "删除部门信息", tags = "删除部门信息", notes = "删除部门信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result deleteOauthDept(@RequestBody List<String> idList) {
		return oauthDeptService.deleteOauthDept(idList);
	}

	/**
	 * 查询部门信息树
	 */
	@PostMapping("/getDeptStreet")
	@ApiOperation(value = "查询部门信息树", tags = "查询部门信息树", notes = "查询部门信息树", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<OauthDeptResult>> getDeptStreet(@RequestBody OauthDeptParam oauthDept) {
		return oauthDeptService.getDeptStreet(oauthDept);
	}

	/**
	 * 通过租户id获取部门树
	 */
	@PostMapping("/getDeptStreetByTenant")
	@ApiOperation(value = "通过租户id获取部门", tags = "通过租户id获取部门", notes = "通过租户id获取部门", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<OauthDeptResult>> getDeptStreetByTenant(@RequestBody DeptStreetByTenantParam oauthDept) {
		return oauthDeptService.getDeptStreetByTenant(oauthDept);
	}
}