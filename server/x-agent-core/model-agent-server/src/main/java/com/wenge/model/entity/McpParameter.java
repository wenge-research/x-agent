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
 * Description: mcp 工具实体类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:39:04
 */
@ApiModel
@Data
@Table(value = "mcp_parameter", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class McpParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 创建人ID
	 */
	@Column("create_user_id")
	@ApiModelProperty(name = "createUserId", value = "创建人ID", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_ID)
	private String createUserId;

	/**
	 * 创建人名称
	 */
	@Column("create_user_name")
	@ApiModelProperty(name = "createUserName", value = "创建人名称", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUserName;

	/**
	 * 0-未删除 1-删除
	 */
	@Column(value = "deleted_flag",isLogicDelete = true)
	@ApiModelProperty(name = "deletedFlag", value = "0-未删除 1-删除", dataType = "Integer")
	private Integer deletedFlag;

	/**
	 * 工具 id
	 */
	@Column("function_id")
	@ApiModelProperty(name = "functionId", value = "工具 id", dataType = "String")
	private String functionId;

	/**
	 * 参数类型
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "参数类型", dataType = "String")
	private String type;

	/**
	 * 参数 id
	 */
	@Column("param_id")
	@ApiModelProperty(name = "paramId", value = "参数 id", dataType = "String")
	private String paramId;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * mcp的id
	 */
	@Column("mcp_id")
	@ApiModelProperty(name = "mcpId", value = "mcp的id", dataType = "String")
	private String mcpId;

	/**
	 * 描述
	 */
	@Column("description")
	@ApiModelProperty(name = "description", value = "描述", dataType = "String")
	private String description;

	/**
	 * 参数名称
	 */
	@Column("param_name")
	@ApiModelProperty(name = "paramName", value = "参数名称", dataType = "String")
	private String paramName;

	/**
	 * 是否必填，1-必填，0-非必填
	 */
	@Column("required")
	@ApiModelProperty(name = "required", value = "是否必填，1-必填，0-非必填", dataType = "String")
	private String required;

	/**
	 * 默认值
	 */
	@Column("defaults")
	@ApiModelProperty(name = "defaults", value = "默认值", dataType = "String")
	private String defaults;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 更新人ID
	 */
	@Column("update_user_id")
	@ApiModelProperty(name = "updateUserId", value = "更新人ID", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_ID)
	private String updateUserId;

	/**
	 * 更新人名称
	 */
	@Column("update_user_name")
	@ApiModelProperty(name = "updateUserName", value = "更新人名称", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUserName;

	/**
	 * 参数方式，body， query，path
	 */
	@Column("form")
	@ApiModelProperty(name = "form", value = "参数方式", dataType = "String")
	private String form;
}