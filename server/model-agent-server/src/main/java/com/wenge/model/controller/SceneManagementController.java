package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.SceneManagementPageParam;
import com.wenge.model.entity.SceneManagement;
import com.wenge.model.service.SceneManagementService;
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
 * Description: 业务场景表接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 10:12:42
 *
 */
@RestController
@RequestMapping("/sceneManagement")
@Slf4j
@Api(tags = "业务场景表接口")
public class SceneManagementController {

	/**
	 * 	业务场景表服务类
	 */
	@Autowired
	private SceneManagementService sceneManagementService;

	/**
	 * 新增业务场景表
	 */
	@PostMapping("/addSceneManagement")
	@ApiOperation(value = "新增业务场景表", tags = "新增业务场景表", notes = "新增业务场景表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<SceneManagement> addSceneManagement(@RequestBody SceneManagement sceneManagement) {
		return sceneManagementService.addSceneManagement(sceneManagement);
	}

	/**
	 * 查询业务场景表列表
	 */
	@PostMapping("/getSceneManagementList")
	@ApiOperation(value = "查询业务场景表", tags = "查询业务场景表", notes = "查询业务场景表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<Page<SceneManagement>> getSceneManagementList(@RequestBody SceneManagementPageParam param) {
		return sceneManagementService.getSceneManagementList(param);
	}

	/**
	 * 删除业务场景表
	 */
	@PostMapping("/deleteSceneManagement")
	@ApiOperation(value = "删除业务场景表", tags = "删除业务场景表", notes = "删除业务场景表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteSceneManagement(@RequestBody List<String> idList) {
		return sceneManagementService.deleteSceneManagement(idList);
	}

	/**
	 * 查询业务场景详情
	 */
	@PostMapping("/getSceneDetail")
	@ApiOperation(value = "查询业务场景详情", tags = "查询业务场景详情", notes = "查询业务场景详情", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result<SceneManagement> getSceneDetail(@RequestBody StringParam param) {
		return sceneManagementService.getSceneDetail(param);
	}
}