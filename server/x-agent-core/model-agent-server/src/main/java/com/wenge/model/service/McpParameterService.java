package com.wenge.model.service;


import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.McpParameter;
import com.wg.appframe.core.bean.Result;

import java.util.List;
import java.util.Map;

/**
 * Description: mcp 工具服务类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:39:05
 */
public interface McpParameterService extends IService<McpParameter> {

    void addMcpParameter(List<McpParameter> mcpParameters);

    Result getMcpParameterList(McpParameter mcpParameter);

    Result updateMcpParameter(McpParameter mcpParameter);

    Result deleteMcpParameter(List<String> idList);

    void deleteMcpParameterByMcpId(String mcpId);

    Map<String, List<McpParameter>> getParameterByFunIdList(List<String> funIdList);

}