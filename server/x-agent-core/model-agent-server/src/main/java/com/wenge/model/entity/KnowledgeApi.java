package com.wenge.model.entity;

import cn.hutool.json.JSONObject;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
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
 * Description: 知识库API采集实体类
 *
 * @Author: ZHAISHUAI
 * Version: 1.0
 * Create Date Time: 2025-05-10 18:06:35
 */
@ApiModel
@Data
@Table(value = "knowledge_api", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class KnowledgeApi implements Serializable, BaseTaskConfig {

	@Id(value = "id", keyType = KeyType.Auto)
	private Long id;

	@Column("knowledge_api_id")
	@ApiModelProperty(name = "knowledgeApiId", value = "业务id", dataType = "String")
	private String knowledgeApiId;

	@Column("name")
	@ApiModelProperty(name = "name", value = "api名称", dataType = "String")
	private String name;

	@Column("request_type")
	@ApiModelProperty(name = "requestType", value = "请求方式 0-GET 1-POST", dataType = "Integer")
	private Integer requestType;

	@Column("api_address")
	@ApiModelProperty(name = "apiAddress", value = "接口地址", dataType = "String")
	private String apiAddress;

	@Column("storage_type")
	@ApiModelProperty(name = "storageType", value = "存储类型 0-全量更新 1-增量更新", dataType = "Integer")
	private Integer storageType;

	@Column("scheduled_task")
	@ApiModelProperty(name = "scheduledTask", value = "定时任务", dataType = "String")
	private String scheduledTask;

	@Column(value = "scheduled_task_desc", typeHandler = Fastjson2TypeHandler.class)
	@ApiModelProperty(name = "scheduledTaskDesc", value = "定时任务描述", dataType = "String")
	private String scheduledTaskDesc;

	@Column("auth_flag")
	@ApiModelProperty(name = "authFlag", value = "是否需要鉴权 0-否 1-是", dataType = "Integer")
	private Integer authFlag;

	@Column("knowledge_id")
	@ApiModelProperty(name = "knowledgeId", value = "知识库id", dataType = "String")
	private String knowledgeId;

	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	@Column(value = "config", typeHandler = Fastjson2TypeHandler.class)
	@ApiModelProperty(name = "config", value = "api请求响应字段信息", dataType = "json")
	private JSONObject config;

	@Column(value = "response_config_desc", typeHandler = Fastjson2TypeHandler.class)
	@ApiModelProperty(name = "responseConfigDesc", value = "api响应字段描述", dataType = "String")
	private String responseConfigDesc;

	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除 0-否 1-是", dataType = "Integer")
	private Integer deleteFlag;

	@Column(value = "scheduled_task_last_time")
	@ApiModelProperty(name = "scheduledTaskLastTime", value = "定时任务最新更新时间", dataType = "String")
	private String scheduledTaskLastTime;

	@Column(value = "scheduled_task_last_state")
	@ApiModelProperty(name = "scheduledTaskLastState", value = "定时任务最新执行状态", dataType = "Integer")
	private Integer scheduledTaskLastState;

	@Column(value = "status")
	@ApiModelProperty(name = "status", value = "状态 0-关闭 1-开启 主要控制任务是否执行", dataType = "Integer")
	private Integer status;

	/**
	 * 文件夹 ID
	 */
	@Column("folders_id")
	@ApiModelProperty(name = "foldersId", value = "文件夹 ID", dataType = "String")
	private String foldersId;

	@Override
	public String getTaskId() {
		return String.valueOf(id);
	}

	@Override
	public String getCrontab() {
		return scheduledTask;
	}
}