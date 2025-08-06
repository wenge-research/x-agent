package com.wenge.model.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.McpParameter;
import com.wenge.model.mapper.McpParameterMapper;
import com.wenge.model.service.McpParameterService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.McpParameterTableDef.MCP_PARAMETER;

/**
 * Description: mcp 工具服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:39:05
 *
 */
@Service
@Slf4j
public class McpParameterServiceImpl extends ServiceImpl<McpParameterMapper, McpParameter> implements McpParameterService {
	/**
	 * 	mcp 工具数据库处理类
	 */
	@Autowired
	private McpParameterMapper mcpParameterMapper;

	@Override
	public void addMcpParameter(List<McpParameter> mcpParameters){
		if (CollectionUtil.isEmpty(mcpParameters)) {
			return;
		}
		saveBatch(mcpParameters);
	}

	@Override
	public Result getMcpParameterList(McpParameter mcpParameter){
		return Result.success(null);
	}

	@Override
	public Result updateMcpParameter(McpParameter mcpParameter){
		updateById(mcpParameter);
		return Result.success();
	}

	@Override
	public Result deleteMcpParameter(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public void deleteMcpParameterByMcpId(String mcpId) {
		if (StringUtils.isBlank(mcpId)) {
			return;
		}

		Wrappers<McpParameter> where = Wrappers.of(mapper)
				.where(MCP_PARAMETER.MCP_ID.eq(mcpId));
		remove(where);
	}

	@Override
	public Map<String, List<McpParameter>> getParameterByFunIdList(List<String> funIdList) {
		if (CollectionUtil.isEmpty(funIdList)) {
			return Maps.newHashMap();
		}

		List<McpParameter> list = Wrappers.of(mapper)
				.where(MCP_PARAMETER.FUNCTION_ID.in(funIdList))
				.list();

		return list.stream().collect(Collectors.groupingBy(McpParameter::getFunctionId));
	}
}