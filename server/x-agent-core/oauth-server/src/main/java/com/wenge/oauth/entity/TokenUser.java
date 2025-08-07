package com.wenge.oauth.entity;

import com.wenge.oauth.enums.UserSourceEnum;
import com.wenge.oauth.enums.UserTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Description: 用户表实体类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-05 14:13:28
 */
@Data
public class TokenUser implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户登录账号
	 */
	@ApiModelProperty(name = "accountName", value = "用户登录账号", dataType = "String")
	private String accountName;

	/**
	 * 租户id
	 */
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	private String tenantId;

	/**
	 * 年龄
	 */
	@ApiModelProperty(name = "age", value = "年龄", dataType = "Integer")
	private Integer age;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 创建人
	 */
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	private String createUser;

	/**
	 * 邮箱
	 */
	@ApiModelProperty(name = "email", value = "邮箱", dataType = "String")
	private String email;

	/**
	 * 过期时间(默认为空，表示长期有效)
	 */
	@ApiModelProperty(name = "expireTime", value = "过期时间(默认为空，表示长期有效)", dataType = "String")
	private String expireTime;

	/**
	 * 锁定时间
	 */
	@ApiModelProperty(name = "lockTime", value = "锁定时间", dataType = "String")
	private String lockTime;

	/**
	 * 用户id
	 */
	@ApiModelProperty(name = "id", value = "用户id", dataType = "Long")
	private Long id;

	/**
	 * 证件号码
	 */
	@ApiModelProperty(name = "idNo", value = "证件号码", dataType = "String")
	private String idNo;

	/**
	 * 证件类型（1.身份证;2.护照;3.户口簿;4.军人证;5.港澳台居民身份证;6.武警身份证；7.边民出入境通行证）
	 */
	@ApiModelProperty(name = "idType", value = "证件类型（1.身份证;2.护照;3.户口簿;4.军人证;5.港澳台居民身份证;6.武警身份证；7.边民出入境通行证）", dataType = "Integer")
	private Integer idType;

	/**
	 * 登录密码
	 */
	@ApiModelProperty(name = "password", value = "登录密码", dataType = "String")
	private String password;

	/**
	 * 密码过期时间：新增时写入、重置/修改密码时更新
	 */
	@ApiModelProperty(name = "passwordExpiredTime", value = "密码过期时间：新增时写入、重置/修改密码时更新", dataType = "String")
	private String passwordExpiredTime;

	/**
	 * 手机号
	 */
	@ApiModelProperty(name = "phone", value = "手机号", dataType = "String")
	private String phone;

	/**
	 * 备注
	 */
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 性别: 男或女
	 */
	@ApiModelProperty(name = "sex", value = "性别: 男或女", dataType = "null")
	private String sex;

	/**
	 * 用户状态 （0.未激活 1.正常 2.锁定 3.删除）
	 */
	@ApiModelProperty(name = "status", value = "用户状态 （0.未激活 1.正常 2.锁定 3.删除）", dataType = "Integer")
	private String status;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 更新人
	 */
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	private String updateUser;

	/**
	 * 用户真实姓名
	 */
	@ApiModelProperty(name = "userName", value = "用户真实姓名", dataType = "String")
	private String userName;

	/**
	 * 密码错误次数
	 */
	@ApiModelProperty(name = "pwErrorNum", value = "密码错误次数", dataType = "String")
	private Integer pwErrorNum;

	/**
	 * 用户权限类型,0-系统管理员(超级管理员),1-普通管理员，2-普通用户
	 */
	@ApiModelProperty(name = "powerType", value = "用户权限类型,0-系统管理员(超级管理员),1-普通管理员，2-普通用户", dataType = "String")
	private String powerType;

	/**
	 * 部门id
	 */
	@ApiModelProperty(name = "deptId", value = "部门id", dataType = "String")
	private String deptId;

	/**
	 * 部门名称
	 */
	@ApiModelProperty(name = "deptName", value = "部门名称", dataType = "String")
	private String deptName;

	/**
	 * 用户类型，具体查看 {@link UserTypeEnum}
	 */
	@ApiModelProperty(name = "userType", value = "用户类型，resident：微信公众号的普通居民，staff：工作人员", dataType = "String")
	private String userType;

	/**
	 * 用户来源 {@link UserSourceEnum}
	 */
	@ApiModelProperty(name = "source", value = "用户来源", dataType = "String")
	private String source;

	/**
	 * 是否首次登录
	 */
	private Boolean firstLoginFlag;

	/**
	 * 微信id
	 */
	@ApiModelProperty(name = "openId", value = "微信用户标识", dataType = "String")
	private String openId;

	/**
	 * 用户头像
	 */
	@ApiModelProperty(name = "avatar", value = "avatar", dataType = "String")
	private String avatar;

	/**
	 * 用户登录方式
	 */
	private UserSourceEnum longinWay;

}
