package com.wenge.model.workflow.component.event;

import com.wenge.model.workflow.component.dto.NodeRunStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * 所有节点的返回参数
 */
@Data
public class RunCompletedEvent implements Serializable, Cloneable {
    private static final long serialVersionUID = -5424412826358731324L;
    public NodeRunStatus result;

    public RunCompletedEvent(NodeRunStatus result) {
        this.result = result;
    }

    @Override
    public RunCompletedEvent clone() {
        try {
            RunCompletedEvent clone = (RunCompletedEvent) super.clone();
            if (null != result) {
                clone.result = result.clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
