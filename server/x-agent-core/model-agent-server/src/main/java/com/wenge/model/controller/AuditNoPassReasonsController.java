package com.wenge.model.controller;

import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.ServerPublishAuditPageParam;
import com.wenge.model.entity.AuditNoPassReasons;
import com.wenge.model.entity.ServerPublishAudit;
import com.wenge.model.service.AuditNoPassReasonsService;
import com.wenge.model.service.ServerPublishAuditService;
import com.wenge.oauth.annotation.OperaLogs;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditNoPassReasons")
@Slf4j
@Api("审核不通过理由接口")
public class AuditNoPassReasonsController {

    @Autowired
    private AuditNoPassReasonsService auditNoPassReasonsService;

    /**
     * 查询 分页
     *
     * @return
     */
    @ApiOperation(value = "审核不通过理由列表", tags = "审核不通过理由列表", notes = "审核不通过理由列表", response = Result.class, httpMethod = "GET", produces = "application/json", consumes = "application/json")
    @GetMapping("/getListByPid")
    public Result<List<AuditNoPassReasons>> getListByPid(@RequestParam(value = "pid", defaultValue = "0", required = true) Integer pid) {
        return Result.success(auditNoPassReasonsService.getAuditNoPassReasons(pid));
    }

}
