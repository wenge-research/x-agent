package com.wenge.model.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.mybatisflex.core.util.StringUtil;
import com.wenge.model.dto.param.*;
import com.wenge.model.service.DataSourceParserInfoService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/dataSourceParserInfo")
@Slf4j
public class DataSourceParserInfoController {

	@Autowired
	private DataSourceParserInfoService dataSourceParserInfoService;

	@PostMapping("/unionData")
	public Result unionData(@RequestBody UnionParam param) {
		return dataSourceParserInfoService.unionData(param);
	}

	@PostMapping("/updateSynchStatusUnionData")
	public Result updateSynchStatusUnionData(@RequestBody UnionParam param) {
		return dataSourceParserInfoService.updateSynchStatusUnionData(param);
	}

	@GetMapping("/getTableList/{businessId}")
	public Result getTableList(@PathVariable(name = "businessId")String businessId) {
		return dataSourceParserInfoService.getTableList(businessId);
	}

	@PostMapping("/getDataSourceDataList")
	@ApiOperation("获取源端数据表下的数据，显示列表")
	public Result getDataSourceDataList(@RequestBody DataSourceTableDataPageParam param) throws SQLException {
		if(StringUtils.isEmpty(param.getTableName())){
			return Result.fail("数据库表名tableName不能为空");
		}
		if(StringUtils.isEmpty(param.getDataSourceId())){
			return Result.fail("dataSourceId不能为空");
		}
		if(StringUtils.isEmpty(param.getBussiness())){
			return Result.fail("bussiness不能为空");
		}
		return dataSourceParserInfoService.getDataSourceDataList(param);
	}


	@PostMapping("/addDataSourceParserInfo")
	@ApiOperation("新增知识库数据")
	public Result addDataSourceParserInfo(@Valid @RequestBody AddDataSourceParserInfoParam param) {
		try {
			if(ObjectUtil.isEmpty(param.getDsType())){
				return Result.fail("数据源类型1 MYSQL 2 DM 3 Yashan 不能为空");
			}
			if(StringUtils.isEmpty(param.getDesc())){
				return Result.fail("数据库名称不能为空");
			}
			if(StringUtils.isEmpty(param.getJdbcUrl())){
				return Result.fail("数据库ip不能为空");
			}
			if(StringUtils.isEmpty(param.getPort())){
				return Result.fail("数据库端口不能为空");
			}
			if(StringUtils.isEmpty(param.getJdbcName())){
				return Result.fail("数据库账账户名，不能为空");
			}
			if(StringUtils.isEmpty(param.getJdbcPassword())){
				return Result.fail("数据库密码不能为空");
			}
			return dataSourceParserInfoService.addDataSourceParserInfo(param);
		}catch (Exception e){
			return Result.fail();
		}
	}

	@PostMapping("/runTask")
	@ApiOperation("执行任务，开始向量化")
	public Result runTask(@RequestBody RunTaskDataSourceParserInfoParam param) {
		try {
			if(StringUtils.isEmpty(param.getTableName())){
				return Result.fail("数据库表名tableName不能为空");
			}
			if(StringUtils.isEmpty(param.getKnowledgeId())){
				return Result.fail("knowledgeId不能为空");
			}
			if(StringUtils.isEmpty(param.getDataSourceId())){
				return Result.fail("dataSourceId不能为空");
			}
			return dataSourceParserInfoService.runTask(param);
		}catch (Exception e){
			return Result.fail();
		}
	}

	@GetMapping("/getDetail/{dataSourceId}")
	@ApiOperation("详情，查询数据连接方式详情")
	public Result getDetail(@PathVariable(name = "dataSourceId") String dataSourceId) {
		return dataSourceParserInfoService.getDetail(dataSourceId);
	}

	@PostMapping("/del")
	@ApiOperation("删除任务表，不涉及源数据操作")
	public Result del(@RequestBody StrutsDelParam param) {
		return dataSourceParserInfoService.del(param);
	}

	@PostMapping("/enable")
	@ApiOperation("启用关闭")
	public Result enable(@RequestBody EnableParam param) {
		return dataSourceParserInfoService.enable(param);
	}

	@PostMapping("/strutsList")
	@ApiOperation("查看结构化结果")
	public Result strutsList(@RequestBody StrutsResultPageParam param) {
		try {
			return dataSourceParserInfoService.strutsList(param);
		} catch (IOException e) {
			e.printStackTrace();
			return Result.fail();
		}
	}

	@PostMapping("/updateValidDate")
	@ApiOperation("修改有效时间")
	public Result updateValidDate(@RequestBody UpdateStructDataValidDateParam param) {
		return dataSourceParserInfoService.updateValidDate(param);
	}

}
