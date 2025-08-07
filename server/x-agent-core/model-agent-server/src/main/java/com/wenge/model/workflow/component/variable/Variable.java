package com.wenge.model.workflow.component.variable;

import lombok.Builder;
import lombok.Data;

/**
 * 运行时变量类
 */
@Data
@Builder
public class Variable {
    private Segments<?> segments;

    /**
     * 指明该变量属于哪个节点
     */
    private String nodeId;

    /**
     * 变量名称
     */
    private String name;

    public String getSelector() {
        return nodeId + "." + name;
    }
}
