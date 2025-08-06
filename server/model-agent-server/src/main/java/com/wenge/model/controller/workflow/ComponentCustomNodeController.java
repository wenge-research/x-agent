package com.wenge.model.controller.workflow;

import com.wenge.model.dto.param.workflow.ComponentCustomNodeParam;
import com.wenge.model.dto.result.workflow.ComponentCustomNodeResult;
import com.wenge.model.enums.WorkFlowNodeEnum;
import com.wenge.model.service.ComponentCustomNodeService;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "工作流-节点管理-自定义节点控制")
@RequestMapping("/custom/node")
@RestController
public class ComponentCustomNodeController {

    @Autowired
    private ComponentCustomNodeService componentCustomNodeService;

    @ApiOperation(value = "新增", tags = "新增自定义节点")
    @PostMapping("/save")
    public Result<ComponentCustomNodeResult> save(@RequestBody ComponentCustomNodeParam param) {
        return Result.success(componentCustomNodeService.save(param));
    }

    @ApiOperation(value = "更新", tags = "更新自定义节点")
    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody ComponentCustomNodeParam param) {
        return Result.success(componentCustomNodeService.update(param));
    }

    @ApiOperation(value = "删除", tags = "删除自定义节点")
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestParam("id") Long id) {
        return Result.success(componentCustomNodeService.delete(id));
    }

    @ApiOperation(value = "查询单个", tags = "查询单个自定义节点")
    @GetMapping("/getById")
    public Result<ComponentCustomNodeResult> getById(@RequestParam("id") Long id) {
        return Result.success(componentCustomNodeService.getById(id));
    }

    @ApiOperation(value = "查询列表", tags = "查询自定义节点列表")
    @GetMapping("/list")
    public Result<List<ComponentCustomNodeResult>> list(@RequestParam("type") WorkFlowNodeEnum type) {
        return Result.success(componentCustomNodeService.getList(type));
    }


}
