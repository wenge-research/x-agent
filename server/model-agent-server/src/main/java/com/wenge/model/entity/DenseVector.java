package com.wenge.model.entity;

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
 * Description: 向量化模型实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-09-11 14:45:53
 *
 */
@ApiModel
@Data
@Table("dense_vector")
@EqualsAndHashCode(callSuper = false)
public class DenseVector implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 维度数量
	 */
	@Column("dimension")
	@ApiModelProperty(name = "dimension", value = "维度数量", dataType = "Integer")
	private Integer dimension;

	/**
	 * 主键
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * 调用api接口地址
	 */
	@Column("uri")
	@ApiModelProperty(name = "uri", value = "调用api接口地址", dataType = "String")
	private String uri;

	/**
	 * 向量化模型编码，用来匹配java代码块
	 */
	@Column("vector_code")
	@ApiModelProperty(name = "vectorCode", value = "向量化模型编码，用来匹配java代码块", dataType = "String")
	private String vectorCode;

	/**
	 * 向量化模型id
	 */
	@Column("vector_id")
	@ApiModelProperty(name = "vectorId", value = "向量化模型id", dataType = "String")
	private String vectorId;

	/**
	 * 向量化模型名称
	 */
	@Column("vector_name")
	@ApiModelProperty(name = "vectorName", value = "向量化模型名称", dataType = "String")
	private String vectorName;

	/**
	 * deleteFlag
	 */
	@Column(value = "delete_flag", isLogicDelete = true)
	@ApiModelProperty(name = "deleteFlag", value = "deleteFlag", dataType = "Integer")
	private Integer deleteFlag;

	/**
	 * enableFlag
	 */
	@Column(value = "enable_flag")
	@ApiModelProperty(name = "enableFlag", value = "enableFlag", dataType = "Integer")
	private Integer enableFlag;

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

	/**
	 * 租户id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	private String tenantId;
}