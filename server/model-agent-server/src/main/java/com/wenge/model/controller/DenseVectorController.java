package com.wenge.model.controller;


import com.wenge.model.dto.param.DenseVectorPageParam;
import com.wenge.model.entity.DenseVector;
import com.wenge.model.service.DenseVectorService;
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
 * Description: 向量化模型接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-09-11 14:45:53
 *
 */
@RestController
@RequestMapping("/denseVector")
@Slf4j
@Api(tags = "向量化模型接口")
public class DenseVectorController {

	/**
	 * 	向量化模型服务类
	 */
	@Autowired
	private DenseVectorService denseVectorService;

	/**
	 * 新增向量化模型
	 */
	@PostMapping("/addDenseVector")
	@ApiOperation(value = "新增向量化模型", tags = "新增向量化模型", notes = "新增向量化模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addDenseVector(@RequestBody DenseVector denseVector) {
		return denseVectorService.addDenseVector(denseVector);
	}

	/**
	 * 查询向量化模型列表
	 */
	@PostMapping("/getDenseVectorList")
	@ApiOperation(value = "查询向量化模型", tags = "查询向量化模型", notes = "查询向量化模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getDenseVectorList(@RequestBody DenseVectorPageParam denseVector) {
		return denseVectorService.getDenseVectorList(denseVector);
	}

	/**
	 * 更新向量化模型
	 */
	@PostMapping("/updateDenseVector")
	@ApiOperation(value = "更新向量化模型", tags = "更新向量化模型", notes = "更新向量化模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateDenseVector(@RequestBody DenseVector denseVector) {
		return denseVectorService.updateDenseVector(denseVector);
	}

	/**
	 * 删除向量化模型
	 */
	@PostMapping("/deleteDenseVector")
	@ApiOperation(value = "删除向量化模型", tags = "删除向量化模型", notes = "删除向量化模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteDenseVector(@RequestBody List<String> idList) {
		return denseVectorService.deleteDenseVector(idList);
	}

}