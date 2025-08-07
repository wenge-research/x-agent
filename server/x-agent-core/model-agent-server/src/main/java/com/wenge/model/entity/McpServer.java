package com.wenge.model.entity;

import cn.hutool.json.JSONObject;
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
 * Description: mcp 服务实体类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-04-14 21:32:47
 */
@ApiModel
@Data
@Table(value = "mcp_server", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class McpServer implements Serializable {

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
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deletedFlag", value = "0-未删除 1-删除", dataType = "Integer")
	private Integer deletedFlag;

	/**
	 * 描述
	 */
	@Column("description")
	@ApiModelProperty(name = "description", value = "描述", dataType = "String")
	private String description;

	/**
	 * 图标地址
	 */
	@Column("icon")
	@ApiModelProperty(name = "icon", value = "图标地址", dataType = "String")
	private String icon;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 安装方式，默认 sse
	 */
	@Column("install_way")
	@ApiModelProperty(name = "installWay", value = "安装方式，默认 sse", dataType = "String")
	private String installWay;

	/**
	 * mcp的id
	 */
	@Column("mcp_id")
	@ApiModelProperty(name = "mcpId", value = "mcp的id", dataType = "String")
	private String mcpId;

	/**
	 * mcp名称
	 */
	@Column("mcp_name")
	@ApiModelProperty(name = "mcpName", value = "mcp名称", dataType = "String")
	private String mcpName;

	/**
	 * 状态：1 暂存；2 上架
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态：1 暂存；2 上架", dataType = "String")
	private String status;

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
	 * 服务地址
	 */
	@Column("url")
	@ApiModelProperty(name = "url", value = "服务地址", dataType = "String")
	private String url;

	/**
	 * 服务类型
	 */
	@Column("mcp_type")
	@ApiModelProperty(name = "mcpType", value = "服务类型", dataType = "String")
	private String mcpType;

	@Column("api_key")
	@ApiModelProperty(name = "apiKey", value = "apikey", dataType = "String")
	private String apiKey;

	@Column("npx_uvx_service")
	@ApiModelProperty(name = "npxUvxService", value = "NPX和UVX服务", dataType = "String")
	private String npxUvxService;

	@Column(ignore = true)
	private JSONObject arg;

	/**
	 * 安装方式，默认 sse
	 */
	@Column(ignore = true)
	private String type;

	@Column(ignore = true)
	private List<McpFunction> mcpFunctionList;

	@Column(ignore = true)
	private String applicationId;

	/**
	 * mcp关联用户状态 0 关闭  1 开启
	 */
	@Column(ignore = true)
	private Integer mcpServerUserStatus = 0;

	/**
	 * mcp归属,是官方创建还是个人创建
	 */
	@Column("owner_type")
	@ApiModelProperty(name = "ownerType",value = "MCP服务归属,是官方创建还是个人创建", dataType = "String", notes = "personal:个人创建, official:官方创建")
	private String ownerType;
}