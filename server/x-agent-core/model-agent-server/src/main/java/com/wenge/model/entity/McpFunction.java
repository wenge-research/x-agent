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
import java.util.List;

/**
 * Description: mcp 工具实体类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:38:54
 *
 */
@ApiModel
@Data
@Table(value ="mcp_function", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class McpFunction implements Serializable {

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
	 * 工具名称
	 */
	@Column("function_name")
	@ApiModelProperty(name = "functionName", value = "工具名称", dataType = "String")
	private String functionName;

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
	 * 请求方式
	 */
	@Column("method")
	@ApiModelProperty(name = "method", value = "请求方式", dataType = "String")
	private String method;

	/**
	 * 请求地址
	 */
	@Column("url")
	@ApiModelProperty(name = "url", value = "请求地址", dataType = "String")
	private String url;

	/**
	 * 请求头，json 格式
	 */
	@Column("headers")
	@ApiModelProperty(name = "headers", value = "请求头，json 格式", dataType = "String")
	private String headers;

	/**
	 * 工具参数
	 */
	@Column(ignore = true)
	List<McpParameter> parameterList;

	/**
	 * 安装方式
	 */
	@Column(ignore = true)
	private String installWay;

}