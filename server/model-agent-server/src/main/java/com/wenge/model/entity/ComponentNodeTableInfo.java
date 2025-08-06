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
 * Description: 数据库节点表数据实体类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:11:32
 *
 */
@ApiModel
@Data
@Table(value = "component_node_table_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class ComponentNodeTableInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键ID", dataType = "Long")
	private Long id;

	/**
	 * 表业务 id
	 */
	@Column("table_id")
	@ApiModelProperty(name = "tableId", value = "表业务 id", dataType = "String")
	private String tableId;

	/**
	 * 表名称
	 */
	@Column("table_name")
	@ApiModelProperty(name = "tableName", value = "表名称", dataType = "String")
	private String tableName;

	/**
	 * 描述
	 */
	@Column("description")
	@ApiModelProperty(name = "description", value = "描述", dataType = "String")
	private String description;

	/**
	 * 图标
	 */
	@Column("icon")
	@ApiModelProperty(name = "icon", value = "图标", dataType = "String")
	private String icon;

	/**
	 * 数据库
	 */
	@Column("data_base")
	@ApiModelProperty(name = "dataBase", value = "数据库", dataType = "String")
	private String dataBase;

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
	 * 是否删除 0-否 1-是
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "是否删除 0-否 1-是", dataType = "Integer")
	private Integer deleteFlag;

	/**
	 * 租户
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenant_id", value = "租户", dataType = "Integer")
	private Integer tenantId;
}