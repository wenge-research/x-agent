package com.wenge.oauth.controller;


import com.wenge.oauth.dto.param.AuthChannelPageParam;
import com.wenge.oauth.dto.result.AuthChannelResult;
import com.wenge.oauth.entity.AuthChannel;
import com.wenge.oauth.service.AuthChannelService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 认证方式渠道配置表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-02 15:28:16
 *
 */
@RestController
@RequestMapping("/authChannel")
@Slf4j
@Api(tags = "认证方式渠道配置表接口")
public class AuthChannelController {

	/**
	 * 	认证方式渠道配置表服务类
	 */
	@Autowired
	private AuthChannelService authChannelService;

	/**
	 * 新增认证方式渠道配置表
	 */
	@PostMapping("/addAuthChannel")
	@ApiOperation(value = "新增认证方式渠道配置表", tags = "新增认证方式渠道配置表", notes = "新增认证方式渠道配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addAuthChannel(@RequestBody AuthChannel authChannel) {
		return authChannelService.addAuthChannel(authChannel);
	}

	/**
	 * 分页查询认证方式渠道配置表列表
	 */
	@PostMapping("/getAuthChannelList")
	@ApiOperation(value = "分页查询认证方式渠道配置表列表", tags = "分页查询认证方式渠道配置表列表", notes = "分页查询认证方式渠道配置表列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getAuthChannelList(@RequestBody AuthChannelPageParam authChannel) {
		return authChannelService.getAuthChannelList(authChannel);
	}

	/**
	 * 更新认证方式渠道配置表
	 */
	@PostMapping("/updateAuthChannel")
	@ApiOperation(value = "更新认证方式渠道配置表", tags = "更新认证方式渠道配置表", notes = "更新认证方式渠道配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateAuthChannel(@RequestBody AuthChannel authChannel) {
		return authChannelService.updateAuthChannel(authChannel);
	}

	/**
	 * 删除认证方式渠道配置表
	 */
	@PostMapping("/deleteAuthChannel")
	@ApiOperation(value = "删除认证方式渠道配置表", tags = "删除认证方式渠道配置表", notes = "删除认证方式渠道配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteAuthChannel(@RequestBody List<String> idList) {
		return authChannelService.deleteAuthChannel(idList);
	}

	/**
	 * 获取认证方式渠道配置表列表
	 */
	@PostMapping("/getAuthChannels")
	@ApiOperation(value = "查询认证方式渠道配置表", tags = "查询认证方式渠道配置表", notes = "查询认证方式渠道配置表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<AuthChannelResult>> getAuthChannels(@RequestBody AuthChannelPageParam authChannel) {
		return authChannelService.getAuthChannels(authChannel);
	}

	@GetMapping("/getAuthChannelByApplicationId")
	@ApiOperation(value = "根据应用ID获取配置信息", tags = "根据应用ID获取配置信息", notes = "根据应用ID获取配置信息", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
	public Result getAuthChannelByApplicationId(@RequestParam("applicationId") String applicationId,@RequestParam("channelCode") String channelCode) {
		return Result.success(authChannelService.getAuthChannelByApplicationId(applicationId,channelCode));
	}

}