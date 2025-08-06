package com.wenge.model.dto.template;

import com.alibaba.excel.annotation.ExcelIgnore;
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
public class ApplicationDatasetDTO implements Serializable
{
    /** session_id */
    @Column("session_id")
    @ExcelProperty("问题id(session_id)")
    private String sessionId;
    /** query_id */
    @Column("query_id")
    @ExcelProperty("对话轮次(query_id)")
    private String queryId;

    /** 问题id，UUID */
    @Column("question_id")
    @ApiModelProperty(name = "问题id，UUID")
    @ExcelIgnore
//    @ExcelProperty("问题id，UUID")
    private String questionId;
    /** 代表问题 */
    @Column("input")
    @ExcelProperty("用户输入(input)")
    private String input;

    /** 代表回答 */
    @Column("output")
    @ExcelProperty("实际输出(output)")
    private String output;

    /** 专家答案 */
    @Column("reference_output")
    @ExcelProperty("预期输出(reference_output)")
    private String referenceOutput;

    /** 数据集id */
    @Column("dataset_id")
//    @ExcelProperty("数据集id")
    @ExcelIgnore
    private String datasetId;

    /** 评分 */
    @Column("score")
    @ApiModelProperty(name = "评分(score)")
    @ExcelProperty("评分")
    private String score;

    /** 评分原因 */
    @Column("score_reason")
    @ApiModelProperty(name = "评分理由")
    @ExcelProperty("评分理由(score_reason)")
    private String scoreReason;
    /** 部门id//用来数据隔离 */
    //@Column("dept_id")
    //@ExcelProperty("dept_id")
    //private String deptId;

    /** 用户id//用来数据隔离 */
    //@Column("user_id")
    //@ExcelProperty("user_id")
    //private String userId;

    /** 租户id//用来数据隔离 */
    //@Column("tenant_id")
    //@ExcelProperty("tenant_id")
    //private String tenantId;

    /** 是否删除[1-删除,0-未删除] */
    //@Column("delete_flag")
    //@ExcelProperty("delete_flag")
    //private Long deleteFlag;

//    /** 创建人 */
//    @Column("create_user")
//    @ExcelProperty("create_user")
//    private String createUser;
//    /**
//     * 创建时间
//     */
//    @Column("create_time")
//    @ExcelProperty("create_time")
//    private String createTime;
//    /** 更新人 */
//    @Column("update_user")
//    @ExcelProperty("update_user")
//    private String updateUser;
//    /**
//     * 更新时间
//     */
//    @Column("update_time")
//    @ExcelProperty("update_time")
//    private String updateTime;
}
