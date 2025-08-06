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
 * Description: 龙华政府在线实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-20 15:57:11
 *
 */
@ApiModel
@Data
@Table("lh_online")
@EqualsAndHashCode(callSuper = false)
public class LhOnline implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 栏目ID，主键，自动递增
	 */
	@Column("column_id")
	@ApiModelProperty(name = "columnId", value = "栏目ID，主键，自动递增", dataType = "String")
	private String columnId;

	/**
	 * 栏目名称
	 */
	@Column("column_name")
	@ApiModelProperty(name = "columnName", value = "栏目名称", dataType = "String")
	private String columnName;

	/**
	 * 创建时间，默认为当前时间
	 */
	@Column("created_time")
	@ApiModelProperty(name = "createdTime", value = "创建时间，默认为当前时间", dataType = "String")
	private String createdTime;

	/**
	 * 目录
	 */
	@Column("directory")
	@ApiModelProperty(name = "directory", value = "目录", dataType = "String")
	private String directory;

	/**
	 * HTML网页地址
	 */
	@Column("html_url")
	@ApiModelProperty(name = "htmlUrl", value = "HTML网页地址", dataType = "String")
	private String htmlUrl;

	/**
	 * 主键，自动递增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键，自动递增", dataType = "Long")
	private Long id;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 状态，active为激活，inactive为未激活
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态，active为激活，inactive为未激活", dataType = "String")
	private String status;
}