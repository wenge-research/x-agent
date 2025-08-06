package com.wenge.model.controller;

import cn.hutool.json.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.NodeTableDataAddParam;
import com.wenge.model.dto.param.NodeTableDataListParam;
import com.wenge.model.dto.param.NodeTableFieldAddParam;
import com.wenge.model.entity.ComponentNodeTableField;
import com.wenge.model.service.ComponentNodeTableFieldService;
import com.wg.appframe.core.bean.Result;
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
 * Description: 数据库节点表字段数据接口
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:47:59
 *
 */
@RestController
@RequestMapping("/componentNodeTableField")
@Slf4j
@Api(tags = "数据库节点表字段数据接口")
public class ComponentNodeTableFieldController {

	/**
	 * 	数据库节点表字段数据服务类
	 */
	@Autowired
	private ComponentNodeTableFieldService componentNodeTableFieldService;

	/**
	 * 编辑表结构
	 */
	@PostMapping("/addComponentNodeTableField")
	@ApiOperation(value = "编辑表结构", tags = "编辑表结构", notes = "编辑表结构", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addComponentNodeTableField(@RequestBody NodeTableFieldAddParam param) {
		return componentNodeTableFieldService.addComponentNodeTableField(param);
	}

	/**
	 * 查询表字段列表
	 */
	@PostMapping("/getNodeTableFields")
	@ApiOperation(value = "查询表字段列表", tags = "查询表字段列表", notes = "查询表字段列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<ComponentNodeTableField>> getNodeTableFields(@RequestBody StringParam param) {
		return componentNodeTableFieldService.getNodeTableFields(param);
	}

	/**
	 * 查询表数据列表
	 */
	@PostMapping("/getNodeTableDataList")
	@ApiOperation(value = "查询表数据列表", tags = "查询表数据列表", notes = "查询表数据列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<JSONObject>> getNodeTableDataList(@RequestBody NodeTableDataListParam param) {
		return componentNodeTableFieldService.getComponentNodeTableFieldList(param);
	}

	/**
	 * 新增表数据
	 */
	@PostMapping("/addNodeTableData")
	@ApiOperation(value = "新增表数据", tags = "新增表数据", notes = "新增表数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addNodeTableData(@RequestBody NodeTableDataAddParam param) {
		return componentNodeTableFieldService.addNodeTableData(param);
	}
	
}