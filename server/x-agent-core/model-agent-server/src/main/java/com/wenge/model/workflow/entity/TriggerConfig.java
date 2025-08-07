package com.wenge.model.workflow.entity;

import cn.hutool.json.JSONObject;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.wenge.model.task.manager.BaseTaskConfig;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 触发器相关配置
 */
@Data(staticConstructor = "create")
@ApiModel(value = "触发器相关配置")
@Table(value = "trigger_config", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class TriggerConfig implements Cloneable, Serializable, BaseTaskConfig {

    @Id
    private Long id;

    @Column("triggerId")
    @ApiModelProperty(name = "triggerId", value = "组件id 有业务作用", dataType = "String")
    private String triggerId;

    @Column("application_id")
    @ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
    private String applicationId;

    @Column("component_id")
    @ApiModelProperty(name = "componentId", value = "组件id", dataType = "String")
    private String componentId;

    @Column("trigger_type")
    @ApiModelProperty(name = "triggerType", value = "触发器方式 (1: 定时触发, 2: 事件)", dataType = "String")
    private Integer triggerType;

    @Column("time_zone")
    @ApiModelProperty(name = "timeZone", value = "时区", dataType = "String")
    private String timeZone;

    @Column("crontab")
    @ApiModelProperty(name = "crontab", value = "cron表达式", dataType = "String")
    private String crontab;

    @Column("crontab_type")
    @ApiModelProperty(name = "crontabType", value = "cron表达式类型 1 cron表达式  2 下拉(前端展示样式，下拉前端也需要转成cron表达式)", dataType = "String")
    private String crontabType;

    @Column("trigger_range")
    @ApiModelProperty(name = "triggerRange", value = "触发器范围 (1: 全部, 2: 指定用户)", dataType = "String")
    private Integer triggerRange;

    @Column("event_id")
    @ApiModelProperty(name = "eventId", value = "事件ID", dataType = "String")
    private String eventId;

    @Column(value = "config", typeHandler = Fastjson2TypeHandler.class)
    @ApiModelProperty(name = "config", value = "配置信息 (JSON格式)", dataType = "String")
    private JSONObject config;

    @Column("status")
    @ApiModelProperty(name = "eventId", value = "状态 (1: 启用, 0: 禁用)", dataType = "String")
    private Integer status;

    @Column("created_time")
    private String createdTime;

    @Column("updated_time")
    private String updatedTime;

    @Column("create_user")
    @OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String createUser;

    @Column("update_user")
    @OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
    private String updateUser;

    @Column("tenant_id")
    @ApiModelProperty(name = "tenantId", value = "租户ID", dataType = "String")
    private String tenantId;

    @Column("user_id")
    @ApiModelProperty(name = "userId", value = "用户ID", dataType = "String")
    private String userId;

    @Override
    public String getTaskId() {
        return String.valueOf(id);
    }
}