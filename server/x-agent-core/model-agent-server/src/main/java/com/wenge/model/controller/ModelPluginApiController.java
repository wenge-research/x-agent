package com.wenge.model.controller;


import com.alibaba.fastjson.JSONObject;
import com.wenge.model.dto.param.ModelPluginApiPageParam;
import com.wenge.model.entity.ModelPluginApiManage;
import com.wenge.model.service.ModelPluginApiService;
import com.wenge.oauth.annotation.InterfaceCallLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: caohaifeng
 * @date: 2024/11/4 14:55
 * @Description: 模型对外插件接口提供能力
 * @Version:1.0
 **/
@RestController
@RequestMapping("/modelPluginApi")
@Slf4j
@Api(tags = "模型对外插件接口")
public class ModelPluginApiController {

	@Autowired
	private ModelPluginApiService modelPluginApiService;

	/**
	 * 新增插件模型
	 */
	@PostMapping("/addModelPluginApi")
	@ApiOperation(value = "新增插件模型", tags = "新增插件模型", notes = "新增插件模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addModelPluginApi(@RequestBody ModelPluginApiManage plugin) {
		return modelPluginApiService.addModelPluginApi(plugin);
	}

	/**
	 * 查询插件模型列表
	 */
	@PostMapping("/getModelPluginApiList")
	@ApiOperation(value = "查询插件模型", tags = "查询插件模型", notes = "查询插件模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getModelPluginApiList(@RequestBody ModelPluginApiPageParam param) {
		if (param.getPageNo() == null) {
			param.setPageNo(1);
		}
		if (param.getPageSize() == null) {
			param.setPageSize(10);
		}
		return modelPluginApiService.getModelPluginApiList(param);
	}

	/**
	 * 更新插件模型
	 */
	@PostMapping("/updateModelPluginApi")
	@ApiOperation(value = "更新插件模型", tags = "更新插件模型", notes = "更新插件模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateModelPluginApi(@RequestBody ModelPluginApiManage modelPluginApiManage) {
		return modelPluginApiService.updateModelPluginApi(modelPluginApiManage);
	}

	/**
	 * 删除插件模型
	 */
	@PostMapping("/deleteModelPluginApi")
	@ApiOperation(value = "删除插件模型", tags = "删除插件模型", notes = "删除插件模型", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteModelPluginApi(@RequestBody List<String> idList) {
		return modelPluginApiService.deleteModelPluginApi(idList);
	}

	/**
	 * 根据Id查询插件模型详情
	 */
	@GetMapping("/getModelPluginApiById")
	@ApiOperation(value = "根据Id查询插件模型详情", tags = "根据Id查询插件模型详情", notes = "根据Id查询插件模型详情", response = Result.class, httpMethod = "GET")
	public Result getModelPluginApiById(@RequestParam(value = "id", required = true) String id) {
		return modelPluginApiService.getModelPluginApiById(id);
	}

	/**
	 * 根据插件Id查询插件模型详情
	 */
	@GetMapping("/findByPluginId")
	@ApiOperation(value = "根据Id查询插件模型详情", tags = "根据Id查询插件模型详情", notes = "根据Id查询插件模型详情", response = Result.class, httpMethod = "GET")
	public Result findByPluginId(@RequestParam(value = "pluginId", required = true) String pluginId) {
		return modelPluginApiService.findByPluginId(pluginId);
	}


	/**
	 * 模型插件调用API-通用
	 * @param request
	 * @param modelPluginApiId
	 * @param paramJSONObject
	 * @return
	 */
	@PostMapping("/capability/{modelPluginApiId}")
	@ApiOperation(value = "模型插件调用API-通用", tags = "模型插件调用API-通用", notes = "模型插件调用API-通用", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@InterfaceCallLog(description = "模型插件调用API-通用")
	public Result capability(HttpServletRequest request,@PathVariable(required = true) String modelPluginApiId, @RequestBody JSONObject paramJSONObject) {
		if (paramJSONObject == null) {
			return Result.error("401", "请求body参数异常");
		}
		if (modelPluginApiId == null) {
			return Result.error("402", "请求API不存在");
		}
		return modelPluginApiService.handleCapability(request, modelPluginApiId, paramJSONObject);
	}


}