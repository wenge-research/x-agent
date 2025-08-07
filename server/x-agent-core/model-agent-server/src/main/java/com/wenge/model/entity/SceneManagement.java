package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
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
 * Description: 业务场景表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-11-05 10:12:41
 *
 */
@ApiModel
@Data
@Table(value = "scene_management", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class SceneManagement implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 场景ID
	 */
	@Column("scene_id")
	@ApiModelProperty(name = "sceneId", value = "场景ID", dataType = "String")
	private String sceneId;

	/**
	 * 场景名称
	 */
	@Column("scene_name")
	@ApiModelProperty(name = "sceneName", value = "场景名称", dataType = "String")
	private String sceneName;

	/**
	 * 场景别名
	 */
	@Column("alias_name")
	@ApiModelProperty(name = "aliasName", value = "场景别名", dataType = "String")
	private String aliasName;

	/**
	 * 场景描述
	 */
	@Column("matter_desc")
	@ApiModelProperty(name = "matterDesc", value = "场景描述", dataType = "String")
	private String matterDesc;

	/**
	 * 大模型预设话术，大模型率先发起对话
	 */
	@Column("assistant_content")
	@ApiModelProperty(name = "assistantContent", value = "大模型预设话术，大模型率先发起对话", dataType = "String")
	private String assistantContent;

	/**
	 * 系统提示词
	 */
	@Column("system_prompt")
	@ApiModelProperty(name = "systemPrompt", value = "系统提示词", dataType = "String")
	private String systemPrompt;

	/**
	 * 是否显示入口[是/否]
	 */
	@Column("show_enter_flag")
	@ApiModelProperty(name = "showEnterFlag", value = "是否显示入口[是/否]", dataType = "String")
	private String showEnterFlag;

	/**
	 * 状态，1-启用，0-停用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态，1-启用，0-停用", dataType = "String")
	private String status;

	/**
	 * 图标地址
	 */
	@Column("icon")
	@ApiModelProperty(name = "icon", value = "图标地址", dataType = "String")
	private String icon;

	/**
	 * 处理方式，json格式
	 */
	@Column("processing")
	@ApiModelProperty(name = "processing", value = "处理方式，json格式", dataType = "String")
	private String processing;

	/**
	 * 0未删除 1删除
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "0未删除 1删除", dataType = "Integer")
	private Integer deleteFlag;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 创建人
	 */
	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	/**
	 * 关联应用的数量
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "appNum", value = "关联应用的数量", dataType = "String")
	private Long appNum;

	/**
	 * 租户ID
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户ID", dataType = "String")
	private String tenantId;
}