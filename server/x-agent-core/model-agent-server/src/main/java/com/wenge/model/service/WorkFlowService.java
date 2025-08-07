package com.wenge.model.service;

import com.wenge.model.dto.param.FairAICheckParam;
import com.wenge.model.dto.param.RunComponentNodeParam;
import com.wenge.model.dto.param.ScreenshotParam;
import com.wenge.model.dto.param.WorkFlowPageParam;
import com.wenge.model.dto.result.ScreenshotResult;
import com.wenge.model.dto.result.WorkFlowDto;
import com.wenge.model.workflow.entity.Component;
import com.wg.appframe.core.bean.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 工作流接口
 */
public interface WorkFlowService {

    /**
     * 保存工作流
     * @param component
     * @return
     */
    Result<WorkFlowDto> save(Component component);

    SseEmitter dialogueRun(RunComponentNodeParam param);

    /**
     * 复制工作流
     *
     * @param component
     * @return
     */
    Result<WorkFlowDto> copy(Component component, String from, String name);

    /**
     * 复制到指定id，应用复制使用
     *
     * @param component
     * @return
     */
    Result<WorkFlowDto> copy2Target(Component component, String targetId, String from, String name);

    /**
     * 公平竞争ai审查系统
     * @param param
     * @return
     */
    @Deprecated
    SseEmitter fairAICheck(FairAICheckParam param);

    /**
     * 更新工作流名称
     * @param component
     * @return
     */
    Result<Component> update(Component component);

    /**
     * 删除工作流
     * @param id
     */
    void deleteById(String id);

    /**
     * 网页快照
     * @param param
     * @return
     */
    Result<ScreenshotResult> screenshot(ScreenshotParam param);

    Result setPreset(WorkFlowPageParam pageParam);
}
