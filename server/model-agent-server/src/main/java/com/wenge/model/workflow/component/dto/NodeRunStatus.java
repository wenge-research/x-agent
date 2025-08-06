package com.wenge.model.workflow.component.dto;

import cn.hutool.json.JSONObject;
import com.wenge.model.workflow.enums.WorkflowNodeExecutionStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class NodeRunStatus implements Serializable, Cloneable {

    private static final long serialVersionUID = 6469306609271287515L;

    /**
     * 运行状态
     */
    private WorkflowNodeExecutionStatus status = WorkflowNodeExecutionStatus.RUNNING;
    /**
     * 节点入参
     */
    private JSONObject input;
    /**
     * 节点执行情况
     */
    private JSONObject processData;
    /**
     * 节点出参
     */
    private JSONObject output;
    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 运行信息
     */
    private String message;

    /**
     * 耗时
     */
    private Long elapsedTime;

    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 用户问题
     */
    private String question;

    /**
     * 节点开始时间戳
     */
    private Long startTimestamp;

    /**
     * 节点开始时间
     */
    private String startTime;

    /**
     * 结束时间戳
     */
    private String endTime;

    /**
     * 迭代号
     */
    protected Integer iterationNum;

    /**
     * 节点类型
     *
     * @see com.wenge.model.workflow.enums.ComponentNodeEnum
     */
    private Integer type;

    public NodeRunStatus(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.processData = new JSONObject();
        // this.question = WorkflowRunStatus.question;
    }

    public NodeRunStatus(String nodeId, String nodeName, Integer type) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.type = type;
    }

    @Override
    public NodeRunStatus clone() {
        try {
            NodeRunStatus clone = (NodeRunStatus) super.clone();
            if (null != input) {
                clone.input = input.clone();
            }
            if (null != processData) {
                clone.processData = processData.clone();
            }
            if (null != output) {
                clone.output = output.clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
