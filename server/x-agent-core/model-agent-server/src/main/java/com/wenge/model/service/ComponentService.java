package com.wenge.model.service;

import cn.hutool.json.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.wenge.model.dto.param.*;
import com.wenge.model.dto.result.ComponentDto;
import com.wenge.model.entity.StepEntity;
import com.wenge.model.workflow.entity.Component;
import com.wenge.oauth.entity.GrantData;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.StringParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

public interface ComponentService {


    /**
     * 根据组件id，查询组件信息，包含关联项
     * @param componentIds
     * @return
     */
    List<ComponentDto> selectById(List<String> componentIds);

    Result<Component> save(Component component);

    Result draft(ComponentDto component);

    /**
     * 分页查询工作流应用
     * @param pageParam
     * @return
     */
    Result<Page<ComponentDto>> page(WorkFlowPageParam pageParam);

    /**
     * 执行组件=流式
     * @param param
     * @return
     */
    SseEmitter run(RunComponentNodeParam param, String model);


    SseEmitter runParallel(RunComponentNodeParam param, String model);


    Result delete(Component component);

    /**
     * 新建API组件，仅包含 开始 API 结束
     * @return
     */
    Result<ComponentDto> createAPI(Component component);

    /**
     * 查询插件列表
     * @return
     */
    Result queryPlugin(PluginParam pageInfo);

    /**
     * 修改API插件
     * @param component
     * @return
     */
    Result updateAPI(ComponentDto component);

    /**
     * API节点验证
     * @param apiNode
     * @return
     */
    // Result validate(ApiNode apiNode);

    /**
     * 执行一个节点
     * @param param
     * @return
     */
    Result runNode(RunNodeParam param);

    /**
     * 该接口是为了执行工具节点，尽可能快返回结果
     *
     * @return
     */
    JSONObject runTool(RunComponentNodeParam param, List<StepEntity> stepEntityList) throws Exception;

    /**
     * 执行迭代节点
     * @param param
     * @return
     */
    JSONObject runIteration(RunComponentNodeParam param) throws Exception;

    /**
     * 简单更新工作流
     * @param component
     * @return
     */
    Result<Component> update(Component component);

    /**
     * 查询单个工作流，唯一校验用
     * @param componentId
     * @return
     */
    Component selectOne(String componentId);

    /**
     * 执行流程
     *
     * @param param
     * @return
     */
    Map<String, Object> runFlow(RunComponentNodeParam param, String model, boolean stopFlag);

    Map<String, Object> runFlowParallel(RunComponentNodeParam param, String model);

    /**
     * 获取应用绑定的有效的工作流
     * @param applicationId
     * @return
     */
    List<Component> effectiveComponent(String applicationId);

    /**
     * 执行工作流节点
     * @param nodeParam
     * @return
     */
    Map<String, Object> runWorkFlowNode(RunComponentNodeParam nodeParam) throws Exception;

    /**
     * 创建一个代码工具
     * @return
     */
    Result createCode(ComponentDto component);

    /**
     * 修改代码工具 主要是为了适配页面，封装参数转换逻辑
     * @return
     */
    Result updateCode(CodeToolUpdateParam param);


    /**
     * @author: caohaifeng
     * @date: 2025/2/15 16:22
     * @Description: 审核通过 更新上架字段
     * @Version:1.0
     **/
    int updateAppStoreByPluginId(String pluginId, Integer publishAppStore);

    Result clearFlowCache(StringParam param);

    /**
     * 获取工作流详细
     * @param componentId
     * @param param
     * @return
     */
    ComponentDto getComponentDto(String componentId, RunComponentNodeParam param);

    Result setPreset(PluginParam pluginParam);

    public List<GrantData> queryGrantData(ComponentDto componentDto);
}
