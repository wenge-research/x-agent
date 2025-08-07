package com.wenge.model.dto.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CanvasNodeLink implements Serializable {

    private static final long serialVersionUID = -410994186631836610L;

    /**
     * 节点id
     */
    private String id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 是否为顶层
     */
    private String isTop;

    /**
     * 后置节点
     */
    private List<CanvasNodeLink> nextList;
}
