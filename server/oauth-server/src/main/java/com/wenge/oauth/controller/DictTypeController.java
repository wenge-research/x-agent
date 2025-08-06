package com.wenge.oauth.controller;


import com.wenge.oauth.entity.DictType;
import com.wenge.oauth.service.DictTypeService;
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
 * Description: 字典类型接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:03:59
 *
 */
@RestController
@RequestMapping("/dictType")
@Slf4j
@Api(tags = "字典类型接口")
public class DictTypeController {

	/**
	 * 	字典类型服务类
	 */
	@Autowired
	private DictTypeService dictTypeService;

	/**
	 * 新增字典类型
	 */
	@PostMapping("/addDictType")
	@ApiOperation(value = "新增字典类型", tags = "新增字典类型", notes = "新增字典类型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addDictType(@RequestBody DictType dictType) {
		return dictTypeService.addDictType(dictType);
	}

	/**
	 * 查询字典类型列表
	 */
	@PostMapping("/getDictTypeList")
	@ApiOperation(value = "查询字典类型", tags = "查询字典类型", notes = "查询字典类型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getDictTypeList(@RequestBody DictType dictType) {
		return dictTypeService.getDictTypeList(dictType);
	}

	/**
	 * 更新字典类型
	 */
	@PostMapping("/updateDictType")
	@ApiOperation(value = "更新字典类型", tags = "更新字典类型", notes = "更新字典类型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateDictType(@RequestBody DictType dictType) {
		return dictTypeService.updateDictType(dictType);
	}

	/**
	 * 删除字典类型
	 */
	@PostMapping("/deleteDictType")
	@ApiOperation(value = "删除字典类型", tags = "删除字典类型", notes = "删除字典类型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteDictType(@RequestBody List<String> idList) {
		return dictTypeService.deleteDictType(idList);
	}

}