package com.wenge.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.ApplicationMcpRef;
import com.wenge.model.entity.McpServer;
import com.wenge.model.mapper.ApplicationMcpRefMapper;
import com.wenge.model.service.ApplicationMcpRefService;
import com.wenge.model.service.McpServerService;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wenge.model.entity.table.ApplicationMcpRefTableDef.APPLICATION_MCP_REF;
import static com.wenge.model.entity.table.McpServerTableDef.MCP_SERVER;

/**
 * Description: 应用-mcp 服务关联表服务实现类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-15 16:14:14
 *
 */
@Service
@Slf4j
public class ApplicationMcpRefServiceImpl extends ServiceImpl<ApplicationMcpRefMapper, ApplicationMcpRef> implements ApplicationMcpRefService {

	/**
	 * 	应用-mcp 服务关联表数据库处理类
	 */
	@Autowired
	private ApplicationMcpRefMapper applicationMcpRefMapper;

	@Autowired
	private McpServerService mcpServerService;

	@Override
	public void bindApplicationMcpRef(String appId, List<String> mcpServerList) {
		if (StringUtils.isBlank(appId)) {
			return;
		}
		Wrappers<ApplicationMcpRef> where = Wrappers.of(mapper)
				.where(APPLICATION_MCP_REF.APPLICATION_ID.eq(appId));
		// 删除关联
		remove(where);

		if (CollectionUtil.isNotEmpty(mcpServerList)) {
			List<ApplicationMcpRef> refList = mcpServerList.stream().map(p -> {
				ApplicationMcpRef applicationMcpRef = new ApplicationMcpRef();
				applicationMcpRef.setApplicationId(appId);
				applicationMcpRef.setMcpServerId(p);
				return applicationMcpRef;
			}).collect(Collectors.toList());
			saveBatch(refList);
		}
	}

	@Override
	public List<McpServer> getMcpServerByAppId(String appId) {

		if (StringUtils.isBlank(appId)) {
			return Lists.newArrayList();
		}

		Wrappers<Object> where = Wrappers.init()
				.select(MCP_SERVER.ALL_COLUMNS)
				.innerJoin(APPLICATION_MCP_REF).on(APPLICATION_MCP_REF.MCP_SERVER_ID.eq(MCP_SERVER.MCP_ID))
				.where(APPLICATION_MCP_REF.APPLICATION_ID.eq(appId));
        return mcpServerService.list(where);
	}

	@Override
	public Map<String, List<McpServer>> getMcpServerByAppIds(List<String> appIds) {
		if (CollectionUtil.isEmpty(appIds)) {
			return Maps.newHashMap();
		}

		// 根据应用 id查询 mcp 关联表，并封装为 map，key 为 appId
		Wrappers<Object> where = Wrappers.init()
				.select(MCP_SERVER.ALL_COLUMNS, APPLICATION_MCP_REF.APPLICATION_ID)
				.innerJoin(APPLICATION_MCP_REF).on(APPLICATION_MCP_REF.MCP_SERVER_ID.eq(MCP_SERVER.MCP_ID))
				.where(APPLICATION_MCP_REF.APPLICATION_ID.in(appIds));
		List<McpServer> list = mcpServerService.list(where);
        return list.stream().collect(Collectors.groupingBy(McpServer::getApplicationId));
	}


}