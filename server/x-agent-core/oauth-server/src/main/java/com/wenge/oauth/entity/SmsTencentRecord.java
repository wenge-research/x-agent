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
 * Description: 腾讯云短信记录表实体类
 * @Author: antord
 * Version: 1.0
 * Create Date Time: 2025-02-08 18:11:08
 *
 */
@ApiModel
@Data
@Table("sms_tencent_record")
@EqualsAndHashCode(callSuper = false)
public class SmsTencentRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键自增
	 */
	@Id(value = "id",keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "主键自增", dataType = "Long")
	private Long id;

	/**
	 * 手机号
	 */
	@Column("phone_number")
	@ApiModelProperty(name = "phoneNumber", value = "手机号", dataType = "String")
	private String phoneNumber;

	/**
	 * 短信模板id
	 */
	@Column("template_id")
	@ApiModelProperty(name = "templateId", value = "短信模板id", dataType = "String")
	private String templateId;

	/**
	 * 短信内容参数，是个数组内容
	 */
	@Column("template_param")
	@ApiModelProperty(name = "templateParam", value = "短信内容参数，是个数组内容", dataType = "String")
	private String templateParam;

	/**
	 * 发送时间
	 */
	@Column("send_time")
	@ApiModelProperty(name = "sendTime", value = "发送时间", dataType = "String")
	private String sendTime;

	/**
	 * 状态,成功/失败
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "状态,成功/失败", dataType = "String")
	private String status;

	/**
	 * 上下文信息
	 */
	@Column("session_context")
	@ApiModelProperty(name = "sessionContext", value = "上下文信息", dataType = "String")
	private String sessionContext;

	/**
	 * 平台返回的结果
	 */
	@Column("callback_content")
	@ApiModelProperty(name = "callbackContent", value = "平台返回的结果", dataType = "String")
	private String callbackContent;
}