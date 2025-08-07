package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ServerPublishAuditPageParam;
import com.wenge.model.entity.ServerPublishAudit;
import com.wenge.model.service.ServerPublishAuditService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/serverPublishAudit")
@Slf4j
@Api("服务上架审核")
public class ServerPublishAuditController {

    @Autowired
    private ServerPublishAuditService serverPublishAuditService;

    /**
     * 新增服务发布审核
     */
    @ApiOperation(value = "新增服务发布审核",tags = "新增服务发布审核", notes = "新增服务发布审核", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/add")
    @OperaLogs
    public Result add(@RequestBody ServerPublishAudit serverPublishAudit) {
        return serverPublishAuditService.add(serverPublishAudit);
    }


    /**
     * 删除审核记录
     */
    @ApiOperation(value = "删除服务发布审核",tags = "删除服务发布审核", notes = "删除服务发布审核", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/delete")
    @OperaLogs
    public Result delete(@RequestBody ServerPublishAudit serverPublishAudit) {
        return serverPublishAuditService.delete(serverPublishAudit);
    }

    /**
     * 查询 分页
     * @return
     */
    @ApiOperation(value = "查询服务发布审核 分页",tags = "查询服务发布审核 分页", notes = "查询服务发布审核 分页", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/getListPage")
    public Result<Page<ServerPublishAudit>> getListPage(@RequestBody ServerPublishAuditPageParam param) {
        return serverPublishAuditService.getListPage(param);
    }

    /**
     * 详情
     */
    @ApiOperation(value = "服务发布审核详情(支持上下翻)", tags = "服务发布审核详情", notes = "服务发布审核详情", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
    @GetMapping("/getDataById")
    public Result getDataById(@RequestParam(value = "id", required = true) Long id, @RequestParam(value = "upAnddown", required = false) String upAnddown) {
        return serverPublishAuditService.getDataById(id, upAnddown);
    }

    @ApiOperation(value = "应用商店详情", tags = "应用商店详情", notes = "应用商店详情", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
    @GetMapping("/getStoreDataByApplicationId")
    public Result getStoreDataByApplicationId(@RequestParam(value = "applicationId", required = true) String applicationId) {
        return serverPublishAuditService.getStoreDataByApplicationId(applicationId);
    }

    /**
     * 编辑审核记录
     */
    @ApiOperation(value = "编辑审核记录",tags = "编辑审核记录", notes = "编辑审核记录", response = Result.class, httpMethod = "POST", produces = "application/json", consumes = "application/json")
    @PostMapping("/update")
    @OperaLogs
    public Result update(@RequestBody ServerPublishAudit serverPublishAudit) {
        return serverPublishAuditService.update(serverPublishAudit);
    }
}
