package com.wenge.oauth.controller;


import com.wenge.oauth.dto.param.GrantDataGetParam;
import com.wenge.oauth.dto.param.GrantDataParam;
import com.wenge.oauth.entity.GrantData;
import com.wenge.oauth.service.GrantDataService;
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
 * Description: 授权数据接口
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-08 19:59:43
 *
 */
@RestController
@RequestMapping("/grantData")
@Slf4j
@Api(tags = "授权数据接口")
public class GrantDataController {

	/**
	 * 	授权数据服务类
	 */
	@Autowired
	private GrantDataService grantDataService;

	/**
	 * 新增授权数据
	 */
	@PostMapping("/addGrantData")
	@ApiOperation(value = "新增授权数据", tags = "新增授权数据", notes = "新增授权数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result addGrantData(@RequestBody GrantDataParam param) {
		return grantDataService.addGrantData(param);
	}

	/**
	 * 查询授权数据列表
	 */
	@PostMapping("/getGrantDataList")
	@ApiOperation(value = "查询授权数据", tags = "查询授权数据", notes = "查询授权数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result getGrantDataList(@RequestBody GrantDataGetParam param) {
		return grantDataService.getGrantDataList(param);
	}

	/**
	 * 更新授权数据
	 */
	@PostMapping("/updateGrantData")
	@ApiOperation(value = "更新授权数据", tags = "更新授权数据", notes = "更新授权数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result updateGrantData(@RequestBody GrantData grantData) {
		return grantDataService.updateGrantData(grantData);
	}

	/**
	 * 删除授权数据
	 */
	@PostMapping("/deleteGrantData")
	@ApiOperation(value = "删除授权数据", tags = "删除授权数据", notes = "删除授权数据", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	public Result deleteGrantData(@RequestBody List<String> idList) {
		return grantDataService.deleteGrantData(idList);
	}

}