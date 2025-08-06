package com.wenge.model.dto.template;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.Column;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * ApplicationEvaluation对象 application_evaluation
 * 
 * @author yjz
 * @date 2025-04-22
 */
@ApiModel
@Data
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEvaluationDTO implements Serializable
{
    /** 主键，自增id，没有业务作用 */
    @ExcelProperty("主键，自增id，没有业务作用 ")
    private Long id;

    /** 应用id，有业务作用 */
    @ExcelProperty("应用id，有业务作用")
    private String applicationId;

    /** 任务名称 */
    @ExcelProperty("任务名称")
    private String taskName;

    /** 评测对象,应用名称，应用id关联 */
    @ExcelProperty("评测对象")
    private String evaluationObject;

    /** 评测规则 */
    @ExcelProperty("评测规则")
    private String evaluationRules;

    /** 评测数据集 */
    @ExcelProperty("评测数据集")
    private String evaluationDataset;

    /** 评测数据集id */
    @ExcelProperty("评测数据集id")
    private String datasetId;

    /** 评测创建时间 */
    @ExcelProperty("评测创建时间")
    private String evaluationTime;

    /** 运行时常 */
    @ExcelProperty("运行时常")
    private String runningFrequently;

    /** 评测状态 */
    @ExcelProperty("评测状态")
    private int evaluationStatus;

    /** 裁判模型 */
    @ExcelProperty("裁判模型")
    private String refereeModel;

    /** 裁判模型id */
    @ExcelProperty("裁判模型id")
    private String llmInfoId;

//    /** 部门id//用来数据隔离 */
//    @ExcelProperty("input")
//    private String deptId;
//
//    /** 用户id//用来数据隔离 */
//    @ExcelProperty("input")
//    private String userId;
//
//    /** 租户id//用来数据隔离 */
//    @ExcelProperty("input")
//    private String tenantId;
//
//    /** 是否删除[1-删除,0-未删除] */
//    @ExcelProperty("input")
//    private Long deleteFlag;
//
//    /** 创建人 */
//    @ExcelProperty("input")
//    private String createUser;
//    /**
//     * 创建时间
//     */
//    @ExcelProperty("input")
//    private String createTime;
//    /** 更新人 */
//    @ExcelProperty("input")
//    private String updateUser;
//    /**
//     * 更新时间
//     */
//    @ExcelProperty("input")
//    private String updateTime;
}
