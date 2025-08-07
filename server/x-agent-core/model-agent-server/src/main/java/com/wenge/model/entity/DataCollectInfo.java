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
 * Description: 数据集信息表实体类
 *
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-07-01 18:05:32
 */
@ApiModel
@Data
@Table(value = "collect_info", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class DataCollectInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 业务 id
	 */
	@Column("collect_id")
	@ApiModelProperty(name = "collectId", value = "业务 id", dataType = "String")
	private String collectId;

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
	 * 数据集描述
	 */
	@Column("describe")
	@ApiModelProperty(name = "describe", value = "数据集描述", dataType = "String")
	private String describe;

	/**
	 * 类型：1-excel,2-mysql,3-csv
	 */
	@Column("type")
	@ApiModelProperty(name = "type", value = "类型：1-excel,2-mysql,3-csv", dataType = "String")
	private String type;

	/**
	 * 自增ID
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "视频ID", dataType = "Long")
	private Long id;

	/**
	 * 数据集名称
	 */
	@Column("name")
	@ApiModelProperty(name = "name", value = "数据集名称", dataType = "String")
	private String name;

	/**
	 * 标签
	 */
	@Column("tag")
	@ApiModelProperty(name = "tag", value = "标签", dataType = "String")
	private String tag;

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
	 * 表数量
	 */
	@Column(ignore = true)
	private Long tableNum;
}