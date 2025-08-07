package com.wenge.model.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 组件节点关系表
 */
@Table("component_node_rel")
@Data
public class ComponentNodeRel {
    @Id(value = "id", keyType = KeyType.Auto)
    private Long id;

    @Column("component_id")
    private String componentId;

    @Column("source_node_id")
    private String sourceNodeId;

    @Column("target_node_id")
    private String targetNodeId;

    @Column("case_id")
    private String caseId;
}
