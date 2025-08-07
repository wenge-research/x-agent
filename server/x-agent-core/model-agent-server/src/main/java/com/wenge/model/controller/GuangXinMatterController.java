package com.wenge.model.controller;


import com.wenge.model.entity.GuangXinMatter;
import com.wenge.model.service.GuangXinMatterService;
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
 * Description: 关芯智巡的事项判别表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-19 10:44:19
 *
 */
@RestController
@RequestMapping("/guangXinMatter")
@Slf4j
@Api(tags = "关芯智巡的事项判别表接口")
public class GuangXinMatterController {

	/**
	 * 	关芯智巡的事项判别表服务类
	 */
	@Autowired
	private GuangXinMatterService guangXinMatterService;

	/**
	 * 新增关芯智巡的事项判别表
	 */
	@PostMapping("/addGuangXinMatter")
	@ApiOperation(value = "新增关芯智巡的事项判别表", tags = "新增关芯智巡的事项判别表", notes = "新增关芯智巡的事项判别表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addGuangXinMatter(@RequestBody GuangXinMatter guangXinMatter) {
		return guangXinMatterService.addGuangXinMatter(guangXinMatter);
	}

	/**
	 * 查询关芯智巡的事项判别表列表
	 */
	@PostMapping("/getGuangXinMatterList")
	@ApiOperation(value = "查询关芯智巡的事项判别表", tags = "查询关芯智巡的事项判别表", notes = "查询关芯智巡的事项判别表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getGuangXinMatterList(@RequestBody GuangXinMatter guangXinMatter) {
		return guangXinMatterService.getGuangXinMatterList(guangXinMatter);
	}

	/**
	 * 更新关芯智巡的事项判别表
	 */
	@PostMapping("/updateGuangXinMatter")
	@ApiOperation(value = "更新关芯智巡的事项判别表", tags = "更新关芯智巡的事项判别表", notes = "更新关芯智巡的事项判别表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateGuangXinMatter(@RequestBody GuangXinMatter guangXinMatter) {
		return guangXinMatterService.updateGuangXinMatter(guangXinMatter);
	}

	/**
	 * 删除关芯智巡的事项判别表
	 */
	@PostMapping("/deleteGuangXinMatter")
	@ApiOperation(value = "删除关芯智巡的事项判别表", tags = "删除关芯智巡的事项判别表", notes = "删除关芯智巡的事项判别表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteGuangXinMatter(@RequestBody List<String> idList) {
		return guangXinMatterService.deleteGuangXinMatter(idList);
	}

}