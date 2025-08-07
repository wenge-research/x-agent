package com.wenge.model.controller.component;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.wenge.model.dto.param.CodeToolUpdateParam;
import com.wenge.model.dto.param.PluginParam;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.service.ComponentService;
import com.wenge.model.workflow.entity.Component;
import com.wenge.model.workflow.enums.ComponentTypeEnum;
import com.wg.appframe.core.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 插件管理
 */
@Api(tags = "插件管理")
@RestController
@RequestMapping("/plugin")
@Slf4j
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    /**
     * 新建一个测试API插件
     * @param component
     * @return
     */
    @RequestMapping("/api/create")
    @ApiOperation(value = "新建API插件")
    public Result create(@RequestBody Component component) {
        return componentService.createAPI(component);
    }

    /**
     * 修改API插件
     * @param component
     * @return
     */
    @RequestMapping("/api/update")
    @ApiOperation(value = "修改API插件")
    public Result update(@RequestBody ComponentDto component) {
        return componentService.updateAPI(component);
    }

    /**
     * 查询插件列表
     * @param pageInfo
     * @return
     */
    @RequestMapping("/api/query")
    @ApiOperation(value = "查询插件列表")
    public Result query(@RequestBody PluginParam pageInfo) {
        return componentService.queryPlugin(pageInfo);
    }


    /**
     * 查询插件详情
     * @param component
     * @return
     */
    @RequestMapping("/api/queryDetail")
    @ApiOperation(value = "查询插件详情")
    public Result<ComponentDto> queryDetail(@RequestBody Component component) {
        List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(component.getComponentId()));
        if (CollectionUtil.isNotEmpty(componentDtoList)) {
            return Result.success(componentDtoList.get(0));
        }
        return Result.success(null);
    }
    //
    // /**
    //  * API节点验证
    //  */
    // @RequestMapping("/api/validate")
    // @ApiOperation(value = "API节点验证")
    // public Result validate(@RequestBody ApiNode apiNode) {
    //     return componentService.validate(apiNode);
    // }


    /**
     * 创建一个迭代工作流
     * @param component
     * @return
     */
    @RequestMapping("/iteration/create")
    @ApiOperation(value = "新建迭代节点")
    public Result createIteration(@RequestBody Component component) {
        component.setType(ComponentTypeEnum.ITERATION.getCode());
        return componentService.save(component);
    }

    @RequestMapping("/code/create")
    @ApiOperation("初始化一个代码插件")
    public Result codeCreate(@RequestBody ComponentDto component) {
        return componentService.createCode(component);
    }

    @PostMapping("/code/update")
    @ApiOperation("修改代码工具")
    public Result codeUpdate(@RequestBody CodeToolUpdateParam param) {
        return componentService.updateCode(param);
    }

    /**
     * 将插件设为预设
     * @param pluginParam
     * @return Result
     */
    @PostMapping("/api/setPreset")
    public Result setPreset(@RequestBody PluginParam pluginParam) {
        return componentService.setPreset(pluginParam);
    }
}
