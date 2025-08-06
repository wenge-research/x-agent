package com.wenge.model.controller;

import com.wenge.model.entity.McpServer;
import com.wenge.model.entity.McpServerUser;
import com.wenge.model.service.McpServerService;
import com.wenge.model.service.McpServerUserService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcpServerUser")
@Slf4j
@Api(tags = "mcp 关联用户接口")
public class McpServerUserController {

    /**
     * 	mcp 服务服务类
     */
    @Autowired
    private McpServerUserService mcpServerUserService;

    /**
     * 新增mcp 服务
     */
    @PostMapping("/updateStatus")
    @ApiOperation(value = "修改mcp服务用户关联关系", tags = "修改mcp服务用户关联关系", notes = "修改mcp服务用户关联关系", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    public Result updateStatus(@RequestBody McpServerUser mcpServerUser) {
        return mcpServerUserService.updateStatus(mcpServerUser);
    }




}
