package com.wenge.model.dto.param;

import com.wenge.model.entity.ApplicationDataset;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
import java.util.Date;
import java.util.List;
@Data
public class ApplicationEvaluationParamAsync extends ApplicationEvent {
    /** 主键，自增id，没有业务作用 */
    @ApiModelProperty(name = "id")
    private Long id;

    /** 应用id，有业务作用 */
    @ApiModelProperty(name = "应用id，有业务作用")
    private String applicationId;
    /** 应用编码*/
    @ApiModelProperty(name = "应用编码")
    private String applicationCode;

    /** 任务名称 */
    @ApiModelProperty(name = "任务名称")
    private String taskName;

    /** 评测对象,应用名称，应用id关联 */
    @ApiModelProperty(name = "评测对象,应用名称，应用id关联")
    private String evaluationObject;

    /** 评测规则 */
    @ApiModelProperty(name = "评测规则")
    private String evaluationRules;

    /** 评测数据集 */
    @ApiModelProperty(name = "评测数据集")
    private String evaluationDataset;

    /** 评测数据集id */
    @ApiModelProperty(name = "评测数据集id")
    private String datasetId;

    /** 评测创建时间 */
    @ApiModelProperty(name = "评测创建时间")
    private Date evaluationTime;

    /** 运行时常 */
    @ApiModelProperty(name = "运行时常")
    private String runningFrequently;

    /** 评测状态 */
    @ApiModelProperty(name = "评测状态")
    private int evaluationStatus;

    /** 裁判模型 */
    @ApiModelProperty(name = "裁判模型")
    private String refereeModel;

    /** 裁判模型id */
    @ApiModelProperty(name = "裁判模型id")
    private String llmInfoId;
    /** 提示词 */
    @ApiModelProperty(name = "提示词")
    private String cueWord;

    /** 部门id//用来数据隔离 */
    @ApiModelProperty(name = "部门id//用来数据隔离")
    private String deptId;

    /** 用户id//用来数据隔离 */
    @ApiModelProperty(name = "用户id//用来数据隔离")
    private String userId;

    /** 租户id//用来数据隔离 */
    @ApiModelProperty(name = "租户id//用来数据隔离")
    private String tenantId;

    /** 是否删除[1-删除,0-未删除] */
    @ApiModelProperty(name = "是否删除[1-删除,0-未删除]")
    private Long deleteFlag;

    /** 创建人 */
    @ApiModelProperty(name = "创建人")
    private String createUser;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;
    /** 更新人 */
    @ApiModelProperty(name = "更新人")
    private String updateUser;
    @ApiModelProperty
    private List<ApplicationDataset> applicationDataset;
    /**
     * 更新时间
     */
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;
    @ApiModelProperty(name = "startTime", value = "查询开始时间", dataType = "LocalDateTime")
    private String startTime;
    @ApiModelProperty(name = "endTime", value = "查询结束时间", dataType = "LocalDateTime")
    private String endTime;

    public ApplicationEvaluationParamAsync(Object source) {
        super(source);
    }

    public ApplicationEvaluationParamAsync(Object source, Clock clock) {
        super(source, clock);
    }
}
