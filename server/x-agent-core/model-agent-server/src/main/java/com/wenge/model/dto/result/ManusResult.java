package com.wenge.model.dto.result;

import com.wenge.model.enums.StepStatusEnum;
import com.wg.appframe.yayi.result.PromptWebResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ManusResult implements Serializable {

    private static final long serialVersionUID = -6644392441300467792L;

    /**
     * 任务 id
     */
    private String taskId;

    /**
     * 当前不走
     */
    private String step;

    /**
     * 消息
     */
    private String msg;

    /**
     * 总结回答
     */
    private String answer;

    /**
     * 总结推理
     */
    private String reasoningContent;

    /**
     * 任务清单
     */
    private List<ManusTask> manusTaskList;

    /**
     * 网页列表
     */
    private List<PromptWebResult.ResInfoDetail> resInfo;

    /**
     * 推流状态
     */
    private StepStatusEnum status;
}
