package com.wenge.model.controller.component;


import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.RunNodeParam;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.service.ComponentNodeService;
import com.wenge.model.service.ComponentService;
import com.wenge.model.workflow.component.dto.WorkflowRunStatus;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.entity.ComponentNode;
import com.wg.appframe.core.annotation.GlobalLog;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@Api(tags = "组件节点管理")
@RestController
@RequestMapping("/component")
@Slf4j
public class ComponentNodeController {

    @Autowired
    private ComponentNodeService componentNodeService;

    @Autowired
    private ComponentService componentService;

    @PostMapping("/save")
    @ApiOperation(value = "新建组件")
    public Result<Component> save(@RequestBody Component component){
        return componentService.save(component);
    }

    @PostMapping("/delete")
    @ApiModelProperty(value = "级联删除组件，包含节点等信息")
    public Result delete(@RequestBody Component component){
        return componentService.delete(component);
    }

    /**
     * 保存组件草稿
     * @return
     */
    @PostMapping("/draft")
    @ApiOperation(value = "保存组件草稿")
    @GlobalLog(paramEnable = false)
    public Result draft(@RequestBody ComponentDto component){
        return componentService.draft(component);
    }

    @PostMapping("/run")
    @ApiOperation("执行组件")
    public SseEmitter run(@RequestBody RunComponentNodeParam param) {
        return componentService.run(param, WorkflowRunStatus.DEBUG);
    }

    /**
     * 并行执行组件
     * @param param
     * @return
     */
    @PostMapping("/runParallel")
    @ApiOperation("执行组件")
    public SseEmitter runParallel(@RequestBody RunComponentNodeParam param) {
        return componentService.runParallel(param, WorkflowRunStatus.DEBUG);
    }


    @PostMapping("/saveNode")
    @ApiOperation(value = "新建组件节点")
    public Result<ComponentNode> saveNode(@RequestBody ComponentNode component){
        return componentNodeService.saveNode(component);
    }


    @PostMapping("/deleteNode")
    @ApiOperation(value = "删除组件节点")
    public Result deleteNode(@RequestBody ComponentNode component){
        return componentNodeService.deleteNode(component);
    }

    @PostMapping("/queryByComponentNode")
    @ApiOperation(value = "根据组件id查询所有节点")
    public Result<List<ComponentNode>> queryByComponentNode(@RequestBody ComponentNode node){
        return componentNodeService.queryByComponentNode(node);
    }

    /**
     * 执行节点
     * @param param
     * @return
     */
    @PostMapping("/runNode")
    @ApiOperation("执行节点")
    public Result runNode(@RequestBody RunNodeParam param){
        return componentService.runNode(param);
    }

}
