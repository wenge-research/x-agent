package com.wenge.model.service;


import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.ApplicationMcpRef;
import com.wenge.model.entity.McpServer;

import java.util.List;
import java.util.Map;

/**
 * Description: 应用-mcp 服务关联表服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-15 16:14:14
 *
 */
public interface ApplicationMcpRefService extends IService<ApplicationMcpRef> {

    void bindApplicationMcpRef(String appId, List<String> mcpServerList);

    List<McpServer> getMcpServerByAppId(String appId);

    Map<String, List<McpServer>> getMcpServerByAppIds(List<String> appIds);
}