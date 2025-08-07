package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * Description: 认证方式渠道配置表实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-08-02 15:28:16
 *
 */
@ApiModel
@Data
@Table("auth_channel")
@EqualsAndHashCode(callSuper = false)
public class AuthChannel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增id
	 */
	@Column("id")
	@ApiModelProperty(name = "id", value = "自增id", dataType = "Long")
	private Long id;

	/**
	 * 认证渠道id
	 */
	@Column("auth_channel_id")
	@ApiModelProperty(name = "authChannelId", value = "认证渠道id", dataType = "String")
	private String authChannelId;

	/**
	 * 应用id
	 */
	@Column("application_id")
	@ApiModelProperty(name = "applicationId", value = "应用id", dataType = "String")
	private String applicationId;

	/**
	 * 认证渠道名称
	 */
	@Column("channel_name")
	@ApiModelProperty(name = "channelName", value = "认证渠道名称", dataType = "String")
	private String channelName;

	/**
	 * 认证渠道code
	 */
	@Column("channel_code")
	@ApiModelProperty(name = "channelCode", value = "认证渠道code", dataType = "String")
	private String channelCode;

	/**
	 * 在渠道上的appid
	 */
	@Column("app_id")
	@ApiModelProperty(name = "appId", value = "在渠道上的appid", dataType = "String")
	private String appId;

	/**
	 * 秘钥
	 */
	@Column("secret")
	@ApiModelProperty(name = "secret", value = "秘钥", dataType = "String")
	private String secret;

	/**
	 * 令牌
	 */
	@Column("token")
	@ApiModelProperty(name = "token", value = "令牌", dataType = "String")
	private String token;

	/**
	 * 对称加密密钥
	 */
	@Column("aes_key")
	@ApiModelProperty(name = "aesKey", value = "对称加密密钥", dataType = "String")
	private String aesKey;

	/**
	 * 回调地址
	 */
	@Column("redirect_uri")
	@ApiModelProperty(name = "redirectUri", value = "回调地址", dataType = "String")
	private String redirectUri;

	/**
	 * 客户端类型[H5,PC]
	 */
	@Column("client_type")
	@ApiModelProperty(name = "clientType", value = "客户端类型[H5,PC]", dataType = "String")
	private String clientType;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;
}