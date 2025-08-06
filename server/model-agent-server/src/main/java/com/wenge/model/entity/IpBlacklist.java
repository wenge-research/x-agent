package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Description: IP黑名单实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-09 15:42:07
 *
 */
@ApiModel
@Data
@Table("ip_blacklist")
@EqualsAndHashCode(callSuper = false)
public class IpBlacklist implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 主键
	 */
	@Column("id")
	@ApiModelProperty(name = "id", value = "主键", dataType = "Long")
	private Long id;

	/**
	 * ip地址
	 */
	@Column("ip_address")
	@ApiModelProperty(name = "ipAddress", value = "ip地址", dataType = "String")
	private String ipAddress;

	/**
	 * 禁用结束时间
	 */
	@Column("lock_time_end")
	@ApiModelProperty(name = "lockTimeEnd", value = "禁用结束时间", dataType = "String")
	private String lockTimeEnd;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 状态[禁用/移除]
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态[禁用/移除]", dataType = "String")
	private String status;
}