package com.wenge.model.entity;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;

@EqualsAndHashCode(callSuper = true)
@IndexName
public class MatterData extends JSONObject {

    private static final long serialVersionUID = 6835483522546908111L;

    /**
     * id
     */
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    @IndexId
    private String id;

    /**
     * 事项id
     */
    @ApiModelProperty(value = "事项id")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String matterId;

    /**
     * 应用ID
     */
    @ApiModelProperty(value = "应用ID")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String applicationId;

    /**
     * 流水id
     */
    @ApiModelProperty(value = "traceId")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String traceId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String status;

    /**
     * 处理人
     */
    @ApiModelProperty(value = "处理人")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String dealUser;

    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String dealTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String remark;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @IndexField(fieldType = FieldType.KEYWORD_TEXT)
    private String createUser;


    public void setMatterId(String matterId) {
        this.matterId = matterId;
        put("matterId", matterId);
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        put("applicationId", applicationId);
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
        put("traceId", traceId);
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        put("createTime", createTime);
    }

    public void setStatus(String status) {
        this.status = status;
        put("status", status);
    }

    public void setId(String id) {
        this.id = id;
        put("id", id);
    }

    public void setRemark(String remark) {
        this.remark = remark;
        put("remark", remark);
    }

    public void setDealUser(String dealUser) {
        this.dealUser = dealUser;
        put("dealUser", dealUser);
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
        put("dealTime", dealTime);
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
        put("createUser", createUser);
    }

    public String getMatterId() {
        return getString("matterId");
    }
    public String getApplicationId() {
        return getString("applicationId");
    }
    public String getCreateTime() {
        return getString("createTime");
    }
    public String getStatus() {
        return getString("status");
    }
    public String getId() {
        return getString("id");
    }
    public String getRemark() {
        return getString("remark");
    }

    public String getTraceId() {
        return getString("traceId");
    }
    public String getDealUser() {
        return getString("dealUser");
    }
    public String getDealTime() {
        return getString("dealTime");
    }
    public String getCreateUser() {
        return getString("createUser");
    }
}

