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
 * Description: 实体类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-27 16:05:01
 *
 */
@ApiModel
@Data
@Table("oauth_login_log")
@EqualsAndHashCode(callSuper = false)
public class OauthLoginLog implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 用户id
	 */
	@Column("user_id")
	@ApiModelProperty(name = "userId", value = "用户id", dataType = "Long")
	private Long userId;

	/**
	 * 操作
	 */
	@Column("opera")
	@ApiModelProperty(name = "opera", value = "操作", dataType = "String")
	private String opera;

	/**
	 * 操作账号
	 */
	@Column("account_name")
	@ApiModelProperty(name = "accountName", value = "操作账号", dataType = "String")
	private String accountName;

	/**
	 * 登录ip
	 */
	@Column("login_ip")
	@ApiModelProperty(name = "loginIp", value = "登录ip", dataType = "String")
	private String loginIp;

	/**
	 * 登录系统
	 */
	@Column("login_system")
	@ApiModelProperty(name = "loginSystem", value = "登录系统", dataType = "String")
	private String loginSystem;

	/**
	 * 登录时间
	 */
	@Column("login_time")
	@ApiModelProperty(name = "loginTime", value = "登录时间", dataType = "String")
	private String loginTime;

	/**
	 * 登出时间
	 */
	@Column("logout_time")
	@ApiModelProperty(name = "logoutTime", value = "登出时间", dataType = "String")
	private String logoutTime;

	/**
	 * 创建时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "创建时间", dataType = "String")
	private String updateTime;

	/**
	 * 登录方式
	 */
	@Column("login_way")
	@ApiModelProperty(name = "loginWay", value = "登录方式", dataType = "String")
	private String loginWay;
}