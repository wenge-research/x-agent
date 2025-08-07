package com.wenge.model.controller;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.InterceptWordHousePageParam;
import com.wenge.model.dto.param.InterceptWordPageParam;
import com.wenge.model.entity.InterceptWord;
import com.wenge.model.entity.InterceptWordHouse;
import com.wenge.model.entity.MatterGuide;
import com.wenge.model.service.InterceptWordHouseService;
import com.wenge.model.service.InterceptWordService;
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

import javax.validation.Valid;
import java.util.List;

/**
 * @author: caohaifeng
 * @date: 2024/8/22 15:20
 * @Description: 敏感词库管理Controller
 * @Version:1.0
 **/
@Api(tags = "Description: 敏感词库管理Controller")
@RestController
@RequestMapping("/interceptWordHouse")
@Slf4j
public class InterceptWordHouseController {

	/**
	 * 拦截词库服务类
	 */
	@Autowired
	private InterceptWordHouseService interceptWordHouseService;

	/**
	 * 新增拦截词库
	 */
	@ApiOperation(value = "新增拦截词库", tags = "新增拦截词库", notes = "新增拦截词库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/addInterceptWordHouse")
	@OperaLogs
	public Result addInterceptWordHouse(@RequestBody InterceptWordHouse interceptWordHouse) {
		if (StrUtil.isNotBlank(interceptWordHouse.getRemark()) && interceptWordHouse.getRemark().length() > 200) {
			return Result.fail("备注长度不能超过200个字符");
		}
		return interceptWordHouseService.addInterceptWordHouse(interceptWordHouse);
	}

	/**
	 * 查询拦截词列表
	 */
	@ApiOperation(value = "查询拦截词库列表", tags = "查询拦截词库列表", notes = "查询拦截词库列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getInterceptWordHouseList")
	public Result<Page<InterceptWordHouse>> getInterceptWordHouseList(@RequestBody InterceptWordHousePageParam interceptWordHouse) {
		return interceptWordHouseService.getInterceptWordHouseList(interceptWordHouse);
	}

	/**
	 * 查询拦截词库所有词库 并标记已经关联的应用
	 */
	@ApiOperation(value = "查询拦截词库所有词库 并标记已经关联的应用", tags = "查询拦截词库列表", notes = "查询拦截词库列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getInterceptWordHouseListAll")
	public Result<Page<InterceptWordHouse>> getInterceptWordHouseListAll(@RequestBody InterceptWordHousePageParam interceptWordHouse) {
		if (interceptWordHouse.getPageNo()==null) {
			interceptWordHouse.setPageNo(1);
		}
		if (interceptWordHouse.getPageSize()==null) {
			interceptWordHouse.setPageSize(500);
		}
		return interceptWordHouseService.getInterceptWordHouseListAll(interceptWordHouse);
	}

	/**
	 * 更新拦截词库
	 */
	@ApiOperation(value = "更新拦截词库", tags = "更新拦截词库", notes = "更新拦截词库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/updateInterceptWordHouse")
	public Result updateInterceptWordHouse(@RequestBody InterceptWordHouse interceptWordHouse) {
		return interceptWordHouseService.updateInterceptWordHouse(interceptWordHouse);
	}

	/**
	 * 删除拦截词库
	 */
	@ApiOperation(value = "删除拦截词库", tags = "删除拦截词库", notes = "删除拦截词库", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/deleteInterceptWordHouse")
	public Result deleteInterceptWordHouse(@RequestBody List<String> idList) {
		return interceptWordHouseService.deleteInterceptWordHouse(idList);
	}

	/**
	 * 关闭-开启
	 * @param interceptWordHouse
	 * @return
	 */
	@PostMapping("/updateStatus")
	public Result updateStatus(@RequestBody InterceptWordHouse interceptWordHouse) {
		return interceptWordHouseService.updateStatus(interceptWordHouse);
	}

	/**
	 * 将敏感词设为预设
	 * @param interceptWordHouse
	 * @return Result
	 */
	@PostMapping("/setPreset")
	public Result setPreset(@RequestBody InterceptWordHousePageParam interceptWordHouse) {
		return interceptWordHouseService.setPreset(interceptWordHouse);
	}

}