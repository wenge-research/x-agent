package com.wenge.model.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.Data;

/**
 * 组件节点
 */
@Table("component_node")
@Data
public class ComponentNode {

    @Id(value = "id", keyType = KeyType.Auto)
    private Long id;

    @Column("node_id")
    private String nodeId;

    @Column("name")
    private String nodeName;

    @Column("component_id")
    private String componentId;

    @Column(value = "type")
    private Integer nodeType;

    @Column(value = "settings", typeHandler = Fastjson2TypeHandler.class)
    private String settings;

    /* 节点位置字段 */


}
