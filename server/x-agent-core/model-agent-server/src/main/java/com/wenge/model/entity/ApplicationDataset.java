package com.wenge.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * ApplicationEvaluation对象 application_evaluation
 *
 * @author yjz
 * @date 2025-04-22
 */
@ApiModel
@Data
@Table(value = "application_dataset", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ApplicationDataset implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键，自增id，没有业务作用 */
    @Column("id")
    private Long id;
    /** 问题id，UUID */
    @Column("question_id")
    @ApiModelProperty(name = "问题id，UUID")
    @ExcelProperty("问题id，UUID")
    private String questionId;

    /** 代表问题 */
    @Column("input")
    @ApiModelProperty(name = "代表问题")
    @ExcelProperty("input")
    private String input;

    /** 代表回答 */
    @Column("output")
    @ApiModelProperty(name = "代表回答")
    @ExcelProperty("output")
    private String output;

    /** 专家答案 */
    @Column("reference_output")
    @ApiModelProperty(name = "专家答案")
    @ExcelProperty("reference_output")
    private String referenceOutput;
    /** 评分 */
    @Column("score")
    @ApiModelProperty(name = "评分")
    @ExcelProperty("score")
    private String score;

    /** 评分原因 */
    @Column("score_reason")
    @ApiModelProperty(name = "评分原因")
    @ExcelProperty("score_reason")
    private String scoreReason;
    /** 数据集id */
    @Column("dataset_id")
    @ApiModelProperty(name = "数据集id")
    @ExcelProperty("dataset_id")
    private String datasetId;
    /** session_id */
    @Column("session_id")
    @ApiModelProperty(name = "session_id")
    @ExcelProperty("session_id")
    private String sessionId;
    /** query_id */
    @Column("query_id")
    @ApiModelProperty(name = "query_id")
    @ExcelProperty("query_id")
    private String queryId;

    /** 部门id//用来数据隔离 */
    @Column("dept_id")
    @ApiModelProperty(name = "部门id//用来数据隔离")
    private String deptId;

    /** 用户id//用来数据隔离 */
    @Column("user_id")
    @ApiModelProperty(name = "用户id//用来数据隔离")
    private String userId;

    /** 租户id//用来数据隔离 */
    @Column("tenant_id")
    @ApiModelProperty(name = "租户id//用来数据隔离")
    private String tenantId;

    /** 是否删除[1-删除,0-未删除] */
    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "是否删除[1-删除,0-未删除]")
    private Integer deleteFlag;

    /** 创建人 */
    @Column("create_user")
    @ApiModelProperty(name = "创建人")
    private String createUser;
    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;
    /** 更新人 */
    @Column("update_user")
    @ApiModelProperty(name = "更新人")
    private String updateUser;
    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;
    /**
     * 出错log
     */
    @Column("error_message")
    @ApiModelProperty(name = "errorMessage", value = "出错log")
    private String errorMessage;
}
