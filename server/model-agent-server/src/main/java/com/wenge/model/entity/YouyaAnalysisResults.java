package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@Table(value = "youya_analysis_results", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class YouyaAnalysisResults {

    @Id(value = "id", keyType = KeyType.None)
    @ApiModelProperty(name = "requestId", value = "主键", dataType = "String")
    @Column("id")
    private String requestId;

    @Column("status")
    @ApiModelProperty(name = "status", value = "任务状态 0 待解析 1 解析完成", dataType = "Integer")
    private int status;

    @Column("video_url")
    @ApiModelProperty(name = "video_url", value = "任务状态 0 待解析 1 解析完成", dataType = "String")
    private String videoUrl;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    /**
     * 创建时间
     */
    @Column("end_time")
    @ApiModelProperty(name = "endTime", value = "解析完时间", dataType = "String")
    private String endTime;

}
