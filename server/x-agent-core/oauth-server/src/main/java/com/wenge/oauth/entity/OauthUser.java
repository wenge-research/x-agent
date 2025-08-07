package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wenge.oauth.enums.UserTypeEnum;
import com.wg.appframe.mybatisflex.annotate.OnFieldFill;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.enums.FieldFill;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Description: 用户表实体类
 *
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-06-05 14:13:28
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "oauth_user", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OauthUser extends FlexModel<OauthUser> implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

	/**
	 * 微信id
	 */
	@Column("open_id")
	@ApiModelProperty(name = "openId", value = "微信用户标识", dataType = "String")
	private String openId;

	/**
	 * 用户登录账号
	 */
	@Column("account_name")
	@ApiModelProperty(name = "accountName", value = "用户登录账号", dataType = "String")
	private String accountName;

	/**
	 * 租户id
	 */
	@Column(value = "tenant_id")
	@ApiModelProperty(name = "tenantId", value = "租户id", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_TENANT_ID)
	private String tenantId;

	/**
	 * 年龄
	 */
	@Column("age")
	@ApiModelProperty(name = "age", value = "年龄", dataType = "Integer")
	private Integer age;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 创建人
	 */
	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	/**
	 * 邮箱
	 */
	@Column("email")
	@ApiModelProperty(name = "email", value = "邮箱", dataType = "String")
	private String email;

	/**
	 * 过期时间(默认为空，表示长期有效)
	 */
	@Column("expire_time")
	@ApiModelProperty(name = "expireTime", value = "过期时间(默认为空，表示长期有效)", dataType = "String")
	private String expireTime;

	/**
	 * 锁定时间
	 */
	@Column("lock_time")
	@ApiModelProperty(name = "lockTime", value = "锁定时间", dataType = "String")
	private String lockTime;

	/**
	 * 用户id
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "用户id", dataType = "Long")
	private Long id;

	/**
	 * 证件号码
	 */
	@Column("id_no")
	@ApiModelProperty(name = "idNo", value = "证件号码", dataType = "String")
	private String idNo;

	/**
	 * 证件类型（1.身份证;2.护照;3.户口簿;4.军人证;5.港澳台居民身份证;6.武警身份证；7.边民出入境通行证）
	 */
	@Column("id_type")
	@ApiModelProperty(name = "idType", value = "证件类型（1.身份证;2.护照;3.户口簿;4.军人证;5.港澳台居民身份证;6.武警身份证；7.边民出入境通行证）", dataType = "Integer")
	private Integer idType;

	/**
	 * 登录密码
	 */
	@Column("password")
	@ApiModelProperty(name = "password", value = "登录密码", dataType = "String")
	private String password;

	/**
	 * 密码过期时间：新增时写入、重置/修改密码时更新
	 */
	@Column("password_expired_time")
	@ApiModelProperty(name = "passwordExpiredTime", value = "密码过期时间：新增时写入、重置/修改密码时更新", dataType = "String")
	private String passwordExpiredTime;

	/**
	 * 手机号
	 */
	@Column("phone")
	@ApiModelProperty(name = "phone", value = "手机号", dataType = "String")
	private String phone;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 性别: 男或女
	 */
	@Column("sex")
	@ApiModelProperty(name = "sex", value = "性别: 男或女", dataType = "String")
	private String sex;

	/**
	 * 用户状态 （0.未激活 1.正常 2.锁定 3.删除，4-注销）
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "用户状态 （0.未激活 1.正常 2.锁定 3.删除）", dataType = "Integer")
	private String status;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	/**
	 * 用户真实姓名
	 */
	@Column("user_name")
	@ApiModelProperty(name = "userName", value = "用户真实姓名", dataType = "String")
	private String userName;

	/**
	 * 密码错误次数
	 */
	@Column("pw_error_num")
	@ApiModelProperty(name = "pwErrorNum", value = "密码错误次数", dataType = "Integer")
	private Integer pwErrorNum;

	/**
	 * 部门id
	 */
	@Column("dept_id")
	@ApiModelProperty(name = "deptId", value = "部门id", dataType = "String")
	private String deptId;

	/**
	 * 用户权限类型,0-系统管理员(超级管理员),1-普通管理员，2-普通用户
	 */
	@Column("power_type")
	@ApiModelProperty(name = "powerType", value = "用户权限类型,0-系统管理员(超级管理员),1-普通管理员，2-普通用户", dataType = "String")
	private String powerType;

	/**
	 * 用户类型，具体查看 {@link UserTypeEnum}
	 */
	@Column("user_type")
	@ApiModelProperty(name = "userType", value = "用户类型，resident：微信公众号的普通居民，staff：工作人员", dataType = "String")
	private String userType;

	/**
	 * 工作职务
	 */
	@Column("work_position")
	@ApiModelProperty(name = "workPosition", value = "工作职务", dataType = "String")
	private String workPosition;

	/**
	 * 主要工作内容
	 */
	@Column("main_duty")
	@ApiModelProperty(name = "mainDuty", value = "主要工作内容", dataType = "String")
	private String mainDuty;

	/**
	 * 审核状态：pass-审核通过，reject-审核不通过，waiting-待审核
	 */
	@Column("check_status")
	@ApiModelProperty(name = "checkStatus", value = "审核状态：pass-审核通过，reject-审核不通过，waiting-待审核", dataType = "String")
	private String checkStatus;

	/**
	 * 审核意见
	 */
	@Column("check_opinion")
	@ApiModelProperty(name = "checkOpinion", value = "审核意见", dataType = "String")
	private String checkOpinion;

	/**
	 * 工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire
	 */
	@Column("work_status")
	@ApiModelProperty(name = "workStatus", value = "工作状态，在职-on，离职-off，调离-transferred，休假-retired，病休-sick，退休-retire", dataType = "String")
	private String workStatus;

	/**
	 * 座机号
	 */
	@Column("landline")
	@ApiModelProperty(name = "landline", value = "座机号", dataType = "String")
	private String landline;

	/**
	 * 角色id
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "roleIdList", value = "角色id", dataType = "String")
	private List<String> roleIdList;

	/**
	 * 用户权限类型,0-系统管理员(超级管理员),1-普通管理员，2-普通用户
	 */
	@Column(ignore = true)
	private String tenantName;

	/**
	 * 部门名称
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "deptName", value = "部门名称", dataType = "String")
	private String deptName;

	/**
	 * 部门id,包括上级id，用于前端回显
	 */
	@Column(ignore = true)
	@ApiModelProperty(name = "deptIds", value = "包括上级id", dataType = "String")
	private List<String> deptIds;

	/**
	 * 用户来源
	 */
	@Column("source")
	@ApiModelProperty(name = "source", value = "用户来源", dataType = "String")
	private String source;

	/**
	 * 智巡主键id
	 */
	@Column("zx_id")
	@ApiModelProperty(name = "zxId", value = "智巡id", dataType = "String")
	private String zxId;
	/**
	 * 用户头像
	 */
	@Column("avatar")
	@ApiModelProperty(name = "avatar", value = "avatar", dataType = "String")
	private String avatar;
	/**
	 * 标签
	 */
	@Column("lable")
	@ApiModelProperty(name = "lable", value = "标签", dataType = "String")
	private String lable;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	//
	//@Override
	//public Collection<? extends GrantedAuthority> getAuthorities() {
	//	return Collections.emptyList();
	//}
	//
	//@Override
	//public String getUsername() {
	//	return this.userName;
	//}
	//
	//@Override
	//public boolean isAccountNonExpired() {
	//	return true;
	//}
	//
	//@Override
	//public boolean isAccountNonLocked() {
	//	return true;
	//}
	//
	//@Override
	//public boolean isCredentialsNonExpired() {
	//	return true;
	//}
	//
	//@Override
	//public boolean isEnabled() {
	//	return true;
	//}
}
