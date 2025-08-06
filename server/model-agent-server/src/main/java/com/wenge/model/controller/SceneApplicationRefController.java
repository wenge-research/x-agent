package com.wenge.model.controller;


import com.wenge.model.entity.SceneApplicationRef;
import com.wenge.model.service.SceneApplicationRefService;
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
 * Description: 业务场景与应用关联表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 11:45:38
 *
 */
@RestController
@RequestMapping("/sceneApplicationRef")
@Slf4j
@Api(tags = "业务场景与应用关联表接口")
public class SceneApplicationRefController {

	/**
	 * 	业务场景与应用关联表服务类
	 */
	@Autowired
	private SceneApplicationRefService sceneApplicationRefService;

	/**
	 * 新增业务场景与应用关联表
	 */
	@PostMapping("/addSceneApplicationRef")
	@ApiOperation(value = "新增业务场景与应用关联表", tags = "新增业务场景与应用关联表", notes = "新增业务场景与应用关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addSceneApplicationRef(@RequestBody SceneApplicationRef sceneApplicationRef) {
		return sceneApplicationRefService.addSceneApplicationRef(sceneApplicationRef);
	}

	/**
	 * 查询业务场景与应用关联表列表
	 */
	@PostMapping("/getSceneApplicationRefList")
	@ApiOperation(value = "查询业务场景与应用关联表", tags = "查询业务场景与应用关联表", notes = "查询业务场景与应用关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<List<SceneApplicationRef>> getSceneApplicationRefList(@RequestBody SceneApplicationRef sceneApplicationRef) {
		return sceneApplicationRefService.getSceneApplicationRefList(sceneApplicationRef);
	}

	/**
	 * 删除业务场景与应用关联表
	 */
	@PostMapping("/deleteSceneApplicationRef")
	@ApiOperation(value = "删除业务场景与应用关联表", tags = "删除业务场景与应用关联表", notes = "删除业务场景与应用关联表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteSceneApplicationRef(@RequestBody SceneApplicationRef sceneApplicationRef) {
		return sceneApplicationRefService.deleteSceneApplicationRef(sceneApplicationRef);
	}

}