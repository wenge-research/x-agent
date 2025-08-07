package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Description: 授权数据实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-08 19:59:42
 *
 */
@ApiModel
@Data
@Table("grant_data")
@EqualsAndHashCode(callSuper = false)
public class GrantData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 目标id
	 */
	@Column("target_id")
	@ApiModelProperty(name = "targetId", value = "目标id", dataType = "String")
	private String targetId;

	/**
	 * 数据id
	 */
	@Column("data_id")
	@ApiModelProperty(name = "dataId", value = "数据id", dataType = "String")
	private String dataId;

	/**
	 * 数据类型，app:应用，knowledge:知识库
	 */
	@Column("data_type")
	@ApiModelProperty(name = "dataType", value = "数据类型，app:应用，knowledge:知识库", dataType = "String")
	private String dataType;

	/**
	 * 目标类型，tenant:租户，user:用户,dept:部门
	 */
	@Column("target_type")
	@ApiModelProperty(name = "targetType", value = "目标类型，tenant:租户，user:用户,dept:部门", dataType = "String")
	private String targetType;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 是否有“复制”按钮权限 0-是 1-否
	 */
	@Column("copy_permission")
	@ApiModelProperty(name = "copyPermission", value = "备注", dataType = "String")
	private Integer copyPermission;

	@Column(ignore = true)
	@ApiModelProperty(name = "targetName", value = "目标类型 目标名称", dataType = "String")
	private String targetName;
}