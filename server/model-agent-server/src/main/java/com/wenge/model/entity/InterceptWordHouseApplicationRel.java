package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author: caohaifeng
 * @date: 2024/8/22 15:03
 * @Description: 拦截词库与应用关联实体类
 * @Version:1.0
 **/
@ApiModel
@Data
@Table("intercept_word_house_application_rel")
@EqualsAndHashCode(callSuper = false)
public class InterceptWordHouseApplicationRel implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	@Column("id")
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;


	/**
	 *
	 */
	@Column("intercept_word_house_id")
	@ApiModelProperty(name = "intercept_word_house_id", value = "", dataType = "Long")
	private Long interceptWordHouseId;

	/**
	 * 状态：1 启用；2 停用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态：1 启用；2 停用", dataType = "String")
	private String status;

	/**
	 * 租户id
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	private String tenantId;

	/**
	 * 应用id
	 */
	@Column(value = "application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	@Column(ignore = true)
	@ApiModelProperty(name = "applicationInfo 应用详情")
	private ApplicationInfo applicationInfo;
}