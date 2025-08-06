package com.wenge.oauth.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.dto.param.OauthTenantParam;
import com.wenge.oauth.dto.param.TenantUpdateStatusParam;
import com.wenge.oauth.entity.Tenant;
import com.wenge.oauth.service.OauthTenantService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 租户表接口
 * @Author: author
 * Version: 1.0
 * Create Date Time: 2024-06-28 14:31:45
 *
 */
@RestController
@RequestMapping("/oauthTenant")
@Slf4j
@Api(tags = "租户表接口")
public class TenantController {

	/**
	 * 	租户表服务类
	 */
	@Autowired
	private OauthTenantService oauthTenantService;

	/**
	 * 新增租户表
	 */
	@PostMapping("/addOauthTenant")
    @ApiOperation(value = "新增租户表",tags = "新增租户表", notes = "新增租户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result addOauthTenant(@RequestBody Tenant tenant) {
		return oauthTenantService.addOauthTenant(tenant);
	}

	/**
	 * 查询租户表列表
	 */
	@PostMapping("/getOauthTenantList")
    @ApiOperation(value = "查询租户表",tags = "查询租户表", notes = "查询租户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<Tenant>> getOauthTenantList(@RequestBody OauthTenantParam param) {
		return oauthTenantService.getOauthTenantList(param);
	}

	/**
	 * 更新租户表
	 */
	@PostMapping("/updateOauthTenant")
    @ApiOperation(value = "更新租户表",tags = "更新租户表", notes = "更新租户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result updateOauthTenant(@RequestBody Tenant tenant) {
		return oauthTenantService.updateOauthTenant(tenant);
	}

	/**
	 * 删除租户表
	 */
	@PostMapping("/deleteOauthTenant")
    @ApiOperation(value = "删除租户表",tags = "删除租户表", notes = "删除租户表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result deleteOauthTenant(@RequestBody ListStringParam idList) {
		return oauthTenantService.deleteOauthTenant(idList);
	}

	/**
	 * 更新租户状态(启用/停用)
	 */
	@PostMapping("/updateStatus")
    @ApiOperation(value = "更新租户状态(启用/停用)",tags = "更新租户状态(启用/停用)", notes = "更新租户状态(启用/停用)", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@OperaLogs
	public Result updateStatus(@RequestBody TenantUpdateStatusParam param) {
		return oauthTenantService.updateStatus(param);
	}

	/**
	 * 根据租户业务id（tenantId）查询租户详情
	 */
	@GetMapping("/getOauthTenantInfoByTenantId")
	@ApiOperation(value = "根据租户业务id（tenantId）查询租户详情", tags = "根据租户业务id（tenantId）查询租户详情", notes = "根据租户业务id（tenantId）查询租户详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getOauthTenantInfoByTenantId(@RequestParam(value = "tenantId", required = false) String tenantId) {
		if (StringUtils.isBlank(tenantId)) {
			return Result.success();
		}
		Tenant tenant = oauthTenantService.getDeatail(tenantId);
		return Result.success(tenant);
	}

}