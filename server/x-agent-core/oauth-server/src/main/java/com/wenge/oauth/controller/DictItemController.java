package com.wenge.oauth.controller;


import com.wenge.oauth.entity.DictItem;
import com.wenge.oauth.service.DictItemService;
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
 * Description: 字典条目接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:04:39
 *
 */
@RestController
@RequestMapping("/dictItem")
@Slf4j
@Api(tags = "字典条目接口")
public class DictItemController {

	/**
	 * 	字典条目服务类
	 */
	@Autowired
	private DictItemService dictItemService;

	/**
	 * 新增字典条目
	 */
	@PostMapping("/addDictItem")
	@ApiOperation(value = "新增字典条目", tags = "新增字典条目", notes = "新增字典条目", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addDictItem(@RequestBody DictItem dictItem) {
		return dictItemService.addDictItem(dictItem);
	}

	/**
	 * 查询字典条目列表
	 */
	@PostMapping("/getDictItemList")
	@ApiOperation(value = "查询字典条目", tags = "查询字典条目", notes = "查询字典条目", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getDictItemList(@RequestBody DictItem dictItem) {
		return dictItemService.getDictItemList(dictItem);
	}

	/**
	 * 更新字典条目
	 */
	@PostMapping("/updateDictItem")
	@ApiOperation(value = "更新字典条目", tags = "更新字典条目", notes = "更新字典条目", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateDictItem(@RequestBody DictItem dictItem) {
		return dictItemService.updateDictItem(dictItem);
	}

	/**
	 * 删除字典条目
	 */
	@PostMapping("/deleteDictItem")
	@ApiOperation(value = "删除字典条目", tags = "删除字典条目", notes = "删除字典条目", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteDictItem(@RequestBody List<String> idList) {
		return dictItemService.deleteDictItem(idList);
	}

	/**
	 * 根据字典类型查询字典
	 */
	@PostMapping("/getDictItemByType")
	@ApiOperation(value = "根据字典类型查询字典", tags = "根据字典类型查询字典", notes = "根据字典类型查询字典", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getDictItemByType(@RequestBody StringParam typeParam) {
		return dictItemService.getDictItemByType(typeParam);
	}
}