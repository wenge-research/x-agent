package com.wenge.model.controller;

import com.wenge.model.dto.param.*;
import com.wenge.model.service.ExcelParserInfoService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/excelParserInfo")
@Slf4j
public class ExcelParserInfoController {

	@Autowired
	private ExcelParserInfoService excelParserInfoService;

	@PostMapping("/getExcelTableDataList")
	public Result getExcelTableDataList(@RequestBody ExcelParserTablePageParam param) {
		return excelParserInfoService.getExcelTableDataList(param);
	}

	/**
	 * 新增知识库数据
	 */
	@PostMapping("/addExcelParserInfo")
	public Result addExcelParserInfo(AddExcelParserInfoParam param) {
		try {
			return excelParserInfoService.addExcelParserInfo(param);
		}catch (Exception e){
			e.printStackTrace();
			return Result.fail();
		}
	}

	/**
	 * 开始执行
	 */
	@PostMapping("/runTask")
	public Result runTask(@RequestBody RunTaskExcelParserInfoParam param) {
		try {
			return excelParserInfoService.runTask(param);
		}catch (Exception e){
			return Result.fail();
		}
	}

	/**
	 * 查询数据连接方式详情
	 */
	@GetMapping("/getDetail/{id}")
	@ApiOperation("详情")
	public Result getDetail(@PathVariable(name = "id") String id) {
		return excelParserInfoService.getDetail(id);
	}

	@PostMapping("/enable")
	@ApiOperation("启用关闭")
	public Result enable(@RequestBody EnableParam param) {
		return excelParserInfoService.enable(param);
	}

	@PostMapping("/updateValidDate")
	@ApiOperation("更新有效时间")
	public Result updateValidDate(@RequestBody UpdateStructDataValidDateParam param) {
		return excelParserInfoService.updateValidDate(param);
	}

}