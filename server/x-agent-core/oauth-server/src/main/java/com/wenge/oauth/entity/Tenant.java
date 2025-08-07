package com.wenge.oauth.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.oauth.constants.MybatisFiledConstant;
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

import java.io.Serializable;

/**
 * Description: 租户表实体类
 * @Author: author
 * Version: 1.0
 * Create Date Time: 2024-06-28 14:31:45
 *
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "oauth_tenant", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Tenant extends FlexModel<Tenant> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(ignore = true)
	private QueryWrapper currentWrapper;

	/**
	 *
	 */
	@Id(value = "id", keyType = KeyType.Auto)
	@ApiModelProperty(name = "id", value = "", dataType = "Long")
	private Long id;

	/**
	 * 业务id
	 */
	@Column("tenant_id")
	@ApiModelProperty(name = "tenantId", value = "业务id", dataType = "String")
	private String tenantId;

	/**
	 * 租户名称
	 */
	@Column("tenant_name")
	@ApiModelProperty(name = "tenantName", value = "租户名称", dataType = "String")
	private String tenantName;

	/**
	 * 联系人
	 */
	@Column("contacts")
	@ApiModelProperty(name = "contacts", value = "联系人", dataType = "String")
	private String contacts;

	/**
	 * 联系邮箱
	 */
	@Column("contacts_email")
	@ApiModelProperty(name = "contactsEmail", value = "联系邮箱", dataType = "String")
	private String contactsEmail;

	/**
	 * 联系电话
	 */
	@Column("contacts_phone")
	@ApiModelProperty(name = "contactsPhone", value = "联系电话", dataType = "String")
	private String contactsPhone;

	/**
	 * 父租户ID
	 */
	@Column("parent_tenant_id")
	@ApiModelProperty(name = "parentTenantId", value = "父租户ID", dataType = "String")
	private String parentTenantId;

	/**
	 * 租户全路径
	 */
	@Column("full_id")
	@ApiModelProperty(name = "fullId", value = "租户全路径", dataType = "String")
	private String fullId;

	/**
	 * 租户全路径
	 */
	@Column("full_name")
	@ApiModelProperty(name = "fullName", value = "租户全路径", dataType = "String")
	private String fullName;

	/**
	 * 备注
	 */
	@Column("remark")
	@ApiModelProperty(name = "remark", value = "备注", dataType = "String")
	private String remark;

	/**
	 * 用户配额限制
	 */
	@Column("user_num_limit")
	@ApiModelProperty(name = "userNumLimit", value = "用户配额限制", dataType = "Integer")
	private Integer userNumLimit;

	/**
	 * 是否启用: 0 禁用, 1 启用
	 */
	@Column("status")
	@ApiModelProperty(name = "status", value = "是否启用: 0 禁用, 1 启用", dataType = "Integer")
	private Integer status;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@ApiModelProperty(name = "createTime", value = "创建时间", dataType = "String")
	private String createTime;

	/**
	 * 更新时间
	 */
	@Column("update_time")
	@ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
	private String updateTime;

	/**
	 * 创建人
	 */
	@Column("create_user")
	@ApiModelProperty(name = "createUser", value = "创建人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String createUser;

	/**
	 * 更新人
	 */
	@Column("update_user")
	@ApiModelProperty(name = "updateUser", value = "更新人", dataType = "String")
	@OnFieldFill(fill = FieldFill.INSERT_UPDATE, mdcKey = MybatisFiledConstant.MDC_USER_NAME)
	private String updateUser;

	/**
	 * 租户logo地址
	 */
	@Column("logo_url")
	@ApiModelProperty(name = "logoUrl", value = "租户logo地址", dataType = "String")
	private String logoUrl;

	/**
	 * 默认密码
	 */
	@Column("default_password")
	@ApiModelProperty(name = "defaultPassword", value = "默认密码", dataType = "String")
	private String defaultPassword;

	/**
	 * 标题
	 */
	@Column("title")
	@ApiModelProperty(name = "title", value = "标题", dataType = "String")
	private String title;

	/**
	 * 默认密码
	 */
	@Column("favicon")
	@ApiModelProperty(name = "favicon", value = "图标", dataType = "String")
	private String favicon;

	/**
	 * 租户智能体构建数限制
	 */
	@Column("app_num_limit")
	@ApiModelProperty(name = "appNumLimit", value = "租户智能体构建数限制", dataType = "Integer")
	private Integer appNumLimit;

}