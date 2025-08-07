package com.wenge.model.controller;

import cn.hutool.db.PageResult;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.KnowledgeDataTypeParam;
import com.wenge.model.entity.KnowledgeDataType;
import com.wenge.model.service.KnowledgeDataTypeService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: 知识数据分类接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-18 18:21:03
 *
 */
@Api(tags = "Description: 知识数据分类接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-18 18:21:03")
@RestController
@RequestMapping("/knowledgeDataType")
@Slf4j
public class KnowledgeDataTypeController {

	/**
	 * 	知识数据分类服务类
	 */
	@Autowired
	private KnowledgeDataTypeService knowledgeDataTypeService;

	/**
	 * 新增知识数据分类
	 */
    @ApiOperation(value = "新增知识数据分类",tags = "新增知识数据分类", notes = "新增知识数据分类", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addKnowledgeDataType")
	@OperaLogs
	public Result addKnowledgeDataType(@RequestBody KnowledgeDataType knowledgeDataType) {
		return knowledgeDataTypeService.addKnowledgeDataType(knowledgeDataType);
	}

	/**
	 * 查询知识数据分类列表
	 */
    @ApiOperation(value = "查询知识数据分类列表",tags = "查询知识数据分类列表", notes = "查询知识数据分类列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getKnowledgeDataTypeList")
	public Result<Page<KnowledgeDataType>> getKnowledgeDataTypeList(@RequestBody KnowledgeDataTypeParam param) {
		return knowledgeDataTypeService.getKnowledgeDataTypeList(param);
	}

	/**
	 * 更新知识数据分类
	 */
    @ApiOperation(value = "更新知识数据分类",tags = "更新知识数据分类", notes = "更新知识数据分类", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateKnowledgeDataType")
	@OperaLogs
	public Result updateKnowledgeDataType(@RequestBody KnowledgeDataType knowledgeDataType) {
		return knowledgeDataTypeService.updateKnowledgeDataType(knowledgeDataType);
	}

	/**
	 * 删除知识数据分类
	 */
    @ApiOperation(value = "删除知识数据分类",tags = "删除知识数据分类", notes = "删除知识数据分类", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteKnowledgeDataType")
	@OperaLogs
	public Result deleteKnowledgeDataType(@RequestBody ListStringParam idList) {
		return knowledgeDataTypeService.deleteKnowledgeDataType(idList);
	}


}