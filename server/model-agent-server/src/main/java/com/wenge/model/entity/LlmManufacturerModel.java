package com.wenge.model.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexId;

import java.io.Serializable;

@ApiModel
@Data
@Table(value = "llm_manufacturer_model", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class LlmManufacturerModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    @IndexId
    private Long id;

    @Column("manufacturer_model_id")
    @ApiModelProperty(name = "manufacturerModelId", value = "大模型厂商模型信息表的id", dataType = "String")
    private String manufacturerModelId;

    @Column("manufacturer")
    @ApiModelProperty(name = "manufacturer", value = "厂商名称", dataType = "String")
    private String manufacturer;

    @Column("api_url")
    @ApiModelProperty(name = "apiUrl", value = "模型api地址", dataType = "String")
    private String apiUrl;

    @Column("model")
    @ApiModelProperty(name = "model", value = "模型", dataType = "String")
    private String model;

    @Column("model_name")
    @ApiModelProperty(name = "modelName", value = "模型名称", dataType = "String")
    private String modelName;

    @Column("max_response_tokens")
    @ApiModelProperty(name = "maxResponseTokens", value = "最大回复长度", dataType = "String")
    private Integer maxResponseTokens;

    @Column("min_response_tokens")
    @ApiModelProperty(name = "minResponseTokens", value = "最小回复长度", dataType = "String")
    private Integer minResponseTokens;

    @Column("context_tokens")
    @ApiModelProperty(name = "contextTokens", value = "上下文长度", dataType = "String")
    private Integer contextTokens;

    @Column(value = "delete_flag", isLogicDelete = true)
    @ApiModelProperty(name = "deleteFlag", value = "删除状态 0 未删除 1 删除", dataType = "String")
    private Integer deleteFlag;

    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
    private String createTime;

    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    @Column("create_user")
    @ApiModelProperty(name = "createUser", value = "创建账号", dataType = "String")
    private String createUser;

    @Column("update_user")
    @ApiModelProperty(name = "updateUser", value = "更新账号", dataType = "String")
    private String updateUser;

    @Column("manufacturer_model_desc")
    @ApiModelProperty(name = "manufacturerModelDesc", value = "模型详情", dataType = "String")
    private String manufacturerModelDesc;

    @Column("website")
    @ApiModelProperty(name = "website", value = "官网站点", dataType = "String")
    private String website;

    @Column("manufacturer_icon")
    @ApiModelProperty(name = "manufacturerIcon", value = "厂商logo", dataType = "String")
    private String manufacturerIcon;
}
