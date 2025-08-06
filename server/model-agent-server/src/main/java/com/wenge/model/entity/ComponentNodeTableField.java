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
 * Description: 数据库节点表字段数据实体类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-05-19 14:47:58
 *
 */
@ApiModel
@Data
@Table("component_node_table_field")
@EqualsAndHashCode(callSuper = false)
public class ComponentNodeTableField implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键ID", dataType = "Long")
	private Long id;

	/**
	 * 字段业务id
	 */
	@Column("table_id")
	@ApiModelProperty(name = "tableId", value = "字段业务id", dataType = "String")
	private String tableId;

	/**
	 * 字段id
	 */
	@Column("field_id")
	@ApiModelProperty(name = "fieldId", value = "字段id", dataType = "String")
	private String fieldId;

	/**
	 * 字段名称
	 */
	@Column("field_name")
	@ApiModelProperty(name = "fieldName", value = "字段名称", dataType = "String")
	private String fieldName;

	/**
	 * 字段类型(varchar,int,decimal,datetime)
	 */
	@Column("field_type")
	@ApiModelProperty(name = "fieldType", value = "字段类型(varchar,int,decimal,datetime)", dataType = "String")
	private String fieldType;

	/**
	 * 字段注释
	 */
	@Column("comment")
	@ApiModelProperty(name = "comment", value = "字段注释", dataType = "String")
	private String comment;

	/**
	 * 是否必要，默认 0（1-必要，0-不必要）
	 */
	@Column("request_flag")
	@ApiModelProperty(name = "requestFlag", value = "是否必要，默认 0（1-必要，0-不必要）", dataType = "String")
	private String requestFlag;
}