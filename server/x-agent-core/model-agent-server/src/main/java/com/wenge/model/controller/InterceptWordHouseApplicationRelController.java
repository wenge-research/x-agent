package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.BindInterceptWordHouseParam;
import com.wenge.model.dto.param.InterceptWordHouseApplicationRelPageParam;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.entity.InterceptWordHouseApplicationRel;
import com.wenge.model.service.InterceptWordHouseApplicationRelService;
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
 * @author: caohaifeng
 * @date: 2024/8/22 14:02
 * @Description: 敏感词库与应用关联管理Controller
 * @Version:1.0
 **/
@Api(tags = "Description: 敏感词库与应用关系管理Controller")
@RestController
@RequestMapping("/interceptWordHouseAppRel")
@Slf4j
public class InterceptWordHouseApplicationRelController {

	/**
	 * 拦截词库服务类
	 */
	@Autowired
	private InterceptWordHouseApplicationRelService interceptWordHouseApplicationRelService;

	/**
	 * 新增拦截词
	 */
	@ApiOperation(value = "新增拦截词库与应用关系", tags = "新增拦截词库与应用关系", notes = "新增拦截词库与应用关系", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addInterceptWordHouseApplicationRel")
	@OperaLogs
	public Result addInterceptWordHouseApplicationRel(@RequestBody InterceptWordHouseApplicationRel interceptWordHouse) {
		return interceptWordHouseApplicationRelService.addInterceptWordHouseApplicationRel(interceptWordHouse);
	}

	/**
	 * 查询拦截词列表
	 */
	@ApiOperation(value = "查询拦截词库与应用关系列表", tags = "查询拦截词库与应用关系列表", notes = "查询拦截词库与应用关系列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getInterceptWordHouseApplicationRelList")
	public Result<Page<InterceptWordHouse>> getInterceptWordHouseApplicationRelList(@RequestBody InterceptWordHouseApplicationRelPageParam interceptWordHouse) {
		interceptWordHouse.setPageNo(interceptWordHouse.getPageNo() == null ? 1 : interceptWordHouse.getPageNo());
		interceptWordHouse.setPageSize(interceptWordHouse.getPageSize() == null ? 500 : interceptWordHouse.getPageSize());
		return interceptWordHouseApplicationRelService.getInterceptWordHouseApplicationRelList(interceptWordHouse);
	}

	/**
	 * 更新拦截词
	 */
	@ApiOperation(value = "更新拦截词库与应用关系", tags = "更新拦截词库与应用关系", notes = "更新拦截词库与应用关系", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateInterceptWordHouseApplicationRel")
	@OperaLogs
	public Result updateInterceptWordHouseApplicationRel(@RequestBody InterceptWordHouseApplicationRel interceptWordHouse) {
		return interceptWordHouseApplicationRelService.updateInterceptWordHouseApplicationRel(interceptWordHouse);
	}

	/**
	 * 删除拦截词
	 */
	@ApiOperation(value = "删除拦截词库与应用关系", tags = "删除拦截词库与应用关系", notes = "删除拦截词库与应用关系", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteInterceptWordHouseApplicationRel")
	@OperaLogs
	public Result deleteInterceptWordHouseApplicationRel(@RequestBody List<String> idList) {
		return interceptWordHouseApplicationRelService.deleteInterceptWordHouseApplicationRel(idList);
	}

	/**
	 * 批量绑定拦截词库
	 */
	@ApiOperation(value = "批量绑定拦截词库", tags = "批量绑定拦截词库", notes = "批量绑定拦截词库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/batchBindInterceptWordHouse")
	@OperaLogs
	public Result batchBindInterceptWordHouse(@RequestBody BindInterceptWordHouseParam param) {
		return interceptWordHouseApplicationRelService.batchBindInterceptWordHouse(param);
	}

}