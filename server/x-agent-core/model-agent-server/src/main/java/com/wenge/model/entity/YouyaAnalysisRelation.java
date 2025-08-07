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
@Table(value = "youya_analysis_relation", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class YouyaAnalysisRelation {
    @Id(value = "任务id[对应优雅id]", keyType = KeyType.None)
    @ApiModelProperty(name = "id", value = "任务id[对应优雅id]", dataType = "String")
    @Column("id")
    private String analysisId;

    @Column("request_id")
    @ApiModelProperty(name = "requestId", value = "请求id", dataType = "String")
    private String requestId;

    @Column("status")
    @ApiModelProperty(name = "status", value = "状态码", dataType = "Integer")
    private Integer status;

    @Column("time")
    @ApiModelProperty(name = "time", value = "耗时", dataType = "Float")
    private float time;

    @Column("msg")
    @ApiModelProperty(name = "msg", value = "状态信息", dataType = "String")
    private String msg;

    @Column("code")
    @ApiModelProperty(name = "code", value = "状态码", dataType = "Integer")
    private Integer code;

}