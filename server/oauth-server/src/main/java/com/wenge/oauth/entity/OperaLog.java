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
import java.util.List;

/**
 * Description: 操作日志实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 16:17:49
 *
 */
@ApiModel
@Data
@Table("opera_log")
@EqualsAndHashCode(callSuper = false)
public class OperaLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 标题
	 */
	@Column("title")
	@ApiModelProperty(name = "title", value = "标题", dataType = "String")
	private String title;

	/**
	 * 业务类型
	 */
	@Column("business_type")
	@ApiModelProperty(name = "businessType", value = "业务类型", dataType = "String")
	private String businessType;

	/**
	 * 请求方式
	 */
	@Column("request_method")
	@ApiModelProperty(name = "requestMethod", value = "请求方式", dataType = "String")
	private String requestMethod;

	/**
	 * 操作类型
	 */
	@Column("operator_type")
	@ApiModelProperty(name = "operatorType", value = "操作类型", dataType = "String")
	private String operatorType;

	/**
	 * 操作人员
	 */
	@Column("oper_name")
	@ApiModelProperty(name = "operName", value = "操作人员", dataType = "String")
	private String operName;

	/**
	 * 请求URL
	 */
	@Column("oper_url")
	@ApiModelProperty(name = "operUrl", value = "请求URL", dataType = "String")
	private String operUrl;

	@Column(ignore = true)
	private List<String> operUrls;

	/**
	 * 操作ip
	 */
	@Column("oper_ip")
	@ApiModelProperty(name = "operIp", value = "操作ip", dataType = "String")
	private String operIp;

	/**
	 * 操作时间
	 */
	@Column("oper_time")
	@ApiModelProperty(name = "operTime", value = "操作时间", dataType = "String")
	private String operTime;
}