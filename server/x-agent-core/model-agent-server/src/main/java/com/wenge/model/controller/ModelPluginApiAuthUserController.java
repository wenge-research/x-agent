package com.wenge.model.controller;


import com.wenge.model.dto.param.ModelPluginApiAuthUserPageParam;
import com.wenge.model.entity.ModelPluginApiAuthUser;
import com.wenge.model.service.ModelPluginApiAuthUserService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: caohaifeng
 * @date: 2024/11/13 14:22
 * @Description: 模型插件关联用户控制层
 * @Version:1.0
 **/
@RestController
@RequestMapping("/ModelPluginApiAuthUserAuthUser")
@Slf4j
@Api(tags = "模型插件关联用户控制层")
public class ModelPluginApiAuthUserController {

	@Autowired
	private ModelPluginApiAuthUserService modelPluginApiAuthUserService;

	/**
	 * 新增插件模型用户绑定
	 */
	@PostMapping("/addModelPluginApiAuthUser")
	@ApiOperation(value = "新增插件模型用户绑定", tags = "新增插件模型用户绑定", notes = "新增插件模型用户绑定", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addModelPluginApiAuthUser(@RequestBody ModelPluginApiAuthUser modelPluginApiAuthUser) {
		return modelPluginApiAuthUserService.addModelPluginApiAuthUser(modelPluginApiAuthUser);
	}

	/**
	 * 查询插件模型用户绑定列表
	 */
	@PostMapping("/getModelPluginApiAuthUserList")
	@ApiOperation(value = "查询插件模型用户绑定", tags = "查询插件模型用户绑定", notes = "查询插件模型用户绑定", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getModelPluginApiAuthUserList(@RequestBody ModelPluginApiAuthUserPageParam param) {
		if (param.getPageNo() == null) {
			param.setPageNo(1);
		}
		if (param.getPageSize() == null) {
			param.setPageSize(100);
		}
		return modelPluginApiAuthUserService.getModelPluginApiAuthUserList(param);
	}

	/**
	 * 更新插件模型用户绑定
	 */
	@PostMapping("/updateModelPluginApiAuthUser")
	@ApiOperation(value = "更新插件模型用户绑定", tags = "更新插件模型用户绑定", notes = "更新插件模型用户绑定", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateModelPluginApiAuthUser(@RequestBody ModelPluginApiAuthUser modelPluginApiAuthUser) {
		return modelPluginApiAuthUserService.updateModelPluginApiAuthUser(modelPluginApiAuthUser);
	}

	/**
	 * 删除插件模型用户绑定
	 */
	@PostMapping("/deleteModelPluginApiAuthUser")
	@ApiOperation(value = "删除插件模型用户绑定", tags = "删除插件模型用户绑定", notes = "删除插件模型用户绑定", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteModelPluginApiAuthUser(@RequestBody List<String> idList) {
		return modelPluginApiAuthUserService.deleteModelPluginApiAuthUser(idList);
	}

	/**
	 * 根据Id查询插件模型用户绑定详情
	 */
	@GetMapping("/getModelPluginApiAuthUserById")
	@ApiOperation(value = "根据Id查询插件模型用户绑定详情", tags = "根据Id查询插件模型用户绑定详情", notes = "根据Id查询插件模型用户绑定详情", response = Result.class, httpMethod = "GET")
	public Result getModelPluginApiAuthUserById(@RequestParam(value = "id", required = true) String id) {
		return modelPluginApiAuthUserService.getModelPluginApiAuthUserById(id);
	}

	/**
	 * 更新状态
	 */
	@GetMapping("/updateStatus")
	@ApiOperation(value = "更新状态", tags = "更新状态", notes = "更新状态", response = Result.class, httpMethod = "GET")
	public Result updateStatus(@RequestBody ModelPluginApiAuthUser modelPluginApiAuthUser) {
		return modelPluginApiAuthUserService.updateStatus(modelPluginApiAuthUser);
	}
}