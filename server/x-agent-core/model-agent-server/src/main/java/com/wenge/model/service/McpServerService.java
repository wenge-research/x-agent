package com.wenge.model.service;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.wenge.model.dto.param.McpFindParam;
import com.wenge.model.dto.param.McpServerListParam;
import com.wenge.model.dto.param.TestApiParam;
import com.wenge.model.entity.McpServer;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.ListStringParam;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

/**
 * Description: mcp 服务服务类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:32:48
 *
 */
public interface McpServerService extends IService<McpServer> {

    Result addMcpServer(McpServer mcpServer);

    Result getMcpServerList(McpServerListParam mcpServer);

    Result updateMcpServer(McpServer mcpServer);

    Result deleteMcpServer(List<String> idList);

    Result checkMcp(McpServer mcpServer);

    Result getDetail(StringParam param);

    Result getListAndDetail(ListStringParam param);

    Result getMyMcpServerList(McpServerListParam mcpServer);

    /**
     * 通过 mcp的 id 获取 mcp 服务
     * @param mcpServerIdList
     * @return
     */
    List<McpServer> getMcpServerByIds(List<String> mcpServerIdList);

    List<McpServer> getActifList(McpFindParam param);

    Result testApi(TestApiParam param);

    Result setPreset(McpServerListParam mcpServerListParam);

    Result updateMcpStatus(McpServerListParam mcpServerParam);
}