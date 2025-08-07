package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.DataCollectPageParam;
import com.wenge.model.entity.DataCollectInfo;
import com.wenge.model.service.DataCollectInfoService;
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
 * Description: 数据集信息表接口
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 18:05:32
 *
 */
@RestController
@RequestMapping("/dataCollectInfo")
@Slf4j
@Api(tags = "数据集信息表接口")
public class DataCollectInfoController {

	/**
	 * 	数据集信息表服务类
	 */
	@Autowired
	private DataCollectInfoService dataCollectInfoService;

	/**
	 * 新增数据集信息表
	 */
	@PostMapping("/addDataCollectInfo")
	@ApiOperation(value = "新增数据集信息表", tags = "新增数据集信息表", notes = "新增数据集信息表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addDataCollectInfo(@RequestBody DataCollectInfo dataCollectInfo) {
		return dataCollectInfoService.addDataCollectInfo(dataCollectInfo);
	}

	/**
	 * 查询数据集信息表列表
	 */
	@PostMapping("/getDataCollectInfoList")
	@ApiOperation(value = "查询数据集信息表", tags = "查询数据集信息表", notes = "查询数据集信息表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<DataCollectInfo>> getDataCollectInfoList(@RequestBody DataCollectPageParam dataCollectInfo) {
		return dataCollectInfoService.getDataCollectInfoList(dataCollectInfo);
	}

	/**
	 * 删除数据集信息表
	 */
	@PostMapping("/deleteDataCollectInfo")
	@ApiOperation(value = "删除数据集信息表", tags = "删除数据集信息表", notes = "删除数据集信息表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteDataCollectInfo(@RequestBody List<String> idList) {
		return dataCollectInfoService.deleteDataCollectInfo(idList);
	}

}