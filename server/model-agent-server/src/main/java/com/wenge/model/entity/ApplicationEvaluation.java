package com.wenge.model.entity;

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
@Table(value = "application_evaluation", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ApplicationEvaluation implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键，自增id，没有业务作用 */
    @Column("id")
    private Long id;

    /** 应用id，有业务作用 */
    @Column("application_id")
    @ApiModelProperty(name = "应用id，有业务作用")
    private String applicationId;

    /** 任务名称 */
    @Column("task_name")
    @ApiModelProperty(name = "任务名称")
    private String taskName;

    /** 评测对象,应用名称，应用id关联 */
    @Column("evaluation_object")
    @ApiModelProperty(name = "评测对象,应用名称，应用id关联")
    private String evaluationObject;

    /** 评测规则 */
    @Column("evaluation_rules")
    @ApiModelProperty(name = "评测规则")
    private String evaluationRules;

    /** 评测数据集 */
    @Column("evaluation_dataset")
    @ApiModelProperty(name = "评测数据集")
    private String evaluationDataset;

    /** 评测数据集id */
    @Column("dataset_id")
    @ApiModelProperty(name = "评测数据集id")
    private String datasetId;

    /** 评测创建时间 */
    @Column("evaluation_time")
    @ApiModelProperty(name = "评测创建时间")
    private String evaluationTime;

    /** 运行时常 */
    @Column("running_frequently")
    @ApiModelProperty(name = "运行时长")
    private String runningFrequently;

    /** 评测状态 */
    @Column("evaluation_status")
    @ApiModelProperty(name = "评测状态1:已完成，2：已暂停，3：任务失败")
    private int evaluationStatus;

    /** 裁判模型 */
    @Column("referee_model")
    @ApiModelProperty(name = "裁判模型")
    private String refereeModel;

    /** 裁判模型id */
    @Column("llm_info_id")
    @ApiModelProperty(name = "裁判模型id")
    private String llmInfoId;

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
     * 成功概率
     */
    @Column("success_rate")
    @ApiModelProperty(name = "successRate", value = "成功概率")
    private String successRate;
    /**
     * 成功个数
     */
    @Column("success_number")
    @ApiModelProperty(name = "successNumber", value = "成功个数")
    private Integer successNumber;
    /**
     * 总个数
     */
    @Column("total_number")
    @ApiModelProperty(name = "totalNumber", value = "总个数")
    private Integer totalNumber;
    /**
     * 出错log
     */
    @Column("error_message")
    @ApiModelProperty(name = "errorMessage", value = "出错log")
    private String errorMessage;
}
