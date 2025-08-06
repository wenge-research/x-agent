package com.wenge.model.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.McpServerListParam;
import com.wenge.model.dto.param.TestApiParam;
import com.wenge.model.entity.McpServer;
import com.wenge.model.service.McpServerService;
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
 * Description: mcp 服务接口
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:32:48
 *
 */
@RestController
@RequestMapping("/mcpServer")
@Slf4j
@Api(tags = "mcp 服务接口")
public class McpServerController {

	/**
	 * 	mcp 服务服务类
	 */
	@Autowired
	private McpServerService mcpServerService;

	/**
	 * 新增mcp 服务
	 */
	@PostMapping("/addMcpServer")
	@ApiOperation(value = "新增mcp 服务", tags = "新增mcp 服务", notes = "新增mcp 服务", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addMcpServer(@RequestBody McpServer mcpServer) {
		return mcpServerService.addMcpServer(mcpServer);
	}

	/**
	 * 查询mcp 服务列表
	 */
	@PostMapping("/getMcpServerList")
	@ApiOperation(value = "查询mcp 服务", tags = "查询mcp 服务", notes = "查询mcp 服务", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getMcpServerList(@RequestBody McpServerListParam mcpServer) {
		return mcpServerService.getMcpServerList(mcpServer);
	}

	/**
	 * 更新mcp 服务
	 */
	@PostMapping("/updateMcpServer")
	@ApiOperation(value = "更新mcp 服务", tags = "更新mcp 服务", notes = "更新mcp 服务", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateMcpServer(@RequestBody McpServer mcpServer) {
		return mcpServerService.updateMcpServer(mcpServer);
	}

	/**
	 * 删除mcp 服务
	 */
	@PostMapping("/deleteMcpServer")
	@ApiOperation(value = "删除mcp 服务", tags = "删除mcp 服务", notes = "删除mcp 服务", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteMcpServer(@RequestBody List<String> idList) {
		return mcpServerService.deleteMcpServer(idList);
	}

	/**
	 * 验证 mcp
	 */
	@PostMapping("/checkMcp")
	@ApiOperation(value = "验证 mcp", tags = "验证 mcp", notes = "验证 mcp", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result checkMcp(@RequestBody McpServer mcpServer) {
		return mcpServerService.checkMcp(mcpServer);
	}

	/**
	 * 获取 mcp 详情
	 */
	@PostMapping("/getDetail")
	@ApiOperation(value = "验证 mcp", tags = "验证 mcp", notes = "验证 mcp", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getDetail(@RequestBody StringParam param) {
		return mcpServerService.getDetail(param);
	}

	/**
	 * 获取 mcp 以及详情详情
	 */
	@PostMapping("/getListAndDetail")
	@ApiOperation(value = "获取 mcp 以及详情详情", tags = "获取 mcp 以及详情详情", notes = "获取 mcp 以及详情详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getListAndDetail(@RequestBody ListStringParam param) {
		return mcpServerService.getListAndDetail(param);
	}

	/**
	 * 查询我开通的mcp 服务列表
	 */
	@PostMapping("/getMyOpenMcpServerList")
	@ApiOperation(value = "查询我开通的mcp 服务列表", tags = "查询我开通的mcp 服务列表", notes = "查询我开通的mcp 服务列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getMyMcpServerList(@RequestBody McpServerListParam mcpServer) {
		return mcpServerService.getMyMcpServerList(mcpServer);
	}

	/**
	 * 验证 api
	 */
	@PostMapping("/testApi")
	@ApiOperation(value = "验证 api", tags = "验证 api", notes = "验证 api", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result testApi(@RequestBody TestApiParam param) {
		return mcpServerService.testApi(param);
	}

	/**
	 * 将mcp服务设为预设
	 * @param mcpServerListParam
	 * @return Result
	 */
	@PostMapping("/setPreset")
	@ApiOperation(value = "预置mcp", tags = "预置mcp", notes = "预置mcp", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result setPreset(@RequestBody McpServerListParam mcpServerListParam) {
		return mcpServerService.setPreset(mcpServerListParam);
	}


	/**
	 * 修改mcp开通状态
	 */
	@PostMapping("/updateMcpStatus")
	@ApiOperation(value = "修改mcp开通状态", tags = "修改mcp开通状态", notes = "修改mcp开通状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateMcpStatus(@RequestBody McpServerListParam mcpServerParam) {
		return mcpServerService.updateMcpStatus(mcpServerParam);
	}

}