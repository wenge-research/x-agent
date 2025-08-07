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
 * Description: 应用用户关联表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-12 13:41:04
 *
 */
@ApiModel
@Data
@Table("application_user")
@EqualsAndHashCode(callSuper = false)
public class ApplicationUser implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	/**
	 * 主键自增
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 用户id
	 */
	@Column("user_id")
	@ApiModelProperty(name = "userId", value = "用户id", dataType = "String")
	private String userId;
}