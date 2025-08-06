package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.McpFunction;
import com.wenge.model.mapper.McpFunctionMapper;
import com.wenge.model.service.McpFunctionService;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wenge.model.entity.table.McpFunctionTableDef.MCP_FUNCTION;

/**
 * Description: mcp 工具服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:38:54
 *
 */
@Service
@Slf4j
public class McpFunctionServiceImpl extends ServiceImpl<McpFunctionMapper, McpFunction> implements McpFunctionService {
	/**
	 * 	mcp 工具数据库处理类
	 */
	@Autowired
	private McpFunctionMapper mcpFunctionMapper;

	@Override
	public void addMcpFunction(List<McpFunction> mcpFunctions){
		if (CollectionUtil.isEmpty(mcpFunctions)) {
			return;
		}

		// 新增Emoji过滤逻辑
		mcpFunctions.forEach(function -> {
			if (StringUtils.isNotBlank(function.getDescription())) {
				// 使用正则表达式过滤Emoji
				String filteredDesc = function.getDescription()
						.replaceAll("[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}\\x{2600}-\\x{26FF}]", "");
				function.setDescription(filteredDesc);
			}
		});

		saveBatch(mcpFunctions);
	}

	@Override
	public Result getMcpFunctionList(McpFunction mcpFunction){
		if (StringUtils.isBlank(mcpFunction.getMcpId())) {
			return Result.success();
		}
		List<McpFunction> list = Wrappers.of(mapper)
				.where(MCP_FUNCTION.MCP_ID.eq(mcpFunction.getMcpId()))
				.list();
		return Result.success(list);
	}

	@Override
	public Result updateMcpFunction(McpFunction mcpFunction){
		updateById(mcpFunction);
		return Result.success();
	}

	@Override
	public Result deleteMcpFunction(List<String> idList){
		removeByIds(idList);
		return Result.success();
	}

	@Override
	public void deleteMcpFunctionByMcpId(String mcpId) {
		if (StringUtils.isBlank(mcpId)) {
			return;
		}


		Wrappers<McpFunction> where = Wrappers.of(mapper)
				.where(MCP_FUNCTION.MCP_ID.eq(mcpId));

		remove(where);
	}
}