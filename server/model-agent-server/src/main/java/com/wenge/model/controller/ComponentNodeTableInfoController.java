package com.wenge.model.controller;


import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.NodeTableInfoParam;
import com.wenge.model.entity.ComponentNodeTableInfo;
import com.wenge.model.service.ComponentNodeTableInfoService;
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
 * Description: 数据库节点表数据接口
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:11:33
 *
 */
@RestController
@RequestMapping("/componentNodeTableInfo")
@Slf4j
@Api(tags = "数据库节点表数据接口")
public class ComponentNodeTableInfoController {

	/**
	 * 	数据库节点表数据服务类
	 */
	@Autowired
	private ComponentNodeTableInfoService componentNodeTableInfoService;

	/**
	 * 新增数据库表信息
	 */
	@PostMapping("/addComponentNodeTableInfo")
	@ApiOperation(value = "新增数据库表信息", tags = "新增数据库表信息", notes = "新增数据库表信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addComponentNodeTableInfo(@RequestBody ComponentNodeTableInfo componentNodeTableInfo) {
		return componentNodeTableInfoService.addComponentNodeTableInfo(componentNodeTableInfo);
	}

	/**
	 * 查询数据库节点表信息列表
	 */
	@PostMapping("/getComponentNodeTableInfoList")
	@ApiOperation(value = "查询数据库节点表信息列表", tags = "查询数据库节点表信息列表", notes = "查询数据库节点表信息列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<ComponentNodeTableInfo>> getComponentNodeTableInfoList(@RequestBody NodeTableInfoParam param) {
		return componentNodeTableInfoService.getComponentNodeTableInfoList(param);
	}

	/**
	 * 删除数据库节点表信息
	 */
	@PostMapping("/deleteNodeTableInfo")
	@ApiOperation(value = "删除数据库节点表信息", tags = "删除数据库节点表信息", notes = "删除数据库节点表信息", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteNodeTableInfo(@RequestBody List<String> idList) {
		return componentNodeTableInfoService.deleteNodeTableInfo(idList);
	}

}
