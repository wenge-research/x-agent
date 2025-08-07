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
 * Description: 字典类型实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-30 18:03:58
 *
 */
@ApiModel
@Data
@Table("dict_type")
@EqualsAndHashCode(callSuper = false)
public class DictType implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id(value = "id",keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 租户id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	private String tenantId;

	/**
	 * 字典分类id
	 */
	@Column("dict_type_id")
	@ApiModelProperty(name = "dictTypeId", value = "字典分类id", dataType = "String")
	private String dictTypeId;

	/**
	 * 字典分类
	 */
	@Column("dict_type_code")
	@ApiModelProperty(name = "dictTypeCode", value = "字典分类", dataType = "String")
	private String dictTypeCode;

	/**
	 * 字典名称
	 */
	@Column("dict_name")
	@ApiModelProperty(name = "dictName", value = "字典名称", dataType = "String")
	private String dictName;

	/**
	 * 排序
	 */
	@Column("dict_order")
	@ApiModelProperty(name = "dictOrder", value = "排序", dataType = "Integer")
	private Integer dictOrder;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 1.正常，0.停用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "1.正常，0.停用", dataType = "Integer")
	private Integer status;

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
	private String createUser;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	private String updateUser;
}