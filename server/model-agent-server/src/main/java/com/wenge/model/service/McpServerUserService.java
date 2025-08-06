package com.wenge.model.service;

import com.wenge.model.entity.McpServerUser;
import com.wg.appframe.core.bean.Result;

import java.util.List;

public interface McpServerUserService {

    Result updateStatus(McpServerUser mcpServerUser);

    McpServerUser queryMcpServerUser(String mcpId);

    List<McpServerUser> myMcpServerUserList();

    List<McpServerUser> myCheckMcpServerUserList(Boolean permisson, Integer status);
}
