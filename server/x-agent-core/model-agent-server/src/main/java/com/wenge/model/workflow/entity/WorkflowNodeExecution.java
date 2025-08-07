package com.wenge.model.workflow.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wenge.model.workflow.enums.ComponentNodeEnum;
import com.wenge.model.workflow.enums.WorkflowNodeExecutionStatus;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

/**
 * 工作流执行记录
 */
@Data
@Table("workflow_node_executions")
public class WorkflowNodeExecution {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(value = "tenant_id", jdbcType = JdbcType.VARCHAR)
    private String tenantId;

    @Column(value = "workflow_id", jdbcType = JdbcType.VARCHAR)
    private String componentId;

    @Column(value = "triggered_from", jdbcType = JdbcType.VARCHAR)
    private String triggeredFrom;

    @Column(value = "workflow_run_id", jdbcType = JdbcType.VARCHAR)
    private String workflowRunId;

    @Column(value = "index", jdbcType = JdbcType.INTEGER)
    private Integer index;

    @Column(value = "predecessor_node_id", jdbcType = JdbcType.VARCHAR)
    private String predecessorNodeId;

    @Column(value = "node_execution_id", jdbcType = JdbcType.VARCHAR)
    private String nodeExecutionId;

    @Column(value = "node_id", jdbcType = JdbcType.VARCHAR)
    private String nodeId;

    @Column(value = "node_type", jdbcType = JdbcType.VARCHAR)
    private ComponentNodeEnum nodeType;

    @Column(value = "title", jdbcType = JdbcType.VARCHAR)
    private String title;

    @Column(value = "inputs", jdbcType = JdbcType.CLOB)
    private String inputs;

    @Column(value = "process_data", jdbcType = JdbcType.CLOB)
    private String processData;

    @Column(value = "outputs", jdbcType = JdbcType.CLOB)
    private String outputs;

    @Column(value = "status", jdbcType = JdbcType.VARCHAR)
    private WorkflowNodeExecutionStatus status;

    @Column(value = "error", jdbcType = JdbcType.CLOB)
    private String error;

    /**
     * 耗时s
     */
    @Column(value = "elapsed_time", jdbcType = JdbcType.DOUBLE)
    private Double elapsedTime;

    @Column(value = "execution_metadata", jdbcType = JdbcType.CLOB)
    private String executionMetadata;

    /**
     * 创建时间
     */
    @Column(value = "created_at", jdbcType = JdbcType.VARCHAR)
    private String createdAt;

    @Column(value = "created_by", jdbcType = JdbcType.VARCHAR)
    private String createdBy;

    /**
     * 结束时间
     */
    @Column(value = "finished_at", jdbcType = JdbcType.VARCHAR)
    private String finishedAt;
}
