package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.FixKnowledgeDataTypeParam;
import com.wenge.model.dto.param.KnowledgeDataParam;
import com.wenge.model.dto.param.KnowledgeInfoPageParam;
import com.wenge.model.dto.param.StatusUpdateParam;
import com.wenge.model.dto.result.DeptKnowleageResult;
import com.wenge.model.dto.result.KnowledgeNameInfoResult;
import com.wenge.model.entity.KnowledgeInfo;
import com.wenge.model.service.KnowledgeInfoService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wenge.oauth.annotation.UmsOperationLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 知识库管理接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-07 14:10:23
 *
 */
@Api(tags = "Description: 知识库管理接口   @Author: CHENZHIWEI   Version: 1.0   Create Date Time: 2024-06-07 14:10:23")
@RestController
@RequestMapping("/knowledgeInfo")
@Slf4j
public class KnowledgeInfoController {

	/**
	 * 	知识库管理服务类
	 */
	@Autowired
	private KnowledgeInfoService knowledgeInfoService;

	/**
	 * 新增知识库管理
	 */
    @ApiOperation(value = "新增知识库管理",tags = "新增知识库管理", notes = "新增知识库管理", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addKnowledgeInfo")
	@OperaLogs
	public Result<KnowledgeInfo> addKnowledgeInfo(@RequestBody KnowledgeInfo knowledgeInfo) {
		return knowledgeInfoService.addKnowledgeInfo(knowledgeInfo);
	}

	/**
	 * 查询知识库管理列表
	 */
    @ApiOperation(value = "查询知识库管理列表",tags = "查询知识库管理列表", notes = "查询知识库管理列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getKnowledgeInfoList")
	public Result<Page<KnowledgeInfo>> getKnowledgeInfoList(@RequestBody KnowledgeInfoPageParam knowledgeInfo) {
		return knowledgeInfoService.getKnowledgeInfoList(knowledgeInfo);
	}

	/**
	 * 更新知识库管理
	 */
    @ApiOperation(value = "更新知识库管理",tags = "更新知识库管理", notes = "更新知识库管理", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateKnowledgeInfo")
	@OperaLogs
	public Result updateKnowledgeInfo(@RequestBody KnowledgeInfo knowledgeInfo) {
		return knowledgeInfoService.updateKnowledgeInfo(knowledgeInfo);
	}

	/**
	 * 删除知识库管理
	 */
    @ApiOperation(value = "删除知识库管理",tags = "删除知识库管理", notes = "删除知识库管理", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteKnowledgeInfo")
	@OperaLogs
	public Result deleteKnowledgeInfo(@RequestBody List<String> idList) {
		return knowledgeInfoService.deleteKnowledgeInfo(idList);
	}

	/**
	 * 更新状态
	 */
    @ApiOperation(value = "更新状态",tags = "更新状态", notes = "更新状态", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateStatus")
	public Result updateStatus(@RequestBody StatusUpdateParam param) {
		return knowledgeInfoService.updateStatus(param);
	}


	@ApiOperation(value = "知识库总数")
	@PostMapping("/knowledgeCount")
	@UmsOperationLog(description = "知识库-知识库总数")
	public Result knowledgeCount(@RequestBody KnowledgeDataParam knowledgeDataParam) {
		return knowledgeInfoService.knowledgeCount2(knowledgeDataParam);
	}

	@ApiOperation(value = "应用总数")
	@PostMapping("/applicationCount")
	@UmsOperationLog(description = "知识库-知识库总数")
	public Result applicationCount(@RequestBody KnowledgeDataParam knowledgeDataParam) {
		return knowledgeInfoService.applicationCount(knowledgeDataParam);
	}


	@ApiOperation(value = "知识库增长趋势")
	@PostMapping("/knowledgeCountTrend")
	@UmsOperationLog(description = "知识库-增长趋势")
	public Result knowledgeCountTrend(@RequestBody KnowledgeDataParam knowledgeDataParam) {
		return knowledgeInfoService.knowledgeCountTrend2(knowledgeDataParam);
	}


	/**
	 * 查询知识库管理列表
	 */
	@ApiOperation(value = "查询知识库管理列表",tags = "查询知识库管理列表", notes = "查询知识库管理列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getKnowledgeList")
	public Result<List<KnowledgeInfo>> getKnowledgeList(@RequestBody KnowledgeInfoPageParam knowledgeInfo) {
		return Result.success(knowledgeInfoService.getKnowledgeList(knowledgeInfo));
	}

	@ApiOperation(value = "查询部门科室知识库使用数量",tags = "查询部门科室知识库使用数量", notes = "查询部门科室知识库使用数量", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getDeptKnowledgeData")
	public Result<DeptKnowleageResult> getDeptKnowledgeData(@RequestBody KnowledgeInfoPageParam knowledgeInfo) {
		return knowledgeInfoService.getDeptKnowledgeData(knowledgeInfo);
	}


	@ApiOperation(value = "查询知识库列表数据",tags = "查询知识库列表数据", notes = "查询知识库列表数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@GetMapping("/queryAllKnowledgeNameList")
	public Result<Page<KnowledgeNameInfoResult>> queryAllKnowledgeNameList(KnowledgeInfoPageParam knowledgeInfo) {
		return knowledgeInfoService.queryAllKnowledgeNameList(knowledgeInfo);
	}


	/**
	 * 将知识库设为预设
	 * @param knowledgeInfo
	 * @return Result
	 */
	@PostMapping("/setPreset")
	public Result setPreset(@RequestBody KnowledgeInfoPageParam knowledgeInfo) {
		return knowledgeInfoService.setPreset(knowledgeInfo);
	}

	@ApiOperation(value = "修复知识库QA数据分类",tags = "修复知识库QA数据分类", notes = "修复知识库QA数据分类", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/fixKnowledgeDataType")
	public Result fixKnowledgeDataType(@RequestBody FixKnowledgeDataTypeParam fixKnowledgeDataTypeParam) {
		return knowledgeInfoService.fixKnowledgeDataType(fixKnowledgeDataTypeParam);
	}
}