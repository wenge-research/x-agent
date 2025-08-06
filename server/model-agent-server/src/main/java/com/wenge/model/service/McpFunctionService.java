package com.wenge.model.service;


import com.mybatisflex.core.service.IService;
import com.wenge.model.entity.McpFunction;
import com.wg.appframe.core.bean.Result;

import java.util.List;

/**
 * Description: mcp 工具服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:38:54
 *
 */
public interface McpFunctionService extends IService<McpFunction> {

    void addMcpFunction(List<McpFunction> mcpFunctions);

    Result getMcpFunctionList(McpFunction mcpFunction);

    Result updateMcpFunction(McpFunction mcpFunction);

    Result deleteMcpFunction(List<String> idList);

    void deleteMcpFunctionByMcpId(String mcpId);

}