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
 * Description: 数据集的表配置实体类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 20:14:10
 */
@ApiModel
@Data
@Table(value = "collect_base_config", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class CollectBaseConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 业务 id
	 */
	@Column("config_id")
	@ApiModelProperty(name = "configId", value = "业务 id", dataType = "String")
	private String configId;

	/**
	 * 数据库名称
	 */
	@Column("config_name")
	@ApiModelProperty(name = "configName", value = "数据库名称", dataType = "String")
	private String configName;

	/**
	 * 数据源配置，json 格式，包含 host，port，username，password，database
	 */
	@Column("database_conf")
	@ApiModelProperty(name = "databaseConf", value = "数据源配置，json 格式，包含 host，port，username，password，database", dataType = "String")
	private String databaseConf;

	/**
	 * 源文件地址，导入文件时才有地址
	 */
	@Column("file_url")
	@ApiModelProperty(name = "fileUrl", value = "源文件地址，导入文件时才有地址", dataType = "String")
	private String fileUrl;

	/**
	 * 格式
	 */
	@Column("formatter")
	@ApiModelProperty(name = "formatter", value = "格式", dataType = "String")
	private String formatter;

	/**
	 * 类型:db,file
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型:db,file", dataType = "String")
	private String type;

	/**
	 * 属性字段配置，json 格式
	 */
	@Column("field_config")
	@ApiModelProperty(name = "fieldConfig", value = "属性字段配置，json 格式", dataType = "String")
	private String fieldConfig;

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
	 * 删除标志(0:未删除,1:删除)
	 */
	@Column(value = "deleted_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deletedFlag", value = "删除标志(0:未删除,1:删除)", dataType = "Integer")
	private Integer deletedFlag;

	/**
	 * 数据库描述
	 */
	@Column("describe")
	@ApiModelProperty(name = "describe", value = "数据库描述", dataType = "String")
	private String describe;

	/**
	 * 数据集 id
	 */
	@Column("collect_id")
	@ApiModelProperty(name = "collect_id", value = "数据集 id", dataType = "String")
	private String collectId;

	/**
	 * 数据库配置ID
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "视频ID", dataType = "Long")
	private Long id;

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
}