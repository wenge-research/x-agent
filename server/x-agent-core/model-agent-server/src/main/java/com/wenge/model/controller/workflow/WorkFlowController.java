package com.wenge.model.controller.workflow;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.FairAICheckParam;
import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.ScreenshotParam;
import com.wenge.model.dto.param.WorkFlowPageParam;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.dto.result.ScreenshotResult;
import com.wenge.model.dto.result.WorkFlowDto;
import com.wenge.model.entity.ApplicationInfo;
import com.wenge.model.service.ComponentService;
import com.wenge.model.service.WorkFlowService;
import com.wenge.model.service.impl.ComponentServiceImpl;
import com.wenge.model.workflow.entity.Component;
import com.wenge.oauth.entity.GrantData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.constant.StringConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.xpath.operations.Equals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;

@Api(tags = "工作流管理")
@RestController
@RequestMapping("/workflow")
@Slf4j
public class WorkFlowController {
    @Autowired
    private ComponentService componentService;

    @Autowired
    private WorkFlowService workFlowService;

    /**
     * 新建工作流
     * @param component
     * @return
     */
    @PostMapping("/save")
    @ApiOperation(value = "新建工作流")
    public Result<WorkFlowDto> addApplicationInfo(@RequestBody Component component) {
        return workFlowService.save(component);
    }

    /**
     * 更新工作流
     * @param component
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新工作流")
    public Result<Component> update(@RequestBody Component component) {
        return workFlowService.update(component);
    }

    /**
     * 分页查询工作流
     * @param pageParam
     * @return
     */
    @PostMapping("/list")
    @ApiOperation(value = "分页查询工作流")
    public Result<Page<ComponentDto>> list(@RequestBody WorkFlowPageParam pageParam) {
        return componentService.page(pageParam);
    }

    /**
     * 运行工作流对话
     * @param param
     * @return
     */
    @PostMapping("/dialogueRun")
    @ApiOperation(value = "运行工作流对话")
    public SseEmitter dialogueRun(@RequestBody RunComponentNodeParam param) {
        return workFlowService.dialogueRun(param);
    }

    /**
     * 复制工作流
     * @param component
     * @return
     */
    @PostMapping("/copy")
    @ApiOperation(value = "复制工作流")
    public Result<WorkFlowDto> copy(@RequestBody Component component) {
        return workFlowService.copy(component, "flowwork", StringConstant.BLANK);
    }

    /**
     * 查询工作流详情
     * @param component
     * @return
     */
    @PostMapping("/queryDetail")
    @ApiOperation(value = "查询工作流详情")
    public Result<ComponentDto> queryDetail(@RequestBody Component component) {
        Assert.notNull(component.getComponentId(), "工作流id不能为空");
        try {
            String componentId = ComponentServiceImpl.getComponentId(component);
            List<ComponentDto> componentDtoList = componentService.selectById(ListUtil.toList(componentId));
            if (CollectionUtil.isNotEmpty(componentDtoList)) {
                ComponentDto componentDto = componentDtoList.get(0);
                List<GrantData> grantData = componentService.queryGrantData(componentDto);
                // 菜单工作流进入，类型为普通工作流/对话流，参数：{componentId: xxx_2/3}
                if (componentId.contains("_")){
                    componentDto.setGranted(CollectionUtil.isNotEmpty(grantData));
                }else{
                    // 应用，类型为应用工作流/对话流，参数：{componentId:xxx, type:4/5}
                    if (4 == component.getType() || 5 == component.getType()) {
                        ApplicationInfo applicationInfo = componentDto.getApplicationInfo();
                        applicationInfo.setGranted(CollectionUtil.isNotEmpty(grantData));
                        componentDto.setGranted(CollectionUtil.isNotEmpty(grantData));
                    } else {
                        componentDto.setGranted(CollectionUtil.isNotEmpty(grantData));
                    }
                }
                return Result.success(componentDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.success(null);
    }

    /**
     * 公平竞争ai审查系统
     * @param param
     * @return
     */
    @PostMapping("/fairAICheck")
    @ApiOperation(value = "公平竞争ai审查系统")
    public SseEmitter fairAICheck(@RequestBody FairAICheckParam param) {
        Assert.notNull(param.getFile(), "文件不能为空");
        return workFlowService.fairAICheck(param);
    }

    /**
     * 网页快照
     * @param param
     * @return
     */
    @PostMapping("/screenshot")
    @ApiOperation(value = "公平竞争ai审查系统")
    public Result<ScreenshotResult> screenshot(@RequestBody ScreenshotParam param) {
        return workFlowService.screenshot(param);
    }

    /**
     * 将工作流设为预设
     * @param pageParam
     * @return Result
     */
    @PostMapping("/setPreset")
    @ApiOperation(value = "将工作流预设为预置")
    public Result setPreset(@RequestBody WorkFlowPageParam pageParam) {
        return workFlowService.setPreset(pageParam);
    }
}
