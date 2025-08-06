package com.wenge.model.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.TableDirectoryDataChooseFieldParam;
import com.wenge.model.dto.param.TableDirectoryDataPageParam;
import com.wenge.model.dto.param.TableDirectoryInfoPageParam;
import com.wenge.model.entity.TableDirectoryInfo;
import com.wenge.model.service.TableDirectoryInfoService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tableDirectoryInfo")
@Slf4j
public class TableDirectoryInfoController {

	/**
	 * 	应用信息服务类
	 */
	@Autowired
	private TableDirectoryInfoService tableDirectoryInfoService;

	/**
	 * 查询应用信息列表
	 */
    @ApiOperation(value = "查询信息列表",tags = "查询信息列表", notes = "查询信息列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getApplicationInfoList")
	public Result<Page<TableDirectoryInfo>> getInfoList(@RequestBody TableDirectoryInfoPageParam param) {
		return tableDirectoryInfoService.getInfoList(param);
	}

	/**
	 * 根据表名查对应的数据
	 */
	@ApiOperation(value = "查询信息列表",tags = "查询信息列表", notes = "查询信息列表", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/getDataList")
	public Result<Page<TableDirectoryInfo>> getDataList(@RequestBody TableDirectoryDataPageParam param) {
		return tableDirectoryInfoService.getDataList(param);
	}

	/**
	 * 勾选要处理的字段
	 */
	@ApiOperation(value = "勾选要处理的字段",tags = "勾选要处理的字段", notes = "勾选要处理的字段", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
	@PostMapping("/chooseField")
	public Result chooseField(@RequestBody TableDirectoryDataChooseFieldParam param) {
		if(StringUtils.isEmpty(param.getTableName())){
			return Result.fail("数据库表名tableName不能为空");
		}
		if(StringUtils.isEmpty(param.getBusinessId())){
			return Result.fail("businessId不能为空");
		}
		if(CollectionUtils.isEmpty(param.getTableInfoVos())){
			return Result.fail("tableInfoVos不能为空");
		}
		return tableDirectoryInfoService.chooseField(param);
	}
}
