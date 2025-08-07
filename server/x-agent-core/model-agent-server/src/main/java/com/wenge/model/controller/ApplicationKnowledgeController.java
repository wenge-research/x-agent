package com.wenge.model.controller;

import cn.hutool.db.PageResult;
import com.wenge.model.entity.ApplicationKnowledge;
import com.wenge.model.service.ApplicationKnowledgeService;
import com.wenge.oauth.annotation.OperaLogs;
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
 * Description: 应用-知识库关联表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 19:57:12
 *
 */
@Api(tags = "Description: 应用-知识库关联表接口")
@RestController
@RequestMapping("/applicationKnowledge")
@Slf4j
public class ApplicationKnowledgeController {

	/**
	 * 	应用-知识库关联表服务类
	 */
	@Autowired
	private ApplicationKnowledgeService applicationKnowledgeService;

	/**
	 * 新增应用-知识库关联表
	 */
    @ApiOperation(value = "新增应用-知识库关联表",tags = "新增应用-知识库关联表", notes = "新增应用-知识库关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addApplicationKnowledge")
	@OperaLogs
	public Result addApplicationKnowledge(@RequestBody ApplicationKnowledge applicationKnowledge) {
		return applicationKnowledgeService.addApplicationKnowledge(applicationKnowledge);
	}

	/**
	 * 查询应用-知识库关联表列表
	 */
    @ApiOperation(value = "查询应用-知识库关联表列表",tags = "查询应用-知识库关联表列表", notes = "查询应用-知识库关联表列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationKnowledgeList")
	public Result getApplicationKnowledgeList(@RequestBody ApplicationKnowledge applicationKnowledge) {
		return applicationKnowledgeService.getApplicationKnowledgeList(applicationKnowledge);
	}

	/**
	 * 更新应用-知识库关联表
	 */
    @ApiOperation(value = "更新应用-知识库关联表",tags = "更新应用-知识库关联表", notes = "更新应用-知识库关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateApplicationKnowledge")
	@OperaLogs
	public Result updateApplicationKnowledge(@RequestBody ApplicationKnowledge applicationKnowledge) {
		return applicationKnowledgeService.updateApplicationKnowledge(applicationKnowledge);
	}

	/**
	 * 删除应用-知识库关联表
	 */
    @ApiOperation(value = "删除应用-知识库关联表",tags = "删除应用-知识库关联表", notes = "删除应用-知识库关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteApplicationKnowledge")
	@OperaLogs
	public Result deleteApplicationKnowledge(@RequestBody List<String> idList) {
		return applicationKnowledgeService.deleteApplicationKnowledge(idList);
	}

}