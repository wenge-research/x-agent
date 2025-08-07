package com.wenge.model.controller;

import com.wenge.model.dto.param.GroupMatterRefAddParam;
import com.wenge.model.dto.param.SceneMatterGroupRefListParam;
import com.wenge.model.entity.SceneMatterGroupRef;
import com.wenge.model.service.SceneMatterGroupRefService;
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
 * Description: 接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:20:59
 *
 */
@RestController
@RequestMapping("/sceneMatterGroupRef")
@Slf4j
@Api(tags = "接口")
public class SceneMatterGroupRefController {

	/**
	 * 	服务类
	 */
	@Autowired
	private SceneMatterGroupRefService sceneMatterGroupRefService;

	/**
	 * 新增分组与事项的关系
	 */
	@PostMapping("/addSceneMatterGroupRef")
	@ApiOperation(value = "新增", tags = "新增", notes = "新增", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addSceneMatterGroupRef(@RequestBody GroupMatterRefAddParam param) {
		return sceneMatterGroupRefService.addSceneMatterGroupRef(param);
	}

	/**
	 * 查询分组与事项的关系列表
	 */
	@PostMapping("/getSceneMatterGroupRefList")
	@ApiOperation(value = "查询", tags = "查询", notes = "查询", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<SceneMatterGroupRef>> getSceneMatterGroupRefList(@RequestBody SceneMatterGroupRefListParam param) {
		return sceneMatterGroupRefService.getSceneMatterGroupRefList(param);
	}

	///**
	// * 更新
	// */
	//@PostMapping("/updateSceneMatterGroupRef")
	//@ApiOperation(value = "更新", tags = "更新", notes = "更新", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	//public Result updateSceneMatterGroupRef(@RequestBody SceneMatterGroupRef sceneMatterGroupRef) {
	//	return sceneMatterGroupRefService.updateSceneMatterGroupRef(sceneMatterGroupRef);
	//}
	//
	///**
	// * 删除
	// */
	//@PostMapping("/deleteSceneMatterGroupRef")
	//@ApiOperation(value = "删除", tags = "删除", notes = "删除", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	//public Result deleteSceneMatterGroupRef(@RequestBody List<String> idList) {
	//	return sceneMatterGroupRefService.deleteSceneMatterGroupRef(idList);
	//}

}